package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class BrandsTagsAdapter extends RecyclerView.Adapter<BrandsTagsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BrandEntity> brands = new ArrayList<>();
    private AddNewBrand addNewBrand;
    private boolean isRemovable = true;


    public BrandsTagsAdapter(Context context, ArrayList<BrandEntity> brands, AddNewBrand addNewBrand) {
        this.context = context;
        this.brands = brands;
        this.addNewBrand = addNewBrand;
    }

    public BrandsTagsAdapter(Context context, ArrayList<BrandEntity> brands, AddNewBrand addNewBrand, boolean isRemovable) {
        this.context = context;
        this.brands = brands;
        this.addNewBrand = addNewBrand;
        this.isRemovable = isRemovable;
    }

    public interface AddNewBrand{
        void onAddBrand();
        void onDeleteBrand(int position);
    }

    @NonNull
    @Override
    public BrandsTagsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_brand_tag, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull BrandsTagsAdapter.ViewHolder holder, final int position) {
        BrandEntity brand = (BrandEntity) brands.get(position);
        if(brand.getId()!=0){
            holder.tvBrand.setText(brand.getName());
            holder.tvBrand.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.constraintLayout.setBackgroundResource(R.drawable.item_marca_background);
            holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewBrand.onDeleteBrand(position);
                    notifyDataSetChanged();
                }
            });
        }else {
            holder.tvBrand.setText(brand.getName());
            holder.tvBrand.setTextColor(Color.GRAY);
            holder.constraintLayout.setBackgroundResource(R.drawable.item_marca_background_default);
            holder.ibDelete.setVisibility(GONE);
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        addNewBrand.onAddBrand();
                }
            });
        }

        Glide.with(context)
                .load(""+brand.getLogo())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image))
                .into(holder.civBrand);
        if(!isRemovable){
            holder.ibDelete.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvBrand)
        TextView tvBrand;
        @BindView(R.id.ibDelete)
        ImageButton ibDelete;
        @BindView(R.id.clBrandContainer)
        ConstraintLayout constraintLayout;
        @BindView(R.id.civBrand)
        CircleImageView civBrand;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
