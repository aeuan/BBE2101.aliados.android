package com.dacodes.bepensa.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import retrofit2.Call;

public class FilterNotifications extends AppCompatActivity implements RetrofitWebServices.DefaultResponseListeners
        ,AdapterFilterNotifications.onFilterNotification {

    private List<CategoriesEntity> items = new ArrayList<>();
    private AdapterFilterNotifications adapter = new AdapterFilterNotifications(this,items);
    private RetrofitWebServices retrofitServices;
    private AppController appController;
    private String TAG = FilterNotifications.class.getSimpleName();
    private Call<List<CategoriesEntity>> filterThemes;
    private List<Integer> ids = new ArrayList<>();
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;
    @BindView(R.id.btFilter) Button btFilter;
    @BindView(R.id.swipe)SwipeRefreshLayout swipe;
    @BindView(R.id.progressBar)ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_notifications);
        ButterKnife.bind(this);
        appController = (AppController) getApplication();
        retrofitServices = new RetrofitWebServices(appController,TAG,this,this);
        swipe.setRefreshing(false);
        progressBar.setVisibility(View.VISIBLE);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        btFilter.setOnClickListener(v -> onBackPressed());
        rvNotifications.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvNotifications.setAdapter(adapter);
        swipe.setOnRefreshListener(()->{
            swipe.setRefreshing(true);
            progressBar.setVisibility(View.GONE);
            items.clear();
            adapter.notifyDataSetChanged();
            filterThemes = retrofitServices.getFilterNotifications(appController.getAccessToken());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterThemes = retrofitServices.getFilterNotifications(appController.getAccessToken());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (filterThemes != null && filterThemes.isExecuted())
            filterThemes.cancel();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        swipe.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        ArrayList<CategoriesEntity> list = (ArrayList<CategoriesEntity> ) object;
        if (list != null && list.size()>0){
            if (appController.getFilterThemes() != null) {
                ArrayList<Integer> ids =new Gson().fromJson(appController.getFilterThemes(),
                        new TypeToken<ArrayList<Integer>>() {}.getType());
                for (int i = 0; i < list.size(); i++) {
                    for (int j =0;j<ids.size();j++){
                        if (list.get(i).getId() == ids.get(j))
                            list.get(i).setChecked(true);
                    }
                }
                list.add(0,new CategoriesEntity("Todos los temas",-1,false));
                items.addAll(list);
                adapter.notifyDataSetChanged();
            }
            else {
                list.add(0,new CategoriesEntity("Todos los temas",-1,true));
                items.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        swipe.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        Log.e("FiltroError","status:"+status+"-Me:"+message);
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
            appController.setFilterThemes(new Gson().toJson(ids));
        else
            appController.setFilterThemes(null);
    }
}
