package com.dacodes.bepensa.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.HomeHeaderPagerAdapter;
import com.dacodes.bepensa.adapters.RecyclerViewFeedListAdapter;
import com.dacodes.bepensa.adapters.RecyclerViewHomeRestaurantsAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.entities.feed.FeedResponseEntity;
import com.dacodes.bepensa.models.NewData;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.DM;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_FEED;

/**
 * Created by Eric on 09/03/17.
 *
 */

public class FeedFragment extends Fragment implements RecyclerViewHomeRestaurantsAdapter.ItemClickListener,
        RecyclerViewFeedListAdapter.ItemClickListener ,RetrofitWebServices.DefaultResponseListeners,AlertDIalogLogout.logout
        ,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey , DCListeners.retrofitResponse {

    String LOG_TAG=FeedFragment.class.getSimpleName();
    @BindView(R.id.my_recycler_view)RecyclerView my_recycler_view;
    @BindView(R.id.viewpager)ViewPager viewpager;
    @BindView(R.id.indicator)CircleIndicator indicator;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_detail)View layout;
    @BindView(R.id.text_default)TextView text_default;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.progressBar)ProgressBar progressBar1;
    @BindView(R.id.nestedScrollView)NestedScrollView nestedScrollView;
    @BindView(R.id.appbar)AppBarLayout appBarLayout;

    RetrofitWebServices retrofitWebServices;
    private Context mContext;
    private int pageIndex = 0;
    private int NUM_PAGES = 0;
    AppController app;
    private boolean new_events = false;
    private boolean new_promotions = false;
    private boolean new_surveys = false;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        app = (AppController) getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.feed_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity().getApplicationContext();
        progressBar.setVisibility(View.VISIBLE);
        progressBar1.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        my_recycler_view.setLayoutManager(linearLayoutManager);
        my_recycler_view.setAdapter(new RecyclerViewFeedListAdapter(getActivity(), mContext, null, this));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                retrofitWebServices.getFeed(null,app.getAccessToken());
            }
        });
        ((MainActivity)getActivity()).setToolbar(2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                ,DM.getDisplayWidth(getContext()));
        viewpager.setLayoutParams(params);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNewData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        Log.e("outsave","Feed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateFeaturedRestaurantsViewPager(List<EventosEntity> featuredRestaurantsEntities){
        NUM_PAGES = featuredRestaurantsEntities.size();
        viewpager.setAdapter(new HomeHeaderPagerAdapter(mContext, featuredRestaurantsEntities));
        indicator.setViewPager(viewpager);
        viewpager.setCurrentItem(0);
    }

    //Lista de Categorias VERTICAL que contiene una lista HORIZONTAL de restaurantes
    private void populateRestaurantsRecyclerView(FeedResponseEntity feedResponseEntity) {
        feedResponseEntity.setNew_events(new_events);
        feedResponseEntity.setNew_promotions(new_promotions);
        feedResponseEntity.setNew_surveys(new_surveys);
        List<String> strings=new ArrayList<>();
            strings.add("BENEFICIOS");
            strings.add("promo");
            if (feedResponseEntity.getEvents_near().size()>=1){
                strings.add("EVENTOS");
                strings.add("events");
            }
            strings.add("ENCUESTAS");
            strings.add("test");
        RecyclerViewHomeRestaurantsAdapter restaurantsAdapter = new RecyclerViewHomeRestaurantsAdapter(getActivity(), mContext, feedResponseEntity, this,strings);
        my_recycler_view.setAdapter(restaurantsAdapter);
        my_recycler_view.setHasFixedSize(true);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        if (isVisible()) {
            switch (id) {
                case GET_FEED:
                    progressBar.setVisibility(View.GONE);
                    FeedResponseEntity feedResponseEntity = (FeedResponseEntity) object;
                    if (feedResponseEntity.getEvents_top().size() > 0) {
                        populateFeaturedRestaurantsViewPager(feedResponseEntity.getEvents_top());
                    } else {
                        layout.setVisibility(View.GONE);
                        text_default.setVisibility(View.VISIBLE);
                    }
                    populateRestaurantsRecyclerView(feedResponseEntity);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case DELETE_DEVICE:
                    retrofitWebServices.logoutAction();
                    break;
                case 300:
                    if (isVisible()) {
                        NewData newData = ((NewData) object);
                        if (newData.getNewSurveys() > 0)
                            this.new_surveys = true;
                        if (newData.getNewPromotions() > 0)
                            this.new_promotions = true;
                        if (newData.getNewEvents() > 0)
                            this.new_events = true;
                        retrofitWebServices.getFeed(null, app.getAccessToken());
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        if (isVisible()) {
            progressBar.setVisibility(View.GONE);
            progressBar1.setVisibility(View.GONE);
            switch (status) {
                case 1001:
                    AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(), app, this, this, this);
                    alertDIalogLogout.sesionTerminate();
                    break;
            }
            switch (id) {
                case DELETE_DEVICE:
                    retrofitWebServices.logoutAction();
                    break;
            }
        }
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


    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void dialogSurvey(int status) {
        this.status_survey=status;
        compareDialog();
    }

    @Override
    public void dialogOpportunity(int status) {
        this.status_opportunity=status;
        compareDialog();
    }

    @Override
    public void dialogLogout(int status) {
        this.status_logout=status;
        compareDialog();
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(app.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(app.getAccessToken(),app.getAndroid_id(),deviceId);
        }
    }

    @Override
    public void onClick(View view, String position) {
        switch (position){
            case "BENEFICIOS":
                ((MainActivity)getActivity()).itemSelected(3,204);
                break;
            case "EVENTOS":
                ((MainActivity)getActivity()).itemSelected(4,210);
                break;
            default:
                ((MainActivity)getActivity()).itemSelected(5,208);
                break;
        }
    }

    private void getNewData(){
        DCService.Build(this).setCode(300).setCallResponse(new DCResponse<NewData>() {
            @Override
            public Call<NewData> getResponse(DCResponseInterface dcResponseInterface) {
                return app.getWebServices().getNewData(app.getAccessToken());
            }
        }).init();
    }

    @Override
    public void onFailure(int i, int i1, String s) {
        retrofitWebServices.getFeed(null,app.getAccessToken());
    }
}
