package com.dacodes.bepensa.activities;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.fragments.OpportunityDetailFragment;
import com.dacodes.bepensa.fragments.RegisterFragmentOne;
import com.dacodes.bepensa.fragments.RegisterFragmentTwo;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.service.UploadMediaFilesOpportunity;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunityDetailActivity extends AppCompatActivity implements View.OnClickListener,
        RegisterFragmentOne.OnFragmentInteractionListener, RegisterFragmentTwo.OnFragmentInteractionListener{
    private static final String LOG_TAG = OpportunityDetailActivity.class.getSimpleName();
    Context mContext;
    CountDownTimer countDownTimer;
    @BindView(R.id.toolbar)Toolbar toolbar;

    //    Ends facebook
    OpportunityDetailFragment fragment;
    OpportunityDetailModel opportunityDetailModel;
    UploadMediaFilesOpportunity mService;
    AppController appController;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opportunity_detail_activity_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        appController=(AppController)getApplication();
        //Glide.with(mContext).load(R.drawable.login_background).apply(new RequestOptions().fitCenter()).into(binding.ivBanner);
        toolbar.setTitle("DETALLE DE OPORTUNIDAD");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appController.setIdOpportunityMedias(0);

        int id = getIntent().getIntExtra("id", 0);
        boolean goToChat = getIntent().getBooleanExtra("goToChat", false);
        int files_media=getIntent().getIntExtra("files_media",0);
        int networkLocal=getIntent().getIntExtra("networkLocal",0);
        if (networkLocal==2){
            opportunityDetailModel = (OpportunityDetailModel) getIntent().getSerializableExtra("dataNetworLocal");
        }
        Intent intent = getIntent();
        if (intent!=null){
            if (intent.getSerializableExtra("list_media")!=null){
                int idme = getIntent().getIntExtra("id", 0);
                appController.setIdOpportunityMedias(idme);
                List<MediasFilesEntity> mediasFilesEntities = (List<MediasFilesEntity>) intent.getSerializableExtra("list_media");
                Intent intentService = new Intent(this, UploadMediaFilesOpportunity.class);
                intentService.putExtra("media", (Serializable) mediasFilesEntities);
                intentService.putExtra("id",""+id);
                bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
                statusService();
            }
        }

        detailOpprtunity(id,goToChat,files_media,networkLocal);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void statusService(){
        countDownTimer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isMyServiceRunning(UploadMediaFilesOpportunity.class)){
                    if (mBound) {
                        if (mService.isStatus()) {
                          /*  int id = getIntent().getIntExtra("id", 0);
                            boolean goToChat = getIntent().getBooleanExtra("goToChat", false);
                            int networkLocal = getIntent().getIntExtra("networkLocal", 0);

                            Bundle bundle = new Bundle();
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            OpportunityDetailFragment fragment = new OpportunityDetailFragment();
                            bundle.putSerializable("id", id);
                            bundle.putBoolean("goToChat", goToChat);
                            bundle.putSerializable("files_media",0);
                            bundle.putSerializable("networkLocal",networkLocal);
                            if (opportunityDetailModel!=null){
                                bundle.putSerializable("dataNetworLocal",opportunityDetailModel);
                            }
                            fragment.setArguments(bundle);
                            transaction.replace(R.id.content, fragment);
                            transaction.commit();*/
                            refreshFragment();
                            mService.stop();
                            unbindService(mConnection);
                            mBound = false;
                        }
                    }else{
                        Log.i("mBound","false");
                    }
                }else{
                    appController.setIdOpportunityMedias(0);
                    stopCurrentTime();
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void stopCurrentTime(){
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    private void refreshFragment(){
        int id = getIntent().getIntExtra("id", 0);
        boolean goToChat = getIntent().getBooleanExtra("goToChat", false);
        int networkLocal = getIntent().getIntExtra("networkLocal", 0);

        Bundle bundle = new Bundle();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OpportunityDetailFragment fragment = new OpportunityDetailFragment();
        bundle.putSerializable("id", id);
        bundle.putBoolean("goToChat", goToChat);
        bundle.putSerializable("files_media",0);
        bundle.putSerializable("networkLocal",networkLocal);
        if (opportunityDetailModel!=null){
            bundle.putSerializable("dataNetworLocal",opportunityDetailModel);
        }
        fragment.setArguments(bundle);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    public void detailOpprtunity(int id, boolean goToChat, int files_media, int networkLocal){
        Bundle bundle = new Bundle();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        OpportunityDetailFragment fragment = new OpportunityDetailFragment();
        bundle.putSerializable("id", id);
        bundle.putBoolean("goToChat", goToChat);
        bundle.putSerializable("files_media",files_media);
        bundle.putSerializable("networkLocal",networkLocal);
        if (opportunityDetailModel!=null){
            bundle.putSerializable("dataNetworLocal",opportunityDetailModel);
        }
        fragment.setArguments(bundle);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            UploadMediaFilesOpportunity.LocalBinder binder = (UploadMediaFilesOpportunity.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            try {
                refreshFragment();
            }catch (Exception e){
                Log.e("onServiceDisconnected",""+e.getLocalizedMessage());
            }
        }
    };

    @Override
    public void onFragmentInteraction(String username, String division) {

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(mContext,MainActivity.class);
        intent.putExtra("intentTypeBack",35);
        intent.putExtra("backOpportunity",1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                        Intent intent=new Intent(mContext,MainActivity.class);
                        intent.putExtra("intentTypeBack",35);
                        intent.putExtra("backOpportunity",1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
        }
        return true;
    }
}
