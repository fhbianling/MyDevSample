package com.bian.widget.infotextview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import java.util.Locale;

/**
 * author 边凌
 * date 2017/11/23 13:58
 * 类描述：
 */

public class LetterSpacingTextView extends androidx.appcompat.widget.AppCompatTextView
        implements ViewTreeObserver.OnGlobalLayoutListener {

    private final static String HTML_REPLACE =
            "<p style=\"text-align-last:justify;" +
                    "font-size:%dpx;" +
                    "width:%dpx;" +
                    "color:%s\">%s</p>";
    private boolean autoAdjustLetterSpacing;

    public LetterSpacingTextView(Context context) {
        super(context);
        init();
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public boolean isAutoAdjustLetterSpacing() {
        return autoAdjustLetterSpacing;
    }

    public void setAutoAdjustLetterSpacing(boolean autoAdjustLetterSpacing) {
        this.autoAdjustLetterSpacing = autoAdjustLetterSpacing;
    }

    private void init() {
        setSingleLine(true);
        setMaxLines(1);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (autoAdjustLetterSpacing) {
            text = getAdjustedText(text);
        }
        super.setText(text, type);
    }

    private CharSequence getAdjustedText(CharSequence text) {
        if (text == null) {
            return null;
        }

        int widthPX = getWidth();
        if (widthPX == 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            return text;
        }

        String string = text.toString();
        char[] chars = string.toCharArray();
        StringBuilder results = new StringBuilder();
        for (char aChar : chars) {
            results.append(String.valueOf(aChar)).append(" ");
        }
        Log.d("LetterSpacingTextView", results.toString());

        int fontSizePX = (int) getPaint().getTextSize();
        String colorStr = getColorHexString();

        String formatHtml = String.format(Locale.CHINA,
                                          HTML_REPLACE,
                                          fontSizePX,
                                          widthPX,
                                          colorStr,
                                          results);
        Log.d("LetterSpacingTextView", formatHtml);
        Spanned spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(formatHtml, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spanned = Html.fromHtml(formatHtml);
        }
        return spanned;
    }

    @NonNull
    private String getColorHexString() {
        int textColorRGBHEX = getCurrentTextColor();
        int red = Color.red(textColorRGBHEX);
        int green = Color.green(textColorRGBHEX);
        int blue = Color.blue(textColorRGBHEX);
        return "#" + toHexString(red) + toHexString(green) + toHexString(
                blue);
    }

    private String toHexString(int value) {
        String hexString = Integer.toHexString(value);
        return (hexString.length() == 1 ? "0" : "") + hexString;
    }

    @Override
    public void onGlobalLayout() {
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            setText(text);
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
