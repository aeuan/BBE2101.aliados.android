package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.BeneficioFiltros;
import com.dacodes.bepensa.utils.MyBounceInterpolator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeneficioFragment extends Fragment {
    private Context mContext;
    @BindView(R.id.tablayout)TabLayout tablayout;
    @BindView(R.id.et_search)EditText et_search;
    @BindView(R.id.filtros_iv)ImageView filtros_iv;

    boolean comun =false;
    Activity activity;
    AppController app;
    BeneficioListFragment beneficioListFragment;
    BeneficioMapaFragment beneficioMapaFragment;
    private FragmentManager fragmentManager;
    ActionBar actionBar;
    private searchData searchData;
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

    }

    @OnClick(R.id.filtros_iv)
    public void filter(){
        Intent intent = new Intent(getContext(),BeneficioFiltros.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beneficio_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
        initializeFragments();
        setUpTabs();
        fragmentManager = getChildFragmentManager();
        filtros_iv.setVisibility(View.VISIBLE);
        et_search.addTextChangedListener(textWatcher);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        app.setFiltersPromotions(null);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        textWatcher.afterTextChanged(et_search.getText());
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (tablayout.getSelectedTabPosition() == 0){
                if (beneficioListFragment != null)
                    beneficioListFragment.getAllSearch(s.toString());
                else{
                    beneficioListFragment = new BeneficioListFragment();
                    updateColorTab(0);
                    changeFragment(1);
                    ///beneficioListFragment.getAllSearch(s.toString());
                }
            }else if (tablayout.getSelectedTabPosition() == 1){
                if (beneficioMapaFragment != null)
                    beneficioMapaFragment.getAllSearch(s.toString());
                else{
                    beneficioMapaFragment = new BeneficioMapaFragment();
                    updateColorTab(0);
                    changeFragment(2);
                    beneficioMapaFragment.getAllSearch(s.toString());
                }
            }
        }
    };

    public interface searchData{
        void dataValues(String values);
    }

    private void initializeFragments() {
        beneficioListFragment = new BeneficioListFragment();
        Bundle bundle=getArguments();
        if (bundle!=null){
            if(bundle.getString("filters")!=null) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("filters", bundle.get("filters").toString());
                beneficioListFragment.setArguments(bundle1);
            }
        }
        beneficioMapaFragment = new BeneficioMapaFragment();
    }



    private void setUpTabs() {
        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.ic_launcher);
        imageList.add(R.drawable.ic_launcher);
        imageList.add(R.drawable.ic_launcher);


        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("Promociones");
        titleList.add("Mapa");

        for (int i = 0; i < titleList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tab_layout, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            TextView title = (TextView) view.findViewById(R.id.title);
            Glide.with(mContext).load(imageList.get(i)).apply(new RequestOptions().fitCenter()).into(icon);
            icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorWhite));
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayText));
            title.setText(titleList.get(i));
            tablayout.addTab(tablayout.newTab().setCustomView(view));
        }

        TabLayout.Tab tab = tablayout.getTabAt(0);

        View tabLayout = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(0);
        RelativeLayout background;
        ImageView icon;
        TextView title;
        icon = (ImageView) tabLayout.findViewById(R.id.icon);
        title = (TextView) tabLayout.findViewById(R.id.title);
        background = (RelativeLayout) tabLayout.findViewById(R.id.background);

        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorOrange));
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
        changeFragment(1);
    }

    private void updateColorTab(int position) {
        RelativeLayout background;
        ImageView icon;
        TextView title;
        for (int i = 0; i < tablayout.getTabCount(); i++) {
            final TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab != null) {
                View tabLayout = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(i);
                icon = (ImageView) tabLayout.findViewById(R.id.icon);
                title = (TextView) tabLayout.findViewById(R.id.title);
                background = (RelativeLayout) tabLayout.findViewById(R.id.background);
                if (i == position) {
                    icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorOrange));
                    background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                    title.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    icon.startAnimation(myAnim);
                } else {
                    icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorGrayText));
                    title.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayText));
                    background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrayTab));
                }
            }
        }
    }

    private void changeFragment(int position) {
        hideKeyboard(getActivity());
        fragmentManager = getChildFragmentManager();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        switch (position) {
            case 1:
                //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                actionBar.setTitle("BENEFICIOS");
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(et_search.getText().toString())){
                    if (beneficioListFragment == null)
                        beneficioListFragment = new BeneficioListFragment();
                    bundle.putString("search",et_search.getText().toString());
                    beneficioListFragment.setArguments(bundle);
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, beneficioListFragment)
                        .commit();
                break;
            case 2:
                //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                actionBar.setTitle("BENEFICIOS");
                Bundle bundleMap = new Bundle();
                if (!TextUtils.isEmpty(et_search.getText().toString())){
                    if (beneficioMapaFragment == null)
                        beneficioMapaFragment = new BeneficioMapaFragment();
                    bundleMap.putString("search",et_search.getText().toString());
                    beneficioMapaFragment.setArguments(bundleMap);
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, beneficioMapaFragment)
                        .commit();
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
