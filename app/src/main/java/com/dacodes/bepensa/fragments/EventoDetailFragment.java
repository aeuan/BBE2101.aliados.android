package com.dacodes.bepensa.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.utils.DM;
import com.dacodes.bepensa.utils.DateFormatter;
import com.dacodes.bepensa.utils.FormatDate;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EventoDetailFragment extends Fragment{
    @BindView(R.id.image_iv) ImageView image_iv;
    @BindView(R.id.static_map_iv) ImageView static_map_iv;
    @BindView(R.id.title_tv) TextView title_tv;
    @BindView(R.id.date_tv) TextView date_tv;
    @BindView(R.id.event_description) TextView event_description;
    @BindView(R.id.calendar_bt)
    Button calendar_bt;
    private static final String LOG_TAG = EventoDetailFragment.class.getSimpleName();
    Locale locale = new Locale("es", "ES");

    public EventoDetailFragment() {
        // Required empty public constructor
    }
    Context mContext;
    List<EditText> editTextsList;
    AppController appController;
    AppController app;
    EventosEntity eventosEntity;

    @OnClick(R.id.static_map_iv)
    public void map(){
        loadMapDetail(eventosEntity);
    }


    public void loadMapDetail(EventosEntity eventosEntity){
        if (eventosEntity!=null ){
            try {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr="+eventosEntity.getLat()+","+eventosEntity.getLng()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER );
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                try {
                    String uri = "http://maps.google.com/maps?saddr=&daddr=" + eventosEntity.getLat() + "," + eventosEntity.getLng();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                        Toast.makeText(getContext(),"Por favor instale google maps en su teléfono",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.image_iv)
    public void image(){
        List<String> images = new ArrayList<>();
            images.add(eventosEntity.getImage());
            new ImageViewer.Builder(getActivity(), images)
                    .setStartPosition(0)
                    .hideStatusBar(false)
                    .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorTranslucentBlack))
                    .show();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        app = (AppController) getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.eventos_detail, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventosEntity = (EventosEntity) bundle.getSerializable("evento");
            showUI(eventosEntity);
        }
        return view;
    }


    @OnClick(R.id.calendar_bt)
    public void calendar_bt(){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        String dir=null;
        try {
            Double lat=Double.parseDouble(eventosEntity.getLat());
            Double lgn=Double.parseDouble(eventosEntity.getLng());
            addresses = geocoder.getFromLocation(lat,lgn, 1);
            if (addresses.size()>0){
                dir=addresses.get(0).getAddressLine(0);
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


        Date beginTime = FormatDate.getDate(eventosEntity.getStarts(),getResources().getString(R.string.format_date_events));
        Date endTime = FormatDate.getDate(eventosEntity.getEnds(),getResources().getString(R.string.format_date_events));
        long begin_time = beginTime.getTime();
        long end_time=endTime.getTime();
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin_time);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end_time);
        intent.putExtra(CalendarContract.Events.TITLE, ""+eventosEntity.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, ""+eventosEntity.getText());
        if (dir!=null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "" + dir);
        }
               // .putExtra(CalendarContract.Events.EVENT_LOCATION,""+addresses.get(0).getAdminArea());
        startActivity(intent);
    }

    private void showUI(EventosEntity eventosEntity) {
        String static_url = "https://maps.googleapis.com/maps/api/staticmap?center="
                + eventosEntity.getLat() + "," + eventosEntity.getLng() +
                "&zoom=13&size="+ String.valueOf(800)+"x" + String.valueOf(300) +"&maptype=roadmap%20&markers=color:orange%7C"
                + eventosEntity.getLat() + "," + eventosEntity.getLng() + "&key="
                + getString(R.string.google_maps_key);
        Glide.with(mContext).load(static_url).apply(new RequestOptions().centerCrop()).into(static_map_iv);

        String dateStar=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",eventosEntity.getStarts(),locale);
        String dateEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss","dd 'de' MMMM",eventosEntity.getEnds(),locale);
        if (dateStar.equals(dateEnd)){
            String hourStart=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntity.getStarts(),locale).trim();
            String hourEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntity.getStarts(),locale).trim();

            if (hourStart.equals("")){
                try {
                    hourStart=""+FormatDate.getFormatMin(eventosEntity.getStarts(),mContext.getResources().getString(R.string.format_date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (hourEnd.equals("")){
                try {
                    hourEnd=""+FormatDate.getFormatMin(eventosEntity.getStarts(),mContext.getResources().getString(R.string.format_date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (hourStart.equals(hourEnd)){
                date_tv.setText(""+dateStar+" a las "+hourStart);
            }else{
                date_tv.setText(""+dateStar+" de "+hourStart+" a "+hourEnd);
            }
        }else{
            String hourStart=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntity.getStarts(),locale).trim();
            String hourEnd=""+DateFormatter.formatDate("yyyy-MM-dd'T'HH:mm:ss",
                    "hh:mm a",eventosEntity.getStarts(),locale).trim();

            if (hourStart.equals("")){
                try {
                    hourStart=""+FormatDate.getFormatMin(eventosEntity.getStarts(),mContext.getResources().getString(R.string.format_date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (hourEnd.equals("")){
                try {
                    hourEnd=""+FormatDate.getFormatMin(eventosEntity.getStarts(),mContext.getResources().getString(R.string.format_date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (hourStart!=null && hourStart!="" && hourEnd!=null && hourEnd!=""){
                date_tv.setText("Del "+dateStar+" a las "+hourStart+" al "+dateEnd+" a las "+hourEnd);
            }else{
                date_tv.setText("Del "+dateStar+" de "+hourStart+" a "+hourEnd);
            }
            //date_tv.setText("Del "+dateStar + " al "+dateEnd);
        }
        title_tv.setText(eventosEntity.getName());
        event_description.setText(eventosEntity.getText());
        Glide.with(mContext)
                .load(eventosEntity.getImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image))
                //.apply(new RequestOptions().override(1366,768))
                .into(image_iv);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DM.getDisplayWidth(mContext),DM.getDisplayWidth(mContext));
        image_iv.setLayoutParams(params);
    }

}
