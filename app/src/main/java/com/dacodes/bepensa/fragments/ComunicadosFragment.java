package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.RecyclerViewComunicadosAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ComunicadoEntity;
import com.dacodes.bepensa.models.ComunicadosResponseModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_COMUNICADOS;


public class ComunicadosFragment extends Fragment implements RecyclerViewComunicadosAdapter.customClickListener,
        RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{


    private Context mContext;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    ArrayList<ComunicadoEntity> comunicadoEntities = new ArrayList<>();
    @BindView(R.id.progressBar)ProgressBar progress_bar;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.et_search)EditText et_search;
    @BindView(R.id.not_values_tv)TextView not_values_tv;
    @BindView(R.id.not_available_network_ll) LinearLayout not_available_network;
    @BindView(R.id.recycler)RecyclerView mRecyclerView;

    Activity activity;
    AppController app;
    private static final String LOG_TAG = ComunicadosFragment.class.getSimpleName();

    String query_search="";
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

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
    RetrofitWebServices retrofitWebServices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mContext = getContext();
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_eventos_list, container, false);
        ButterKnife.bind(this, rootView);
        mRecyclerView.setHasFixedSize(true);
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter = new RecyclerViewComunicadosAdapter(mContext, this, this.comunicadoEntities,getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pagination_next=null;
            restartValues();
            validateConnectionNetwork();
        });

        et_search.addTextChangedListener(textWatcher);
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_comunicados)+"?name=";

        ((MainActivity)getActivity()).setToolbar(3);

        return rootView;
    }

    public void validateConnectionNetwork(){
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progress_bar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            retrofitWebServices.getComunicados(null, app.getAccessToken());
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progress_bar.setVisibility(View.GONE);
        }

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            validateConnectionNetworkSearch(et_search.getText().toString());
        }
    };

    public void validateConnectionNetworkSearch(String text){
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            progress_bar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            retrofitWebServices.getComunicados(query_search+text, app.getAccessToken());
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progress_bar.setVisibility(View.GONE);
        }

    }


    @Override
    public void onItemClickListener(int job_id) {

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
            retrofitWebServices.getComunicados(pagination_next, app.getAccessToken());
        }
    }

    private void populateRecyclerView(ArrayList<ComunicadoEntity> comunicadoEntities){
        this.comunicadoEntities.addAll(comunicadoEntities);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        restartValues();
        validateConnectionNetwork();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_COMUNICADOS:
                isLoading = false;
                ComunicadosResponseModel responseModel = (ComunicadosResponseModel) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                ComunicadosResponseModel response = (ComunicadosResponseModel) object;
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
        progress_bar.setVisibility(View.GONE);
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
        progress_bar.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.VISIBLE);
    }

    public void disableNotValues(){
        progress_bar.setVisibility(View.GONE);
        not_available_network.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.GONE);
    }

    public void disableNetork(){
        not_values_tv.setVisibility(View.GONE);
        not_available_network.setVisibility(View.VISIBLE);
    }

    public void restartValues(){
        if (comunicadoEntities.size()>0 && mAdapter!=null) {
            comunicadoEntities.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

}
