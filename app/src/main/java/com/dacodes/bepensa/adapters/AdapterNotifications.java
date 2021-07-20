package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.ActivityImage;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.NotificacionesEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class AdapterNotifications extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NotificacionesEntity> items = new ArrayList<>();
    private AdapterFilterNotifications.onCheckedId onCheckedId;

    public AdapterNotifications(){

    }

    public AdapterNotifications(AdapterFilterNotifications.onCheckedId filterNotifications,Context context){
        this.onCheckedId = filterNotifications;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HolderNotifications(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notifications,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        HolderNotifications holder =(HolderNotifications)viewHolder;
        holder.bindView(i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<NotificacionesEntity> list){
        this.items = list;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem() {
        notifyDataSetChanged();
    }

    class HolderNotifications extends RecyclerView.ViewHolder {

        @BindView(R.id.image) ImageView image;
        @BindView(R.id.tema) TextView tema;
        @BindView(R.id.download) ImageButton download;
        @BindView(R.id.message) TextView message;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.icon)ImageView icon;
        @BindView(R.id.url)TextView url;
        @BindView(R.id.view4)View view;

        HolderNotifications(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bindView(int position) {
            tema.setText(items.get(position).getThemeContentNotification().getTheme().getName());
            message.setText(items.get(position).getMessage());
            if (items.get(position).getMessage().split(" ").length >12) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, convertDpToPixel(1, message.getContext()));
                params.bottomToBottom = ConstraintSet.PARENT_ID;
                params.endToEnd = ConstraintSet.PARENT_ID;
                params.startToStart = ConstraintSet.PARENT_ID;
                params.setMargins(convertDpToPixel(8, message.getContext()),0,convertDpToPixel(8, message.getContext()),0);
                view.setLayoutParams(params);
            }else{
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, convertDpToPixel(1, message.getContext()));
                params.bottomToBottom = ConstraintSet.PARENT_ID;
                params.endToEnd = ConstraintSet.PARENT_ID;
                params.startToStart = ConstraintSet.PARENT_ID;
                params.topToBottom = R.id.image;
                params.setMargins(convertDpToPixel(8, message.getContext()),convertDpToPixel(8, message.getContext()),convertDpToPixel(8, message.getContext()),0);
                view.setLayoutParams(params);
            }

            if (!TextUtils.isEmpty(items.get(position).getThemeContentNotification().getLinkTitle())
                    && !TextUtils.isEmpty(items.get(position).getThemeContentNotification().getLinkUrl())){
                url.setVisibility(View.VISIBLE);
                url.setText(items.get(position).getThemeContentNotification().getLinkTitle());
                url.setOnClickListener(view->
                        url.getContext()
                                .startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(items.get(position).getThemeContentNotification().getLinkUrl()))));
            }else if (TextUtils.isEmpty(items.get(position).getThemeContentNotification().getLinkTitle())
                    && !TextUtils.isEmpty(items.get(position).getThemeContentNotification().getLinkUrl())){
                url.setVisibility(View.VISIBLE);
                url.setText(items.get(position).getThemeContentNotification().getLinkUrl());
                url.setOnClickListener(view->
                        url.getContext()
                                .startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(items.get(position).getThemeContentNotification().getLinkUrl()))));
            }else
                url.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(items.get(position).getThemeContentNotification().getFile())) {
                download.setVisibility(View.VISIBLE);
                download.setOnClickListener(v ->download.getContext()
                        .startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(items.get(position).getThemeContentNotification().getFile()))) );
            }else
                download.setVisibility(View.INVISIBLE);

            if (!TextUtils.isEmpty(items.get(position).getThemeContentNotification().getImage())){
                icon.setVisibility(View.GONE);
                image.setOnClickListener(v ->image.getContext()
                        .startActivity(new Intent(image.getContext(), ActivityImage.class).putExtra("url"
                                ,items.get(position).getThemeContentNotification().getImage())));
                Glide.with(image.getContext())
                        .load(items.get(position).getThemeContentNotification().getImage())
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop())
                        .into(image);
            }else {
                Glide.with(icon.getContext())
                        .load(R.drawable.cirlce_notification)
                        .into(image);
                icon.setVisibility(View.VISIBLE);
            }

            date.setText(items.get(position).getFormattedCreatedAt());

            tema.setOnClickListener(v ->{
                if (onCheckedId != null) {
                    List<Integer> id = new ArrayList<>();
                    id.add(items.get(position).getThemeContentNotification().getTheme().getId());
                    onCheckedId.onChekedIds(id);
                }
            } );

        }
    }

    private int convertDpToPixel(float dp,Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }
    /*
    fun convertDpToPixel(dp: Float, context:Context): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
    }*/
}
