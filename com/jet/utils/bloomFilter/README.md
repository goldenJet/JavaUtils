# 布隆过滤器 java 实现

## 数学推导

#### 误判概率的证明和计算
假设布隆过滤器中的hash function满足simple uniform hashing假设：每个元素都等概率地hash到m个slot中的任何一个，与其它元素被hash到哪个slot无关。若m为bit数，则对某一特定bit位在一个元素由某特定hash function插入时没有被置位为1的概率为：

$1 - \frac 1m$

则k个hash function中没有一个对其置位的概率为：

$(1-\frac 1m)^k$

如果插入了n个元素，但都未将其置位的概率为：

$(1-\frac 1m)^{kn}$

则此位被置位的概率为：

$1-(1-\frac 1m)^{kn}$

现在考虑query阶段，若对应某个待query元素的k bits全部置位为1，则可判定其在集合中。因此将某元素误判的概率为：

$
\left(
    1-\left(
        1-\frac 1m
        \right)^{kn}
\right)^k
$

由于 $(1+x)^{\frac1x} \sim e$，当 $x \rightarrow 0$ 时，并且 $-\frac1m$ 当m很大时趋近于0，所以：

$
\left(1-\left(1-\frac 1m \right)^{kn} \right)^k = \left(1-\left(1- \frac 1m \right)^{-m \cdot \frac {-kn}m} \right)^k \sim \left(1-e^{\frac {-nk}m}\right)^k
$

从上式中可以看出，当m增大或n减小时，都会使得误判率减小，这也符合直觉。&nbsp;现在计算对于给定的m和n，k为何值时可以使得误判率最低。设误判率为k的函数为：

$f(k) = \left(1-e^{-\frac {nk}m}\right)^k$

设 $b=e^{\frac nm}$， 则简化为：$f(k)=(1-b^{-k})^k$ ，

两边取对数得：$lnf(k)=k \cdot ln(1-b^{-k})$，

两边对k求导：

$
\begin{align}
\frac1{f(k)} \cdot f'(k) &= ln(1-b^{-k}) + k\cdot \frac 1{1-b^{-k}}\cdot(-b^{-k})\cdot lnb \cdot (-1) \\
                        &=ln(1-b^{-k}) + k\cdot \frac {b^{-k} \cdot lnb}{1-b^{-k}}
\end{align}
$

下面求最值：

$ln(1-b^{-k}) + k \cdot \frac{b^{-k} \cdot lnb}{1-b^{-k}} = 0$
$\Rightarrow (1-b^{-k}) \cdot ln(1-b^{-k}) = -k \cdot b^{-k} \cdot lnb $
$\Rightarrow (1-b^{-k}) \cdot ln(1-b^{-k}) = b^{-k} \cdot lnb^{-k} $
$\Rightarrow 1-b^{-k} = b^{-k}  $
$\Rightarrow b^{-k} = \frac12  $
$\Rightarrow e^{-\frac{kn}{m}} = \frac12 $ 
$\Rightarrow \frac{kn}{m} = ln2  $
$\Rightarrow k = ln2 \cdot \frac mn = 0.7 \cdot \frac mn $ 

因此，即当 $k = 0.7 \cdot \frac mn$ 时误判率最低，此时误判率为：

$
P(error) = (1-\frac 12)^k = 2^{-k} = 2^{-ln2 \cdot \frac mn} \approx0.6185^{\frac mn}
$

可以看出若要使得误判率 ≤1/2，则：

$
k \ge 1 \Rightarrow \frac mn \ge \frac{1}{ln2}
$

这说明了若想保持某固定误判率不变，则布隆过滤器的 位数 m 与添加的元素数 n 应该是线性同步增加的。

#### 设计和应用布隆过滤器的方法

应用时首先要先由用户决定添加的元素数 n 和期望的误差率 P。这也是一个设计完整的布隆过滤器需要用户输入的仅有的两个参数，之后的所有参数将由系统计算，并由此建立布隆过滤器。

系统首先要计算需要的内存大小 m bits:

$
P = 2^{-ln2 \cdot \frac mn} \Rightarrow lnp = ln2 \cdot(-ln2) \cdot \frac mn \Rightarrow m = - \frac{n \cdot lnp}{(ln2)^2}
$

再由 m，n 得到 hash function 的个数：

$
k = ln2 \cdot \frac mn = 0.7 \cdot \frac mn
$

至此系统所需的参数已经备齐，接下来添加 n个元素至布隆过滤器中，再进行 query。

根据公式，当 k 最优时：

$
P(error) = 2^{-k} \Rightarrow log_2P = -k \Rightarrow k = log_2 \frac 1P \Rightarrow ln2 \frac mn = log_2 \frac  1P
$
$
\Rightarrow \frac mn = ln2 \cdot log_2 \frac 1P = 1.44 \cdot \frac 1P
$

因此可验证当 P=1% 时，存储每个元素需要 9.6 bits：

$
\frac mn  = 1.44 \cdot log_2 \frac 1{0.01} = 9.6 \, bits
$

而每当想将误判率降低为原来的 1/10 ，则存储每个元素需要增加 4.8 bits：

$
\frac mn  = 1.44 \cdot (log_210a - log_2a) = 1.44 \cdot log_210 = 4.8 \, bits
$

这里需要特别注意的是，9.6 bits/element 不仅包含了被置为1的 k 位，还把包含了没有被置为1的一些位数。此时的

$
k = 0.7 \cdot \frac mn = 0.7 * 9.6 = 6.72 \, bits
$

才是每个元素对应的为1的bit位数。

$k = 0.7 \cdot \frac mn$   从而使得 P(error) 最小时，我们注意到：

$P(error) = (1-e^{-\frac{nk}{m}})^k$ 中的 $e^{-\frac{nk}{m}} = \frac 12$   ，即$(1-\frac 1m)^{kn} = \frac 12$

此概率为某 bit 位在插入 n 个元素后未被置位的概率。因此，想保持错误率低，布隆过滤器的空间使用率需为 50%。