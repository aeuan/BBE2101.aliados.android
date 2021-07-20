package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.DataStates;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;

public class DialogDivisions extends BottomSheetDialogFragment implements DCListeners.retrofitResponse {

    private View rootView;
    @BindView(R.id.pickerDialog) NumberPicker np;
    @BindView(R.id.btn_accept) Button btn_accept;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.content) RelativeLayout content;
    private Realm realm;

    private Context mContext;
    private selectDivision selectDivision;
    private AppController appController;
    private RetrofitWebServices retrofitServices;
    private Activity activity;
    private int isSelect=-1;
    private int idRegion = 0;
    private String selected;

    private List<?> divisions = new ArrayList();
    DivisionEntity divisionEntity;
    private List<DivisionEntity> items=new ArrayList<>();

    public DialogDivisions setActivity(Activity activity){
        this.activity=activity;
        return this;
    }

    public DialogDivisions setDivisions(selectDivision selectDivision){
        this.selectDivision=selectDivision;
        return this;
    }

    public DialogDivisions setIdRegion(int idRegion){
        this.idRegion = idRegion;
        return this;
    }

    public DialogDivisions setList(List<?> list){
        this.divisions=list;
        return this;
    }

    public DialogDivisions isSelect(int isSelect){
        this.isSelect=isSelect;
        return this;
    }

    public DialogDivisions setSelected(String isSelect){
        this.selected=isSelect;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController)getActivity().getApplication();
        mContext = getContext();
        //retrofitServices=new RetrofitWebServices(appController,"BottomSheet",mContext,this);
        realm=Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.picker_division,container,false);
        ButterKnife.bind(this,rootView);
        content.setVisibility(View.GONE);
        if (ValidateNetwork.getNetwork(mContext)){
            if (idRegion>0){
                new DCService().Build(this).setCode(GET_DIVISIONS)
                        .setCallResponse(new DCResponse<List<DivisionEntity>>() {
                            @Override
                            public Call<List<DivisionEntity>> getResponse(DCResponseInterface dcResponseInterface) {
                                return appController.getWebServices().getDivisionsState(appController.getAccessToken(),idRegion);
                            }
                        }).init();
            }else {
                new DCService().Build(this).setCode(GET_DIVISIONS)
                        .setCallResponse(new DCResponse<List<DivisionEntity>>() {
                            @Override
                            public Call<List<DivisionEntity>> getResponse(DCResponseInterface dcResponseInterface) {
                                return appController.getWebServices().getDivisions();
                            }
                        }).init();
            }
        }else{
            progressBar.setVisibility(View.GONE);
            getDivisionsLocal();
        }
        np.setOnKeyListener(null);
        return rootView;
    }

    @Override
    public void onFailure(int i, int i1, String s) {
        Toast.makeText(mContext,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(int i, String s, Object o) {
        switch (i){
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(o);
                content.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (divisions!=null){
                    addItemDivision(divisions);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm!=null)
            realm.close();
    }

    @OnClick(R.id.btn_accept)
    public void accept(){
        divisionEntity = items.get(np.getValue());
        selectDivision.idDivision(divisionEntity);

       /* Log.e("value",""+np.getValue());
        if (divisionEntity!=null){
            selectDivision.idDivision(divisionEntity);
        }else{
            if (isSelect==-1){
                try {
                    divisionEntity =items.get(0);
                    selectDivision.idDivision(divisionEntity);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }*/
        dismiss();
    }


    public interface selectDivision{
        void idDivision(DivisionEntity divisionEntity);
    }

    public void init(){
        if (activity instanceof AppCompatActivity){
            AppCompatActivity activity1=(AppCompatActivity)activity;
            show(activity1.getSupportFragmentManager().beginTransaction(),"DIvisions");
        }
    }

    private void addItemDivision(List<?> divisions){
        for (int i=0;i<divisions.size();i++){
            try {
                DivisionEntity div=(DivisionEntity)divisions.get(i);
                if (div.getId()!=4)
                    items.add(div);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (items.size()>0){
            addValuesPicker(items);
        }
    }

    private void getDivisionsLocal(){
        realm.executeTransaction(realm -> {
            RealmResults<OflineDivisionEntity> response= realm.where(OflineDivisionEntity.class)
                    .equalTo("allow_reports",true).findAll();

            RealmResults<OflineBrandsEntity> brands = realm.where(OflineBrandsEntity.class).findAll();

            if (response.size()==0 || brands.size()==0){
                dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(false)
                        .setMessage("Por favor, activa tu conexión a internet para obtener los datos necesarios para crear una oportunidad")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            dialog.dismiss();
                            ((AppCompatActivity)mContext).onBackPressed();
                        }).create().show();
            }else {
                for (OflineDivisionEntity ofline : response) {
                    if (ofline.getId() != 4) {
                        DivisionEntity division = new DivisionEntity();
                        division.setId(ofline.getId());
                        division.setName(ofline.getName());
                        division.setBrands_title(ofline.getBrands_title());
                        List<DataStates> list = Arrays.asList(new GsonBuilder().create().fromJson(ofline.getStates(), DataStates[].class));
                        division.setStates(list);
                        items.add(division);
                    }
                }
                if (items.size() > 0)
                    addValuesPicker(items);
            }
        });
    }

    private void addValuesPicker(List<DivisionEntity> divisions){
        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        final String[] values= new String[divisions.size()];
        for (int i=0;i<divisions.size();i++){
            try {
                values[i] = divisions.get(i).getName();
                if (selected.equalsIgnoreCase(divisions.get(i).getName())){
                    isSelect=i;
                    divisionEntity=divisions.get(i);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (isSelect == -1)
            isSelect = 0;
        np.setMinValue(0);
        np.setMaxValue(values.length-1);
        np.setValue(isSelect);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
       /* np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            try {
                divisionEntity=items.get(newVal);
            }catch (Exception e){
                e.printStackTrace();
            }
        });*/
    }

}
