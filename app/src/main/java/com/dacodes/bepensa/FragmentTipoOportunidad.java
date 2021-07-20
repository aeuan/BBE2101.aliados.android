package com.dacodes.bepensa;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.activities.ActivityOpportunity;
import com.dacodes.bepensa.adapters.OpportunitiesAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTipoOportunidad extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        ,OpportunitiesAdapter.AddOpportunityType,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey {

    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.tvEmpty)TextView tvEmpty;
    @BindView(R.id.progress) ProgressBar progressBar;

    private String TAG= FragmentTipoOportunidad.class.getSimpleName();
    private RetrofitWebServices retrofitWebServices;
    private Context mContext;
    private Activity mActivity;
    private AppController appController;
    private DivisionEntity divisionEntity;
    private LinearLayoutManager linearLayoutManager;
    private Realm realm;
    private RecyclerView.Adapter adapter;
    private List<OpportunityType> opportunities = new ArrayList<>();

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    private int idMarca=0;


    public FragmentTipoOportunidad() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        mActivity=getActivity();
        appController=(AppController)getActivity().getApplication();
        retrofitWebServices=new RetrofitWebServices(appController,TAG,mContext,this);
        realm=Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_marcas, container, false);
        ButterKnife.bind(this,view);
        divisionEntity=((ActivityOpportunity)mActivity).getDivisionEntity();
        idMarca=((ActivityOpportunity)mActivity).getIdMarca();
        if (divisionEntity!=null) {
            tvTitle.setText(divisionEntity.getName());
            progressBar.setVisibility(View.VISIBLE);
            if (ValidateNetwork.getNetwork(mContext))
                retrofitWebServices.getOpportunitiesType(appController.getAccessToken(),divisionEntity.getId());
            else{
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);
                selectLocalDatas();
            }
        }else
            ((ActivityOpportunity)mActivity).fragmentChanger(ActivityOpportunity.NEW_OPPORTUNITY);
        return view;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        progressBar.setVisibility(View.GONE);
        switch (id){
            case RetrofitWebServices.GET_OPPORTUNITIES_TYPE: {
                List<?> list = ObjectToList.ObjectToList(object);
                boolean statusId=false;
                for (Object entity : list) {
                    BrandEntity brandEntity = (BrandEntity) entity;
                    if (brandEntity.getId() == idMarca) {
                        if (brandEntity.getOpportunity_types() != null && brandEntity.getOpportunity_types().size() > 0) {
                            for (OpportunityType opportunityType : brandEntity.getOpportunity_types()) {
                                opportunities.add(opportunityType);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            tvEmpty.setText("Aún no hay tipos de oportunidad para esta marca");
                            tvEmpty.setVisibility(View.VISIBLE);
                            statusId=true;
                            break;
                        }
                        statusId=true;
                        break;
                    }
                }
                if (!statusId){
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setText("Aún no hay tipos de oportunidad para esta marca");
                    tvEmpty.setVisibility(View.VISIBLE);
                }else{
                    setUpRecyclerViewOpportunities();
                }
                break;
            }case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),appController,this,this,this);
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
    public void onDestroy() {
        super.onDestroy();
        if (realm!=null)
            realm.close();
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
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    private void selectLocalDatas(){
        realm.executeTransactionAsync(realm -> {
            if (divisionEntity!=null) {
                RealmResults<OflineBrandsEntity> response = realm.where(OflineBrandsEntity.class)
                        .equalTo("id",idMarca)
                        .equalTo("divisionEntity.id", divisionEntity.getId())
                        .equalTo("divisionEntity.allow_reports", true).findAll();
                if (response.size()>0){
                    for (OflineBrandsEntity brand:response){
                        Gson gson= new Gson();
                        BrandEntity brandEntity = gson.fromJson(brand.getOpportunity_types(),BrandEntity.class);
                        if (brandEntity.getOpportunity_types().size()>0){
                            for (int i=0;i<brandEntity.getOpportunity_types().size();i++){
                                opportunities.add(brandEntity.getOpportunity_types().get(i));
                            }
                        }
                    }
                }
            }
        }, () -> {
            if (opportunities.size()>0)
                setUpRecyclerViewOpportunities();
            else {
                tvEmpty.setText("Aún no hay tipos de oportunidad para esta marca");
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }, error -> {
            tvEmpty.setText("Aún no hay tipos de oportunidad para esta marca");
            tvEmpty.setVisibility(View.VISIBLE);
        });
    }

    public void setUpRecyclerViewOpportunities(){
        recycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext);
        adapter = new OpportunitiesAdapter(mContext, getActivity(), opportunities, this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddOportunityType(OpportunityType opportunity) {
        ((ActivityOpportunity)mActivity).setOpportunityType(opportunity);
        ((ActivityOpportunity)mActivity).setDescription(null);
        ((ActivityOpportunity)mActivity).fragmentChanger(ActivityOpportunity.NEW_OPPORTUNITY);
    }
}
