package com.dacodes.bepensa.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
class BaseActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        Log.e("onSave","Clear");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.clear();
        Log.e("onRestore","Clear");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
