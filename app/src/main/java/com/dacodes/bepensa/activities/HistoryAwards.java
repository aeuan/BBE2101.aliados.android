package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.adapters.RecyclerAwardsRedeem;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.awardsRedeem.AwardRedeemData;
import com.dacodes.bepensa.entities.awardsRedeem.AwardRedeemResponse;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_AWARDS_REDEEM;

public class HistoryAwards extends AppCompatActivity implements RetrofitWebServices.DefaultResponseListeners,
        RecyclerAwardsRedeem.customClickListener{

    String LOG_TAG=HistoryAwards.class.getSimpleName();
    AppController app;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    ArrayList<AwardRedeemData> items=new ArrayList<>();
    @BindView(R.id.my_recycler_view)RecyclerView recyclerView;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.toolbar)Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

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

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_awards);
        ButterKnife.bind(this);
        app=(AppController)getApplication();
        mContext =getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        toolbar.setTitle("HISTORIAL DE CANJES");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAwardsRedeem(mContext, this, null);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(scrollListener);
        progressBar.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerAwardsRedeem(mContext, this, this.items);
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagination_next = null;
                progressBar.setVisibility(View.GONE);
                if (items.size()>0) {
                    RecyclerAwardsRedeem recyclerViewNotificacionesAdapter = (RecyclerAwardsRedeem) mAdapter;
                    recyclerViewNotificacionesAdapter.clearItems();
                    recyclerViewNotificacionesAdapter.notifyDataSetChanged();
                }
                if (ValidateNetwork.getNetwork(mContext)){
                    retrofitWebServices.getAwardsRedeem(pagination_next, app.getAccessToken());
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        retrofitWebServices.getAwardsRedeem(null,app.getAccessToken());

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
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems();
                }
            }

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            retrofitWebServices.getAwardsRedeem(pagination_next, app.getAccessToken());
        }
    }

    private void populateRecyclerView(ArrayList<AwardRedeemData> awardRedeemData){
        this.items.addAll(awardRedeemData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_AWARDS_REDEEM:
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                AwardRedeemResponse responseModel = (AwardRedeemResponse) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                progressBar.setVisibility(View.GONE);
            AwardRedeemResponse response = (AwardRedeemResponse) object;
                populateRecyclerView(response.getData());
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
    public void onItemClickListener(int job_id) {

    }

}
