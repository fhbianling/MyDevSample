package com.bian.base.util.utilrx.rxview;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

/**
 * author 边凌
 * date 2017/3/23 14:05
 * desc ${AdapterView的ItemClick}
 */

final class ItemClickOnSubscribe implements FlowableOnSubscribe<ItemClickOnSubscribe.ItemEvent>{
    private AdapterView absListView;

    ItemClickOnSubscribe(AdapterView absListView) {
        this.absListView = absListView;
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<ItemEvent> e) throws Exception {
        verifyMainThread();
        AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ItemEvent itemEvent=new ItemEvent();
                    itemEvent.parent=parent;
                    itemEvent.view=view;
                    itemEvent.position=position;
                    itemEvent.id=id;
                    e.onNext(itemEvent);
            }
        };

        absListView.setOnItemClickListener(onItemClickListener);
    }

    static class ItemEvent{
        AdapterView parent;
        int position;
        long id;
        View view;
    }
}
