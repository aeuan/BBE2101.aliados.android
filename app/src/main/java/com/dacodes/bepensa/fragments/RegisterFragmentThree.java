package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.activities.RegisterActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.REGISTER_USER;
import static com.dacodes.bepensa.api.RetrofitWebServices.VERIFY_PHONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragmentThree.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragmentThree extends Fragment implements RetrofitWebServices.DefaultResponseListeners {

    private static final String LOG_TAG = RegisterFragmentThree.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    RetrofitWebServices retrofitWebServices;
    String phone, email, password;

    public RegisterFragmentThree() {
        // Required empty public constructor
    }
    Context mContext;
    AppController appController;
    @BindView(R.id.sms_et)EditText sms_et;
    @BindView(R.id.end_bt)Button end_bt;
    @BindView(R.id.send_sms_bt)Button send_sms_bt;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    String username;
    int division;
    int con=0;
    CountDownTimer countDownTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController) getActivity(). getApplication();
        Bundle bundle = this.getArguments();
        int id = 0;
        if (bundle != null) {
            username = bundle.getString("username", "username");
            division = bundle.getInt("division", 0);
            email = bundle.getString("email", "email");
            phone = bundle.getString("phone", "phone");
            password = bundle.getString("password", "password");
        }
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mContext = getContext();
        appController = (AppController)getActivity().getApplication();
        final View view = inflater.inflate(R.layout.register_fragment_three_layout, container, false);
        ButterKnife.bind(this, view);
        end_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    retrofitWebServices.registerUser(username, email,
                            phone,
                            password, String.valueOf(division), sms_et.getText().toString());
                }
            }
        });
        ((RegisterActivity)getActivity()).setCurrent_fragment(2);
        countDownTimer=new  CountDownTimer( 120000,100){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                send_sms_bt.setEnabled(true);
                send_sms_bt.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorLoaderBackground));
            }
        }.start();

        send_sms_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofitWebServices.verifyPhoneNumber(username, ""+phone,division,String.valueOf(password));
                send_sms_bt.setEnabled(false);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        //matar el timer
        if (countDownTimer!=null)
            countDownTimer.cancel();
        super.onDestroy();
    }

    private boolean validateInput(){
        sms_et.setError(null);
        if(TextUtils.isEmpty(sms_et.getText().toString())){
            sms_et.setError("El código es obligatorio");
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
                appController.setEmail(response.getEmail());
                appController.setPoints(((ProfileEntity) object).getPoints());
                String name = response.getName();
                String lastName = response.getLast_name();
                appController.setName(name);
                appController.setLastName(lastName);
                appController.setDivision(response.getDivision().getName());
                appController.setIdDivision(response.getDivision().getId());
                appController.setPhone(response.getContact());
                appController.setLoggedIn(true);
                appController.setRank(response.getRank());
                appController.setImageDivision(response.getDivision().getImage());
                appController.setRankingPrivacy(response.isHidden_ranking());
                appController.setPoints(((ProfileEntity) object).getPoints());
                appController.setPointAvailable1(response.getPoints_available());
                Toast.makeText(getContext(),"Felicidades, el registro de tu usuario fue exitoso y has ganado "+response.getPoints()+" puntos",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("profile", response);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case VERIFY_PHONE:
                /*
                RegisterActivity registerActivity = (RegisterActivity) getActivity();
                registerActivity.showSmsFragment(username, email, phone, password, division);*/
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e("error","code:"+status+"messaje"+message);
        switch (status){
            case 1043:
                showToast(message);
                break;
            default:
                showToast(message);
                break;
        }

    }

    public void showToast(String mesage){
        Toast.makeText(mContext,mesage,Toast.LENGTH_LONG).show();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String username, String division);
    }


    public void onResume(){
        super.onResume();
        // Set title bar
        ((RegisterActivity) getActivity())
                .setActionBarTitle(getString(R.string.verification_code));
    }

}
