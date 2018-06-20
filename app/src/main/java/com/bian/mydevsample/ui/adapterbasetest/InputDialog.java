package com.bian.mydevsample.ui.adapterbasetest;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bian.base.AbsBaseDialog;
import com.bian.mydevsample.R;
import com.bian.util.core.ToastUtil;

/**
 * author 边凌
 * date 2018/6/20 14:44
 * 类描述：
 */
public class InputDialog extends AbsBaseDialog {

    private EditText et;
    private Callback mCallback;

    public InputDialog(Context context) {
        super(context);
    }

    @Override protected int getLayoutResId() {
        return R.layout.dialog_base_test_input;
    }

    @Override protected void initView() {
        et = findViewById(R.id.et);
        findViewById(R.id.confirm).setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        String s = et.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtil.showToastShort("请重新输入");
            return;
        }

        long l = Long.parseLong(s);
        dismiss();
        if (mCallback != null) {
            mCallback.onInput(l);
        }
    }

    public void setCallback(Callback value) {
        this.mCallback = value;
    }

    public interface Callback {
        void onInput(long value);
    }
}
