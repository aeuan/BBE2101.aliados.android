package com.dacodes.bepensa.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.BuildConfig;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.ActivityDetailOportunity;
import com.dacodes.bepensa.activities.ActivityOpportunity;
import com.dacodes.bepensa.activities.ChatActivity;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.RecyclerViewOpportunitiesAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.OpportunityEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.TypeEntity;
import com.dacodes.bepensa.entities.ResponseChatSocket;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.dacodes.bepensa.models.OpportunityModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_OPPORTUNITIES;

/**
 * Created by Eric on 09/03/17.
 *
 */

public class OpportunitiesFragment extends Fragment implements
        RecyclerViewOpportunitiesAdapter.customClickListener, RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey  {

    public static final int NEW_OPPORTUNITY_REQUEST = 1;
    @BindView(R.id.recycler)RecyclerView my_recycler_view;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.not_values_tv)TextView not_values_tv;
    @BindView(R.id.not_available_network_ll) LinearLayout not_available_network;
    @BindView(R.id.progressBar)ProgressBar progressBar;

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
    ArrayList<OpportunityEntity> opportunityEntities = new ArrayList<>();
    private Context mContext;
    AppController app;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mLayoutManager;
    RetrofitWebServices retrofitWebServices;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    Realm realm;

    private Activity mActivity;
    private WebSocketClient mWebSocketClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mActivity=getActivity();
        app = (AppController) getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        realm=Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opportunities_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        my_recycler_view.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity().getBaseContext(), 2);
        my_recycler_view.setLayoutManager(mLayoutManager);
        my_recycler_view.addOnScrollListener(scrollListener);
        mAdapter = new RecyclerViewOpportunitiesAdapter(mContext, this, this.opportunityEntities);
        my_recycler_view.setAdapter(mAdapter);
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pagination_next = null;
            progressBar.setVisibility(View.GONE);
            if (opportunityEntities.size()>0 && mAdapter!=null) {
                opportunityEntities.clear();
                mAdapter.notifyDataSetChanged();
            }
            validateConnectionNetwork();
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        Log.e("outsave","Oportunities");
    }


    @OnClick(R.id.not_available_network_ll)
    public void validateNetword(){
        validateConnectionNetwork();
    }

    @OnClick(R.id.btAddNewOportunity)
    public void submit(View view){
        switch (view.getId()){
            case R.id.btAddNewOportunity:
                Intent intent = new Intent(mContext, ActivityOpportunity.class);
                startActivityForResult(intent, NEW_OPPORTUNITY_REQUEST);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                break;
        }
    }

    public void validateConnectionNetwork(){
        if (ValidateNetwork.getNetwork(mContext)){
            connectChatSocket();
            disableDefaultValues();
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            retrofitWebServices.getOpportunities(null, app.getAccessToken());
        }else{
            validateLocalOpportunites();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver,new IntentFilter(BuildConfig.APPLICATION_ID+"update"));
        restartValues();
        ((MainActivity)getActivity()).refreshTotalPoint();
        validateConnectionNetwork();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
        super.onPause();
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

    @Override
    public void onItemClickListener(int id,View view) {
        switch (view.getId()){
            case R.id.comunicado_iv:
                if (ValidateNetwork.getNetwork(mContext)) {
                    Intent intent = new Intent(mContext, ActivityDetailOportunity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("networkLocal", 1);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                break;
            case R.id.tvNumMessages:
                if (ValidateNetwork.getNetwork(mContext)){
                    Intent intent= new Intent(mContext, ChatActivity.class);
                    intent.putExtra("opportunityId",id);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_OPPORTUNITIES:
                isLoading = false;
                OpportunityModel opportunityModel = (OpportunityModel) object;
                if(opportunityModel.getPagination().getLinks().getNext()!=null){
                    pagination_next = opportunityModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                OpportunityModel response = (OpportunityModel) object;
                swipeRefreshLayout.setRefreshing(false);
                if (response.getData().size()<=0){
                    enableNotValues();
                }else{

                    ArrayList<OpportunityEntity> list= new ArrayList<>();
                    list.addAll(initializeLocalOpportunites());
                    list.addAll(response.getData());
                    populateRecyclerView(list);
                    disableNotValues();
                }
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
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
        Log.e("onfails","id::"+id+"status::"+status+"message::"+message);
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

    @Override
    public void onStop() {
        super.onStop();
        if (mWebSocketClient!= null)
            mWebSocketClient.close();
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

    private void connectChatSocket() {
        String userId = app.getUserId();
        //String url = "wss://echo.websocket.org";
        String url = "wss://" + getString(R.string.socket_url)+"ws/opportunities/"+userId+"/";
        URI uri = getUriForSocket(url);
        if(uri == null) return;

        if(mWebSocketClient!=null){
            mWebSocketClient.close();
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("SocketFragment", "Opened connectDeliveryManWebSocket");
            }

            @Override
            public void onMessage(String s) {
                onChatSocketMessageReceived(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("SocketFragment","Close");
            }

            @Override
            public void onError(Exception e) {
                Log.e("SocketFragment", "Error:" + e.getMessage());
            }
        };

        mWebSocketClient.connect();
    }

    private URI getUriForSocket(String url){
        URI uri;
        try {
            uri = new URI(url);
            return uri;
        } catch (URISyntaxException e) {
            Log.e("uri",""+e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void onChatSocketMessageReceived(final String message){
        mActivity.runOnUiThread(() -> {
            Gson gson= new Gson();
            ResponseChatSocket chatSocket= gson.fromJson(message,ResponseChatSocket.class);
            int position=0;
            for (int i=0;i<opportunityEntities.size();i++){
                if (opportunityEntities.get(i).getStatus()>0) {
                    if (opportunityEntities.get(i).getId() == chatSocket.getDataChatSocket().getId()) {
                        OpportunityEntity opportunityEntity = opportunityEntities.get(i);
                        opportunityEntity.setHas_pending_chat(true);
                        opportunityEntity.setUnread_messages(chatSocket.getDataChatSocket().getHasUnreadCollaborator());
                        opportunityEntity.setStatus(chatSocket.getDataChatSocket().getStatus());
                        opportunityEntities.remove(i);
                        opportunityEntities.add(position, opportunityEntity);
                        mAdapter.notifyDataSetChanged();
                    }
                }else{
                    position++;
                }
            }
        });
    }

    private void populateRecyclerView(ArrayList<OpportunityEntity> opportunityEntities){
        this.opportunityEntities.addAll(opportunityEntities);
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

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            retrofitWebServices.getOpportunities(pagination_next, app.getAccessToken());
        }
    }

    public void restartValues(){
        if (opportunityEntities.size()>0 && mAdapter!=null) {
            opportunityEntities.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<OpportunityEntity> initializeLocalOpportunites(){
        ArrayList<OpportunityEntity> list= new ArrayList<>();

        RealmResults<OflinePostOpportunity> tempOpor= realm.where(OflinePostOpportunity.class)
                .equalTo("has_sync",true).equalTo("id_colaborator",app.getUsername()).findAll();

        if (tempOpor.size()>0){
            for (OflinePostOpportunity opportunity: tempOpor){
                OpportunityEntity opportunityEntity= new OpportunityEntity();
                TypeEntity typeEntity= new TypeEntity();
                BrandEntity brandEntity= new BrandEntity();

                opportunityEntity.setDescription(opportunity.getDescription());
                opportunityEntity.setCreatedAt("");
                opportunityEntity.setHas_pending_chat(false);
                opportunityEntity.setStatus(-1);

                RealmResults<OflineBrandsEntity> oflineBrandsEntities = realm.where(OflineBrandsEntity.class)
                        .equalTo("id",Integer.parseInt(opportunity.getBrands())).findAll();

                if (oflineBrandsEntities.size()>0){
                    for (OflineBrandsEntity brandsEntity:oflineBrandsEntities){
                        Gson gson= new Gson();
                        BrandEntity brands= gson.fromJson(brandsEntity.getOpportunity_types(),BrandEntity.class);
                        if (brands.getOpportunity_types().size()>0){
                            for (int i=0;i<brands.getOpportunity_types().size();i++){
                                if (opportunity.getId_oportunity()==brands.getOpportunity_types().get(i).getId()){
                                    typeEntity.setName(brands.getOpportunity_types().get(i).getName());
                                }
                            }
                        }
                    }
                    brandEntity.setLogo(oflineBrandsEntities.get(0).getLogo());
                    List<BrandEntity> brandEntities= new ArrayList<>();
                    opportunityEntity.setType(typeEntity);
                    brandEntities.add(brandEntity);
                    opportunityEntity.setBrands(brandEntities);
                }

                List<MediaEntity> mediaEntities=new ArrayList<>();
                RealmResults<OflineListMedia> listMedia = realm.where(OflineListMedia.class)
                        .equalTo("key_hash",opportunity.getKey_hash()).findAll();

                if (listMedia.size()>0){
                    for (int i=0;i<listMedia.size();i++){
                        if (listMedia.get(i).getOflineMediaOpportunities()!=null
                                && listMedia.get(i).getOflineMediaOpportunities().size()>0){
                            MediaEntity tempMedia = new MediaEntity();
                            tempMedia.setMediaLocal(listMedia.get(i).getOflineMediaOpportunities().get(0).getPath());
                            tempMedia.setType(listMedia.get(i).getOflineMediaOpportunities().get(0).getType());
                            mediaEntities.add(tempMedia);
                        }
                    }
                }

                opportunityEntity.setMedia(mediaEntities);

                list.add(opportunityEntity);
            }
        }
        return list;
    }

    public void validateLocalOpportunites(){
        String id_colaborator=app.getUsername();
        RealmResults<OflinePostOpportunity> persons = realm.where(OflinePostOpportunity.class)
                .equalTo("has_sync", true).equalTo("id_colaborator",id_colaborator).findAll();
        if (persons.size()>0){
            disableNotValues();
            ArrayList<OpportunityEntity> lits=new ArrayList<>();
            for (OflinePostOpportunity opportunity:persons){
                OpportunityEntity opportunities=new OpportunityEntity();
                TypeEntity typeEntity=new TypeEntity();
                opportunities.setDescription(opportunity.getDescription());
                opportunities.setCreatedAt("");
                int idBrand=0;
                try {
                     idBrand=Integer.parseInt(opportunity.getBrands());
                }catch (Exception e){

                }

                RealmResults<OflineBrandsEntity> oflineBrandsEntityRealmResults = realm.where(OflineBrandsEntity.class)
                        .equalTo("id",idBrand).findAll();

                if (oflineBrandsEntityRealmResults.size() > 0) {

                    for (OflineBrandsEntity brand:oflineBrandsEntityRealmResults){
                        Gson gson= new Gson();
                        BrandEntity brandEntity = gson.fromJson(brand.getOpportunity_types(),BrandEntity.class);
                        if (brandEntity.getOpportunity_types().size()>0){
                            for (int i=0;i<brandEntity.getOpportunity_types().size();i++){
                                if (opportunity.getId_oportunity()==brandEntity.getOpportunity_types().get(i).getId()){
                                    typeEntity.setName(brandEntity.getOpportunity_types().get(i).getName());
                                }
                            }
                        }
                    }
                    BrandEntity brandEntity = new BrandEntity();
                    brandEntity.setLogo(oflineBrandsEntityRealmResults.get(0).getLogo());
                    List<BrandEntity> brandEntities= new ArrayList<>();
                    opportunities.setType(typeEntity);
                    brandEntities.add(brandEntity);
                    opportunities.setBrands(brandEntities);
                }
                opportunities.setHas_pending_chat(false);
                List<MediaEntity> mediaEntities=new ArrayList<>();
                RealmResults<OflineListMedia> listMedia = realm.where(OflineListMedia.class)
                        .equalTo("key_hash",opportunity.getKey_hash()).findAll();

                if (listMedia.size()>0){
                    for (int i=0;i<listMedia.size();i++){
                        if (listMedia.get(i).getOflineMediaOpportunities()!=null
                                && listMedia.get(i).getOflineMediaOpportunities().size()>0){
                            MediaEntity tempMedia = new MediaEntity();
                            tempMedia.setMediaLocal(listMedia.get(i).getOflineMediaOpportunities().get(0).getPath());
                            tempMedia.setType(listMedia.get(i).getOflineMediaOpportunities().get(0).getType());
                            mediaEntities.add(tempMedia);
                        }
                    }
                }

                opportunities.setMedia(mediaEntities);
                lits.add(opportunities);
            }
            populateRecyclerView(lits);
        }else{
            disableNetork();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(BuildConfig.APPLICATION_ID+"update")){
                if (intent.getBooleanExtra("update",false)){
                    onResume();
                }
            }
        }
    };
}
