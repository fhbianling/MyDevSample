package com.bian.mydevsample.ui._main;

import android.app.Activity;

/**
 * author 边凌
 * date 2017/10/13 15:21
 * 类描述：
 */

public class ButtonDesc {
    public String name;
    Class<? extends Activity> target;

    ButtonDesc(String name, Class<? extends Activity> target) {
        this.name = name;
        this.target = target;
    }

}
