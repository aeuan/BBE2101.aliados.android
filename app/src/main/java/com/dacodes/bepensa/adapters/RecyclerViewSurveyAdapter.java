package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewSurveyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<SurveyDataEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewSurveyAdapter.class.getSimpleName();
    Locale locale = new Locale("es", "ES");

    public RecyclerViewSurveyAdapter(Context context,customClickListener customClickListener, ArrayList<SurveyDataEntity> beneficioEntities) {
        this.mContext = context;
        this.items = beneficioEntities;
        this.customClickListener = customClickListener;
    }

    private class SurveyItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView beneficio_title_tv, beneficio_description_tv;
        ImageView beneficio_logo_iv;
        ImageButton img_rigth;
        RelativeLayout view;
        SurveyItem(View v) {
            super(v);
            v.setOnClickListener(this);
            beneficio_logo_iv = (ImageView)v.findViewById(R.id.eventos_logo_iv);
            img_rigth= (ImageButton) v.findViewById(R.id.imageButton);
            beneficio_title_tv=(TextView)v.findViewById(R.id.evento_title_tv);
            beneficio_description_tv=(TextView)v.findViewById(R.id.beneficios_description_tv);
            view = (RelativeLayout) v.findViewById(R.id.view_check);
        }

        @Override
        public void onClick(View v) {
            customClickListener.onItemClickListener(items.get(getAdapterPosition()));
        }
    }

    public interface customClickListener{
        void onItemClickListener(SurveyDataEntity promotionsEntity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.beneficios_item, parent, false);
        return new SurveyItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SurveyItem vhHI = (SurveyItem) viewHolder;
        configureNotificacionItem(vhHI, position);
    }

    private void configureNotificacionItem(SurveyItem vh, final int position) {
       vh.beneficio_description_tv.setText(DateFormatter
                .formatDate("yyyy-MM-dd'T'HH:mm:ss",
                        "dd 'de' MMMM 'de' yyy",
                        items.get(position).getCreatedAt(),locale));
        vh.beneficio_title_tv.setText(items.get(position).getName());
        if (items.get(position).isHasAnswered()){
            vh.view.setVisibility(View.VISIBLE);
        }else{
            vh.view.setVisibility(View.GONE);
        }
        Glide.with(mContext)
                .load(items.get(position).getImage())
                .apply(new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.no_media_placeholder))
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

    public ArrayList<SurveyDataEntity> getItems(){
        return items;
    }
}
