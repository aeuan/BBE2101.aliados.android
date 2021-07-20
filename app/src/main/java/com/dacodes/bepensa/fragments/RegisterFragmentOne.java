package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.RegisterActivity;
import com.dacodes.bepensa.activities.ScanActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.models.ValidateRegistrationEntity;
import com.dacodes.bepensa.utils.FontSingleton;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.StringPicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;
import static com.dacodes.bepensa.api.RetrofitWebServices.VALIDATE_USER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragmentOne.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragmentOne extends Fragment implements RetrofitWebServices.DefaultResponseListeners {
    private static final int REQUEST_USER_KEY = 1;
    private static final String LOG_TAG = RegisterFragmentOne.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    List <?> divisions = new ArrayList();
    private DivisionEntity divisionEntity;
    private boolean backspace = false;
    RetrofitWebServices retrofitWebServices;
    @BindView(R.id.division_et)EditText division_et;
    @BindView(R.id.clave_empleado_et)EditText clave_empleado_et;
    @BindView(R.id.clave_seguridad_social_et)EditText clave_seguridad_social_et;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public RegisterFragmentOne() {
        // Required empty public constructor
    }
    Context mContext;
    AppController appController;


    @OnClick(R.id.next_bt)
    public void nextClick(View view) {
        if(validateInput()){
            retrofitWebServices.validateUser(clave_empleado_et.getText().toString(), String.valueOf(divisionEntity.getId()), clave_seguridad_social_et.getText().toString());
        }
    }

    private boolean validateInput(){
        if(TextUtils.isEmpty(clave_empleado_et.getText().toString())){
            clave_empleado_et.setError("Campo vacío");
            return false;
        }

        if(divisionEntity == null){
            division_et.setError("Selecciona una división");
            return false;
        }

        if(TextUtils.isEmpty(clave_seguridad_social_et.getText().toString())){
            clave_seguridad_social_et.setError("Campo vacío");
            return false;
        }

        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController) getActivity(). getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        retrofitWebServices.getDivisions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();
        appController = (AppController)getActivity().getApplication();
        final View view = inflater.inflate(R.layout.register_fragment_one_layout, container, false);
        ButterKnife.bind(this, view);
        division_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    displayDivisionsDialog(divisions);
                }
            }
        });
        division_et.setKeyListener(null);
        clave_seguridad_social_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5 || s.length() == 8 || s.length() == 13) {
                    if(!backspace) {
                        s.append('-');
                    }
                }
            }
        });

        clave_seguridad_social_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //on backspace
                backspace = keyCode == KeyEvent.KEYCODE_DEL;
                return false;
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDatosPersonalesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                Log.d("Divisions", divisions.toString());
                break;

            case VALIDATE_USER:
                ValidateRegistrationEntity validateRegistrationEntity = (ValidateRegistrationEntity) object;
                if(validateRegistrationEntity.getSuccess().contentEquals("ok")){
                    mListener.onFragmentInteraction(clave_empleado_et.getText().toString(), String.valueOf(divisionEntity.getId()));
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {

    }

    @Override
    public void onWebServiceStart() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onWebServiceEnd() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onExpireToken() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String newUrl, String name);
    }


    @OnClick({ R.id.camera_iv, R.id.scan_rl_layout, R.id.scan_text })
    public void scanCode() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(intent, REQUEST_USER_KEY);
    }

    // Get Result Back
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USER_KEY && resultCode == getActivity().RESULT_OK && data != null) {
            String returnValue = data.getStringExtra("user_key");
            clave_empleado_et.setText(returnValue);
        }
        Log.d("onActivityResult", "requestCode = " + requestCode);
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
        //myStringPicker.setValue(parametrosEntity.getGenero());
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
                    divisionEntity = (DivisionEntity) divisions.get(0);
                    division_et.setText(divisionEntity.getName());
                }
                alertDialog.cancel();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        // Finally, display the alert dialog
        alertDialog.show();


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
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

    public void onResume(){
        super.onResume();
        // Set title bar
        ((RegisterActivity) getActivity())
                .setActionBarTitle("Identificación de Colaborador");
    }

}
