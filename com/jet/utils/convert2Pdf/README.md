**转pdf**
## 一、word 转 pdf
1. 使用Aspose
1. 这是商业版，需要收费，但是在此手动改改源码将就破解一下下，破解思路:
    > 1. 下载想破解的jar包[官网地址](https://downloads.aspose.com/words/java)
    > 1. 然后进入License.class 查找到需要的 zzZLJ.class
    > 1. 进入 zzZLJ.class，然后就能看到需要改的源码
    > 1. 至于怎么改源码，见 tool 文件夹下的 AsposeWordsCrack191，注意，需要借助 javassist 哦，见 tool/javassist.txt
1. 注意修改 locense 哦，如果不使用 license 的话，转出来的 pdf 是有水印的哦，步骤比较简单：
    > 1. 拷贝 license.xml 到项目中
    > 1. 然后就是跟着案例自己去改吧
    > 1. 案例见 WordUtil.java


