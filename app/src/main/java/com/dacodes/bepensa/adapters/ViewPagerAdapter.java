package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.klinker.android.simple_videoview.SimpleVideoView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewPagerAdapter extends PagerAdapter {

    private final Random random = new Random();
    private final SparseArray<TextView> mHolderArray = new SparseArray<>();
    SimpleVideoView simpleVideoView;
    private List<MediaEntity> mediaEntities;
    private Context context;
    private int mSize;
    private Activity activity;
    int networkLocal;

    public ViewPagerAdapter(Activity activity, Context context, List<MediaEntity> imageEntities,int networkLocal) {
        mSize = imageEntities.size();
        this.mediaEntities = imageEntities;
        this.context = context;
        this.activity = activity;
        this.networkLocal=networkLocal;
    }


    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mHolderArray.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        ImageView imageView;
        final ImageButton play_bt;
        //  VideoView videoView;

        v = inflater.inflate(R.layout.opportunity_pager_layout, view, false);
        imageView = (ImageView) v.findViewById(R.id.banner_iv);
        play_bt = (ImageButton) v.findViewById(R.id.play_bt);
        play_bt.setVisibility(View.GONE);

        //videoView = (VideoView) v.findViewById(R.id.videoview);
        simpleVideoView = (SimpleVideoView) v.findViewById(R.id.simple_videoview);

        if (mediaEntities.get(position).getType() == 1) {

            if (networkLocal==1){
                Glide.with(context)
                        .load(mediaEntities.get(position).getArchive())
                        .apply(new RequestOptions().centerCrop().error(
                                context.getResources().getDrawable(R.drawable.no_media_placeholder))
                                .placeholder(context.getResources().getDrawable(R.drawable.no_media_placeholder)))
                        .into(imageView);
            }else{
                File file = new File(mediaEntities.get(position).getArchive());
                Uri imageUri = Uri.fromFile(file);
                Glide.with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().centerCrop().error(
                                context.getResources().getDrawable(R.drawable.no_media_placeholder))
                                .placeholder(context.getResources().getDrawable(R.drawable.no_media_placeholder)))
                        .into(imageView);
            }

            //videoView.setVisibility(View.GONE);
            simpleVideoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else if (mediaEntities.get(position).getType() == 2) {
            {
                play_bt.setVisibility(View.VISIBLE);
                play_bt.setColorFilter(ContextCompat.getColor(context, R.color.colorOrange));

                //videoView.setVisibility(View.VISIBLE);
                simpleVideoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                final String path1 = mediaEntities.get(position).getArchive();
                final Uri uri = Uri.parse(path1);
                //videoView.setVideoURI(uri);
                //videoView.start();
                simpleVideoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(simpleVideoView.isPlaying()){
                            simpleVideoView.pause();
                            play_bt.setVisibility(View.VISIBLE);
                        }else{
                            simpleVideoView.start(uri);
                            play_bt.setVisibility(View.GONE);
                        }
       //                 simpleVideoView.setShowSpinner(true);
                    }
                });

                play_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(simpleVideoView.isPlaying()){
                            simpleVideoView.pause();
                            play_bt.setVisibility(View.VISIBLE);
                        }else{
                            simpleVideoView.start(uri);
                            play_bt.setVisibility(View.GONE);
                        }
                   //     simpleVideoView.setShowSpinner(true);
                    }
                });
            }
        }

        view.addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> images = new ArrayList<>();
                if (mediaEntities.get(position) != null) {
                    images.add(mediaEntities.get(position).getArchive());
                    new ImageViewer.Builder(activity, images)
                            .setStartPosition(0)
                            .hideStatusBar(false)
                            .setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranslucentBlack))
                            .show();
                }
            }
        });
        return v;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void addItem() {
        mSize++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        mSize--;
        mSize = mSize < 0 ? 0 : mSize;
        notifyDataSetChanged();
    }

    public void releaseVideoView() {
        if (simpleVideoView != null) {
            if (simpleVideoView.isPlaying()){
                simpleVideoView.pause();
                simpleVideoView.release();
            }
        }
    }
}