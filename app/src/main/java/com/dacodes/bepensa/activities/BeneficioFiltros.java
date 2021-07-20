package com.dacodes.bepensa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.adapters.AdapterFilterNotifications;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.CategoriesEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dacodes.bepensa.AppController.getContext;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_CATEGORIES_FILTER;

public class BeneficioFiltros extends AppCompatActivity implements RetrofitWebServices.DefaultResponseListeners
        , AdapterFilterNotifications.onFilterNotification {

    @BindView(R.id.recycler)RecyclerView recyclerView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.filtros_btn)Button filtros_btn;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
   // private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    String LOG_TAG=BeneficioFiltros.class.getSimpleName();
    /*List<CategoriesEntity> items=new ArrayList<>();
    ArrayList<Integer> itemId=new ArrayList<>();*/

    private List<CategoriesEntity> items = new ArrayList<>();
    private AdapterFilterNotifications adapter = new AdapterFilterNotifications(this,items);
    private List<Integer> ids = new ArrayList<>();

    String id_filters="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficio_filtros);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("FILTROS");
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appController =(AppController)getApplication();
        mContext = getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
       // mAdapter = new RecyclerCategoriesFilterAdapter(mContext, this, this.items,this,this,appController);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        retrofitWebServices.getCategories(appController.getAccessToken());

        /*
        if (appController.getFiltersPromotions()==null){
            CategoriesEntity categoriesEntity= new CategoriesEntity();
            categoriesEntity.setId(0012);
            categoriesEntity.setName("Todas las categorias");
            categoriesEntity.setChecked(true);
            categoriesEntity.setEnable(true);
            items.add(0,categoriesEntity);
        }else{
            CategoriesEntity categoriesEntity= new CategoriesEntity();
            categoriesEntity.setId(0012);
            categoriesEntity.setName("Todas las categorias");
            categoriesEntity.setChecked(false);
            categoriesEntity.setEnable(true);
            items.add(0,categoriesEntity);
        }*/

    }

    @OnClick(R.id.filtros_btn)
    public void filtros(){
        checkStatusFilter();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        progressBar.setVisibility(View.GONE);
        switch (id){
            case GET_CATEGORIES_FILTER:
                ArrayList<CategoriesEntity> list = (ArrayList<CategoriesEntity> ) object;
                if (list != null && list.size()>0){
                    if (appController.getFiltersPromotions() != null) {
                        ArrayList<Integer> ids2 =new Gson().fromJson("["+appController.getFiltersPromotions()+"]",
                                new TypeToken<ArrayList<Integer>>() {}.getType());
                        for (int i = 0; i < list.size(); i++) {
                            for (int j =0;j<ids2.size();j++){
                                if (list.get(i).getId() == ids2.get(j))
                                    list.get(i).setChecked(true);
                            }
                        }
                        ids.addAll(ids2);
                        list.add(0,new CategoriesEntity("Todas las categorias",-1,false));
                        items.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        list.add(0,new CategoriesEntity("Todas las categorias",-1,true));
                        items.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
                /*
                List<? > list = ObjectToList.ObjectToList(object);
                List<CategoriesEntity> categoriesEntities = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    CategoriesEntity categoriesEntity = (CategoriesEntity)list.get(i);
                    categoriesEntities.add(categoriesEntity);
                }
                populateRecyclerView(categoriesEntities);*/
                break;
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkStatusFilter();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        checkStatusFilter();
    }

    public void checkStatusFilter(){


        if (ids.size()>0) {
            id_filters = new Gson().toJson(ids).replace("[","").replace("]","");
            appController.setFiltersPromotions(id_filters);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("intentTypeBack", 34);
            intent.putExtra("filters", id_filters);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }else{
            appController.setFiltersPromotions(null);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("intentTypeBack", 34);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    @Override
    public void onFilterTheme(int position) {
        ids.clear();
        if (position==0){
            if (!items.get(position).isChecked()){
                items.get(position).setChecked(true);
                for (int i=1;i<items.size();i++)
                    items.get(i).setChecked(false);
            }
        }else{
            items.get(0).setChecked(false);
            items.get(position).setChecked(!items.get(position).isChecked());
        }
        adapter.notifyDataSetChanged();
        for (int i=1;i<items.size();i++){
            if (items.get(i).isChecked())
                ids.add(items.get(i).getId());
        }

        if (ids.size()>0)
            appController.setFiltersPromotions(new Gson().toJson(ids));
        else
            appController.setFiltersPromotions(null);
    }
}
