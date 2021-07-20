package com.dacodes.bepensa.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dacodes.bepensa.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class ShowMessages extends DialogFragment {
    private View rootView = null;
    private Context mContext = null;
    private Activity mActivity = null;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvMessage)TextView tvMessage;
    @BindView(R.id.btOk)
    Button btOk;
    private int number =0;

    public static ShowMessages getInstance(String name){
        ShowMessages showMessages = new ShowMessages();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        showMessages.setArguments(bundle);
        return showMessages;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        mActivity=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        LayoutInflater layoutInflater=requireActivity().getLayoutInflater();
        rootView=layoutInflater.inflate(R.layout.show_message,null);
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvTitle.setText(mActivity.getString(R.string.aliados_bepensa,getArguments().getString("name","")));
    }

    @OnClick(R.id.btOk)
    public void setOk(){
        dismiss();
    }
}
