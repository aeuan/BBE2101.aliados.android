package com.dacodes.bepensa.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.models.NewData;
import com.dacodes.bepensa.utils.MyBounceInterpolator;
import com.dacodes.bepensa.utils.ShowMessages;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class HomeFragment extends Fragment implements DCListeners.retrofitResponse {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    GeneralFragment principalFragment;
    FeedFragment feedFragment;
    OpportunitiesFragment opportunitiesFragment;
    private FragmentManager fragmentManager;

    Context mContext;
    AppController app;
    @BindView(R.id.tablayout)TabLayout tablayout;
    private boolean isBoletin = false;

    Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
        initializeFragments();
        setUpTabs();
        //getNewData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNewData();
    }

    private void getNewData(){
        DCService.Build(this).setCallResponse(new DCResponse<NewData>() {
            @Override
            public Call<NewData> getResponse(DCResponseInterface dcResponseInterface) {
                return app.getWebServices().getNewData(app.getAccessToken());
            }
        }).init();
    }

    @Override
    public void onFailure(int i, int i1, String s) {
        Log.e("tabs",s);
    }

    @Override
    public void onResponse(int i, String s, Object o) {
        //if (isAdded()) {
            NewData data = ((NewData) o);
            if (data.getNewEvents() > 0 || data.getNewPromotions() > 0) {
                View tab = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(0);
                ImageButton circle = tab.findViewById(R.id.circle);
                circle.setVisibility(View.VISIBLE);
            }
            if (data.getUnreadMessages() > 0) {
                tablayout.getTabAt(1).select();
                ShowMessages updateApp = ShowMessages.getInstance(app.getName());
                updateApp.setCancelable(false);
                updateApp.show(getChildFragmentManager(), "UpdateApp");
            }
       // }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    private void setUpTabs() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("Mi boletín");
        titleList.add("Mis oportunidades");
        titleList.add("General");

        for (int i = 0; i < titleList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayText));
            title.setText(titleList.get(i));
            tablayout.addTab(tablayout.newTab().setCustomView(view));
        }
        View tabLayout = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(0);
        ConstraintLayout background;
        TextView title;
        title = (TextView) tabLayout.findViewById(R.id.title);
        background = (ConstraintLayout) tabLayout.findViewById(R.id.background);
        title.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        title.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateColorTab(tab.getPosition());
                changeFragment(tab.getPosition() + 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null){
            int backOpportunity=bundle.getInt("backOpportunity",0);
            if (backOpportunity>0){
                tablayout.getTabAt(1).select();
            }else{
                changeFragment(1);
            }
        }else {
            changeFragment(1);
        }
    }

    private void updateColorTab(int position) {
        ConstraintLayout background;
        TextView title;
        ImageButton circle;
        for (int i = 0; i < tablayout.getTabCount(); i++) {
            final TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab != null) {
                View tabLayout = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(i);
                title = (TextView) tabLayout.findViewById(R.id.title);
                background = (ConstraintLayout) tabLayout.findViewById(R.id.background);
               /* circle = tabLayout.findViewById(R.id.circle);
                if (position == 0 && isBoletin)
                    circle.setVisibility(View.VISIBLE);
                else
                    circle.setVisibility(View.INVISIBLE);*/
                if (i == position) {
                    //circle.setVisibility(View.INVISIBLE);
                    background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                    title.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                } else {
                    //circle.setVisibility(View.VISIBLE);
                    title.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayText));
                    background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrayTab));

                }
            }
        }
    }

    private void changeFragment(int position) {
        fragmentManager = getChildFragmentManager();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        switch (position) {
            case 1:
                actionBar.setTitle("MI BOLETÍN");
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, feedFragment)
                        .commit();
                break;
            case 2:
                actionBar.setTitle("MIS OPORTUNIDADES");
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, opportunitiesFragment)
                        .commit();
                break;
            case 3:
                actionBar.setTitle("GENERAL");
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, principalFragment)
                        .commit();
                break;
        }
    }

    private void initializeFragments() {
        principalFragment = new GeneralFragment();
        opportunitiesFragment = new OpportunitiesFragment();
        feedFragment = new FeedFragment();
    }

}
