package com.dacodes.bepensa.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.RecyclerViewRankingAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ObjectToList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_POINTS;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_RANKING;

/**
 * Created by Eric on 09/03/17.
 *
 */

public class RankingFragment extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey  {
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.my_recycler_view)RecyclerView my_recycler_view;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    private Context mContext;
    AppController app;
    private RecyclerView.Adapter mAdapter;
    RetrofitWebServices retrofitWebServices;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    ArrayList<ProfileEntity> ranking = new ArrayList();
    View view;
    boolean status_rank=false;
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mActivity=getActivity();
        app = (AppController) getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity().getApplicationContext();
        my_recycler_view.setHasFixedSize(true);
        my_recycler_view.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(), 1));
        progressBar.setVisibility(VISIBLE);
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        retrofitWebServices.getRanking(app.getAccessToken());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(GONE);
            if (ranking.size()>0) {
                RecyclerViewRankingAdapter recyclerViewEventosAdapter = (RecyclerViewRankingAdapter) mAdapter;
                recyclerViewEventosAdapter.clearItems();
                recyclerViewEventosAdapter.notifyDataSetChanged();
            }
            retrofitWebServices.getRanking(app.getAccessToken());
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        retrofitWebServices.getPoints();
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

    private void syncUuid(ArrayList<ProfileEntity> profileEntities){
        try {
            ((MainActivity) getActivity()).viewRanking();
        }catch (Exception e){
            e.printStackTrace();
        }
        progressBar.setVisibility(GONE);
        if (app.getRankingPrivacy()) {
            for (ProfileEntity dta : profileEntities) {
                if (dta.getAccess().getUuid().equals(app.getUserId())) {
                    status_rank = true;
                    mAdapter = new RecyclerViewRankingAdapter(mContext,profileEntities, app.getUserId());
                    my_recycler_view.setAdapter(mAdapter);
                    break;
                } else {
                    status_rank = false;
                }
            }
            if (!status_rank){
                mAdapter = new RecyclerViewRankingAdapter(mContext,profileEntities, app.getUserId());
                my_recycler_view.setAdapter(mAdapter);
                rowStick();
            }
        }else{
            rowStick();
            mAdapter = new RecyclerViewRankingAdapter(mContext,profileEntities, app.getUserId());
            my_recycler_view.setAdapter(mAdapter);
        }

    }

    public void rowStick(){
        LayoutInflater inflate = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.row_stick_ranking, null);
        CircleImageView image_iv=(CircleImageView) v.findViewById(R.id.image_iv);
        TextView name_tv=(TextView)v.findViewById(R.id.name_tv);
        TextView point_tv=(TextView)v.findViewById(R.id.point_tv);
        TextView position=(TextView)v.findViewById(R.id.position_tv);
        TextView privacy=(TextView)v.findViewById(R.id.userPrivacy);

        if (!app.getRankingPrivacy()){
            privacy.setVisibility(VISIBLE);
        }else{
            privacy.setVisibility(GONE);
        }
        Glide.with(mContext)
                .load(app.getImageUrl())
                .apply(new RequestOptions().centerCrop().centerCrop())
                .into(image_iv);
        name_tv.setText(app.getName()+" "+app.getLastName());
        point_tv.setText("Puntos: "+app.getPoints());
        position.setText("Posición: "+app.getRank());
        ViewGroup main = (ViewGroup) view.findViewById(R.id.ranking);
        main.addView(v, 0);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_RANKING:
                List<? >list = ObjectToList.ObjectToList(object);
                for (Object entity : list) {
                    ProfileEntity profileEntity = (ProfileEntity) entity;
                    ranking.add(profileEntity);
                }
                if (ranking.size()>0){
                    syncUuid(ranking);
                }

                swipeRefreshLayout.setRefreshing(false);
                break;
            case GET_POINTS:
                PointsResponseModel responseModel= (PointsResponseModel) object;
                app.setPoints(responseModel.getPoints());
                app.setRankingPosition(String.valueOf(responseModel.getRank()));
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(GONE);
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
}
