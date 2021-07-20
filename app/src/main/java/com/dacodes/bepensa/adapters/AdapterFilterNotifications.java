package com.dacodes.bepensa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.CategoriesEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class AdapterFilterNotifications extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CategoriesEntity> items = new ArrayList<>();
    private onFilterNotification onCheckedId;

    public AdapterFilterNotifications(AdapterFilterNotifications.onFilterNotification onCheckedId,List<CategoriesEntity> list) {
        this.onCheckedId = onCheckedId;
        this.items = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HolderFilterNotifications(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.checkbox_filter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HolderFilterNotifications holder  = (HolderFilterNotifications)viewHolder;
        holder.bindView(i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HolderFilterNotifications extends RecyclerView.ViewHolder{
        @BindView(R.id.filter_cb) CheckBox filter;
        HolderFilterNotifications(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void bindView(int position){
            filter.setText(items.get(position).getName());
            filter.setChecked(items.get(position).isChecked());
            filter.setOnClickListener(view->onCheckedId.onFilterTheme(position));
        }
    }

    public interface onCheckedId{
        void onChekedIds(List<Integer> ids);
    }

    public interface onFilterNotification{
        void onFilterTheme(int position);
    }
}
