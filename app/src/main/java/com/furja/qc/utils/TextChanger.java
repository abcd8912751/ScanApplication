package com.furja.qc.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class TextChanger implements TextWatcher {
    ChangeListener changeListener;

    public TextChanger(ChangeListener changeListener1) {
        this.changeListener = changeListener1;
    }

    public static TextChanger flat(ChangeListener changeListener1){
        return new TextChanger(changeListener1);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text="";
        if(!TextUtils.isEmpty(s))
            text=s.toString();
        if(changeListener != null)
            changeListener.afterTextChanged(text);
    }

    public int intValueof(String string){
        if(TextUtils.isEmpty(string))
            return 0;
        else{
            try {
                int value=Integer.valueOf(string);
                return value;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    public double doubleValueof(String string){
        if(TextUtils.isEmpty(string))
            return 0;
        else{
            try {
                double value=Double.valueOf(string);
                return value;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    public interface ChangeListener {
        void afterTextChanged(String text);
    }
}
