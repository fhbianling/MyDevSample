package com.bian.base.util.utilbase;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * author 边凌
 * date 2017/6/14 16:56
 * 类描述：输入过滤器工具类
 */

public final class InputFilterUtil {

    /**
     * @param isFilterSpace   过滤空格
     * @param isFilterChinese 过滤中文字符
     */

    public static InputFilter spaceAndChineseFilter(final boolean isFilterSpace, final boolean isFilterChinese) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence src, int start,
                                       int end, Spanned dst, int dstart, int dend) {
                if (src.length() < 1) {
                    return null;
                } else {
                    String content = src.toString();

                    if (isFilterChinese) {// 过滤中文
                        content = content.replaceAll(
                                "[\u4E00-\u9FA5]|\t", "");
                    }
                    if (isFilterSpace) {// 过滤空格
                        content = content.replaceAll(" ", "");
                    }
                    return content;
                }
            }
        };
    }

    /**
     * 获取一个限制输入类型为英文字母或数字的过滤器，
     *
     * @return InputFilter
     */
    public static InputFilter letterAndDigitFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int k = start; k < end; k++) {
                    //数字或字母
                    if (!Character.isLetterOrDigit(source.charAt(k))) {
                        return "";
                    }
                }
                //返回null表示可以输入此字符，""表示禁止输入
                return null;
            }
        };
    }

    /**
     * 获取一个限制最大值和精度的浮点数过滤器，
     *
     * @param max   小数点前最大值
     * @param digit 小数点后有效位数
     * @return InputFilter
     */
    public static InputFilter floatNumberFilter(final int max, final int digit) {
        return new ComponentDigitCtrlFilter(true, digit, max, digit);
    }

    public static InputFilter numberFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int k = start; k < end; k++) {
                    //数字
                    if (!Character.isDigit(source.charAt(k))) {
                        return "";
                    }
                }
                //返回null表示可以输入此字符，""表示禁止输入
                return null;
            }
        };
    }

    private static class ComponentDigitCtrlFilter implements InputFilter {

        private boolean isJPY;
        private int digit;

        private int max = 6;
        private int min = 2;

        public ComponentDigitCtrlFilter(boolean isJPY, int digit) {
            this.isJPY = isJPY;
            this.digit = digit;
        }

        /**
         * @param isJPY 是否有小数
         * @param digit 浮点数精度
         * @param max   小数点前位数
         * @param min   小数点后位数
         */
        public ComponentDigitCtrlFilter(boolean isJPY, int digit, int max, int min) {
            this.isJPY = isJPY;
            this.digit = digit;
            this.max = max;
            this.min = min;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String oriValue = dest.toString();

            //不能以"."开头
            if (TextUtils.isEmpty(oriValue) && TextUtils.equals(source, ".")) {
                return "";
            }
            //不能0*开头

            if (TextUtils.equals("0", dest.toString())) {

                if (TextUtils.equals(source, ".")) {
                    return source;
                }
                return "";
            }

            // 删除等特殊字符，直接返回
            if ("".equals(source.toString())) {
                return null;
            }

            StringBuffer sb = new StringBuffer(oriValue);
            sb.append(source);
            String newValue = sb.toString();
            String[] newValueVec = newValue.split("\\.");
            if (newValueVec.length == 2) {

                double number;
                try {
                    number = Double.parseDouble(newValueVec[0]);
                } catch (Exception e) {
                    return "";
                }

                boolean numberFlag;
                if (isJPY) {
                    numberFlag = (number - Math.pow(10, max) - 1 <= 0.000001);
                } else {
                    numberFlag = (number - Math.pow(10, min) - 1 <= 0.000001);
                }


                boolean digitFlag;
                try {
                    String digitNumber = newValueVec[1];
                    digitFlag = digitNumber.toCharArray().length <= digit;
                } catch (Exception ex) {
                    digitFlag = false;
                }
                if (numberFlag && digitFlag) {
                    return source;
                } else {
                    return "";
                }
            } else {
                double value;

                try {
                    value = Double.parseDouble(newValue);
                } catch (Exception e) {
                    return "";
                }
                if (isJPY) {
                    return value > Math.pow(10, max) - 1 ? "" : source;
                } else {
                    return value > Math.pow(10, min) - 1 ? "" : source;
                }
            }
            // dest.subSequence(dstart, dend)
        }
    }
}
