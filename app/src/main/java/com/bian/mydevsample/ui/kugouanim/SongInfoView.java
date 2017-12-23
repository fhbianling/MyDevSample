package com.bian.mydevsample.ui.kugouanim;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/12/23 12:51
 * 类描述：
 */

public class SongInfoView extends FrameLayout {
    public SongInfoView(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater from = LayoutInflater.from(context);
        View inflate = from.inflate(R.layout.view_songinfo, this, false);
        addView(inflate);
        for (int i = 0; i < ((ViewGroup) inflate).getChildCount(); i++) {
            View childAt = ((ViewGroup) inflate).getChildAt(i);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setTextColor(Color.WHITE);
            }
        }
    }
}
