package com.jet.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: SimilarityRatioUtil
 * @Description: 相似度比较
 * @Author: Jet.Chen
 * @Date: 2019/3/26 17:03
 * @Version: 1.0
 **/
public class SimilarityRatioUtil {

    public static void main(String[] args) {
        System.out.println(getSimilarityRatio("www.jetchen.cn", "jet"));
    }

    /**
     * @Description: 相似度比较，
     * @Param: [str, target]
     * @return: float 完全相似=1.0    完全不相似=0.0
     * @Author: Jet.Chen
     * @Date: 2019/3/26
     */
    public static float getSimilarityRatio(String str, String target) {
        if (StringUtils.isBlank(str)) {
            if (StringUtils.isBlank(target)) return 1.0f;
            return 0.0f;
        } else {
            if (StringUtils.isBlank(target)) return 0.0f;
        }
        return 1 - (float) getSimilarityRatioCompare(str, target) / Math.max(str.length(), target.length());
    }

    /**
     * @Description: 矩阵模式来比较相似度
     * @Param: [str, target]
     * @return: int
     * @Author: Jet.Chen
     * @Date: 2019/3/26
     */
    private static int getSimilarityRatioCompare(String str, String target) {
        int d[][];
        int n = str.length();
        int m = target.length();
        int i;
        int j;
        char ch1;
        char ch2;
        int temp;
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

}
