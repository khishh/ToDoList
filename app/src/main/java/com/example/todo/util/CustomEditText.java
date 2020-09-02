package com.example.todo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

// reference
// https://stackoverflow.com/questions/3940127/intercept-back-button-from-soft-keyboard

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Listener listener;

    public interface Listener{
        void onKeyboardDownClicked();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.listener.onKeyboardDownClicked();
            return true;
        }

        return false;
    }
}
