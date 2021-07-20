package com.dacodes.bepensa.utils;

import android.content.Context;
import android.util.TypedValue;

public class DM {

    public static int getDisplayWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeght(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float convertDpToPixel(Context context,float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }

    public static float convertPixelsToDp(Context context,int px){
        return px / (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }
}
