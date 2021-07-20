package com.dacodes.bepensa.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dacodes.bepensa.R;

import java.util.Calendar;

public class BepensaUtils {

    public static void applyFontForToolbarTitle(Activity context, Toolbar toolbar){
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(toolbar.getTitle())){
                    //tv.setTypeface(FontSingleton.getInstance().getTrade18());
                    break;
                }
            }
        }
    }


    public static String putZeros(int num){
        String date = "";
        if(num>0 && num <10){
            date = "0" + String.valueOf(num);
        }else{
            date = String.valueOf(num);
        }
        return date;
    }


    public static int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        int dayOfYear = dob.get(Calendar.DAY_OF_YEAR);
        if(year % 4 == 0) dayOfYear-- ;
        if (today.get(Calendar.DAY_OF_YEAR) < dayOfYear) age-- ;
        return age;
    }

    public static int dpToPixels(int dp, Context mContext) {

        return (int) (dp * (mContext.getResources().getDisplayMetrics().density));
    }


    /*
     * Tipos de alineación
     * 4-3-3 => 1
     * 4-4-2 => 2
     * 3-4-3 => 3
     * 5-3-2 => 4
     * */
    public static int getAlineacionNumber(int alineacion, int adapterPosition, int position) {
        switch (alineacion) {
            case 1:
                return getAlineacion("4-3-3",adapterPosition,position);
            case 2:
                return getAlineacion("4-4-2",adapterPosition,position);
            case 3:
                return getAlineacion("3-4-3",adapterPosition,position);
            case 4:
                return getAlineacion("5-3-2",adapterPosition,position);
        }
        return 0;
    }

    private static int getAlineacion(String alineacion, int adapterPosition, int position){
        String[] numbers = alineacion.split("-");
        int adapterPosition0 = Integer.parseInt(numbers[2]);
        int adapterPosition1 = Integer.parseInt(numbers[1]);
        int adapterPosition2 = Integer.parseInt(numbers[0]);
        if(adapterPosition == 0){
            return 11-(adapterPosition0-position);
        }

        if(adapterPosition == 1){
            return 11-adapterPosition0-(adapterPosition1-position);
        }

        if(adapterPosition == 2){
            return 11-adapterPosition0-adapterPosition1-(adapterPosition2-position);
        }

        if(adapterPosition == 3){
            return 11-adapterPosition0-adapterPosition1-adapterPosition2;
        }
        return 0;
    }

    public static int[] getBothPositions(int alineacion, int position) {
        switch (alineacion) {
            case 1:
                return getPositions("4-3-3",position);
            case 2:
                return getPositions("4-4-2",position);
            case 3:
                return getPositions("3-4-3",position);
            case 4:
                return getPositions("5-3-2",position);
        }
        return new int[] {0,0};
    }

    private static int[] getPositions(String alineacion, int number){
        int playerPositionNumber = number;
        String[] numbers = alineacion.split("-");
        int adapterPosition0 = Integer.parseInt(numbers[2]);
        int adapterPosition1 = Integer.parseInt(numbers[1]);
        int adapterPosition2 = Integer.parseInt(numbers[0]);
        int adapterPosition = 3;
        int position = 0;
        playerPositionNumber--;
        if(playerPositionNumber == 0){
            position = 1;
            return new int[] {adapterPosition,position};
        }
        playerPositionNumber = playerPositionNumber - adapterPosition2;
        if(playerPositionNumber <= 0){
            adapterPosition -= 1;
            position = number - 11 + adapterPosition0 + adapterPosition1 + adapterPosition2;
            return new int[] {adapterPosition,position};
        }
        playerPositionNumber = playerPositionNumber - adapterPosition1;
        if(playerPositionNumber <= 0){
            adapterPosition -= 2;
            position = number - 11 + adapterPosition0 + adapterPosition1;
            return new int[] {adapterPosition,position};
        }
        playerPositionNumber = playerPositionNumber - adapterPosition0;
        if(playerPositionNumber <= 0){
            adapterPosition -= 3;
            position = number - 11 + adapterPosition0;
            return new int[] {adapterPosition,position};
        }

        return new int[] {adapterPosition,position};
    }


    public static int getLocalId(String number){
        switch (number){
            case "100":
                return R.drawable.ic_launcher;
            case "101":
                return R.drawable.ic_launcher;
            case "102":
                return R.drawable.ic_launcher;
            case "103":
                return R.drawable.ic_launcher;
            case "104":
                return R.drawable.ic_launcher;
            case "105":
                return R.drawable.ic_launcher;
            default:
                return -1;
        }
    }

    public static int formacionId(String alineacion){
        switch (alineacion){
            case "4-3-3":
                return 1;
            case "4-4-2":
                return 2;
            case "3-4-3":
                return 3;
            case "5-3-2":
                return 4;
        }
        return 1;
    }
    public static String formacionString(int alineacion){
        switch (alineacion){
            case 1:
                return "4-3-3";
            case 2:
                return "4-4-2";
            case 3:
                return "3-4-3";
            case 4:
                return "5-3-2";
        }
        return "4-3-3";
    }

    public static String convertPositionFromIntToString(int position){
        String positionString = "";
        switch (position){
            case 4:
                positionString = "Delantero";
                break;
            case 3:
                positionString = "Medio";
                break;
            case 2:
                positionString = "Defensa";
                break;
            case 1:
                positionString = "Portero";
                break;
            case 5:
                positionString = "DT";
                break;

        }
        return positionString;
    }

}
