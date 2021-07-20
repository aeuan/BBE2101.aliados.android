package com.dacodes.bepensa.adapters;

/**

 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Locale;


public class RecyclerViewEventosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<EventosEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewEventosAdapter.class.getSimpleName();
    Locale locale = new Locale("es", "ES");

    public RecyclerViewEventosAdapter(Context context, customClickListener customClickListener, ArrayList<EventosEntity> eventosEntities) {
        this.mContext = context;
        this.items = eventosEntities;
        this.customClickListener = customClickListener;
    }

    private class EventoItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  tvEventoName,tvTiempo, tvMessage;
        ImageView eventos_logo_iv;

        EventoItem(View v) {
            super(v);
            v.setOnClickListener(this);
            tvEventoName = v.findViewById(R.id.evento_title_tv);
            tvTiempo = v.findViewById(R.id.evento_time_tv);
            eventos_logo_iv = v.findViewById(R.id.eventos_logo_iv);

        }

        @Override
        public void onClick(View v) {
            customClickListener.onItemClickListener(items.get(getAdapterPosition()));
        }

    }

    public interface customClickListener{
        void onItemClickListener(EventosEntity eventosEntity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.eventos_item, parent, false);
        return new EventoItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EventoItem vhHI = (EventoItem) viewHolder;
        configureEventoItem(vhHI, position);
    }

    private void configureEventoItem(final EventoItem vh, final int position) {
        vh.tvEventoName.setText(items.get(position).getName());
        Glide.with(mContext)
                .load(items.get(position).getImage())
                .apply(new RequestOptions().circleCrop()
                        .error(mContext.getResources().getDrawable(R.drawable.no_media_placeholder))
                        .placeholder(mContext.getResources().getDrawable(R.drawable.no_media_placeholder)))
                .into(vh.eventos_logo_iv);

        String dateStar=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",items.get(position).getStarts(),locale);
        String dateEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",items.get(position).getEnds(),locale);
        if (dateStar.equals(dateEnd)){
            String hourStart=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",items.get(position).getStarts(),locale).trim();
            String hourEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",items.get(position).getStarts(),locale).trim();
            if (hourStart.equals(hourEnd)){
                vh.tvTiempo.setText(""+dateStar+" a las "+hourStart);
            }else{
                vh.tvTiempo.setText(""+dateStar+" de "+hourStart+" a "+hourEnd);
            }
        }else{
            String hourStart=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",items.get(position).getStarts(),locale).trim();
            String hourEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",items.get(position).getStarts(),locale).trim();
            if (hourStart.equals(hourEnd)){
                vh.tvTiempo.setText(""+dateStar+" a las "+hourStart);
            }else{
                vh.tvTiempo.setText(""+dateStar+" de "+hourStart+" a "+hourEnd);
            }

            if (hourStart!=null && hourStart!="" && hourEnd!=null && hourEnd!=""){
                vh.tvTiempo.setText("Del "+dateStar +" a las "+hourStart+" al "+dateEnd+" a las "+hourEnd);
            }else{
                vh.tvTiempo.setText("Del "+dateStar +" al "+dateEnd);
            }
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

    public void clearItems(){
        items.clear();
    }

    public ArrayList<EventosEntity> getItems(){
        return items;
    }

}


