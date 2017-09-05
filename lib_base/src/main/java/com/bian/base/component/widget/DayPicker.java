package com.bian.base.component.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;

import java.util.Calendar;
import java.util.Date;


/**
 * author 边凌
 * date 2017/4/20 11:53
 * desc ${日期选择器}
 */

public class DayPicker {
    private final static int COLOR_SUBMIT = Color.parseColor("#159fff");
    private final static int COLOR_CANCEL = Color.parseColor("#959595");
    private final static int COLOR_BG = Color.parseColor("#f7f7f7");
    private TimePickerView timePickerView;

    public DayPicker(Context context, TimePickerView.OnTimeSelectListener listener) {
        timePickerView = new TimePickerView.Builder(context, listener)
                .setSubmitText("完成")
                .setBgColor(COLOR_BG)
                .setCancelColor(COLOR_CANCEL)
                .setSubmitColor(COLOR_SUBMIT)
                .setSubCalSize(15)
                .setDate(Calendar.getInstance())
                .setType(TimePickerView.Type.YEAR_MONTH_DAY).build();

    }

    public DayPicker(Context context, TimePickerView.OnTimeSelectListener listener, TimePickerView.Type type) {
        timePickerView = new TimePickerView.Builder(context, listener)
                .setSubmitText("完成")
                .setBgColor(COLOR_BG)
                .setCancelColor(COLOR_CANCEL)
                .setSubmitColor(COLOR_SUBMIT)
                .setSubCalSize(15)
                .setDate(Calendar.getInstance())
                .setType(type).build();

    }

    //指定开始时间 构造
    public DayPicker(Context context, TimePickerView.OnTimeSelectListener listener, Date startDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        timePickerView = new TimePickerView.Builder(context, listener)
                .setSubmitText("完成")
                .setBgColor(COLOR_BG)
                .setCancelColor(COLOR_CANCEL)
                .setSubmitColor(COLOR_SUBMIT)
                .setSubCalSize(15)
                .setRangDate(start, Calendar.getInstance())
                .setDate(Calendar.getInstance())
                .setType(TimePickerView.Type.YEAR_MONTH_DAY).build();

    }

    public void show() {
        timePickerView.show();
    }

    public void show(View clickView) {
        timePickerView.show(clickView);
    }

    public void dismiss() {
        timePickerView.dismiss();
    }

    /**
     * 设置所选中的默认日期
     */
    public void setDate(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        timePickerView.setDate(instance);
    }
}
