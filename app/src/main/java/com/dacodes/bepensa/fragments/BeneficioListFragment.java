package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.PromotionsDetailActivity;
import com.dacodes.bepensa.adapters.RecyclerViewBeneficiosAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;
import com.dacodes.bepensa.entities.promotion.PromotionsEntity;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_PROMOTIONS;

public class BeneficioListFragment extends Fragment implements
        RecyclerViewBeneficiosAdapter.customClickListener,RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey {

    private Context mContext;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    RetrofitWebServices retrofitWebServices;
    Activity activity;
    AppController app;

    private static final String LOG_TAG = BeneficioListFragment.class.getSimpleName();
    ArrayList<PromotionDataEntity> promotionsEntities = new ArrayList<>();
    //PAGINATION
    public int PAGE_SIZE = 0;
    public void setPAGE_SIZE(int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
    }
    private boolean isLoading = false;
    private boolean isLastPage = false;
    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
    private String pagination_next;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler)RecyclerView mRecyclerView;
    @BindView(R.id.not_values_tv)TextView not_values_tv;
    @BindView(R.id.not_available_network_ll)LinearLayout not_available_network;

    String query_search="";
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    String query_filters="";
    private Call<PromotionsEntity> callPromotion;
    private Bundle args = null;
    private String temp = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beneficios, container, false);
        ButterKnife.bind(this,rootView);
        mContext = getActivity().getApplicationContext();
        app = (AppController)getActivity().getApplication();
        mRecyclerView.setHasFixedSize(true);
        activity = getActivity();
        if (args != null)
            temp = args.getString("search");
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        mRecyclerView.addOnScrollListener(scrollListener);
        mAdapter = new RecyclerViewBeneficiosAdapter(mContext, this, this.promotionsEntities);
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            checkFilterNetwork();
        });
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_promotions)+"?name=";
        query_filters=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_promotions)+"?categories=";

        return rootView;
    }

    public void validateConnectionNetwork(){
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            if (temp != null)
                getAllSearch(temp);
            else
                checkFilterNetwork();
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        promotionsEntities.clear();
        mAdapter.notifyDataSetChanged();
        super.onDestroyView();
    }

    public void getAllSearch(String name){
        promotionsEntities.clear();
        mAdapter.notifyDataSetChanged();
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_promotions)+"?name=";
        progressBar.setVisibility(View.VISIBLE);
        if (callPromotion != null){
            if (callPromotion.isExecuted())
                callPromotion.cancel();
        }
        if (!TextUtils.isEmpty(name)) {
            if (callPromotion != null && callPromotion.isExecuted())
                callPromotion.cancel();
            callPromotion = retrofitWebServices.getPromotions(query_search + name, app.getAccessToken());
        }else
            checkFilterNetwork();
    }

    public void validateConnectionNetworkFilters(String query_filters,String bundle){
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            if (callPromotion != null && callPromotion.isExecuted())
                callPromotion.cancel();
            callPromotion = retrofitWebServices.getPromotions("" + query_filters + "" + bundle, app.getAccessToken());
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }

    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        checkFilterNetwork();
    }

    private void checkFilterNetwork(){
        progressBar.setVisibility(View.VISIBLE);
        if (app.getFiltersPromotions()!=null){
            restartValues();
            validateConnectionNetworkFilters(query_filters,app.getFiltersPromotions());
        }else{
            restartValues();
            if (callPromotion != null && callPromotion.isExecuted())
                callPromotion.cancel();
            callPromotion = retrofitWebServices.getPromotions(null, app.getAccessToken());
        }
    }

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            if (callPromotion != null && callPromotion.isExecuted())
                callPromotion.cancel();
            callPromotion = retrofitWebServices.getPromotions(pagination_next, app.getAccessToken());
        }
    }

    private void populateRecyclerView(ArrayList<PromotionDataEntity> promotionDataEntities){
        this.promotionsEntities.addAll(promotionDataEntities);
        progressBar.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(PromotionDataEntity promotionsEntity) {
        Intent intent = new Intent(mContext, PromotionsDetailActivity.class);
        intent.putExtra("beneficio", promotionsEntity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_PROMOTIONS:
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                PromotionsEntity responseModel= (PromotionsEntity) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                PromotionsEntity response = (PromotionsEntity) object;
                if (response.getData().size()<=0){
                    enableNotValues();
                }else{
                    populateRecyclerView(response.getData());
                    disableNotValues();
                }
                swipeRefreshLayout.setRefreshing(false);
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e("BeneficioFragment",message);
        progressBar.setVisibility(View.GONE);
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),app,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
        }
        switch (id){
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
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

    public void disableDefaultValues(){
        not_available_network.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.GONE);
    }

    public void enableNotValues(){
        not_available_network.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.VISIBLE);
    }

    public void disableNotValues(){
        progressBar.setVisibility(View.GONE);
        not_available_network.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.GONE);
    }

    public void disableNetork(){
        not_values_tv.setVisibility(View.GONE);
        not_available_network.setVisibility(View.VISIBLE);
    }

    public void restartValues(){
        if (promotionsEntities.size()>0 && mAdapter!=null) {
            promotionsEntities.clear();
            mAdapter.notifyDataSetChanged();
        }
    }
}
