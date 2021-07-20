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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.activities.ActivityOpportunity;
import com.dacodes.bepensa.adapters.BrandsAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
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
public class FragmentMarcas extends Fragment implements RetrofitWebServices.DefaultResponseListeners ,BrandsAdapter.AddBrand
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey {

    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.tvEmpty)TextView tvEmpty;
    @BindView(R.id.progress) ProgressBar progressBar;

    private String TAG=FragmentMarcas.class.getSimpleName();
    private RetrofitWebServices retrofitWebServices;
    private Context mContext;
    private Activity mActivity;
    private AppController appController;
    private DivisionEntity divisionEntity;

    private GridLayoutManager manager;
    private RecyclerView.Adapter adapter;
    private List<BrandEntity> brandsList = new ArrayList<>();
    private Realm realm;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;


    public FragmentMarcas() {
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
        if (divisionEntity!=null) {
            tvTitle.setText(divisionEntity.getName());
            progressBar.setVisibility(View.VISIBLE);
            if (ValidateNetwork.getNetwork(mContext))
                retrofitWebServices.getBrands(appController.getAccessToken(),divisionEntity.getId());
            else{
                selectBrandLocal();
            }
        }else
            ((ActivityOpportunity)mActivity).fragmentChanger(ActivityOpportunity.NEW_OPPORTUNITY);
        return view;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        progressBar.setVisibility(View.GONE);
        switch (id){
            case RetrofitWebServices.GET_BRANDS:
                List<? >list = ObjectToList.ObjectToList(object);
                for (Object entity : list) {
                    BrandEntity brandEntity = (BrandEntity) entity;
                    brandsList.add(brandEntity);
                }
                if (brandsList.size()>0)
                    setUpRecyclerView();
                else
                    tvEmpty.setVisibility(View.VISIBLE);
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm!=null)
            realm.close();
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
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
    public void onAddBrand(BrandEntity brand) {
        ((ActivityOpportunity)mActivity).setOpportunityType(null);
        ((ActivityOpportunity)mActivity).setIdMarca(brand.getId());
        List<BrandEntity> brandEntities=((ActivityOpportunity)mActivity).getBrandEntities();
        if (brandEntities!=null){
            if (brandEntities.size()>=1){
                brandEntities.clear();
                brandEntities.add(brand);
                brandEntities.add(new BrandEntity("Modificar",1));
                ((ActivityOpportunity)mActivity).setBrandEntities(brandEntities);
                ((ActivityOpportunity)mActivity).fragmentChanger(ActivityOpportunity.NEW_OPPORTUNITY);
            }
        }
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    private void selectBrandLocal(){
        progressBar.setVisibility(View.GONE);
        realm.executeTransactionAsync(realm -> {
            if (divisionEntity!=null) {
                RealmResults<OflineBrandsEntity> response = realm.where(OflineBrandsEntity.class)
                        .equalTo("divisionEntity.id", divisionEntity.getId())
                        .equalTo("divisionEntity.allow_reports", true).findAll();
                if (response.size()>0){
                    for (OflineBrandsEntity brand:response){
                        BrandEntity brandEntity = new Gson().fromJson(brand.getOpportunity_types(),BrandEntity.class);
                        /*BrandEntity brandEntity = new BrandEntity();
                        brandEntity.setId(brand.getId());
                        brandEntity.setName(brand.getName());
                        brandEntity.setLogo(brand.getLogo());
                        brandEntity.setExtra_field_title(brand.getExtra_field_title());*/
                        brandsList.add(brandEntity);
                    }
                }
            }
        }, () -> {
            if (brandsList.size()>0)
                setUpRecyclerView();
            else
                tvEmpty.setVisibility(View.VISIBLE);
        }, error -> {
            tvEmpty.setVisibility(View.VISIBLE);
        });
    }

    public void setUpRecyclerView(){
        recycler.setHasFixedSize(true);
        manager = new GridLayoutManager(mContext, 3);
        adapter = new BrandsAdapter(mContext, mActivity, brandsList, this);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
