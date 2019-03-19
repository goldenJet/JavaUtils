package com.jet.mini.utils;

import java.util.Arrays;

/**
 * @ClassName: SortArrayConsistentHash
 * @Description: 初代数组实现的一致性哈数算法
 * @Author: Jet.Chen
 * @Date: 2019/3/19 23:11
 * @Version: 1.0
 **/
public class SortArrayConsistentHash {

    /**
     * 最为核心的数据结构
     */
    private Node[] buckets;

    /**
     * 桶的初始大小
     */
    private static final int INITIAL_SIZE = 32;

    /**
     * 当前桶的大小
     */
    private int length = INITIAL_SIZE;
    /**
     * 当前桶的使用量
     */
    private int size = 0;

    public SortArrayConsistentHash(){
        buckets = new Node[INITIAL_SIZE];
    }

    public SortArrayConsistentHash(int length){
        if (length < 32) {
            buckets = new Node[INITIAL_SIZE];
        } else {
            this.length = length;
            buckets = new Node[length];
        }
    }

    /**
     * @Description: 写入数据
     * @Param: [hash, value]
     * @return: void
     * @Author: Jet.Chen
     * @Date: 2019/3/19 23:38
     */
    public void add(long hash, String value){
        // 大小判断是否需要扩容
        if (size == length) reSize();
        Node node = new Node(value, hash);
        buckets[++size] = node;
    }

    /**
     * @Description: 删除节点
     * @Param: [hash]
     * @return: boolean
     * @Author: Jet.Chen
     * @Date: 2019/3/20 0:24
     */
    public boolean del(long hash) {
        if (size == 0) return false;
        Integer index = null;
        for (int i = 0; i < length; i++) {
            Node node = buckets[i];
            if (node == null) continue;
            if (node.hash == hash) index = i;
        }
        if (index != null) {
            buckets[index] = null;
            return true;
        }
        return false;
    }

    /**
     * @Description: 排序
     * @Param: []
     * @return: void
     * @Author: Jet.Chen
     * @Date: 2019/3/19 23:48
     */
    public void sort() {
        // 此处的排序不需要关注 eqals 的情况
        Arrays.sort(buckets, 0, size, (o1, o2) -> o1.hash > o2.hash ? 1 : -1);
    }

    /**
     * @Description: 扩容
     * @Param: []
     * @return: void
     * @Author: Jet.Chen
     * @Date: 2019/3/19 23:42
     */
    public void reSize() {
        // 扩容1.5倍
        int newLength = length >> 1 + length;
        buckets = Arrays.copyOf(buckets, newLength);
    }

    /**
     * @Description: 根据一致性hash算法获取node值
     * @Param: [hash]
     * @return: java.lang.String
     * @Author: Jet.Chen
     * @Date: 2019/3/20 0:16
     */
    public String getNodeValue(long hash) {
        if (size == 0) return null;
        for (Node bucket : buckets) {
            // 防止空节点
            if (bucket == null) continue;
            if (bucket.hash >= hash) return bucket.value;
        }
        // 防止循环无法尾部对接首部
        // 场景：仅列出node的hash值，[null, 2, 3...]，但是寻求的hash值是4，上面的第一遍循环很显然没能找到2这个节点，所有需要再循环一遍
        for (Node bucket : buckets) {
            if (bucket != null) return bucket.value;
        }
        return null;
    }

    /**
     * node 记录了hash值和原始的IP地址
     */
    private class Node {
        public String value;
        public long hash;

        public Node(String value, long hash) {
            this.value = value;
            this.hash = hash;
        }

        @Override
        public String toString() {
            return "Node{hash="+hash+", value="+value+"}";
        }
    }



}
