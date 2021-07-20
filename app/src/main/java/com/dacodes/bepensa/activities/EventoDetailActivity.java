package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.fragments.EventoDetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_EVENT_DETAIL;

public class EventoDetailActivity extends BaseActivity implements View.OnClickListener
        ,RetrofitWebServices.DefaultResponseListeners{
    private static final String LOG_TAG = EventoDetailActivity.class.getSimpleName();

    Context mContext;
    @BindView(R.id.toolbar)Toolbar toolbar;
    EventoDetailFragment fragment;
    EventosEntity eventosEntity;
    RetrofitWebServices rws;
    AppController app;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opportunity_detail_activity_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        app = (AppController) getApplication();
        rws = new RetrofitWebServices(app, LOG_TAG, mContext, this);
        toolbar.setTitle("DETALLE DEL EVENTO");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventosEntity = (EventosEntity) getIntent().getSerializableExtra("evento");
        if(eventosEntity==null){
            rws.getEventDetail(String.valueOf(getIntent().getIntExtra("eventId", 0)));
        }else{
            rws.getEventDetail(String.valueOf(eventosEntity.getId()));
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                        onBackPressed();
                        break;
        }
        return true;
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_EVENT_DETAIL:
                if (!isFinishing()) {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    fragment = new EventoDetailFragment();
                    Bundle bundle = new Bundle();
                    EventosEntity eventosEntity = (EventosEntity) object;
                    bundle.putSerializable("evento", eventosEntity);
                    fragment.setArguments(bundle);
                    transaction.add(R.id.content, fragment);
                    transaction.commit();
                }
                break;
            case DELETE_DEVICE:
                rws.logoutAction();
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

}
