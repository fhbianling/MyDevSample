package com.bian.base.util.utilbase;

import android.text.TextUtils;

/**
 * author 边凌
 * date 2017/3/17 15:28
 * desc ${字符串工具类}
 */

public final class StringUtil {
    private StringUtil() {
        throw new UnsupportedOperationException();
    }

    public static String cutOutString(String orgStr, int prefixCount, int suffixCount, char replace) {
        try {
            String substring = orgStr.substring(prefixCount, orgStr.length() - suffixCount);
            String temp = "";
            for (int i = 0; i < substring.length(); i++) {
                temp += replace;
            }
            String before=orgStr.substring(0,prefixCount);
            String after=orgStr.substring(orgStr.length()-suffixCount,orgStr.length());
            return before+temp+after;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orgStr;
    }

    public static String fill0ToString(int value) {
        if (value < 10 && value >= 0) {
            return "0" + value;
        } else if (value >= 10) {
            return String.valueOf(value);
        } else if (value < 0 && value > -10) {
            return "-0" + String.valueOf(Math.abs(value));
        } else {
            return "-" + String.valueOf(Math.abs(value));
        }
    }

    /*转换为首字母大写形式*/
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /*判断字符串是否为空，纯为空格输入的字符串也视为空*/
    public static boolean isEmptyWithoutSpace(String text){
        return TextUtils.isEmpty(text.replace(" ", ""));
    }
}
