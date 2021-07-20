package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.fragments.FragmentDetailOportunityLocal;
import com.dacodes.bepensa.fragments.FragmentDetailOportunityNetwork;
import com.dacodes.bepensa.models.OpportunityDetailModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityDetailOportunity extends AppCompatActivity implements FragmentDetailOportunityNetwork.uploadMedias{

    private AppController appController;
    private Context mContext;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private OpportunityDetailModel opportunityDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_oportunity);

        ButterKnife.bind(this);
        mContext = getApplicationContext();
        appController=(AppController)getApplication();
        //Glide.with(mContext).load(R.drawable.login_background).apply(new RequestOptions().fitCenter()).into(binding.ivBanner);
        toolbar.setTitle("DETALLE DE OPORTUNIDAD");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void uploadMedias(boolean status) {
        if (status)
            checkData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void replaceOpportunityNetwork(int id, boolean goToChat, int filesMedia, int networkLocal){
        Bundle bundle = new Bundle();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        FragmentDetailOportunityNetwork fragment = new FragmentDetailOportunityNetwork();
        bundle.putSerializable("id", id);
        bundle.putBoolean("goToChat", goToChat);
        bundle.putSerializable("files_media",filesMedia);
        bundle.putSerializable("networkLocal",networkLocal);
        fragment.setArguments(bundle);
        transaction.replace(R.id.content, fragment);
        try {
            transaction.commit();
        }catch (Exception e){
            transaction.commitAllowingStateLoss();
        }
    }

    private void replaceOpportunityLocal(String keyhash,int network){
        Bundle bundle = new Bundle();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        bundle.putSerializable("keyhash",keyhash);
        bundle.putSerializable("networkLocal",network);
        FragmentDetailOportunityLocal fragment = new FragmentDetailOportunityLocal();
        fragment.setArguments(bundle);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    private void checkData(){
        int id = getIntent().getIntExtra("id", 0);
        boolean goToChat = getIntent().getBooleanExtra("goToChat", false);
        int files_media=getIntent().getIntExtra("files_media",0);
        int networkLocal=getIntent().getIntExtra("networkLocal",0);

        if (networkLocal==1){
            replaceOpportunityNetwork(id,goToChat,files_media,networkLocal);
        }else if (networkLocal==2){
            String keyhas=getIntent().getStringExtra("keyhash");
           // opportunityDetailModel = (OpportunityDetailModel) getIntent().getSerializableExtra("dataNetworLocal");
            if (keyhas!=null){
                replaceOpportunityLocal(keyhas,networkLocal);
            }else{
                onBackPressed();
            }
        }else{
            onBackPressed();
        }
    }
}
