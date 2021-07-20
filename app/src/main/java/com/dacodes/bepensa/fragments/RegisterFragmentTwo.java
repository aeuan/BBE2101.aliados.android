package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.activities.RegisterActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.REGISTER_USER;
import static com.dacodes.bepensa.api.RetrofitWebServices.VERIFY_PHONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragmentTwo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragmentTwo extends Fragment implements RetrofitWebServices.DefaultResponseListeners {

    private static final String LOG_TAG = RegisterFragmentTwo.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    RetrofitWebServices retrofitWebServices;

    public RegisterFragmentTwo() {
        // Required empty public constructor
    }
    Context mContext;
    AppController appController;
    @BindView(R.id.email_et)EditText email_et;
    @BindView(R.id.phone_et)EditText phone_et;
    @BindView(R.id.password_et)TextInputEditText password_et;
    @BindView(R.id.confirm_password_et)TextInputEditText confirm_password_et;
    @BindView(R.id.end_bt)Button end_bt;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swich_terms)SwitchCompat switchCompat;
    @BindView(R.id.terms_conditions_tv)TextView terms_conditions_tv;
    String username, password;
    int division;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController) getActivity(). getApplication();
        mActivity=getActivity();
        Bundle bundle = this.getArguments();
        int id = 0;
        if (bundle != null) {
            username = bundle.getString("username", "username");
            division = bundle.getInt("division", 0);
            password = bundle.getString("password", "password");
        }
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((RegisterActivity)getActivity()).setCurrent_fragment(1);
        mContext = getContext();
        appController = (AppController)getActivity().getApplication();
        final View view = inflater.inflate(R.layout.register_fragment_two_layout, container, false);
        ButterKnife.bind(this, view);

        username=((RegisterActivity)getActivity()).getUserName();
        division=((RegisterActivity)getActivity()).getDivisionId();
        password=((RegisterActivity)getActivity()).getPasswordNew();
        end_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    if (switchCompat.isChecked()) {
                        retrofitWebServices.verifyPhoneNumber(username, "+52" + phone_et.getText().toString(), division,String.valueOf(password));
                    }else{
                        Toast.makeText(getActivity(),"Debes aceptar términos y condiciones de uso y política de privacidad para continuar.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        /*
        confirm_password_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    end_bt.performClick();
                    return true;
                }
                return false;
            }
        });
        */
        if(((RegisterActivity)getActivity()).getEmail()!=null){
            setupViews();
        }

        return view;
    }

    @OnClick(R.id.terms_conditions_tv)
    public void termConditions(){
        try {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.bepensa.com/aviso-de-privacidad/"));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViews(){
        email_et.setText(((RegisterActivity)getActivity()).getEmail());
        phone_et.setText(((RegisterActivity)getActivity()).getPhone());
        password_et.setText(((RegisterActivity)getActivity()).getNew_password());
        confirm_password_et.setText(((RegisterActivity)getActivity()).getNew_password());
    }

    private boolean validateInput(){
        phone_et.setError(null);
        email_et.setError(null);
        //password_et.setError(null);
        //confirm_password_et.setError(null);

        if(TextUtils.isEmpty(email_et.getText().toString())){
            email_et.setError("El correo es obligatorio");
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email_et.getText().toString()).matches()) {
            email_et.setError("El formato de correo es incorrecto");
            return false;
        }


        if(TextUtils.isEmpty(phone_et.getText().toString())){
            phone_et.setError("Campo vacío");
            return false;
        }

        if(!Patterns.PHONE.matcher(phone_et.getText().toString()).matches()) {
            phone_et.setError("El número celular debe contener sólo números");
            return false;
        }

        if(phone_et.getText().toString().length()!=10) {
            phone_et.setError("El número celular debe contener 10 dígitos");
            return false;
        }


        if(TextUtils.isEmpty(password_et.getText().toString())){
            password_et.setError("Campo vacío");
            return false;
        }


        if(TextUtils.isEmpty(confirm_password_et.getText().toString())){
            confirm_password_et.setError("Campo vacío");
            return false;
        }

        if(!confirm_password_et.getText().toString().contentEquals(password_et.getText().toString())){
            confirm_password_et.setError("Las contraseñas no coinciden");
            return false;
        }

        return true;
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
            case REGISTER_USER:
                ProfileEntity response = (ProfileEntity) object;
                appController.setAccessToken(response.getToken().getHash());
                appController.setUserId(response.getUuid());
                appController.setUsername(response.getUsername());
                appController.setImageUrl(response.getAvatarUrl());
                appController.setPoints(((ProfileEntity) object).getPoints());
                String name = response.getName();
                String lastName = response.getLast_name();
                appController.setName(name);
                appController.setLastName(lastName);
                appController.setLoggedIn(true);
                appController.setPoints(((ProfileEntity) object).getPoints());
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("profile", response);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                break;
            case VERIFY_PHONE:
                RegisterActivity registerActivity = (RegisterActivity) mActivity;
                registerActivity.showSmsFragment(username, email_et.getText().toString(),
                        "+52" + phone_et.getText().toString(),
                        password_et.getText().toString(), division);
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e("Amazon","id:"+id+"status:"+status+"mesagge::"+message);
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
        void onFragmentInteraction(String username, String division);
    }


    public void onResume(){
        super.onResume();
        // Set title bar
        ((RegisterActivity) getActivity())
                .setActionBarTitle(getString(R.string.worker_identification));
    }

}
