package com.dacodes.bepensa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.adapters.AdapterNotifications;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.NotificacionesEntity;
import com.dacodes.bepensa.fragments.NotificacionesFragment;
import com.dacodes.bepensa.models.NotificacionesResponseModel;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_NOTIFICACIONES;

public class FilterThemeNotifications extends AppCompatActivity implements  RetrofitWebServices.DefaultResponseListeners{

    private AppController app;

    private static final String LOG_TAG = NotificacionesFragment.class.getSimpleName();
    ArrayList<NotificacionesEntity> notificacionesEntities = new ArrayList<>();

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
    private LinearLayoutManager mLayoutManager;
    RetrofitWebServices retrofitWebServices;
    private AdapterNotifications adapterNotifications = new AdapterNotifications();
    private String idFilter ="";

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_theme_notifications);
        ButterKnife.bind(this);
        app = (AppController)getApplication();
        idFilter = getIntent().getStringExtra("idFilter");
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,this,this);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapterNotifications);
        mRecyclerView.addOnScrollListener(scrollListener);
        adapterNotifications.setItems(notificacionesEntities);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pagination_next = null;
            if (ValidateNetwork.getNetwork(this)){
                if (notificacionesEntities.size()>0) {
                    notificacionesEntities.clear();
                    adapterNotifications.notifyDataSetChanged();
                }
                retrofitWebServices.getNotificaciones(null, app.getAccessToken(),idFilter);
            }else{
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_NOTIFICACIONES:
                isLoading = false;
                NotificacionesResponseModel responseModel = (NotificacionesResponseModel) object;
                if(responseModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                NotificacionesResponseModel response = (NotificacionesResponseModel) object;
                populateRecyclerView(response.getData());
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {

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
    public void onResume() {
        super.onResume();
        notificacionesEntities.clear();
        retrofitWebServices.getNotificaciones(null, app.getAccessToken(),idFilter);
    }

    private void populateRecyclerView(ArrayList<NotificacionesEntity> notificacionesEntities){
        this.notificacionesEntities.addAll(notificacionesEntities);
        adapterNotifications.notifyDataSetChanged();
    }

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            retrofitWebServices.getNotificaciones(pagination_next, app.getAccessToken(),idFilter);
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
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {

                    loadMoreItems();

                }
            }

        }
    };
}
