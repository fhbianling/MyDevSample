package com.bian.base.util.utilbase;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author 边凌
 * date 2017/2/15 10:19
 * desc ${正则表达式工具类,封装了常用字符串格式检查方法}
 */

@SuppressWarnings({"WeakerAccess"})
public final class RegExpUtil {
    /**
     * 手机号
     **/
    private final static String REG_MOBILE = "^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
    /**
     * 座机
     **/
    private final static String REG_TELEPHONE = "(^(0[0-9]{2,3}-)?\\d{6,8})";
    /**
     * 车牌号
     **/
    private final static String REG_PLATES = "[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z0-9]{5}";
    /**
     * 身份证
     **/
    private final static String REG_IDCARD = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
    /**
     * 邮箱效
     **/
    private final static String REG_EMAIL = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /**
     * Url
     */
    private static final String REG_URL = "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";
    /**
     * 中文
     */
    private static final String REG_CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    /**
     * ASCII
     */
    private static final String REG_ASCII = "^[\\x00-\\xFF]+$";
    /**
     * IP4
     */
    private static final String REG_IP4 = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";


    private RegExpUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 效验目标字符串是否和正则表达式匹配
     */
    public static boolean checkRegExp(String checkedStr, String regExp) {
        if (TextUtils.isEmpty(checkedStr)) {
            Log.e("RegExpUtil", "被效验的文字为空，返回false");
            return false;
        }
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(checkedStr);
        return matcher.matches();
    }

    /**
     * 效验是否是字母和数字的混合密码,且长度在指定范围内
     */
    public static boolean isPasswordInRange(String password, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("the variable min cannot larger than max");
        }

        String regPassword = "[0-9a-zA-Z]{" + min + "," + max + "}";
        return checkRegExp(password, regPassword);
    }

    /**
     * 效验手机号
     */
    public static boolean isMobile(String mobileNumber) {
        return !(TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11)
                && checkRegExp(mobileNumber, REG_MOBILE);

    }

    /**
     * 效验座机号
     */
    public static boolean isTelephone(String telephoneNumber) {
        return checkRegExp(telephoneNumber, REG_TELEPHONE);
    }

    /**
     * 效验电话号码（手机或座机）
     */
    public static boolean isPhone(String phoneNumber) {
        return isMobile(phoneNumber) || isTelephone(phoneNumber);
    }

    /**
     * 效验车牌号
     */
    public static boolean isPlates(String plates) {
        return checkRegExp(plates, REG_PLATES);
    }

    /**
     * 效验身份证号
     */
    public static boolean isIdCardNum(String idCardNum) {
        return checkRegExp(idCardNum, REG_IDCARD);
    }

    /**
     * 效验邮箱
     */
    public static boolean isEmail(String email) {
        return checkRegExp(email, REG_EMAIL);
    }

    /**
     * 效验Url
     */
    public static boolean isUrl(String url) {
        return checkRegExp(url, REG_URL);
    }

    /**
     * 效验是否是中文
     */
    public static boolean isChinese(String text) {
        return checkRegExp(text, REG_CHINESE);
    }

    /**
     * 效验ASCII字符
     */
    public static boolean isASCII(String text) {
        return checkRegExp(text, REG_ASCII);
    }

    /**
     * 效验是IPV4地址
     */
    public static boolean isIP4(String ip) {
        return checkRegExp(ip, REG_IP4);
    }

    /**
     * 效验字符串长度
     */
    public static boolean isLengthInRange(String str, int min, int max) {
        if (str == null) {
            str = "";
        }
        return str.length() >= min && str.length() <= max;
    }
}
