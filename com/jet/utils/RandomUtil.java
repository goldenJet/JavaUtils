package com.jet.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Created by jet.chen on 2/24/2016.
 */
public class RandomUtil {

    public static String generateFourRandomCharacters() {
        String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "a", "b", "c" , "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z"};
        String result = generate(beforeShuffle);
        return result;
    }
    public static String generateSixRandomCharacters() {
        String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J",
                "K",  "M", "N",  "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "a", "b", "c" , "d", "e", "f", "g", "h",
                 "j", "k",  "m", "n",  "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z"};
        String result = generateSix(beforeShuffle);
        return result;
    }
    public static String generateFourRandomDigitals() {
        String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9"};
        String result = generate(beforeShuffle);
        return result;
    }

    private static String generate(String[] beforeShuffle){
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }
    private static String generateSix(String[] beforeShuffle){
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 11);
        return result;
    }
    public static String generateRandomCharacters() {
        String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "a", "b", "c" , "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z"};
        String result = generate2(beforeShuffle);
        return result;
    }

    private static String generate2(String[] beforeShuffle){
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(3, 14);
        return result;
    }
    
}
