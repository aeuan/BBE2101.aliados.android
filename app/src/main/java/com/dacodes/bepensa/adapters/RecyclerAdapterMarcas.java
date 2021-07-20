package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapterMarcas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BrandEntity> items;
    private brands brands;
    private optionBrand optionBrand;

    public RecyclerAdapterMarcas (Context context,List<BrandEntity> items,brands brands,optionBrand optionBrand){
        this.mContext=context;
        this.items=items;
        this.brands=brands;
        this.optionBrand=optionBrand;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1)
            return new AdapterAddNewMarcar(LayoutInflater.from(mContext).inflate(R.layout.item_add_new_brand,parent,false));
        else
            return new AdapterMarcas(LayoutInflater.from(mContext).inflate(R.layout.item_brand_tag,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 1:
                try {
                    AdapterAddNewMarcar adapterAddNewMarcar=(AdapterAddNewMarcar)holder;
                    addBrand(adapterAddNewMarcar,position);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    AdapterMarcas adapterMarcas=(AdapterMarcas)holder;
                    addNewBrand(adapterMarcas,position);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (items!=null)
            return items.size();
        else
            return 0;
    }

    public class AdapterMarcas extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.civBrand) CircleImageView civBrand;
        @BindView(R.id.tvBrand)TextView tvBrand;
        @BindView(R.id.ibDelete) ImageButton ibDelete;
        public AdapterMarcas(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            civBrand.setOnClickListener(this);
            tvBrand.setOnClickListener(this);
            ibDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ibDelete:
                    brands.removeBrand(getAdapterPosition());
                    optionBrand.enableOptionBrand(false,null);
                    break;
            }
        }
    }

    public class AdapterAddNewMarcar extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tvBrand) TextView tvBrand;
        @BindView(R.id.clBrand) ConstraintLayout clBrand;

        public AdapterAddNewMarcar(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvBrand.setOnClickListener(this);
            clBrand.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            brands.addBrand(getAdapterPosition());
        }
    }
    public interface brands{
        void addBrand(int position);
        void removeBrand(int position);
    }

    public interface optionBrand{
        void enableOptionBrand(boolean status,String title);
    }

    private void addBrand(AdapterAddNewMarcar adapter,int position){
        adapter.tvBrand.setText(items.get(position).getName());
    }

    private void addNewBrand(AdapterMarcas adapter,int position){
        adapter.tvBrand.setText(items.get(position).getName());
        Glide.with(mContext).load(items.get(position).getLogo()).thumbnail(0.1f).into(adapter.civBrand);
        if (items.get(position).getExtra_field_title()!=null && !TextUtils.isEmpty(items.get(position).getExtra_field_title())
                && !items.get(position).getExtra_field_title().equalsIgnoreCase("null"))
            optionBrand.enableOptionBrand(true,items.get(position).getExtra_field_title());
        else
            optionBrand.enableOptionBrand(false,null);
    }
}
