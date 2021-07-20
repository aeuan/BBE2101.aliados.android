package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EventoDetailActivity;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.utils.DM;
import com.dacodes.bepensa.utils.DateFormatter;

import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * Created by Eric on 21/03/16.
 *
 */
public class HomeHeaderPagerAdapter extends PagerAdapter {

    private List<EventosEntity> eventosEntities;
    private Context context;
    private final Random random = new Random();
    private final SparseArray<TextView> mHolderArray = new SparseArray<>();
    private int mSize;
    Locale locale = new Locale("es", "ES");


    public HomeHeaderPagerAdapter(Context context, List<EventosEntity> eventosEntities) {
        mSize = eventosEntities.size();
        this.eventosEntities = eventosEntities;
        this.context = context;
    }


    @Override public int getCount() {
        return mSize;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mHolderArray.get(position));
    }

    @Override public Object instantiateItem(ViewGroup view, final int position) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        ImageView imageView;
        TextView fecha_tv;
        TextView voluntario_tv;
        TextView evento_tv;
        RelativeLayout contenido;
        v = inflater.inflate(R.layout.home_header_layout, view, false);
        imageView = (ImageView) v.findViewById(R.id.banner_iv);
        fecha_tv =(TextView)v.findViewById(R.id.fecha_tv);
        voluntario_tv =(TextView)v.findViewById(R.id.voluntario_tv);
        evento_tv =(TextView)v.findViewById(R.id.evento_tv);
        contenido = v.findViewById(R.id.contenido);
        Glide.with(context).load(""+eventosEntities.get(position).getImage())
                .apply(new RequestOptions().centerCrop()).into(imageView);
        voluntario_tv.setText(eventosEntities.get(position).getText());
        evento_tv.setText(eventosEntities.get(position).getName());

        String dateStar=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",eventosEntities.get(position).getStarts(),locale);
        String dateEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",eventosEntities.get(position).getEnds(),locale);
        if (dateStar.equals(dateEnd)){
            String hourStart=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntities.get(position).getStarts(),locale).trim();
            String hourEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntities.get(position).getStarts(),locale).trim();
            if (hourStart.equals(hourEnd)){
                fecha_tv.setText(""+dateStar+" a las "+hourStart);
            }else{
                fecha_tv.setText(""+dateStar+" de "+hourStart+" a "+hourEnd);
            }
        }else{
            fecha_tv.setText("Del "+dateStar +" al "+dateEnd);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DM.getDisplayWidth(context),DM.getDisplayWidth(context));
        contenido.setLayoutParams(params);

        view.addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventoDetailActivity.class);
                intent.putExtra("evento", eventosEntities.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return v;
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}