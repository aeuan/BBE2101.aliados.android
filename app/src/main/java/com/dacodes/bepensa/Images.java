package com.dacodes.bepensa;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.dacodes.bepensa.utils.CompressImage;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

public class Images extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    @BindView(R.id.ivOrigin) ImageView ivOrigin;
    @BindView(R.id.ivCompress)ImageView ivCompress;
    @BindView(R.id.tvOrigin) TextView tvOring;
    @BindView(R.id.tvCompress)TextView tvCompress;
    @BindView(R.id.ivBit)ImageView ivBit;
    @BindView(R.id.tvBit)TextView tvBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btImage)
    public void select(){
        ivOrigin.setBackground(null);
        ivCompress.setBackground(null);
        new BSImagePicker.Builder("com.dacodes.bepensa.utils.provider").build().show(getSupportFragmentManager(),"Medias");
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        if (uri!=null){
            File file2=new File(uri.getPath());
            File file=new File(uri.getPath());
            File file3=new File(CompressImage.compressImage(uri.getPath(),this));
            Glide.with(this).load(uri).into(ivOrigin);
            long fileSizeInBytes = file.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            long fileSizeInMB = fileSizeInKB / 1024;
            tvOring.setText("Tamaño:Bt::"+fileSizeInBytes+"::KB::"+fileSizeInKB+"::MB::"+fileSizeInMB);

            Glide.with(this).load(file3).into(ivBit);
            long InBytes = file3.length();
            long InKB = InBytes / 1024;
            long InMB = InKB / 1024;
            tvCompress.setText("Tamaño:Bt::"+InBytes+"::KB::"+InKB+"::MB::"+InMB);

            try {
                File compressedImageFile =
                        new Compressor(this)
                                .setDestinationDirectoryPath(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(file2);
                long bytes = compressedImageFile.length();
                long KB = bytes / 1024;
                long MB = KB / 1024;
                tvCompress.setText("Tamaño:Bt::"+bytes+"::KB::"+KB+"::MB::"+MB);
                Glide.with(this).load(compressedImageFile).into(ivCompress);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
}
