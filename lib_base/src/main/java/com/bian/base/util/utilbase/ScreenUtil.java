package com.bian.base.util.utilbase;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * author 边凌
 * date 2017/6/16 11:37
 * 类描述：
 */

public class ScreenUtil {
    private static int sScreenWidth=-1;
    private static int sScreenHeight=-1;
    private ScreenUtil(){
      throw new UnsupportedOperationException();
    }

    public static int getScreenWidth(Context context){
        if (sScreenWidth==-1){
            screenValueAssignment(context);
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Context context){
        if (sScreenHeight==-1){
            screenValueAssignment(context);
        }
        return sScreenHeight;
    }

    private static void screenValueAssignment(Context context) {
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize=new Point();
        windowManager.getDefaultDisplay().getSize(outSize);
        sScreenWidth=outSize.x;
        sScreenHeight=outSize.y;
    }
}
