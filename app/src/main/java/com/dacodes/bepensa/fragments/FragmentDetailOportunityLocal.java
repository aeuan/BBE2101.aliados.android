package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.google.gson.Gson;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;

import static android.view.View.GONE;


public class FragmentDetailOportunityLocal extends Fragment {

    @BindView(R.id.indicator) CircleIndicator indicator;
    @BindView(R.id.viewpager) ViewPager viewpager;
    @BindView(R.id.logo_iv) ImageView logo_iv;
    @BindView(R.id.date_tv) TextView date_tv;
    @BindView(R.id.opportunity_title_tv) TextView opportunity_title_tv;
    @BindView(R.id.opportunity_id_tv) TextView opportunity_id_tv;
    @BindView(R.id.opportunity_description_tv) TextView opportunity_description_tv;
    @BindView(R.id.division_static_tv) TextView division_static_tv;
    @BindView(R.id.division_tv) TextView division_tv;
    @BindView(R.id.static_map_iv) ImageView static_map_iv;
    /*@BindView(R.id.comentario_static_tv) TextView comentario_static_tv;
    @BindView(R.id.comentario_number_static_tv) TextView comentario_number_static_tv;
    @BindView(R.id.arrow_bt) ImageButton arrow_bt;*/
    //@BindView(R.id.rvMarcas) RecyclerView rvMarcas;
    @BindView(R.id.btUploadPendingMedia) Button btUploadPendingMedia;
    @BindView(R.id.ivPlaceHolder) ImageView ivPlaceHolder;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.prMedias) ProgressBar prMedias;
    //@BindView(R.id.chat_available)View chat_available;
    //@BindView(R.id.comentario_constraint_layout) ConstraintLayout comentario_constraint_layout;
    @BindView(R.id.contenido) LinearLayout contenido;
    @BindView(R.id.vChat)ImageView vChat;

    private AppController app;
    private Context mContext;
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mContext=getContext();
        app = (AppController) getActivity().getApplicationContext();
        mContext=getContext();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_detail_oportunity_network, container, false);
        ButterKnife.bind(this,view);
        vChat.setVisibility(GONE);
        Bundle bundle =getArguments();
        if (bundle!=null){
            int networkLocal=bundle.getInt("networkLocal",0);
            if (networkLocal==2) {
                String keyhash=bundle.getString("keyhash");
                if (keyhash!=null){
                    getOpportunity(keyhash);
                }
            }

        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm!=null)
            realm.close();
    }

    private void getOpportunity(String keyHash){
        hideViews();
        RealmResults<OflinePostOpportunity> persons = realm.where(OflinePostOpportunity.class)
                .equalTo("key_hash", keyHash).findAll();
        if (persons.size()>0){
            opportunity_description_tv.setText(persons.get(0).getDescription());

            RealmResults<OflineDivisionEntity> divisionEntities=realm.where(OflineDivisionEntity.class)
                    .equalTo("id",persons.get(0).getId_division()).findAll();
            if (divisionEntities.size()>0)
                division_tv.setText(divisionEntities.get(0).getName());

            /*
            RealmResults<OflineOpportunityType> oflineOpportunityTypes=realm.where(OflineOpportunityType.class)
                    .equalTo("id",persons.get(0).getId_oportunity()).findAll();
            if (oflineOpportunityTypes.size()>0)
                opportunity_title_tv.setText(oflineOpportunityTypes.get(0).getName());*/

            int idBrand=0;
            try {
                idBrand=Integer.parseInt(persons.get(0).getBrands());
            }catch (Exception e){
                Log.e("erro",""+e.getLocalizedMessage());
            }
            RealmResults<OflineBrandsEntity> brandsEntities=realm.where(OflineBrandsEntity.class)
                    .equalTo("id",idBrand).findAll();
            if (brandsEntities.size()>0){
                for (OflineBrandsEntity brand:brandsEntities){
                    Gson gson= new Gson();
                    BrandEntity brandEntity = gson.fromJson(brand.getOpportunity_types(),BrandEntity.class);
                    if (brandEntity.getOpportunity_types().size()>0){
                        for (int i=0;i<brandEntity.getOpportunity_types().size();i++){
                            if (persons.get(0).getId_oportunity()==brandEntity.getOpportunity_types().get(i).getId()){
                                opportunity_title_tv.setText(brandEntity.getOpportunity_types().get(0).getName());
                            }
                        }
                    }
                }
                Glide.with(this).load(brandsEntities.get(0).getLogo()).apply(new RequestOptions().circleCrop()).into(logo_iv);
            }

            RealmResults<OflineListMedia> listMedia = realm.where(OflineListMedia.class)
                    .equalTo("key_hash",persons.get(0).getKey_hash()).findAll();

            if (listMedia.size()>0){
                if (listMedia.get(0).getOflineMediaOpportunities().size()>0){
                    File file= new File(listMedia.get(0).getOflineMediaOpportunities().get(0).getPath());
                    Glide.with(this).load(file).into(ivPlaceHolder);
                }
            }else {
                Glide.with(this).load(R.drawable.logo).into(ivPlaceHolder);
            }
        }
    }

    private void hideViews(){
        //rvMarcas.setVisibility(GONE);
        date_tv.setVisibility(GONE);
        btUploadPendingMedia.setVisibility(GONE);
        opportunity_id_tv.setVisibility(View.INVISIBLE);
        contenido.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        //comentario_constraint_layout.setVisibility(View.GONE);
        static_map_iv.setVisibility(View.GONE);
    }
}
