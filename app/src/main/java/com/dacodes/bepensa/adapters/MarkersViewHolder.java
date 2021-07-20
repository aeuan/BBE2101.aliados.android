package com.dacodes.bepensa.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.models.Markers;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarkersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)TextView name;
    @BindView(R.id.card)ConstraintLayout card;

    public MarkersViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bindView(Markers markers){
        name.setText(markers.getAddress());
        card.setOnClickListener(view ->{
            try {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + markers.getLat() + "," + markers.getLng()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                card.getContext().startActivity(intent);
            } catch (Exception e) {
                String uri = "http://maps.google.com/maps?saddr=&daddr=" + markers.getLat() + "," + markers.getLng();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                card.getContext().startActivity(intent);
            }
        });
    }
}
