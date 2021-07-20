package com.dacodes.bepensa.fragments;

import android.app.Activity;
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
import androidx.appcompat.app.AppCompatActivity;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.DataStates;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogStates extends BottomSheetDialogFragment{

    private View rootView;
    @BindView(R.id.pickerDialog) NumberPicker np;
    @BindView(R.id.btn_accept) Button btn_accept;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.content) RelativeLayout content;
    private List<DataStates> states = new ArrayList<>();

    private Context mContext;
    private selectState selectState;
    private Activity activity;
    private int isSelect=-1;

    private List<StatesLocalEntity> items = new ArrayList();
    StatesLocalEntity statesLocalEntity;

    public DialogStates setActivity(Activity activity){
        this.activity=activity;
        return this;
    }

    public DialogStates setDivisions(selectState selectDivision){
        this.selectState =selectDivision;
        return this;
    }

    public DialogStates isSelect(int isSelect){
        this.isSelect=isSelect;
        return this;
    }

    public DialogStates setStates(List<DataStates> list){
        this.states = list;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.picker_division,container,false);
        ButterKnife.bind(this,rootView);
        progressBar.setVisibility(View.GONE);
        np.setOnKeyListener(null);
        if (states.size()>0){
            for (DataStates data : states){
                StatesLocalEntity statesLocal = new StatesLocalEntity(data.getName(), data.getId());
                items.add(statesLocal);
            }
        }else {
            addStates();
        }
        addValuesPicker();
        return rootView;
    }

    @OnClick(R.id.btn_accept)
    public void accept(){
        if (statesLocalEntity !=null){
            selectState.idState(statesLocalEntity);
        }else{
            if (isSelect==-1){
                try {
                    statesLocalEntity =items.get(0);
                    selectState.idState(statesLocalEntity);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        dismiss();
    }

    public interface selectState{
        void idState(StatesLocalEntity divisionEntity);
    }

    private void addStates(){
        String[] states= getResources().getStringArray(R.array.name_location);
        for (int i=0;i<states.length;i++){
            if (i != 8) {
                StatesLocalEntity statesLocal = new StatesLocalEntity(states[i], i + 1);
                items.add(statesLocal);
            }
        }
    }

    public void init(){
        if (activity instanceof AppCompatActivity){
            AppCompatActivity activity1=(AppCompatActivity)activity;
            show(activity1.getSupportFragmentManager().beginTransaction(),"DIvisions");
        }
    }

    private void addValuesPicker(){
        final String[] values= new String[items.size()];
        for (int i=0;i<items.size();i++){
            values[i]=items.get(i).getName();
            if (isSelect==items.get(i).getId()){
                isSelect=i;
                statesLocalEntity=items.get(i);
            }
        }
        np.setMinValue(0);
        np.setMaxValue(values.length-1);
        if (isSelect<0)
            np.setValue(0);
        else
            np.setValue(isSelect);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            try {
                statesLocalEntity =items.get(newVal);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

}
