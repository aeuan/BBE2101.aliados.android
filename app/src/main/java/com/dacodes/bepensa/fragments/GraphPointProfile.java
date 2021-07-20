package com.dacodes.bepensa.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.models.PointsResponseModel;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphPointProfile extends Fragment implements RetrofitWebServices.DefaultResponseListeners {


    AppController app ;
    Context mContext;
    @BindView(R.id.point_test_tv)TextView point_test_tv;
    @BindView(R.id.point_oportinities_tv)TextView point_oportinities_tv;
    @BindView(R.id.rela_test)RelativeLayout rela_test;
    @BindView(R.id.rela_point)RelativeLayout rela_point;
    @BindView(R.id.text_point_tv)TextView text_point_tv;
    @BindView(R.id.text2_point_tv)TextView text2_point_tv;
    @BindView(R.id.text_test_tv)TextView text_test_tv;
    @BindView(R.id.text2_test_tv)TextView text2_test_tv;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.layout_graph)RelativeLayout layout_graph;

    RetrofitWebServices retrofitWebServices;
    double height=0;
    int point_test=0;
    int point_opportunities=0;

    public GraphPointProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_graph_point_profile, container, false);
        ButterKnife.bind(this,view);
        app = (AppController)getActivity().getApplication();
        mContext = getContext();
        retrofitWebServices=new RetrofitWebServices(app,"Graph",mContext,this);
        retrofitWebServices.getPoints();
        rela_point.setVisibility(View.GONE);
        rela_test.setVisibility(View.GONE);
        point_test_tv.setVisibility(View.GONE);
        point_oportinities_tv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        layout_graph.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            height= layout_graph.getHeight();
        });

        return view;
    }

    public void heightOpportunity(double height){
        int heigthFin=(int)height;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rela_point.getLayoutParams();
        params.height=heigthFin;
        rela_point.setLayoutParams(params);
    }

    public void heigthTest(double height){
        int heigthFin=(int)height;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rela_test.getLayoutParams();
        params.height=heigthFin;
        rela_test.setLayoutParams(params);
    }

    public void size(){
        height=height-180;
        if (point_opportunities>point_test){
            double text_heigth=height/point_opportunities;
            loadDatas(text_heigth);
        }else if (point_test>point_opportunities){
            double text_heigth=height/point_test;
            loadDatas(text_heigth);
        }else{
            if (point_test==0 && point_opportunities==0){
                loadDatas(0);
            }
        }
    }

    public void loadDatas(double height){
        heightOpportunity(height*point_opportunities);
        heigthTest(height*point_test);
        point_oportinities_tv.setText(""+point_opportunities+"\npuntos");
        point_test_tv.setText(""+point_test+"\npuntos");
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        progressBar.setVisibility(View.GONE);
        PointsResponseModel responseModel = (PointsResponseModel) object;
        app.setPoints(responseModel.getPoints());
        app.setRankingPosition(String.valueOf(responseModel.getRank()));
        app.setPointAvailable1(responseModel.getPoints_available());
        point_opportunities=responseModel.getPoints_opportunities();
        point_test=responseModel.getPoints_surveys();
        rela_point.setVisibility(View.VISIBLE);
        rela_test.setVisibility(View.VISIBLE);
        point_test_tv.setVisibility(View.VISIBLE);
        point_oportinities_tv.setVisibility(View.VISIBLE);
        size();
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(View.GONE);
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
}
