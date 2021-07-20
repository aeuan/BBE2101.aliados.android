package com.dacodes.bepensa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;

@SuppressLint("ValidFragment")
public class BottomSheetDivisions extends BottomSheetDialogFragment implements RetrofitWebServices.DefaultResponseListeners {

    private Context mContext;
    private selectDivision selectDivision;
    private AppController appController;
    private RetrofitWebServices retrofitServices;
    private boolean isLoguin = false;

    @BindView(R.id.pickerDialog)
    NumberPicker np;
    @BindView(R.id.btn_accept)
    Button btn_accept;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.content)
    RelativeLayout content;

    private List<?> divisions = new ArrayList();

    DivisionEntity divisionEntity;

    @SuppressLint("ValidFragment")
    public BottomSheetDivisions(selectDivision selectDivision){
        this.selectDivision=selectDivision;
    }

    @SuppressLint("ValidFragment")
    public BottomSheetDivisions(selectDivision selectDivision, boolean isLoguin){
        this.selectDivision=selectDivision;
        this.isLoguin = isLoguin;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController)getActivity().getApplication();
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picker_division,container,false);
        ButterKnife.bind(this,view);
        retrofitServices=new RetrofitWebServices(appController,"BottomSheet",mContext,this);
        Bundle bundle=getArguments();
        if (bundle!=null){
            progressBar.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (bundle.getStringArrayList("divisions")!=null){
                divisions=bundle.getStringArrayList("divisions");
                /*for (int i=0;i<divisions.size();i++){
                    DivisionEntity divisionEntity=(DivisionEntity)divisions.get(i);
                    if (divisionEntity.getId()==4)
                        divisions.remove(i);
                }*/
                addValuesPicker(divisions);
            }
        }else{
            retrofitServices.getDivisions();
            content.setVisibility(View.GONE);
        }
        return view;
    }

    @OnClick(R.id.btn_accept)
    public void accept(){
        if (divisionEntity!=null){
            selectDivision.idDivision(divisionEntity);
        }else{
            try {
                divisionEntity = (DivisionEntity) divisions.get(0);
                selectDivision.idDivision(divisionEntity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        dismiss();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                content.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (divisions!=null){
                    if (isLoguin) {
                        List<DivisionEntity> list = new ArrayList<>();
                        for (int i = 0; i < divisions.size(); i++) {
                            DivisionEntity division = (DivisionEntity)divisions.get(i);
                            if (division.isAllow_login())
                                list.add(division);
                        }
                        divisions = list;
                    }else
                        addValuesPicker(divisions);
                }
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

    public interface selectDivision{
        void idDivision(DivisionEntity divisionEntity);
    }

    public void addValuesPicker(List<?> divisions){
        final String[] values= new String[divisions.size()];
        for (int i=0;i<divisions.size();i++){
            try {
                DivisionEntity divisionEntity =(DivisionEntity)divisions.get(i);
                //if (divisionEntity.getId()!=4)*/
                    values[i]=divisionEntity.getName();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        np.setMinValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                try {
                    divisionEntity=(DivisionEntity) divisions.get(newVal);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
