package com.dacodes.bepensa.adapters;

/**

 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;

import java.util.ArrayList;


public class RecyclerViewBeneficiosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<PromotionDataEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewBeneficiosAdapter.class.getSimpleName();

    public RecyclerViewBeneficiosAdapter(Context context, customClickListener customClickListener, ArrayList<PromotionDataEntity> beneficioEntities) {
        this.mContext = context;
        this.items = beneficioEntities;
        this.customClickListener = customClickListener;
    }

    private class BeneficioItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  beneficio_title_tv, beneficio_description_tv;
        ImageView beneficio_logo_iv;
        ImageButton imageButton;

        BeneficioItem(View v) {
            super(v);
            v.setOnClickListener(this);
            beneficio_logo_iv = (ImageView)v.findViewById(R.id.eventos_logo_iv);
            imageButton=(ImageButton)v.findViewById(R.id.imageButton);
            beneficio_title_tv=(TextView)v.findViewById(R.id.evento_title_tv);
            beneficio_description_tv=(TextView)v.findViewById(R.id.beneficios_description_tv);

        }

        @Override
        public void onClick(View v) {
            customClickListener.onItemClickListener(items.get(getAdapterPosition()));
        }

    }

    public interface customClickListener{
        void onItemClickListener(PromotionDataEntity promotionsEntity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.beneficios_item, parent, false);
        return new BeneficioItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BeneficioItem vhHI = (BeneficioItem) viewHolder;
        configureNotificacionItem(vhHI, position);
    }

    private void configureNotificacionItem(final BeneficioItem vh, final int position) {
        vh.beneficio_description_tv.setText(items.get(position).getText());
        vh.beneficio_title_tv.setText(items.get(position).getName());
        Glide.with(mContext)
                .load(items.get(position).getImage())
                .apply(new RequestOptions().circleCrop()
                        .error(mContext.getResources().getDrawable(R.drawable.no_media_placeholder))
                        .placeholder(mContext.getResources().getDrawable(R.drawable.no_media_placeholder)))
                .into(vh.beneficio_logo_iv);
    }


    @Override
    public int getItemCount() {
        if (items!=null){
            return items.size();
        }else{
            return 0;
        }

    }

    public void clearItems(){
        items.clear();
    }

    public ArrayList<PromotionDataEntity> getItems(){
        return items;
    }

}


