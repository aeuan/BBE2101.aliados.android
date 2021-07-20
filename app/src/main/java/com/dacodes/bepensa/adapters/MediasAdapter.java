package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.MediasFilesEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MediasFilesEntity> items = new ArrayList<>();
    private Context context;
    private Activity activity;
    private DeleteItem deleteItem;
    private addNewMedia addNewMedia;

    public MediasAdapter(List<MediasFilesEntity> items, Context context, Activity activity, DeleteItem deleteItem) {
        this.items = items;
        this.context = context;
        this.activity = activity;
        this.deleteItem = deleteItem;
    }

    public MediasAdapter(List<MediasFilesEntity> items, Context context, Activity activity, DeleteItem deleteItem,addNewMedia addNewMedia) {
        this.items = items;
        this.context = context;
        this.activity = activity;
        this.deleteItem = deleteItem;
        this.addNewMedia=addNewMedia;
    }

    public interface DeleteItem{
        void onDeleteItem(int position);
    }

    public interface addNewMedia{
        void addNewMedia();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType ==1 || viewType==2)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,parent,false));
        else return new ItemCamera(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (items.get(position).getType()){
            case 1:
            case 2:
                createViewMedia((ViewHolder)holder,position);
                break;
            case 3:
                createViewCamera((ItemCamera)holder,position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivMedia)ImageView ivMedia;
        @BindView(R.id.ibClose)ImageButton ivClose;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ItemCamera extends RecyclerView.ViewHolder{
        @BindView(R.id.ivCamera)ImageView ivCamera;
        @BindView(R.id.ibCamera)ImageButton ibCamera;

        public ItemCamera(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    private void createViewMedia(ViewHolder holder,int position){
        switch (items.get(position).getType()){
            case 1:
                Glide.with(context)
                        .load(items.get(position).getFile())
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.ivMedia);
                break;
            case 2:
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(items.get(position).getFile().getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                Glide.with(context)
                        .load(thumb)
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.ivMedia);
                break;
        }
        holder.ivClose.setOnClickListener(view -> deleteItem.onDeleteItem(position));
    }

    private void createViewCamera(ItemCamera holder ,int position){
        holder.ibCamera.setOnClickListener(v -> {
            if (addNewMedia!=null)
                addNewMedia.addNewMedia();
        });
    }
}
