package com.dacodes.bepensa.adapters;

/**
 * Created by marioguillermo on 3/31/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.models.AwardsModel;
import com.dacodes.bepensa.utils.FontSingleton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class PremiosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<AwardsModel> items;
    private CustomMenuClickListener listener;
    private int mPuntos = 0;
    private static final String tag = "MenuAdapter";
    public static final int WITH_LOGO = 0;
    public static final int WITHOUT_LOGO = 1;

    public interface CustomMenuClickListener {
        void onCustomMenuItemClick(int position, int type, int id, AwardsModel current);
    }

    public PremiosAdapter(Context context, CustomMenuClickListener listener, int puntos) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.listener = listener;
        mPuntos = puntos;
    }


    private class WithLogo extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ivItem, ivCompanyLogo;
        TextView tvStock, tvItemTitle, tvPuntos;

        WithLogo(View v) {
            super(v);
            v.setOnClickListener(this);
            ivItem = (CircleImageView)v.findViewById(R.id.ivItem);
            ivCompanyLogo = (CircleImageView)v.findViewById(R.id.ivCompanyLogo);
            tvStock = (TextView)v.findViewById(R.id.tvStock);
            tvItemTitle = (TextView)v.findViewById(R.id.tvItemTitle);
            tvPuntos = (TextView)v.findViewById(R.id.tvPuntos);
        }

        @Override
        public void onClick(View v) {
            if(items.get(getAdapterPosition()).isActive()) {
                listener.onCustomMenuItemClick(getAdapterPosition(), 20, 30, items.get(getAdapterPosition()));
            }else{
                Toast.makeText(mContext, "Aún no cuentas con los puntos necesarios para canjear este premio.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class WithOutLogo extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ivItem, ivCompanyLogo;
        TextView tvStock, tvItemTitle, tvPuntos;

        WithOutLogo(View v) {
            super(v);
            v.setOnClickListener(this);
            ivItem = (CircleImageView)v.findViewById(R.id.ivItem);
            ivCompanyLogo = (CircleImageView)v.findViewById(R.id.ivCompanyLogo);
            tvStock = (TextView)v.findViewById(R.id.tvStock);
            tvItemTitle = (TextView)v.findViewById(R.id.tvItemTitle);
            tvPuntos = (TextView)v.findViewById(R.id.tvPuntos);
        }

        @Override
        public void onClick(View v) {
            if(items.get(getAdapterPosition()).isActive()){
                listener.onCustomMenuItemClick(getAdapterPosition(),20,30, items.get(getAdapterPosition()));
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case WITH_LOGO:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.premios_layout, parent, false);
                return new WithLogo(v);
            case WITHOUT_LOGO:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.premios_layout, parent, false);
                return new WithOutLogo(v);
            default:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.premios_layout, parent, false);
                return new WithOutLogo(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case WITH_LOGO:
                WithLogo vhBN = (WithLogo) viewHolder;
                configureNewNotification(vhBN, position);
                break;
        }


    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return WITH_LOGO;
    }

    private void configureNewNotification(final WithLogo vh, final int position) {
        vh.ivCompanyLogo.setVisibility(View.GONE);
        vh.tvItemTitle.setTypeface(FontSingleton.getInstance().getTrade20());
        vh.tvPuntos.setTypeface(FontSingleton.getInstance().getTrade20());
        vh.tvStock.setTypeface(FontSingleton.getInstance().getTrade18());
        vh.tvItemTitle.setText(items.get(position).getName());
        String quantity = "blank";
        vh.tvStock.setText(quantity);
        String puntos = String.valueOf(items.get(position).getPoints()) + "\n";
        vh.tvPuntos.setText(puntos);

        if(items.get(position).getAvailability() == 0 || items.get(position).getPoints()>mPuntos){
            vh.tvPuntos.setBackgroundResource(R.drawable.background_circle_grey);
        }else{
            vh.tvPuntos.setBackgroundResource(R.drawable.background_red);
        }
        Glide.with(mContext).load(items.get(position).getImage()).apply(new RequestOptions().circleCrop()).into(vh.ivItem);

        if(items.get(position).isActive()){
            vh.tvPuntos.setBackgroundResource(R.drawable.background_red);
        }else{
            vh.tvPuntos.setBackgroundResource(R.drawable.background_circle_grey);
        }



    }


    @Override
    public int getItemCount() {
        if (items!=null){
            return items.size();
        }else{
            return 0;
        }

    }

    public void addAward(AwardsModel awardsModel) {
        items.add(awardsModel);
    }

    public ArrayList<AwardsModel> getItems(){
        return items;
    }

}


