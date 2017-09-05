package com.bian.base.util.utilbase;

/**
 * author 边凌
 * date 2017/8/15 16:03
 * 类描述：
 */

public class MathUtil {
    private final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    private MathUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断一个int值有几位
     * 注意{@link #sizeTable}的最后一个值以及循环条件，超过10位数该方法无法判断
     */
    public static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }
}
