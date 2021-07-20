package com.dacodes.bepensa.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EventoDetailActivity;
import com.dacodes.bepensa.entities.EventosEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class BottomSheetMarker extends BottomSheetDialogFragment implements View.OnClickListener {

    EventosEntity eventosEntity;
    Locale locale = new Locale("es", "ES");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.eventos_item, container, false);
        ImageButton imageButton = v.findViewById(R.id.imageButton);
        TextView evento_title  = v.findViewById(R.id.evento_title_tv);
        TextView evento_time  = v.findViewById(R.id.evento_time_tv);
        ImageView eventos_logo=(ImageView)v.findViewById(R.id.eventos_logo_iv);
        RelativeLayout item=(RelativeLayout)v.findViewById(R.id.item_layout_event);
        v.setOnClickListener(this);
        eventosEntity = (EventosEntity) getArguments().getSerializable("evento");
        if (eventosEntity!=null){
            evento_time.setText(DateFormatter
                    .formatDate("yyyy-MM-dd'T'HH:mm:ss",
                            "dd 'de' MMMM 'de' yyy",
                            ""+eventosEntity.getCreatedAt(),
                            locale));
            evento_title.setText(""+eventosEntity.getName());
            Glide.with(getActivity())
                    .load(""+eventosEntity.getImage())
                    .into(eventos_logo);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),EventoDetailActivity.class);
        intent.putExtra("evento",eventosEntity);
        startActivity(intent);
    }
}
