package com.bian.base.util.utilbase;

import android.widget.EditText;

public final class EditTextUtil {

    private EditTextUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 为EditText设置文字并将光标调整到最后
     */
    public static void setTextWithSelectionAtLast(EditText editText, String text) {
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

}
