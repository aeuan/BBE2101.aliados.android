package com.dacodes.bepensa.adapters;

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
import com.dacodes.bepensa.entities.awardsRedeem.AwardRedeemData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAwardsRedeem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<AwardRedeemData> items;
    private customClickListener customClickListener;
    private static final String LOG_TAG = RecyclerAwardsRedeem.class.getSimpleName();

    public RecyclerAwardsRedeem(Context context, customClickListener customClickListener,
                                          ArrayList<AwardRedeemData> awardRedeemData) {
        this.mContext = context;
        this.items = awardRedeemData;
        this.customClickListener = customClickListener;
    }

    public class Awards extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_iv)ImageView image_iv;
        @BindView(R.id.name_tv)TextView name_tv;
        @BindView(R.id.description_tv)TextView description_tv;
        @BindView(R.id.status_tv)TextView status_tv;
        @BindView(R.id.canje_tv)TextView canje_tv;

        Awards(View v) {
            super(v);
            v.setOnClickListener(this);
            ButterKnife.bind(this,v);
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
                .inflate(R.layout.item_award_redeem, parent, false);
        return new Awards(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Awards vhHI = (Awards) viewHolder;
        configureAwards(vhHI, position);
    }


    private void configureAwards(final Awards vh, final int position) {
        vh.description_tv.setText(items.get(position).getAward().getDescription());
        vh.name_tv.setText(items.get(position).getAward().getName());
        vh.canje_tv.setText(""+items.get(position).getPoints());
        if (items.get(position).isIsChanged()){
            vh.status_tv.setText("Cambiado");
        }else{
            vh.status_tv.setText("Pendiente");
        }

        Glide.with(mContext)
                .load(items.get(position).getAward().getImage())
                .apply(new RequestOptions().centerCrop().circleCrop())
                .into(vh.image_iv);
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

    public ArrayList<AwardRedeemData> getItems(){
        return items;
    }

}

