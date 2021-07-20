package com.dacodes.bepensa.adapters;

/**

 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.NotificacionesEntity;

import java.util.ArrayList;


public class RecyclerViewNotificacionesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<NotificacionesEntity> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerViewNotificacionesAdapter.class.getSimpleName();
    public static final int NOTIFICACION = 0;
    private int mWidth;
    private int mHeight;

    public RecyclerViewNotificacionesAdapter(Context context, customClickListener customClickListener,
                                             ArrayList<NotificacionesEntity> notificacionesEntities) {
        this.mContext = context;
        this.items = notificacionesEntities;
        this.customClickListener = customClickListener;

    }

    private class NotificacionItem extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView  notification_tv,date_tv;
        ConstraintLayout item;

        NotificacionItem(View v) {
            super(v);
            v.setOnClickListener(this);
            notification_tv = v.findViewById(R.id.notification_tv);
            date_tv = v.findViewById(R.id.date_tv);

        }

        @Override
        public void onClick(View v) {
            item = v.findViewById(R.id.item);
            customClickListener.onItemClickListener(items.get(getAdapterPosition()),item);
        }
    }

    public interface customClickListener{
        void onItemClickListener(NotificacionesEntity notificacionesEntity,ConstraintLayout constraintLayout);
    }

    public interface customOnLongClickListener{
        void OnLongClickListener(NotificacionesEntity notificacionesEntity,ConstraintLayout constraintLayout);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.notifications_item, parent, false);
        return new NotificacionItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        NotificacionItem vhHI = (NotificacionItem) viewHolder;
        configureItem(vhHI, position);
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return NOTIFICACION;
    }

    private void configureItem(final NotificacionItem vh, final int position) {
        vh.notification_tv.setText(items.get(position).getMessage());
        vh.date_tv.setText(items.get(position).getFormattedCreatedAt());
    }

    public void itemSelected(RecyclerView.ViewHolder viewHolder,int position){
      /*  final NotificacionesEntity model =items.get(position);
        vhHI.date_tv.setBackground(model);
        holder.view.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
        holder.tvItems.setText(model.getText());*/
    }

    @Override
    public int getItemCount() {
        if (items!=null){
            return items.size();
        }else{
            return 0;
        }

    }
    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem() {
        notifyDataSetChanged();
    }

    public void addNotificacion(NotificacionesEntity current){

        items.add(current);
    }

    public void clearItems(){
        if(items!=null) {
            items.clear();
        }
    }

    public ArrayList<NotificacionesEntity> getItems(){
        return items;
    }

}


