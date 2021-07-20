package com.dacodes.bepensa.adapters;

/**
 * Created by marioguillermo on 3/31/17.
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
import com.dacodes.bepensa.entities.Chat.ChatEntity;
import com.dacodes.bepensa.utils.DateFormatter;
import com.dacodes.bepensa.utils.FormatDate;

import java.util.ArrayList;
import java.util.Locale;


public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    public void setItems(ArrayList<ChatEntity> items) {
        this.items = items;
    }

    private ArrayList<ChatEntity> items;

    public static final int CHAT_BEPENSA_ADMIN = 1;
    public static final int CHAT_BEPENSA_USER = 2;
    Locale locale = new Locale("es", "ES");


    public RecyclerViewChatAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
    }


    private class ChatBepensaAdmin extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profile_iv;
        TextView text_tv, date_tv,tvDate;

        ChatBepensaAdmin(View v) {
            super(v);
            v.setOnClickListener(this);
            profile_iv = (ImageView) v.findViewById(R.id.profile_iv);
            text_tv = (TextView) v.findViewById(R.id.text_tv);
            date_tv = (TextView)v.findViewById(R.id.date_tv);
            tvDate=v.findViewById(R.id.tvDate);
        }

        @Override
        public void onClick(View v) {

        }

    }

    private class ChatBepensaUser extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profile_iv;
        TextView text_tv, date_tv,tvDate;
        ChatBepensaUser(View v) {
            super(v);
            v.setOnClickListener(this);
            profile_iv = (ImageView) v.findViewById(R.id.profile_iv);
            text_tv = (TextView) v.findViewById(R.id.text_tv);
            date_tv = (TextView)v.findViewById(R.id.date_tv);
            tvDate=v.findViewById(R.id.tvDate);
        }

        @Override
        public void onClick(View v) {

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case CHAT_BEPENSA_ADMIN:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_bepensa_item, parent, false);
                return new ChatBepensaAdmin(v);
            case CHAT_BEPENSA_USER:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_user_item, parent, false);
                return new ChatBepensaUser(v);
            default:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.chat_user_item, parent, false);
                return new ChatBepensaUser(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case CHAT_BEPENSA_ADMIN:
                ChatBepensaAdmin vhBN = (ChatBepensaAdmin) viewHolder;
                configureBepensaAdminChat(vhBN, position);
                break;
            case CHAT_BEPENSA_USER:
                ChatBepensaUser vhBO = (ChatBepensaUser) viewHolder;
                configureBepensaUserChat(vhBO, position);
                break;
        }


    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType_model();
    }

    private void configureBepensaAdminChat(final ChatBepensaAdmin vh, int position) {
        String date=FormatDate.getFormatMin(items.get(position).getCreatedAt(),mContext.getResources().getString(R.string.format_date));
        vh.date_tv.setText(date);
        vh.text_tv.setText(""+items.get(position).getText());
        vh.tvDate.setText(DateFormatter
                .formatDate("yyyy-MM-dd",
                        "dd'/'MM'/'yyyy",items.get(position).getCreatedAt(),
                        locale));
        Glide.with(mContext).asBitmap().load(""+items.get(position).getUser().getAvatarUrl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_image).circleCrop())
                .into(vh.profile_iv);
    }

    private void configureBepensaUserChat(final ChatBepensaUser vh, int position) {
        String date=FormatDate.getFormatMin(items.get(position).getCreatedAt(),mContext.getResources().getString(R.string.format_date));
        vh.date_tv.setText(date);
        vh.text_tv.setText(""+items.get(position).getText());
        vh.tvDate.setText(DateFormatter
                .formatDate("yyyy-MM-dd",
                        "dd'/'MM'/'yyyy",items.get(position).getCreatedAt(),
                        locale));
        Glide.with(mContext).asBitmap().load(""+items.get(position).getUser().getAvatarUrl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_image).circleCrop())
                .into(vh.profile_iv);
    }

    @Override
    public int getItemCount() {
        if (items!=null){
            return items.size();
        }else{
            return 0;
        }

    }


    public void addBepensaAdminChat(ChatEntity chatEntity) {
        items.add(chatEntity);
        notifyDataSetChanged();
    }

    public void addBepensaUserChat(ChatEntity chatEntity){
        items.add(chatEntity);
        notifyDataSetChanged();
    }

    public void clearItems(){
        items.clear();
    }

    public ArrayList<ChatEntity> getItems(){
        return items;
    }


}


