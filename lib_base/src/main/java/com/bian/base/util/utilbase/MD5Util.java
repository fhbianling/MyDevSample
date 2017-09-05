package com.bian.base.util.utilbase;

import java.security.MessageDigest;

/**
 * MD5工具类
 */
public final class MD5Util {
    private final static char[] HEX_DIGITS_UPPER = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};
    private final static char[] HEX_DIGITS_LOWER = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    private MD5Util() {
        throw new UnsupportedOperationException();
    }

    public static String encodeByUpper(String target) {
        return getMD5String(target, true);
    }

    public static String encodeByLower(String target) {
        return getMD5String(target, false);
    }

    private static String getMD5String(String s, boolean isUpperCase) {
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
