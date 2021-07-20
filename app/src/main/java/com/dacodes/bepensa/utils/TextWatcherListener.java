package com.dacodes.bepensa.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherListener implements TextWatcher {

    private EditText editText;
    private listenerWatcher listenerWatcher;

    public TextWatcherListener (EditText text,listenerWatcher listenerWatcher){
        this.editText=text;
        this.listenerWatcher=listenerWatcher;
        editText.addTextChangedListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        listenerWatcher.beforeTextChanged(editText,charSequence,i,i1,i2);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        listenerWatcher.onTextChanged(editText,charSequence,i,i1,i2);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        listenerWatcher.afterTextChanged(editText,editable);
    }

    public interface listenerWatcher{
        void afterTextChanged(EditText editText,Editable editable);
        void beforeTextChanged(EditText editText,CharSequence charSequence,int i,int i1,int i2);
        void  onTextChanged(EditText editText,CharSequence charSequence,int i,int i1,int i2);
    }
}
