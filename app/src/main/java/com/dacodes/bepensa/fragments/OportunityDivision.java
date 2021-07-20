package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.NewOportunity;
import com.dacodes.bepensa.adapters.OpportunitiesAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.Opportunity;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunityType;
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

public class OportunityDivision extends Fragment implements RetrofitWebServices.DefaultResponseListeners,
OpportunitiesAdapter.AddOpportunityType, OpportunitiesAdapter.AddDivisionType{

    private Context context;
    private int type;
    private View view;
    private AppController appController;
    private RetrofitWebServices retrofitWebServices;
    private String LOG_TAG = OportunityLocation.class.getSimpleName();

    @BindView(R.id.rvDivisions)
    RecyclerView rvDivisions;
    @BindView(R.id.division_title)
    TextView division_title;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Realm realm;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;

    private List<DivisionEntity> divisions = new ArrayList<>();
    private List<OpportunityType> opportunities = new ArrayList<>();
    private List<OpportunityType> opportunitiesLocal = new ArrayList<>();


    public OportunityDivision() {
        // Required empty public constructor
    }

    public static OportunityDivision newInstance(int type) {
        OportunityDivision fragment = new OportunityDivision();
        Bundle data = new Bundle();
        data.putInt("TYPE", type);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        appController = (AppController)getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);

        if(getArguments()!=null){
            type = getArguments().getInt("TYPE");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_oportunity_division, container, false);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this, view);
        return  view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (type){
            case 2: {
                if (ValidateNetwork.getNetwork(getContext())) {
                    retrofitWebServices.getOpportunitiesType(appController.getAccessToken(), ((NewOportunity) getActivity()).getId_division());
                    division_title.setVisibility(VISIBLE);
                    division_title.setText(((NewOportunity) getActivity()).getDivision_name());
                }else{
                    final int id_division=((NewOportunity)getActivity()).getId_division();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<OflineOpportunity> response= realm.where(OflineOpportunity.class)
                                    .equalTo("oflineOpportunityTypes.statesLocalEntity.id",id_division)
                                    .equalTo("oflineOpportunityTypes.statesLocalEntity.allow_reports",true)
                                    .findAll();
                            if (response.size()>0){
                                for (OflineOpportunity oflineOpportunity:response){
                                    for (OflineOpportunityType types:oflineOpportunity.getOflineOpportunityTypes()){
                                        OpportunityType opor = new OpportunityType();
                                        opor.setId(types.getId());
                                        opor.setName(types.getName());
                                        opor.setPoints(types.getPoints());
                                        opportunitiesLocal.add(opor);
                                    }
                                }

                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Log.i(LOG_TAG,"Complete");
                            setUpRecyclerViewOpportunitiesLocal();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Log.i(LOG_TAG,"error"+error.getLocalizedMessage());
                        }
                    });
                }
                break;
            }
        }

    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case RetrofitWebServices.GET_OPPORTUNITIES_TYPE:

                List<? > lista1 = ObjectToList.ObjectToList(object);
                for (Object entity1 : lista1) {
                    Opportunity opportunity = (Opportunity) entity1;
                    for(OpportunityType opportunityType : opportunity.getOpportunityTypes()){
                        opportunities.add(opportunityType);
                    }
                }
                setUpRecyclerViewOpportunities();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (id){
            case RetrofitWebServices.GET_OPPORTUNITIES_TYPE:
                Log.v("OPPORTINITIES_TYPES", message);
                break;
            case RetrofitWebServices.GET_DIVISIONS:

                break;
        }
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

    public void setUpRecyclerViewOpportunities(){
        rvDivisions.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        adapter = new OpportunitiesAdapter(context, getActivity(), opportunities, this);
        rvDivisions.setLayoutManager(linearLayoutManager);
        rvDivisions.setAdapter(adapter);
    }

    public void setUpRecyclerViewOpportunitiesLocal(){
        rvDivisions.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        adapter = new OpportunitiesAdapter(context, getActivity(), opportunitiesLocal, this);
        rvDivisions.setLayoutManager(linearLayoutManager);
        rvDivisions.setAdapter(adapter);
    }

    @Override
    public void onAddDivisionType(DivisionEntity division) {
        ((NewOportunity)getActivity()).setId_division(division.getId());
        ((NewOportunity)getActivity()).setDivision_name(division.getName());
        ((NewOportunity)getActivity()).fragmentChanger(1);
    }

    @Override
    public void onAddOportunityType(OpportunityType opportunity) {
        ((NewOportunity)getActivity()).setId_opportunity(opportunity.getId());
        ((NewOportunity)getActivity()).setOpportunity_name(opportunity.getName());
        ((NewOportunity)getActivity()).fragmentChanger(1);
    }
}
