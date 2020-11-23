package com.jet;

/**
 * @ClassName: EscapeAnalysisTest
 * @Description: http://www.jetchen.cn
 * 逃逸分析 demo
 * @Author: Jet.Chen
 * @Date: 2020/11/23 14:26
 * @Version: 1.0
 **/
public class EscapeAnalysisTest {

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            allot();
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
    }

    private static void allot() {
        byte[] b = new byte[2];
        b[0] = 1;
    }

}
