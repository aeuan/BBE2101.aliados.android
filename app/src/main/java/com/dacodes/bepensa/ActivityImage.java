package com.dacodes.bepensa;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityImage extends AppCompatActivity {

    @BindView(R.id.ivImage)ImageView ivImage;
    @BindView(R.id.ibClose)ImageButton ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        String url=getIntent().getStringExtra("url");
        if (url!=null){
            Glide.with(this).load(url)
                    .into(ivImage);
        }else
            onBackPressed();
    }

    @OnClick(R.id.ibClose)
    public void close(){
        onBackPressed();
    }
}
