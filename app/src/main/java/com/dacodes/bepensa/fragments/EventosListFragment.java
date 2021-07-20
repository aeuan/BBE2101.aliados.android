package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EventoDetailActivity;
import com.dacodes.bepensa.adapters.RecyclerViewEventosAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.models.EventosResponseModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_EVENTOS;


public class EventosListFragment extends Fragment implements RecyclerViewEventosAdapter.customClickListener,
        RetrofitWebServices.DefaultResponseListeners,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{
    // TODO: Rename parameter arguments, choose name,s that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context mContext;

    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.recycler)RecyclerView mRecyclerView;
    @BindView(R.id.not_values_tv) TextView not_values_tv;
    @BindView(R.id.not_available_network_ll) LinearLayout not_available_network;

    Activity activity;
    AppController app;

    private static final String LOG_TAG = EventosFragment.class.getSimpleName();
    ArrayList<EventosEntity> eventosEntities = new ArrayList<>();
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
    private String pagination_next=null;
    RetrofitWebServices retrofitWebServices;
    private String query_search="";
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    private Call<EventosResponseModel> callEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beneficios, container, false);
        ButterKnife.bind(this, rootView);
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(scrollListener);
        mAdapter = new RecyclerViewEventosAdapter(mContext, this, this.eventosEntities);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            pagination_next = null;
            restartValues();
            validateConnectionNetwork();
        });
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_events)+"?name=";
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.eventosEntities.clear();
    }

    @Override
    public void onItemClickListener(EventosEntity eventosEntity) {
        Intent intent = new Intent(mContext, EventoDetailActivity.class);
        intent.putExtra("evento", eventosEntity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_EVENTOS:
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                EventosResponseModel responseModel = (EventosResponseModel) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                EventosResponseModel response = (EventosResponseModel) object;
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
    public void onWebServiceStart() { }

    @Override
    public void onWebServiceEnd() {}

    @Override
    public void onExpireToken() { }

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


    public void checkData(){
        Bundle bundle=getArguments();
        if (bundle!=null){
            progressBar.setVisibility(View.GONE);
            restartValues();
            validateConnectionNetworkSearch(bundle);
        }else{
            restartValues();
            validateConnectionNetwork();
        }
    }

    public void validateConnectionNetwork(){
        if (callEvents != null){
            if (callEvents.isExecuted())
                callEvents.cancel();
        }
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            callEvents = retrofitWebServices.getEventos(null, app.getAccessToken());
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }

    }

    public void validateConnectionNetworkSearch(Bundle bundle){
        if (callEvents != null){
            if (callEvents.isExecuted())
                callEvents.cancel();
        }
        eventosEntities.clear();
        mAdapter.notifyDataSetChanged();
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            callEvents = retrofitWebServices.getEventos(query_search+""+bundle.getString("data"),app.getAccessToken());
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

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            callEvents = retrofitWebServices.getEventos(pagination_next, app.getAccessToken());
        }
    }

    private void populateRecyclerView(ArrayList<EventosEntity> eventosEntities){
        this.eventosEntities.addAll(eventosEntities);
        mAdapter.notifyDataSetChanged();
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
        if (eventosEntities.size()>0 && mAdapter!=null) {
            eventosEntities.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

}
