package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EventoDetailActivity;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.utils.DM;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapterEventsSingleAdapter extends RecyclerView.Adapter<RecyclerAdapterEventsSingleAdapter.ViewHolder> {
    private Context context;
    private List<EventosEntity> items;
    private Activity activity;

    public RecyclerAdapterEventsSingleAdapter(Activity activity, Context context, List<EventosEntity> items) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.restaurant_iv)
        ImageView restaurant_iv;
        @BindView(R.id.description_tv)TextView description_tv;
        @BindView(R.id.card_view)
        CardView cardView;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                Intent intent = new Intent(context, EventoDetailActivity.class);
                intent.putExtra("evento", items.get(getAdapterPosition()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_home_restaurant, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            Uri imageUri;
            imageUri = Uri.parse(items.get(i).getImage());
        Glide.with(viewHolder.restaurant_iv.getContext())
                .load(imageUri)
                .apply(new RequestOptions().error(R.drawable.no_media_placeholder))
                .into(viewHolder.restaurant_iv);
            //viewHolder.restaurant_iv.setImageURI(imageUri);
            viewHolder.description_tv.setText(items.get(i).getText());
        int heigth = (int) (DM.getDisplayWidth(context)/2.2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(heigth,heigth);
        viewHolder.cardView.setLayoutParams(params);
    }
}