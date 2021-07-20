package com.dacodes.bepensa;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.models.PointsResponseModel;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGraph extends Fragment implements RetrofitWebServices.DefaultResponseListeners{

    @BindView(R.id.btTest) TextView btTest;
    @BindView(R.id.btOportunity)TextView btOportunity;
    @BindView(R.id.tvTest) TextView tvTest;
    @BindView(R.id.tvOportunity)TextView tvOportunity;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.graph) ConstraintLayout graph;

    private AppController appController;
    private Context mContext;
    private RetrofitWebServices retrofitServices;
    private View rootView;

    int point_test=0;
    int point_opportunities=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController=(AppController)getActivity().getApplication();
        mContext=getContext();
        retrofitServices=new RetrofitWebServices(appController,"",mContext,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_graph, container, false);
        ButterKnife.bind(this,rootView);
        retrofitServices.getPoints();

        return rootView;
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        try {
            progressBar.setVisibility(View.GONE);
            PointsResponseModel responseModel = (PointsResponseModel) object;
            appController.setPoints(responseModel.getPoints());
            appController.setRankingPosition(String.valueOf(responseModel.getRank()));
            appController.setPointAvailable1(responseModel.getPoints_available());
            point_opportunities=responseModel.getPoints_opportunities();
            point_test=responseModel.getPoints_surveys();
            size();
        }catch (Exception e){
            e.printStackTrace();
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

    public void size(){
        if (point_opportunities>point_test){
            float percent = (float) (0.75/point_opportunities);
            loadPercent(R.id.btOportunity,percent*point_opportunities);
            loadPercent(R.id.btTest,percent*point_test);
        }else if (point_test>point_opportunities){
            float percent = (float) (0.75/point_test);
            loadPercent(R.id.btOportunity,percent*point_opportunities);
            loadPercent(R.id.btTest,percent*point_test);
        }else if (point_opportunities==point_test && point_test>0){
            float percent = (float) (0.75/point_opportunities);
            loadPercent(R.id.btOportunity,percent*point_opportunities);
            loadPercent(R.id.btTest,percent*point_test);
        } else{
            if (point_test==0 && point_opportunities==0){
                loadPercent(R.id.btOportunity,0.03f);
                loadPercent(R.id.btTest,0.03f);
            }
        }
        loadDatas();
    }

    public void loadDatas(){
        tvOportunity.setText(""+point_opportunities+"\npuntos");
        tvTest.setText(""+point_test+"\npuntos");
    }

    private void loadPercent(int id,float percent){
        ConstraintSet cs =new ConstraintSet();
        ConstraintLayout layout=(ConstraintLayout)rootView.findViewById(R.id.graph);
        cs.clone(layout);
        cs.constrainPercentHeight(id,percent);
        cs.applyTo(layout);
    }

}
