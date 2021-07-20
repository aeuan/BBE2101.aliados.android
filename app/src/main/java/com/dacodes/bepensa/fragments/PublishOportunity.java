package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.ActivityOpportunity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentLigaInteractionListener} interface
 * to handle interaction events.
 */
public class PublishOportunity extends Fragment
        //implements RetrofitWebServices.DefaultResponseListeners
{
    public int NEW_OPPORTUNITY_REQUEST = 1;
    private static final String LOG_TAG = PublishOportunity.class.getSimpleName();
    private OnFragmentLigaInteractionListener mListener;

    public PublishOportunity() {
        // Required empty public constructor
    }

    Context mContext;
    AppController appController;
    int numbreOfCalls = 0;

    @OnClick({R.id.cross_iv,R.id.fragment_liga,R.id.text_publish,R.id.dotted_circle_iv,R.id.image_rectangle})
    public void submit(View view){
        Intent intent = new Intent(mContext, ActivityOpportunity.class);
        startActivityForResult(intent, NEW_OPPORTUNITY_REQUEST);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_liga, container, false);
        ButterKnife.bind(this,rootView);
        appController = (AppController)getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentLigaInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
