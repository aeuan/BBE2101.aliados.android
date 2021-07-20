package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.CategoriesEntity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterTestPageThree extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<CategoriesEntity> items;
    private static final String LOG_TAG = RecyclerViewNotificacionesAdapter.class.getSimpleName();
    addCheked addCheked;
    deleteCheked deleteCheked;

    public RecyclerAdapterTestPageThree(Context context,List<CategoriesEntity> categoriesEntities
            ,addCheked addCheked,deleteCheked deleteCheked) {
        this.mContext = context;
        this.items = categoriesEntities;
        this.addCheked=addCheked;
        this.deleteCheked=deleteCheked;

    }

    private class Category extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        Category(View v) {
            super(v);
            checkBox =v.findViewById(R.id.filter_cb);
        }

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
        int viewType = viewHolder.getItemViewType();
        Category vhHI = (Category) viewHolder;
        configureItem(vhHI, position);
    }

    private void configureItem(final Category vh, final int position) {
        vh.checkBox.setText(""+options().get(position)+items.get(position).getName());
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.checkBox.isChecked()){
                    addCheked.addCheked(items.get(position).getId());
                }else{
                    deleteCheked.deleteCheked(items.get(position).getId());
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

    public List<String> options(){
        List<String> options=new ArrayList<>();
        options.add("a)");
        options.add("b)");
        options.add("c)");
        options.add("d)");
        options.add("e)");
        options.add("f)");
        options.add("g)");
        options.add("h)");
        options.add("i)");
        options.add("j)");

        return options;
    }
}
