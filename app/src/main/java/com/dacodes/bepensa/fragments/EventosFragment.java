package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.dacodes.bepensa.utils.MyBounceInterpolator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventosFragment extends Fragment {
    private Context mContext;
    @BindView(R.id.tablayout)TabLayout tablayout;
    Activity activity;
    AppController app;
    EventosListFragment eventosListFragment;
    EventosMapaFragment eventosMapaFragment;
    private FragmentManager fragmentManager;
    @BindView(R.id.et_search)EditText et_search;
    @BindView(R.id.filtros_iv)ImageView filtros_iv;
    ActionBar actionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
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
        et_search.addTextChangedListener(textWatcher);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        filtros_iv.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textWatcher.afterTextChanged(et_search.getText());
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        public void afterTextChanged(Editable s) {
            if (tablayout.getSelectedTabPosition() == 0){
                Bundle bundle = new Bundle();
                bundle.putString("data",s.toString());
                if (eventosListFragment != null)
                    eventosListFragment.validateConnectionNetworkSearch(bundle);
                else
                    tablayout.getTabAt(0).select();
            }else if (tablayout.getSelectedTabPosition() == 1){
                Bundle bundle = new Bundle();
                bundle.putString("data",s.toString());
                if (eventosMapaFragment != null)
                    eventosMapaFragment.validateConnectionNetworkSearch(bundle);
                else
                    tablayout.getTabAt(1).select();
            }
        }
    };
    private void initializeFragments() {
        eventosListFragment = new EventosListFragment();
        eventosMapaFragment = new EventosMapaFragment();
    }

    private void setUpTabs() {
        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.ic_launcher);
        imageList.add(R.drawable.ic_launcher);
        imageList.add(R.drawable.ic_launcher);


        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("Eventos");
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
                actionBar.setTitle("EVENTOS");
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(et_search.getText().toString())){
                    if (eventosListFragment == null)
                        eventosListFragment = new EventosListFragment();
                    bundle.putString("data",et_search.getText().toString());
                    eventosListFragment.setArguments(bundle);
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, eventosListFragment)
                        .commit();
                break;
            case 2:
                //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                actionBar.setTitle("EVENTOS");
                Bundle bundeMap = new Bundle();
                if (!TextUtils.isEmpty(et_search.getText().toString())){
                    if (eventosMapaFragment == null)
                        eventosMapaFragment = new EventosMapaFragment();
                    bundeMap.putString("data",et_search.getText().toString());
                    eventosMapaFragment.setArguments(bundeMap);
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content, eventosMapaFragment)
                        .commit();
                break;
            default:
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null)
            view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
