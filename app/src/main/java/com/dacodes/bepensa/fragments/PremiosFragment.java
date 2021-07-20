package com.dacodes.bepensa.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.HistoryAwards;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.adapters.PremiosAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.models.AwardsModel;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.BepensaUtils;
import com.dacodes.bepensa.utils.FontSingleton;
import com.dacodes.bepensa.utils.ObjectToList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dacodes.bepensa.api.RetrofitWebServices.CLAIM_AWARD;
import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_AWARDS;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_POINTS;

/**
 * Created by Eric on 09/03/17.
 *
 */

public class PremiosFragment extends Fragment implements PremiosAdapter.CustomMenuClickListener,
        RetrofitWebServices.DefaultResponseListeners ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{

    private static final String LOG_TAG = PremiosFragment.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvPremiosHeader)
    TextView tvPremiosHeader;
    @BindView(R.id.tvLogInPremios)
    TextView tvLogInPremios;
    @BindView(R.id.ivPremiosBack)
    View ivPremiosBack;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    int scrollSize;
    private ViewTreeObserver.OnScrollChangedListener scrollListener;
    private OnFragmentPremiosListener mListener;
    RetrofitWebServices retrofitWebServices;
    @BindView(R.id.viewBackground)
    View viewBackground;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.btn_award)Button btn_award;
    private List<AwardsModel> awardsModels = new ArrayList<>();
    private int displayedAwardPoints;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    public PremiosFragment() {
        // Required empty public constructor
    }

    Context mContext;
    AppController appController;
    PremiosAdapter premiosAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_premios, container, false);
        mContext = getActivity().getApplicationContext();
        appController = (AppController) getActivity().getApplication();
        ButterKnife.bind(this,rootView);
        tvPremiosHeader.setTypeface(FontSingleton.getInstance().getTrade18());
        scrollSize = BepensaUtils.dpToPixels(65, mContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                gridLayoutManager.getOrientation());
        try {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable =  mContext.getDrawable(R.drawable.ic_divider);
            }else{
                drawable =  getResources().getDrawable(R.drawable.ic_divider);
            }
            if(drawable!=null){
                dividerItemDecoration.setDrawable(drawable);
            }
        }catch (Exception e){
            Log.e("ERROR: ",e.getMessage());
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        /*if(appController.getPoints()==null){
            appController.setPoints("0");
        }*/
        premiosAdapter = new PremiosAdapter(mContext, PremiosFragment.this, appController.getPoints());
        recyclerView.setAdapter(premiosAdapter);
        tvLogInPremios.setVisibility(View.GONE);
        scrollListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollX = nestedScrollView.getScrollX(); //for horizontalScrollView
                int scrollY = nestedScrollView.getScrollY();
                float alpha = 0;
                if(scrollY>scrollSize){
                    scrollY = scrollSize;
                }
                alpha=1f-scrollY/(scrollSize*1.0f);
                tvPremiosHeader.setAlpha(alpha);
            }
        };

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(scrollListener);

        displayLoaders();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        retrofitWebServices.getAwards(appController.getAccessToken());

        return rootView;
    }

    @OnClick(R.id.btn_award)
    public void award(){
        Intent intent = new Intent(mContext,HistoryAwards.class);
        startActivity(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentPremiosListener) {
            mListener = (OnFragmentPremiosListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentPremiosListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_AWARDS:
                progressbar.setVisibility(View.GONE);
                awardsModels = new ArrayList<>();
                List<? > list = ObjectToList.ObjectToList(object);
                for (Object entity : list) {
                    AwardsModel awardsModel = (AwardsModel) entity;
                    awardsModels.add(awardsModel);
                }
                for(AwardsModel current : awardsModels){
                    premiosAdapter.addAward(current);
                }
                premiosAdapter.notifyDataSetChanged();
                hideLoaders();
                break;
            case CLAIM_AWARD:
                progressbar.setVisibility(View.GONE);
                Toast.makeText(mContext, "Premio cambiado",Toast.LENGTH_SHORT).show();
                int points = appController.getPoints();
                points = points - displayedAwardPoints;
                appController.setPoints(points);
                displayedAwardPoints = 0;
                retrofitWebServices.getPoints();
                break;
            case GET_POINTS:
                progressbar.setVisibility(View.GONE);
                PointsResponseModel responseModel= (PointsResponseModel) object;
                appController.setPoints(responseModel.getPoints());
                appController.setRankingPosition(String.valueOf(responseModel.getRank()));
                appController.setPointAvailable1(responseModel.getPoints_available());
                ((MainActivity)getActivity()).refreshPoint();
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }


    @Override
    public void onFailError(int id, int status, String message) {
        progressbar.setVisibility(View.GONE);
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

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    @Override
    public void onCustomMenuItemClick(int position, int type, int id, final AwardsModel current) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(current.getName());
        builder.setMessage("¿Estás seguro de canjear este premio?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                retrofitWebServices.claimAwards(appController.getAccessToken(), current.getId());
                displayedAwardPoints = current.getPoints();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

    }


    public interface OnFragmentPremiosListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }


    public void hideLoaders(){
        viewBackground.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
    }

    public void displayLoaders(){
        viewBackground.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
    }
}
