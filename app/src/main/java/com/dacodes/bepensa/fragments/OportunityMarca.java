package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.NewOportunity;
import com.dacodes.bepensa.adapters.BrandsAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OportunityMarca extends Fragment implements RetrofitWebServices.DefaultResponseListeners, BrandsAdapter.AddBrand {


    private Context context;
    private View view;
    private AppController appController;
    private RetrofitWebServices retrofitWebServices;
    private String LOG_TAG = OportunityMarca.class.getSimpleName();

    private GridLayoutManager manager;
    private RecyclerView.Adapter adapter;
    private List<BrandEntity> brandsList = new ArrayList<>();
    private List<BrandEntity> brandsListLocal = new ArrayList<>();

    @BindView(R.id.rvBrands)
    RecyclerView rvBrands;
    @BindView(R.id.division_title)
    TextView division_title;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Realm realm;

    public OportunityMarca() {
        // Required empty public constructor
    }


    public static OportunityMarca newInstance() {
        OportunityMarca fragment = new OportunityMarca();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        appController = (AppController)getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_oportunity_marca, container, false);
        ButterKnife.bind(this, view);

        if (ValidateNetwork.getNetwork(getContext())) {
            retrofitWebServices.getBrands(appController.getAccessToken(), ((NewOportunity) getActivity()).getId_division());
        }else{
            selectLocalData();
        }
        division_title.setText(((NewOportunity)getActivity()).getDivision_name());
        return view;
    }

    public void selectLocalData(){
        final int id_division=((NewOportunity)getActivity()).getId_division();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<OflineBrandsEntity> response=realm.where(OflineBrandsEntity.class)
                        .equalTo("statesLocalEntity.id",id_division)
                        .equalTo("statesLocalEntity.allow_reports",true).findAll();
                if (response.size()>0){
                    for (OflineBrandsEntity brand:response){
                        BrandEntity brandEntity = new BrandEntity();
                        brandEntity.setId(brand.getId());
                        brandEntity.setName(brand.getName());
                        brandEntity.setLogo(brand.getLogo());
                        brandsListLocal.add(brandEntity);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                setRecyclerLocal();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.i(LOG_TAG,"error"+error.getLocalizedMessage());
            }
        });

    }


    @Override
    public void onResponse(int id, String message, Object object) {

        switch (id){
            case RetrofitWebServices.GET_BRANDS:
                List<? >list = ObjectToList.ObjectToList(object);
                for (Object entity : list) {
                    BrandEntity brandEntity = (BrandEntity) entity;
                    brandsList.add(brandEntity);
                }
                setUpRecyclerView();
                break;
        }

    }


    public void setUpRecyclerView(){
        rvBrands.setHasFixedSize(true);
        manager = new GridLayoutManager(context, 3);
        adapter = new BrandsAdapter(context, getActivity(), brandsList, this);
        rvBrands.setLayoutManager(manager);
        rvBrands.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setRecyclerLocal(){
        rvBrands.setHasFixedSize(true);
        manager = new GridLayoutManager(context, 3);
        adapter = new BrandsAdapter(context, getActivity(), brandsListLocal, this);
        rvBrands.setLayoutManager(manager);
        rvBrands.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onFailError(int id, int status, String message) {

    }

    @Override
    public void onWebServiceStart() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onWebServiceEnd() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onExpireToken() {

    }

    @Override
    public void onAddBrand(BrandEntity brand) {
        if (((NewOportunity) getActivity()).getBrands().size() >= 2) {
            ((NewOportunity) getActivity()).getBrands().set(0, brand);
            ((NewOportunity)getActivity()).setBrandsItems(""+brand.getId());
            ((NewOportunity) getActivity()).fragmentChanger(1);
        } else {
            ((NewOportunity) getActivity()).getBrands().add(0, brand);
            ((NewOportunity)getActivity()).setBrandsItems(""+brand.getId());
            ((NewOportunity) getActivity()).getBrands().set(1, new BrandEntity(0, "Modificar", ""));
            ((NewOportunity) getActivity()).fragmentChanger(1);
        }
    }
}
