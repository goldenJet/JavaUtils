package com.jet.mini.utils;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @ClassName: TreeMapConsistentHash
 * @Description: treeMap 实现的进化版一致性hash
 * @Author: Jet.Chen
 * @Date: 2019/3/20 20:44
 * @Version: 1.0
 **/
public class TreeMapConsistentHash {
    /**
     * 主要数据结构
     */
    private TreeMap<Long, String> treeMap = new TreeMap<>();

    /**
     * 自定义虚拟节点数量
     */
    private static final int VIRTUAL_NODE_NUM = 10;

    /**
     * 普通的增加节点
     */
    @Deprecated
    public void add (String key, String value) {
        long hash = hash(key);
        treeMap.put(hash, value);
    }

    /**
     * 存在虚拟节点
     */
    public void add4VirtualNode(String key, String value) {
        for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
            long hash = hash(key + "&&VIR" + i);
            treeMap.put(hash, value);
        }
        treeMap.put(hash(key), value);
    }

    /**
     * 读取节点值
     * @param key
     * @return
     */
    public String getNode(String key) {
        long hash = hash(key);
        SortedMap<Long, String> sortedMap = treeMap.tailMap(hash);
        String value;
        if (!sortedMap.isEmpty()) {
            value = sortedMap.get(sortedMap.firstKey());
        } else {
            value = treeMap.firstEntry().getValue();
        }
        // 判断是否是虚拟节点
        int markIndex = value.indexOf("&&VIR");
        if (markIndex == -1) return value;
        return value.substring(0, markIndex);
    }

    /**
     * 使用的是 FNV1_32_HASH
     */
    public long hash(String key) {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for(int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) hash = Math.abs(hash);
        return hash;
    }
}
