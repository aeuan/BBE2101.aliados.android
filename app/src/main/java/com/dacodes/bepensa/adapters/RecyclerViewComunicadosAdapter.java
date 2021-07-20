package com.dacodes.bepensa.adapters;

/**

 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.ActivityImage;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.ComunicadoEntity;

import java.util.ArrayList;


public class RecyclerViewComunicadosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<ComunicadoEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewComunicadosAdapter.class.getSimpleName();
    public static final int COMUNICADO = 0;
    private int mWidth;
    private int mHeight;
    Activity activity;

    public RecyclerViewComunicadosAdapter(Context context, customClickListener customClickListener,
                                          ArrayList<ComunicadoEntity> comunicadoEntities, Activity activity) {
        this.mContext = context;
        this.items = comunicadoEntities;
        this.customClickListener = customClickListener;
        this.activity=activity;
    }

    private class NotificacionItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  date_tv,title_tv, description_tv;
        ImageView comunicado_iv;

        NotificacionItem(View v) {
            super(v);
            v.setOnClickListener(this);
            date_tv = v.findViewById(R.id.date_tv);
            title_tv = v.findViewById(R.id.title_tv);
            description_tv = v.findViewById(R.id.description_tv);
            comunicado_iv = v.findViewById(R.id.comunicado_iv);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public interface customClickListener{
        void onItemClickListener(int job_id);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.comunicados_item, parent, false);
        return new NotificacionItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        NotificacionItem vhHI = (NotificacionItem) viewHolder;
        configureNotificacionItem(vhHI, position);
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return COMUNICADO;
    }

    private void configureNotificacionItem(final NotificacionItem vh, final int position) {
        vh.title_tv.setText(items.get(position).getName());
        vh.date_tv.setText(items.get(position).getFormattedCreatedAt());
        vh.description_tv.setText(items.get(position).getText());
        Glide.with(mContext)
                .load(items.get(position).getImage())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image))
                .into(vh.comunicado_iv);


        vh.comunicado_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(mContext, ActivityImage.class).putExtra("url",items.get(position).getImage()));
                /*
                List<String> images = new ArrayList<>();
                images.add(items.get(position).getImage());
                new ImageViewer.Builder(activity, images)
                        .setStartPosition(0)
                        .hideStatusBar(false)
                        .show();*/
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

    public void addNotificacion(ComunicadoEntity current){

        items.add(current);
    }

    public void clearItems(){
        items.clear();
    }

    public ArrayList<ComunicadoEntity> getItems(){
        return items;
    }

}


