package com.dacodes.bepensa.adapters;

/**

 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.OpportunityEntity;

import java.io.File;
import java.util.ArrayList;


public class RecyclerViewOpportunitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<OpportunityEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewOpportunitiesAdapter.class.getSimpleName();
    public static final int COMUNICADO = 0;

    public RecyclerViewOpportunitiesAdapter(Context context, customClickListener customClickListener, ArrayList<OpportunityEntity> opportunityEntities) {
        this.mContext = context;
        this.items = opportunityEntities;
        this.customClickListener = customClickListener;
    }

    private class NotificacionItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  date_tv,title_tv, description_tv,tvNumMessages,tvStatus,tvPoint;
        ImageView comunicado_iv,image_brand_iv;
        ImageView chat_available;
        ConstraintLayout ivNotificationContainer;

        NotificacionItem(View v) {
            super(v);
            date_tv = v.findViewById(R.id.date_tv);
            title_tv = v.findViewById(R.id.title_tv);
            description_tv = v.findViewById(R.id.description_tv);
            comunicado_iv = v.findViewById(R.id.comunicado_iv);
            chat_available=v.findViewById(R.id.chat_available);
            image_brand_iv=v.findViewById(R.id.image_brand_iv);
            tvNumMessages=v.findViewById(R.id.tvNumMessages);
            tvPoint=v.findViewById(R.id.tvPoint);
            tvStatus=v.findViewById(R.id.tvTitleStatus);

            date_tv.setOnClickListener(this);
            title_tv.setOnClickListener(this);
            description_tv.setOnClickListener(this);
            comunicado_iv.setOnClickListener(this);
            chat_available.setOnClickListener(this);
            image_brand_iv.setOnClickListener(this);
            tvNumMessages.setOnClickListener(this);
            tvStatus.setOnClickListener(this);
            tvPoint.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tvNumMessages:
                case R.id.chat_available:
                    customClickListener.onItemClickListener(items.get(getAdapterPosition()).getId(),tvNumMessages);
                    break;
                default:
                    customClickListener.onItemClickListener(items.get(getAdapterPosition()).getId(),comunicado_iv);
                    break;
            }
        }
    }

    public interface customClickListener{
        void onItemClickListener(int job_id,View view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificacionItem(LayoutInflater.from(mContext).inflate(R.layout.opportunities_item,parent,false));
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
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(24));

        if(items.get(position).getMedia().size()>0) {
            if (items.get(position).getMedia().get(0).getMediaLocal()!=null){
                File file= new File(items.get(position).getMedia().get(0).getMediaLocal());
                Glide.with(mContext).load(file).apply(requestOptions).into(vh.comunicado_iv);
            }else{
                if (items.get(position).getMedia().get(0).getType() == 1) {
                    Glide.with(mContext).load(items.get(position).getMedia()
                            .get(0).getThumbnail()).apply(requestOptions)
                            .into(vh.comunicado_iv);
                }
                if (items.get(position).getMedia().get(0).getType() == 2) {
                    if(items.get(position).getMedia().get(0).getThumbnail()!=null){
                        Glide.with(mContext).load(items.get(position).getMedia().get(0).getThumbnail())
                                .apply(requestOptions).into(vh.comunicado_iv);
                    }else{
                        Glide.with(mContext).load(R.drawable.no_media_placeholder).apply(requestOptions).into(vh.comunicado_iv);
                    }
                }

            }
        }else{
            Glide.with(mContext).load(R.drawable.no_media_placeholder).apply(requestOptions).into(vh.comunicado_iv);
        }

        vh.date_tv.setText(items.get(position).getFormattedCreatedAt());
        try {
            Glide.with(mContext)
                    .load(items.get(position).getBrands().get(0).getLogo())
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(vh.image_brand_iv);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(items.get(position).getType().getName().contentEquals("") || items.get(position).getType().getName() ==null){
            vh.title_tv.setText("N/A");
        }else{
            vh.title_tv.setText(items.get(position).getType().getName());
        }
        vh.description_tv.setText(items.get(position).getCreatedAt());
        vh.description_tv.setText(items.get(position).getDescription());
        vh.tvStatus.setText(getStatusOportunity(items.get(position).getStatus()));
        if (items.get(position).getPoints()==0)
            vh.tvPoint.setVisibility(View.INVISIBLE);
        else
            vh.tvPoint.setText("Puntos: "+items.get(position).getPoints());
        if (items.get(position).isHas_pending_chat()){
            vh.chat_available.setVisibility(View.VISIBLE);
            vh.tvNumMessages.setVisibility(View.VISIBLE);
            vh.tvNumMessages.setText(""+items.get(position).getUnread_messages());
        }else {
            vh.chat_available.setVisibility(View.GONE);
            vh.tvNumMessages.setVisibility(View.INVISIBLE);
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

    public ArrayList<OpportunityEntity> getItems(){
        return items;
    }

    private String getStatusOportunity(int status){
        String myStatus="Pendiente";
        switch (status){
            case 1:
            case 2:
                myStatus="Enviada";
                break;
            case 5:
                myStatus="En seguimiento";
                break;
            case 4:
            case 3:
            case 6:
            case 7:
            case 8:
                myStatus="Cerrada";
                break;
        }
        return myStatus;
    }

}


