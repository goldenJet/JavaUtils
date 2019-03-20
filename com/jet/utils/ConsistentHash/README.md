**手动实现一致性hash算法**
## 一、简单的手动排序数组形式的一致性hash算法
1. 详见类：SortArrayConsistentHash.java
1. 说明:
    > 1. 思路就是把所有节点信息存在一个无序的数组中
    > 1. 提供相应的CRUD和手动排序方法
1. 缺点：
    > 1. 排序的时机没有确定好
    > 1. 排序的效率没有计算好
    > 1. hash 的规则没有提供

## 二、TreeMap 版本
1. 详见类：TreeMapConsistentHash.java
1. 说明:
    > 1. 思路就是把所有节点信息存在一个 TreeMap 中
    > 1. 利用 TreeMap 的排序和 tailMap 方法
    > 1. hash 的计算使用的是 FNV1_32_HASH
    > 1. 此版本也支持了虚拟节点的设计
