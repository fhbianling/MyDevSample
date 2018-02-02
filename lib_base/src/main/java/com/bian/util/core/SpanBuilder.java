package com.bian.util.core;

import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

/**
 * SpannableString的简化工具类，其方法会返回一个SpanBuilder，get方法返回一个SpannableString
 * SpannableStringUtils
 * Created by BianLing on 2016/8/25.
 */
public class SpanBuilder {
    private SpannableString mSpanStr;

    public SpanBuilder(String string) {
        mSpanStr = new SpannableString(string);
    }

    public static SpannableString buildColor(String format, Object formatObj, @ColorInt int dstColor) {
        String str = String.format(format, formatObj);
        int length = formatObj.toString().length();
        int start = format.indexOf("%");

        SpannableString mSpanStr = new SpannableString(str);
        mSpanStr.setSpan(new ForegroundColorSpan(dstColor), start, length + start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mSpanStr;
    }

    /**
     * 调整指定字段的大小
     *
     * @param start 开始位置
     * @param end   结束位置
     * @param size  字体大小
     * @return SpanBuilder
     */
    public SpanBuilder size(int start, int end, int size) {
        mSpanStr.setSpan(new AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 调整指定字段的颜色
     *
     * @param start  开始位置
     * @param offSet 从开始位置起的偏移量
     * @param color  字体颜色
     * @return SpanBuilder
     */
    public SpanBuilder color(int start, int offSet, int color) {
        mSpanStr.setSpan(new ForegroundColorSpan(color), start, start + offSet, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 设置字段可点击效果
     *
     * @param start    开始位置
     * @param end      结束位置
     * @param listener 点击监听器
     * @return SpanBuilder
     */
    public SpanBuilder clickable(int start, int end, final View.OnClickListener listener) {
        mSpanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.onClick(widget);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * @return SpannableString
     */
    public SpannableString get() {
        return mSpanStr;
    }

}
