package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.FilterThemeNotifications;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.FilterNotifications;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.AdapterFilterNotifications;
import com.dacodes.bepensa.adapters.AdapterNotifications;
import com.dacodes.bepensa.adapters.RecyclerItemTouchHelper;
import com.dacodes.bepensa.adapters.RecyclerViewNotificacionesAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.NotificacionesEntity;
import com.dacodes.bepensa.models.NotificacionesResponseModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_NOTIFICACIONES;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_NOTIFICATION;


public class NotificacionesFragment extends Fragment implements RecyclerViewNotificacionesAdapter.customClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey, AdapterFilterNotifications.onCheckedId {

    private Context mContext;
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    @BindView(R.id.progress_bar)ProgressBar progress_bar;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;

    Activity activity;
    AppController app;

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
    RetrofitWebServices retrofitWebServices;
    private boolean status=false;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    private AdapterNotifications adapterNotifications;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        mContext = getContext();
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        ButterKnife.bind(this, rootView);
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        adapterNotifications = new AdapterNotifications(this,mContext);
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        progress_bar.setVisibility(View.VISIBLE);
        //mAdapter = new RecyclerViewNotificacionesAdapter(mContext, this, this.notificacionesEntities);
        mRecyclerView.setAdapter(adapterNotifications);
        mRecyclerView.addOnScrollListener(scrollListener);

        ((MainActivity)getActivity()).setToolbar(10);
        adapterNotifications.setItems(notificacionesEntities);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pagination_next = null;
            progress_bar.setVisibility(View.GONE);
            if (ValidateNetwork.getNetwork(mContext)){
                notificacionesEntities.clear();
               adapterNotifications.notifyDataSetChanged();
                if (app.getFilterThemes() != null) {
                    retrofitWebServices.getNotificaciones(null, app.getAccessToken()
                            , app.getFilterThemes().replace("[", "").replace("]", ""));
                }else
                    retrofitWebServices.getNotificaciones(null, app.getAccessToken());
            }else{
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progress_bar.setVisibility(View.VISIBLE);
        notificacionesEntities.clear();
        adapterNotifications.notifyDataSetChanged();
        if (app.getFilterThemes() != null) {
            retrofitWebServices.getNotificaciones(null, app.getAccessToken()
                    , app.getFilterThemes().replace("[", "").replace("]", ""));
        }else
            retrofitWebServices.getNotificaciones(null, app.getAccessToken());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app.setFilterThemes(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_notification:
                startActivity(new Intent(mContext, FilterNotifications.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
    public void onItemClickListener(NotificacionesEntity notificacionesEntity, ConstraintLayout constraintLayout) {

    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {

       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Realmente desea eliminarlo");
        builder.setPositiveButton("Si", (dialogInterface, i) -> {
            int id=notificacionesEntities.get(viewHolder.getAdapterPosition()).getId();
            retrofitWebServices.putNotification(id,"3");
            adapterNotifications.removeItem(viewHolder.getAdapterPosition());
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> adapterNotifications.restoreItem());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
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

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            if (app.getFilterThemes() != null) {
                retrofitWebServices.getNotificaciones(pagination_next, app.getAccessToken()
                        , app.getFilterThemes().replace("[", "").replace("]", ""));
            }else
                retrofitWebServices.getNotificaciones(pagination_next, app.getAccessToken());
        }
    }

    private void populateRecyclerView(ArrayList<NotificacionesEntity> notificacionesEntities){
        this.notificacionesEntities.addAll(notificacionesEntities);
        adapterNotifications.notifyDataSetChanged();
        //adapterNotifications.setItems(notificacionesEntities);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_NOTIFICACIONES:
                isLoading = false;
                progress_bar.setVisibility(View.GONE);
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
            case PUT_NOTIFICATION:
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
        //Todo
    }

    @Override
    public void onWebServiceEnd() {

        //Todo
    }

    @Override
    public void onExpireToken() {

    }

    @Override
    public void onChekedIds(List<Integer> ids) {
        startActivity(new Intent(mContext, FilterThemeNotifications.class).putExtra("idFilter",ids.get(0).toString()));
    }
}
