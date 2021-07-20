package com.dacodes.bepensa.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EditPassword;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.utils.FontSingleton;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.StringPicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;
import static com.dacodes.bepensa.api.RetrofitWebServices.POST_PASSWORD_EDIT;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentResetPasswordOne extends Fragment implements RetrofitWebServices.DefaultResponseListeners{

    String LOG_TAG =EditPassword.class.getSimpleName();
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    int mHeight;
    int mWidth;

    @BindView(R.id.username_et)EditText username_et;
    @BindView(R.id.division_et)EditText division_et;
    @BindView(R.id.birthday_et)EditText birthday_et;

    List<?> divisions = new ArrayList();
    private DivisionEntity divisionEntity;
    private boolean failedLoadingDivisions = false;
    private Activity mActivity;

    public FragmentResetPasswordOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController)getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        retrofitWebServices.getDivisions();
        mActivity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_fragment_reset_password_one, container, false);
        ButterKnife.bind(this,view);
        mContext = getContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;

        division_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divisions!=null) {
                    if(divisions.size()>0) {
                        displayDivisionsDialog(divisions);
                    }else{
                        Toast.makeText(mContext, "Cargando divisiones...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    failedLoadingDivisions = true;
                    retrofitWebServices.getDivisions();
                    Toast.makeText(mContext, "Cargando divisiones...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        division_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    division_et.performClick();
                }
            }
        });
        division_et.setKeyListener(null);

        birthday_et.addTextChangedListener(mDateEntryWatcher);

        return  view;
    }

    private TextWatcher mDateEntryWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            try {
                if (working.length()==2 && before ==0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>31) {
                        isValid = false;
                    } else {
                        working+="-";
                        birthday_et.setText(working);
                        birthday_et.setSelection(working.length());
                    }

                    if (!isValid) {
                        birthday_et.setText(null);
                        birthday_et.setError("Dia:(1-31)");
                        birthday_et.setText(null);
                    } else {
                        birthday_et.setError(null);
                    }
                }
                else if (working.length() == 5 && before == 0) {
                    String enteredYear = working.substring(3);
                    if (Integer.parseInt(enteredYear) < 1 || Integer.parseInt(enteredYear) > 12) {
                        isValid = false;
                    } else {
                        working += "-";
                        birthday_et.setText(working);
                        birthday_et.setSelection(working.length());
                    }
                    if (!isValid) {
                        birthday_et.setText(working.substring(0,3));
                        birthday_et.setSelection(3);
                        birthday_et.setError("Mes:(1-12)");
                    } else {
                        birthday_et.setError(null);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                birthday_et.setText(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

    };

    @OnClick(R.id.end_bt)
    public void next(){
        if (validateInput()) {
            String date=birthday_et.getText().toString().replace("-","");
            ((EditPassword)mActivity).getRecoverPassword().setBirthday(date);
            ((EditPassword)mActivity).getRecoverPassword().setDivision(divisionEntity.getId());
            ((EditPassword)mActivity).getRecoverPassword().setEmploye_number(username_et.getText().toString().trim());
            FragmentResetPasswordTwo fragmentResetPasswordTwo = new FragmentResetPasswordTwo();
            ((EditPassword)mActivity).openFragment(fragmentResetPasswordTwo);
            /*
            String date=birthday_et.getText().toString().replace("-","");
            retrofitWebServices.postPassword(appController.getAccessToken(), username_et.getText().toString(), divisionEntity.getId(),date);*/
        }
    }

    private boolean validateInput(){

        if(TextUtils.isEmpty(username_et.getText().toString())){
            username_et.setError("Campo vacío");
            return false;
        } else if(TextUtils.isEmpty(division_et.getText().toString())){
            division_et.setError("Campo vacío");
            return false;
        } else if (TextUtils.isEmpty(birthday_et.getText().toString())){
            birthday_et.setError("Campo vacío");
            return false;
        }else{
            String date=birthday_et.getText().toString().replace("-","");
            if (date.length()!=6){
                birthday_et.setError("La fecha no es valida");
                return false;
            }
        }


        return true;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                if(failedLoadingDivisions){
                    if(divisions!=null) {
                        displayDivisionsDialog(divisions);
                    }
                }
                break;
            case POST_PASSWORD_EDIT:
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_division",divisionEntity.getId());
                    bundle.putInt("username",Integer.parseInt(username_et.getText().toString()));
                    bundle.putString("birthday",birthday_et.getText().toString());
                    Toast.makeText(mContext,"Se ha enviado un código al número celular registrado en la cuenta, por favor ingresa una nueva contraseña y el código recibido",Toast.LENGTH_LONG).show();
                    FragmentResetPasswordTwo fragmentResetPasswordTwo = new FragmentResetPasswordTwo();
                    fragmentResetPasswordTwo.setArguments(bundle);
                    ((EditPassword)mActivity).openFragment(fragmentResetPasswordTwo);
                }catch (Exception e){ }
                break;

        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case 1040:
                Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
                break;
        }
        Toast.makeText(mContext,""+message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWebServiceStart() {

    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }



    private void displayDivisionsDialog(final List<?> divisions) {
        final AlertDialog alertDialog;
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullViewGroup = null;
        final View dialoglayout = inflater.inflate(R.layout.division_dialog_layout, nullViewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.string_picker);
        StringPicker myStringPicker = dialoglayout.findViewById(R.id.string_picker);
        TextView positiveButton = dialoglayout.findViewById(R.id.positiveButton);
        TextView negativeButton = dialoglayout.findViewById(R.id.negativeButton);
        negativeButton.setVisibility(GONE);

        myStringPicker.setWrapSelectorWheel(true);
        final String [] values = new String [divisions.size()];
        for(int i = 0; i<divisions.size(); i++){
            DivisionEntity divisionEntity = (DivisionEntity) divisions.get(i);
            values[i] = divisionEntity.getName();
        }
        myStringPicker.setMaxValue(values.length-1);
        myStringPicker.setMinValue(0);
        myStringPicker.setDisplayedValues(values);
        myStringPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        myStringPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                divisionEntity = (DivisionEntity) divisions.get(newVal);
            }
        });
        builder.setView(dialoglayout);
        alertDialog = builder.create();
        Button positive = dialoglayout.findViewById(R.id.positiveButton);
        Button negative = dialoglayout.findViewById(R.id.negativeButton);
        positive.setTypeface(FontSingleton.getInstance().getTrade18());
        negative.setTypeface(FontSingleton.getInstance().getTrade18());
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divisionEntity!=null) {
                    division_et.setText(divisionEntity.getName());
                }else{
                    if(divisionEntity!=null) {
                        division_et.setText(divisionEntity.getName());
                    }else{
                        divisionEntity = (DivisionEntity) divisions.get(0);
                        division_et.setText(divisionEntity.getName());
                    }
                }
                alertDialog.cancel();
            }
        });
        if(divisionEntity!=null){
            for(int i = 0; i<divisions.size(); i++){
                if(divisionEntity == divisions.get(i)){
                    myStringPicker.setValue(i);
                }
            }
        }

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        // Finally, display the alert dialog
        alertDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.7f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.5f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        alertDialog.getWindow().setAttributes(layoutParams);
    }

}
