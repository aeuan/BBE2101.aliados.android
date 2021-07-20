package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.entities.ProfileEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CardFragment extends Fragment
//        implements RetrofitWebServices.DefaultResponseListeners
{

    ProfileEntity profileEntity;
    @BindView(R.id.puntos_tv)TextView puntos_tv;
    @BindView(R.id.profile_image)ImageView profile_image;
    @BindView(R.id.logo_motriz_iv)ImageView logo_motriz_iv;
    @BindView(R.id.name_card_tv)TextView name_card_tv;
    @BindView(R.id.card_background_iv)ImageView card_background_iv;

    private static final String LOG_TAG = CardFragment.class.getSimpleName();

    public CardFragment() {
        // Required empty public constructor
    }


    AppController appController;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card2, container, false);
        ButterKnife.bind(this,rootView);
        mContext = getActivity().getApplicationContext();
        prepareCard(rootView);
        appController = (AppController)getActivity().getApplication();
        MainActivity mainActivity = (MainActivity) getActivity();
        profileEntity = mainActivity.getProfileEntity();
        try {
            puntos_tv.setText("Total: " +appController.getPoints()+ " puntos");
        }catch (Exception e){
            Log.e("NumberFormat",""+e.getLocalizedMessage());
        }
        Glide.with(mContext)
                .load(appController.getImageDivision())
                .apply(new RequestOptions()
                        .placeholder(mContext.getResources()
                                .getDrawable(R.drawable.logo)))
                .into(logo_motriz_iv);
        Glide.with(mContext).load(appController.getImageUrl()).apply(new RequestOptions().centerCrop()).into(profile_image);
        name_card_tv.setText(""+appController.getName()+" "+appController.getLastName());
        return rootView;
    }

    @OnClick({R.id.logo_motriz_iv,R.id.profile_image,R.id.puntos_tv,R.id.card_background_iv})
    public void logoMotriz(){
        ((MainActivity)getActivity()).loadProfile();
    }

    private void prepareCard(View v){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

}
