package com.dacodes.bepensa.adapters;

/**
 * Created by marioguillermo on 3/31/17.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.MenuEntity;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<MenuEntity> items;
    private CustomMenuClickListener listener;
    private static final String tag = "MenuAdapter";
    public static final int HEADER_IMAGE=0;
    public static final int TEXT_ITEM=1;
    public static final int LINE_ITEM =2;
    public static final int TEXT_ICON_ITEM =3;

    public static final int OPORTUNIDADES_ID = 201;
    public static final int FEED_ID = 202;
    public static final int COMUNICADOS_ID = 203;
    public static final int PROMOCIONES_ID = 204;
    public static final int RANKING_ID = 205;
    public static final int AWARDS_ID = 213;
    public static final int CONFIGURACION_ID = 206;
    public static final int TERMS_CONDITIONS_ID = 208;
    public static final int HEADER_ID = 209;
    public static final int EVENTOS_ID = 210;
    public static final int NOTIFICACIONES_ID = 211;
    public static final int MI_BOLETIN= 220;
    public static final int COMMENTS_SUGGESTIONS= 222;

    public interface CustomMenuClickListener {
        void onCustomMenuItemClick(int position, int type, int id);
    }

    public MenuAdapter(Context context, CustomMenuClickListener listener) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    private class HeaderImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfilePicture;
        TextView tvNombre, id_tv, points_tv,points_available;
        ProgressBar progressBar;

        HeaderImage(View v) {
            super(v);
//            v.setOnClickListener(this);
            tvNombre = v.findViewById(R.id.tvNombre);
            id_tv = v.findViewById(R.id.id_tv);
            points_tv = v.findViewById(R.id.points_tv);
            ivProfilePicture = v.findViewById(R.id.ivProfilePicture);
            points_available = v.findViewById(R.id.points_available);
            progressBar = v.findViewById(R.id.progressBar);
            ivProfilePicture.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivProfilePicture:
                    listener.onCustomMenuItemClick(getAdapterPosition(), 0, HEADER_ID);
                    break;
            }

        }

    }

    private class TextItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvText;
        TextItem(View v) {
            super(v);
            v.setOnClickListener(this);
            tvText = v.findViewById(R.id.tvItemTittle);
        }

        @Override
        public void onClick(View v) {
            listener.onCustomMenuItemClick(getAdapterPosition(),0,items.get(getAdapterPosition()).getId());
        }

    }

    private class LineItem extends RecyclerView.ViewHolder{

        LineItem(View v) {
            super(v);
        }

    }

    private class TextIconItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvItemTittle;
        ImageView ivItem;
        View layout;

        TextIconItem(View v) {
            super(v);
            v.setOnClickListener(this);
            tvItemTittle = v.findViewById(R.id.tvItemTittle);
            ivItem = v.findViewById(R.id.ivItem);
            layout = v.findViewById(R.id.layout);
        }

        @Override
        public void onClick(View v) {


            listener.onCustomMenuItemClick(getAdapterPosition(),0,items.get(getAdapterPosition()).getId());
/*
            if(items.get(getAdapterPosition()).getId() != CURRICULUM_ID){
                for (MenuEntity current : items){
                    current.setSelected(false);
                }

                items.get(getAdapterPosition()).setSelected(true);
                notifyDataSetChanged();
            }*/
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case HEADER_IMAGE:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.drawer_menu_header, parent, false);
                return new HeaderImage(v);
            case TEXT_ITEM:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.drawer_menu_item, parent, false);
                return new TextItem(v);
            case LINE_ITEM:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.drawer_menu_line, parent, false);
                return new LineItem(v);
            case TEXT_ICON_ITEM:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.drawer_menu_item_icon, parent, false);
                return new TextIconItem(v);
            default:
                return new LineItem(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case HEADER_IMAGE:
                HeaderImage vhHI = (HeaderImage) viewHolder;
                configureHeaderImage(vhHI, position);
                break;

            case TEXT_ITEM:
                TextItem vhTI = (TextItem) viewHolder;
                configureTextItem(vhTI, position);
                break;

            case LINE_ITEM:
                LineItem vhBI = (LineItem) viewHolder;
                configureLineItem(vhBI, position);
                break;

            case TEXT_ICON_ITEM:
                TextIconItem vhSNI = (TextIconItem) viewHolder;
                configureTextIconItem(vhSNI, position);
                break;
        }

    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getTypeOfCell();
    }

    private void configureHeaderImage(final HeaderImage vh, int position) {
        String photo = items.get(position).getImage();
        if(!TextUtils.isEmpty(photo)){
            Glide.with(mContext).asBitmap()
                    .load(photo)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .transition(withCrossFade())
                    .into(vh.ivProfilePicture);
        }else{
            Glide.with(mContext).load(items.get(position).getImageId())
                    .apply(new RequestOptions().placeholder(R.drawable.no_media_placeholder))
                    .apply(new RequestOptions().circleCrop())
                    .into(vh.ivProfilePicture);
        }
        vh.tvNombre.setText(items.get(position).getTextField1());
        vh.id_tv.setText("ID: " + items.get(position).getId());
        vh.points_tv.setText(items.get(position).getPoints()+" puntos totales");
        try {
            if(Integer.parseInt(items.get(position).getAvailable())>0){
                vh.points_available.setText(items.get(position).getAvailable()+" puntos disponibles");
            }else{
                vh.points_available.setText(items.get(position).getAvailable()+"");
            }
        }catch(Exception e) {
            e.printStackTrace();
            vh.points_available.setText(""+items.get(position).getAvailable() + "");
        }
    }

    private void configureTextItem(final TextItem vh, int position) {
        vh.tvText.setText(items.get(position).getTextField1());
    }

    private void configureLineItem(final LineItem vh, int position) {

    }

    private void configureTextIconItem(final TextIconItem vh, int position) {
        MenuEntity current = items.get(position);
        vh.tvItemTittle.setText(items.get(position).getTextField1());
        vh.ivItem.setImageResource(items.get(position).getImageId());
        if(current.isSelected()){
            vh.tvItemTittle.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
            vh.ivItem.setColorFilter(ContextCompat.getColor(mContext, R.color.colorBlack));
            vh.layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }else{
            vh.tvItemTittle.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            vh.ivItem.setColorFilter(ContextCompat.getColor(mContext, R.color.colorWhite));
            vh.layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOrange));

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

    public void addHeaderImage(int imageId, String nombre, String username, String points){
        MenuEntity current = new MenuEntity();
        current.setImageId(imageId);
        current.setTextField1(nombre);
        current.setId(Integer.valueOf(username));
        current.setPoints(points);
        current.setTypeOfCell(HEADER_IMAGE);
        items.add(current);
    }

    public void addHeaderImage(String imageId, String nombre, String username, String points,String available){
        MenuEntity current = new MenuEntity();
        current.setImage(imageId);
        current.setTextField1(nombre);
        current.setId(Integer.valueOf(username));
        current.setPoints(points);
        current.setAvailable(available);
        current.setTypeOfCell(HEADER_IMAGE);
        items.add(current);
    }

    public void addTextItem(String textMessage, int id) {
        MenuEntity current = new MenuEntity();
        current.setTypeOfCell(TEXT_ITEM);
        current.setTextField1(textMessage);
        current.setId(id);
        items.add(current);
    }

    public void addLineItem(){
        MenuEntity current = new MenuEntity();
        current.setTypeOfCell(LINE_ITEM);
        items.add(current);
    }

    public void addTextIconItem(int imageId, String textMessage, int id, boolean isSelected){
        MenuEntity current = new MenuEntity();
        current.setTypeOfCell(TEXT_ICON_ITEM);
        current.setImageId(imageId);
        current.setId(id);
        current.setTextField1(textMessage);
        current.setSelected(isSelected);
        items.add(current);
    }

    public void selectRow(int position){
        for(int i = 0; i<items.size(); i++){
            items.get(i).setSelected(false);
        }
        items.get(position).setSelected(true);
        notifyDataSetChanged();
    }

    public ArrayList<MenuEntity> getItems(){
        return items;
    }

}


