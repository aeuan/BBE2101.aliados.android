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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.ProfileEntity;

import java.util.ArrayList;


public class RecyclerViewRankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<ProfileEntity> items;
    private static final String LOG_TAG = RecyclerViewRankingAdapter.class.getSimpleName();
    private String user_id="";

    public RecyclerViewRankingAdapter(Context context,ArrayList<ProfileEntity> itemsRankin,String user_id) {
        this.mContext = context;
        this.items = itemsRankin;
        this.user_id=user_id;
    }

    private class NotificacionItem extends RecyclerView.ViewHolder{
        TextView  ranking_tv,name_tv, division_tv, points_tv;
        ImageView profile_iv;
        ConstraintLayout ranking;
        View background_v;
        View background_view;

        NotificacionItem(View v) {
            super(v);
            ranking_tv = v.findViewById(R.id.ranking_tv);
            name_tv = v.findViewById(R.id.name_tv);
            division_tv = v.findViewById(R.id.division_tv);
            points_tv = v.findViewById(R.id.points_tv);
            profile_iv = v.findViewById(R.id.profile_iv);
            ranking = v.findViewById(R.id.ranking);
            background_v=v.findViewById(R.id.background_v);
            background_view=v.findViewById(R.id.background_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext)
                .inflate(R.layout.ranking_item, parent, false);
        return new NotificacionItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NotificacionItem vhHI = (NotificacionItem) viewHolder;
       configureRank(vhHI,position);
    }

    public void configureRank(NotificacionItem vh,int position){
        if (items.get(position).isHidden_ranking()){
            vh.background_view.setVisibility(View.VISIBLE);
            vh.name_tv.setVisibility(View.INVISIBLE);
            vh.division_tv.setVisibility(View.INVISIBLE);
            vh.points_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
            vh.ranking_tv.setText(String.valueOf(position+1));
            vh.points_tv.setText(items.get(position).getPoints() + " pts");
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.user_default)).into(vh.profile_iv);
        }else {
            vh.background_view.setVisibility(View.INVISIBLE);
            vh.name_tv.setVisibility(View.VISIBLE);
            vh.division_tv.setVisibility(View.VISIBLE);
            configureNotificacionItem(vh,position);
        }
    }

    private void configureNotificacionItem(final NotificacionItem vh, final int position) {
            if (items.get(position).getAccess().getUuid().equals(user_id) && !items.get(position).isHidden_ranking()) {
                vh.ranking.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrgangeTransparent));
                vh.name_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                vh.division_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                vh.points_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                vh.background_v.setVisibility(View.VISIBLE);
                vh.ranking_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            } else {
                vh.background_v.setVisibility(View.GONE);
                vh.ranking_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                vh.ranking.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                vh.name_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                vh.division_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                vh.points_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
            }
            vh.ranking_tv.setText(String.valueOf(position+1));
            vh.name_tv.setText(items.get(position).getName() + " " + items.get(position).getLast_name());
            vh.points_tv.setText(items.get(position).getPoints() + " pts");
            vh.division_tv.setText(items.get(position).getDivision().getName());
            Glide.with(mContext).load(items.get(position).getAccess().getAvatarUrl())
                    .apply(new RequestOptions().centerCrop().circleCrop())
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

    public void clearItems(){
        items.clear();
    }

    public ArrayList<ProfileEntity> getItems(){
        return items;
    }

}


