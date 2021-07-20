package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.CategoriesEntity;

import java.util.ArrayList;
import java.util.List;


public class RecyclerCategoriesFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<CategoriesEntity> items;
    private customClickListener customClickListener;
    private AppController appController;
    private static final String LOG_TAG = RecyclerViewNotificacionesAdapter.class.getSimpleName();
    addCheked addCheked;
    deleteCheked deleteCheked;
    List<Integer> ids=new ArrayList<>();
    boolean status=false;
    public RecyclerCategoriesFilterAdapter(Context context,customClickListener customClickListener,
                                           List<CategoriesEntity> categoriesEntities,addCheked addCheked,
                                           deleteCheked deleteCheked,AppController appController) {
        this.mContext = context;
        this.items = categoriesEntities;
        this.customClickListener = customClickListener;
        this.addCheked=addCheked;
        this.deleteCheked=deleteCheked;
        this.appController=appController;
    }

    private class Category extends RecyclerView.ViewHolder implements View.OnClickListener{
        CheckBox checkBox;
        Category(View v) {
            super(v);
            v.setOnClickListener(this);
            checkBox =v.findViewById(R.id.filter_cb);
        }

        @Override
        public void onClick(View v) {
            customClickListener.onItemClickListener(items.get(getAdapterPosition()),checkBox);
        }
    }

    public interface customClickListener{
        void onItemClickListener(CategoriesEntity notificacionesEntity,CheckBox constraintLayout);
    }

    public interface addCheked{
        void addCheked(int id);
    }

    public interface deleteCheked{
        void deleteCheked(int id);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.checkbox_filter, parent, false);
        return new Category(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Category vhHI = (Category) viewHolder;
        if (appController.getFiltersPromotions()!=null) {
            getFilters(appController.getFiltersPromotions());
        }
        configureItem(vhHI, position);
    }

    private void configureItem(final Category vh, final int position) {
        vh.checkBox.setText(items.get(position).getName());

        if (ids.size()>0){
            checkFilters(vh,position);
        }
        if (items.get(0).isChecked()){
            if (position==0){
                showCategoriesEnable(vh,0);
            }else {
                showCategoriesDisable(vh, position);
            }
        }else{
            showCategoriesEnable(vh,position);
        }
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (vh.checkBox.isChecked()) {
                        if (position==0){
                            setChecked();
                            addCheked.addCheked(items.get(position).getId());
                            appController.setFiltersPromotions(null);
                            ids.clear();
                        }else {
                            addCheked.addCheked(items.get(position).getId());
                        }
                    } else {
                        if (position==0){
                            getChecked();
                            deleteCheked.deleteCheked(items.get(position).getId());
                        }else {
                            deleteCheked.deleteCheked(items.get(position).getId());
                        }
                    }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (items!=null){
            return items.size();
        }else{
            return 0;
        }

    }

    public void showCategoriesDisable(Category vh,int position){
        vh.checkBox.setChecked(items.get(position).isChecked());
        //vh.checkBox.setEnabled(items.get(position).isEnable());
        //vh.checkBox.setAlpha(0.03f);
    }
    public void showCategoriesEnable(Category vh,int position){
        vh.checkBox.setChecked(items.get(position).isChecked());
       // vh.checkBox.setEnabled(items.get(position).isEnable());

        //vh.checkBox.setTextColor(mContext.getResources().getColor(R.color.colorGrayText));
       // vh.checkBox.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
    }

    public void setChecked(){
        for(int i=1;i<items.size();i++){
            items.get(i).setEnable(false);
            items.get(i).setChecked(false);
            notifyItemChanged(i);
        }
        items.get(0).setEnable(true);
        items.get(0).setChecked(true);
    }

    public void getChecked(){
        for(int i=0;i<items.size();i++){
            items.get(i).setEnable(true);
            items.get(i).setChecked(false);
            notifyItemChanged(i);
        }
    }

    private void checkFilters(Category vh,int position){
            List<Integer> values = getFilters(appController.getFiltersPromotions());
            for (Integer id : values) {
                if (id == items.get(position).getId()) {
                    addCheked.addCheked(items.get(position).getId());
                    items.get(position).setChecked(true);
                }
            }
    }

    public List<Integer> getFilters(String filters){
        try {
            String [] dat=filters.split(",");
            if (dat.length>0){
                try {
                    for (String value: dat){
                        int id=Integer.parseInt(value);
                        ids.add(id);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try {
                    int id=Integer.parseInt(filters);
                    ids.add(id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
         e.printStackTrace();
        }
        return ids;
    }

}

