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

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.PrincipalTest;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.utils.DM;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapterSurveySingleAdapter extends RecyclerView.Adapter<RecyclerAdapterSurveySingleAdapter.ViewHolder> {
    private Context context;
    private List<SurveyDataEntity> items;
    private Activity activity;

    public RecyclerAdapterSurveySingleAdapter(Activity activity, Context context, List<SurveyDataEntity> items) {
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
        @BindView(R.id.view)View view;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!items.get(getAdapterPosition()).isHasAnswered()) {
                Intent intent = new Intent(context, PrincipalTest.class);
                intent.putExtra("test", items.get(getAdapterPosition()));
                intent.putExtra("boletin", "3");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else{
                alertDialog();
            }
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
        if (!items.get(i).isHasAnswered()){
            viewHolder.view.setVisibility(View.GONE);
        }else{
            viewHolder.view.setVisibility(View.VISIBLE);
        }
        if (items.get(i).getImage() !=null) {
            Uri imageUri;
            imageUri = Uri.parse(items.get(i).getImage());
            Glide.with(context)
                    .load(imageUri)
                    .apply(new RequestOptions().error(R.drawable.no_media_placeholder).error(R.drawable.no_media_placeholder))
                    .into(viewHolder.restaurant_iv);
        }else{
            Glide.with(context)
                    .load(R.drawable.no_media_placeholder)
                    .into(viewHolder.restaurant_iv);
        }
        //viewHolder.restaurant_iv.setImageURI(imageUri);
        viewHolder.description_tv.setText(items.get(i).getDescription());
        int heigth = (int) (DM.getDisplayWidth(context)/2.2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(heigth,heigth);
        viewHolder.cardView.setLayoutParams(params);
    }
    public void alertDialog(){
        if (activity!=null) {
            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Bepensa")
                        .setCancelable(false)
                        .setMessage("Esta encuesta ha sido resuelta, gracias por colaborar con Bepensa")
                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                builder.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}