package com.dacodes.bepensa.utils;

import android.graphics.Typeface;

import com.dacodes.bepensa.AppController;


/**
 * Created by Eric on 08/05/17.
 *
 */

public class FontSingleton {
    private static FontSingleton mInstance = null;

    private Typeface thirstyScript, trade2, trade18, trade20;

    private FontSingleton(){
        thirstyScript = Typeface.createFromAsset(AppController.getContext().getAssets(),
                "fonts/bariol_regular_italic.otf");
        trade2 = Typeface.createFromAsset(AppController.getContext().getAssets(),
                "fonts/bariol_regular_italic.otf");
        trade18 = Typeface.createFromAsset(AppController.getContext().getAssets(),
                "fonts/bariol_regular_italic.otf");
        trade20 = Typeface.createFromAsset(AppController.getContext().getAssets(),
                "fonts/bariol_regular_italic.otf");

    }

    public static FontSingleton getInstance(){
        if(mInstance == null) {
            mInstance = new FontSingleton();
        }
        return mInstance;
    }

    public static void setmInstance(FontSingleton mInstance) {
        FontSingleton.mInstance = mInstance;
    }

    public static FontSingleton getmInstance() {
        return mInstance;
    }

    public Typeface getThirstyScript() {
        return thirstyScript;
    }

    public void setThirstyScript(Typeface thirstyScript) {
        this.thirstyScript = thirstyScript;
    }

    public Typeface getTrade2() {
        return trade2;
    }

    public void setTrade2(Typeface trade2) {
        this.trade2 = trade2;
    }

    public Typeface getTrade18() {
        return trade18;
    }

    public void setTrade18(Typeface trade18) {
        this.trade18 = trade18;
    }

    public Typeface getTrade20() {
        return trade20;
    }

    public void setTrade20(Typeface trade20) {
        this.trade20 = trade20;
    }
}