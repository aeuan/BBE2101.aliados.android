package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dacodes.bepensa.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogMedias extends BottomSheetDialogFragment{

    private View rootView;
    @BindView(R.id.btFoto) Button btFoto;
    @BindView(R.id.btVideo) Button btVideo;

    private Context mContext;
    private pickerMedias pickerMedias;
    private Activity activity;


    public DialogMedias setActivity(Activity activity){
        this.activity=activity;
        return this;
    }

    public DialogMedias setListenerMedias(pickerMedias pickerMedias){
        this.pickerMedias =pickerMedias;
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
        rootView=inflater.inflate(R.layout.item_dialog_medias,container,false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }


    @OnClick(R.id.btVideo)
    public void btVideo(){
        pickerMedias.idMedia(2);
        dismiss();
    }

    @OnClick(R.id.btFoto)
    public void btFoto(){
        pickerMedias.idMedia(1);
        dismiss();
    }

    public interface pickerMedias{
        void idMedia(int position);
    }


    public void init(){
        if (activity instanceof AppCompatActivity){
            AppCompatActivity activity1=(AppCompatActivity)activity;
            show(activity1.getSupportFragmentManager().beginTransaction(),"DIvisions");
        }
    }


}
