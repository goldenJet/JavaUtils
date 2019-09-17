package com.jet;

import java.util.Random;

/**
* @Description: 跳跃表
* @Author: Jet.Chen
* @Date: 2019/9/16 17:39
*/
public class SkipList <T>{

    private SkipListNode<T> head,tail;
    private int nodes; // 节点总数
    private int listLevel; // 最大层数
    private Random random; // 随机数，用于投掷硬币决定是否要加层高
    private static final double PROBABILITY = 0.25; // 向上提升一个的概率（此处采用redis中的默认值）
    private static final int MAXLEVEL = 32; // 最大层高（此处采用redis中的默认值）

    public SkipList() {
        random = new Random();
        clear();
    }

    /**
    * @Description: 清空跳跃表
    * @Param: []
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:41
    */
    public void clear(){
        head = new SkipListNode<T>(SkipListNode.HEAD_KEY, null);
        tail = new SkipListNode<T>(SkipListNode.TAIL_KEY, null);
        horizontalLink(head, tail);
        listLevel = 0;
        nodes = 0;
    }

    public boolean isEmpty(){
        return nodes == 0;
    }
 
    public int size() {
        return nodes;
    }

    /**
    * @Description: 找到要插入的位置前面的那个key 的最底层节点
    * @Param: [key]
    * @return: com.jet.SkipListNode<T>
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:42
    */
    private SkipListNode<T> findNode(int key){
        SkipListNode<T> p = head;
        while(true){
            while (p.right.getKey() != SkipListNode.TAIL_KEY && p.right.getKey() <= key) {
                p = p.right;
            }
            if (p.down != null) {
                p = p.down;
            } else {
                break;
            }
 
        }
        return p;
    }

    /**
    * @Description: 查找是否存在key，存在则返回该节点，否则返回null
    * @Param: [key]
    * @return: com.wailian.SkipListNode<T>
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:43
    */
    public SkipListNode<T> search(int key){
        SkipListNode<T> p = findNode(key);
        if (key == p.getKey()) {
            return p;
        } else {
            return null;
        }
    }

    /**
    * @Description: 向跳跃表中添加key-value
    * @Param: [k, v]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:43
    */
    public void put(int k,T v){
        SkipListNode<T> p = findNode(k);
        // 如果key值相同，替换原来的value即可结束
        if (k == p.getKey()) {
            p.setValue(v);
            return;
        }
        SkipListNode<T> q = new SkipListNode<>(k, v);
        backLink(p, q);
        int currentLevel = 0; // 当前所在的层级是0
        // 抛硬币
        while (random.nextDouble() < PROBABILITY && currentLevel < MAXLEVEL) {
            // 如果超出了高度，需要重新建一个顶层
            if (currentLevel >= listLevel) {
                listLevel++;
                SkipListNode<T> p1 = new SkipListNode<>(SkipListNode.HEAD_KEY, null);
                SkipListNode<T> p2 = new SkipListNode<>(SkipListNode.TAIL_KEY, null);
                horizontalLink(p1, p2);
                verticalLink(p1, head);
                verticalLink(p2, tail);
                head = p1;
                tail = p2;
            }
            // 将p移动到上一层
            while (p.up == null) {
                p = p.left;
            }
            p = p.up;
 
            SkipListNode<T> e = new SkipListNode<>(k, null); // 只保存key就ok
            backLink(p, e); // 将e插入到p的后面
            verticalLink(e, q); // 将e和q上下连接
            q = e;
            currentLevel++;
        }
        nodes++; // 层数递增
    }

    /**
    * @Description: node1后面插入node2
    * @Param: [node1, node2]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:45
    */
    private void backLink(SkipListNode<T> node1,SkipListNode<T> node2){
        node2.left = node1;
        node2.right = node1.right;
        node1.right.left = node2;
        node1.right = node2;
    }

    /**
    * @Description: 水平双向连接
    * @Param: [node1, node2]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:45
    */
    private void horizontalLink(SkipListNode<T> node1,SkipListNode<T> node2){
        node1.right = node2;
        node2.left = node1;
    }

    /**
    * @Description: 垂直双向连接
    * @Param: [node1, node2]
    * @return: void
    * @Author: Jet.Chen
    * @Date: 2019/9/16 17:45
    */
    private void verticalLink(SkipListNode<T> node1, SkipListNode<T> node2){
        node1.down = node2;
        node2.up = node1;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "跳跃表为空！";
        }
        StringBuilder builder = new StringBuilder();
        SkipListNode<T> p=head;
        while (p.down != null) {
            p = p.down;
        }
 
        while (p.left != null) {
            p = p.left;
        }
        if (p.right!= null) {
            p = p.right;
        }
        while (p.right != null) {
            builder.append(p).append("\n");
            p = p.right;
        }
 
        return builder.toString();
    }
 
}