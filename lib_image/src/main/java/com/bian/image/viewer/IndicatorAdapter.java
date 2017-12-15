package com.bian.image.viewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.image.R;

import java.util.ArrayList;
import java.util.List;


/**
 * author 边凌
 * date 2017/6/19 17:02
 * 类描述：
 */

class IndicatorAdapter {

    private final int count;
    private final LayoutInflater inflater;
    private final List<View> viewList;
    IndicatorAdapter(int count, Context context) {
        this.count = count;
        inflater = LayoutInflater.from(context);
        viewList=new ArrayList<>();
    }

    void setCheckPos(int checkPos) {
        for (int i = 0; i < viewList.size(); i++) {
            View view = viewList.get(i);
            if (checkPos==i){
                view.setBackgroundResource(R.drawable.shape_indicator_select_imageviewer);
            }else {
                view.setBackgroundResource(R.drawable.shape_indicator_unselect_imageviewer);
            }
        }
    }

    int getCount() {
        return count;
    }

    View getView(ViewGroup parent) {
        View inflate = inflater.inflate(R.layout.item_indicator_imageviewer, parent, false);
        viewList.add(inflate.findViewById(R.id.indicator_indicator));
        return inflate;
    }

}
