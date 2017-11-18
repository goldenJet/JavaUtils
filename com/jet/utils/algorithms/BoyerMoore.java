package com.jet.utils.algorithms;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {
    public static void main(String[] args) {

        String text = "中国是一个啊伟大的国度啊；啊伟大的国度啊；啊伟大的祖国啊";
        String pattern = "啊伟大的国度啊";
        boyerMoore(pattern, text);
    }

    public static void boyerMoore(String pattern, String text) {
        int m = pattern.length();
        int n = text.length();
        Map<String, Integer> bmBc = new HashMap<>();
        int[] bmGs = new int[m];
        //初始化
        preBmBc(pattern, m, bmBc);
        preBmGs(pattern, m, bmGs);
        //开始匹配
        int j = 0;
        int i;
        int count = 0;
        while (j <= n - m) {
            for (i = m - 1; i >= 0 && pattern.charAt(i) == text.charAt(i + j); i--) {
                //用于计数
                count++;
            }
            if (i < 0) {
                System.out.println("one position is:" + j);
                j += bmGs[0];
            } else {
                j += Math.max(bmGs[i], getBmBc(String.valueOf(text.charAt(i + j)), bmBc, m) - m + 1 + i);
            }
        }
        System.out.println("count:" + count);
    }

    /**
     * 坏字符初始化
     */
    private static void preBmBc(String pattern, int patLength, Map<String, Integer> bmBc) {
        System.out.println("bmbc start process...");
        for (int i = patLength - 2; i >= 0; i--) {
            if (!bmBc.containsKey(String.valueOf(pattern.charAt(i)))) {
                bmBc.put(String.valueOf(pattern.charAt(i)), patLength - i - 1);
            }
        }
    }

    /**
     * 好后缀初始化
     */
    private static void preBmGs(String pattern, int patLength, int[] bmGs) {
        int i, j;
        int[] suffix = new int[patLength];
        suffix(pattern, patLength, suffix);
        //模式串中没有子串匹配上好后缀，也找不到一个最大前缀
        for (i = 0; i < patLength; i++) {
            bmGs[i] = patLength;
        }
        //模式串中没有子串匹配上好后缀，但找到一个最大前缀
        j = 0;
        for (i = patLength - 1; i >= 0; i--) {
            if (suffix[i] == i + 1) {
                for (; j < patLength - 1 - i; j++) {
                    if (bmGs[j] == patLength) {
                        bmGs[j] = patLength - 1 - i;
                    }
                }
            }
        }
        //模式串中有子串匹配上好后缀
        for (i = 0; i < patLength - 1; i++) {
            bmGs[patLength - 1 - suffix[i]] = patLength - 1 - i;
        }
        System.out.print("bmGs:");
        for (i = 0; i < patLength; i++) {
            System.out.print(bmGs[i] + ",");
        }
        System.out.println();
    }

    private static void suffix(String pattern, int patLength, int[] suffix) {
        suffix[patLength - 1] = patLength;
        int q;
        for (int i = patLength - 2; i >= 0; i--) {
            q = i;
            while (q >= 0 && pattern.charAt(q) == pattern.charAt(patLength - 1 - i + q)) {
                q--;
            }
            suffix[i] = i - q;
        }
    }


    private static int getBmBc(String c, Map<String, Integer> bmBc, int m) {
        //如果在规则中则返回相应的值，否则返回pattern的长度
        return bmBc.getOrDefault(c, m);
    }
}
