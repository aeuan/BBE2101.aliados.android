package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;

public class GeneralFragment extends Fragment {

    private static final String LOG_TAG = GeneralFragment.class.getSimpleName();
    //RegistroFragment lealtadRegistroFragment;
    //RegistroFragment fantasyRegistroFragment;

    private OnPrincipalFragmentInteractionListener mListener;

    public GeneralFragment() {

    }

    AppController appController;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
        mContext = getActivity().getApplicationContext();
        appController = (AppController)getActivity().getApplication();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.lealtadContent,new CardFragment());
        fragmentTransaction.replace(R.id.fantasyContent,new PublishOportunity());
        fragmentTransaction.commit();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        Log.e("outsave","General");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnPrincipalFragmentInteractionListener) {
            mListener = (OnPrincipalFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPrincipalFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


 /*   public void startRegistroActivity(){
        Intent intent = new Intent(mContext, RegistroLealtadActivity.class);
        startActivity(intent);
//        startActivityForResult(intent, StaticValues.SAFR_REGISTRO_ACTIVITY_CODE);
    }*/

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == StaticValues.SAFR_REGISTRO_ACTIVITY_CODE){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(mContext,"LOL",Toast.LENGTH_SHORT).show();
                changeLealtadRegistro();
            }else{
                Toast.makeText(mContext,"ACTION_CANCEL",Toast.LENGTH_SHORT).show();
            }
        }

    }*/


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

    public void changeLealtadRegistro(){
        Log.v(LOG_TAG,"CHANGE FRAGMENT");
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fantasyContent, new PublishOportunity()).commit();
    }

    public interface OnPrincipalFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
