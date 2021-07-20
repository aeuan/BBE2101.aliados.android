package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.HomeCategoriesEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapterHomeSingle extends
        RecyclerView.Adapter<RecyclerAdapterHomeSingle.ViewHolder> {

    private Context context;
    private List<BrandEntity> items;
    private ItemClickListener listener;
    private Activity activity;
    private String sectionTitle;

    public interface ItemClickListener {
        void onClick(View view, int position, HomeCategoriesEntity items);
    }

    public RecyclerAdapterHomeSingle(Activity activity,
                                     Context context, String sectionTitle,
                                     List<BrandEntity> items,
                                     ItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.sectionTitle = sectionTitle;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.civBrand) ImageView civBrand;
        @BindView(R.id.tvBrand) TextView tvBrand;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_brand, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        Glide.with(context).load(items.get(i).getLogo()).into(viewHolder.civBrand);
        viewHolder.tvBrand.setText(items.get(i).getName());
    }
}