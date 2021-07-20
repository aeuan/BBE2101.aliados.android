package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<BrandEntity> brandsList =new ArrayList<>();
    private AddBrand addBrand;

    public BrandsAdapter(Context context, Activity activity, List<BrandEntity> brandsList, AddBrand addBrand) {
        this.context = context;
        this.brandsList = brandsList;
        this.addBrand = addBrand;
        this.activity = activity;

    }

    public interface AddBrand{
        void onAddBrand(BrandEntity brand);
    }

    @NonNull
    @Override
    public BrandsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.ViewHolder holder, final int position) {

        Glide.with(context)
                .load(""+brandsList.get(position).getLogo())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image))
                .into(holder.civBrand);
        holder.tvBrand.setText(brandsList.get(position).getName());

        holder.civBrand.setOnClickListener(view -> addBrand.onAddBrand(brandsList.get(position)));


    }

    @Override
    public int getItemCount() {
        return brandsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civBrand)
        CircleImageView civBrand;
        @BindView(R.id.tvBrand)
        TextView tvBrand;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
