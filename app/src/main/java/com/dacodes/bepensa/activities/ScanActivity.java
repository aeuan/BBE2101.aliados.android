package com.dacodes.bepensa.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{


    private static final int REQUEST_CAMERA = 1;
    private static final String LOG_TAG = ScanActivity.class.getSimpleName();
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    Context mContext;
    Toolbar toolbar;
    AppController appController;
    //RetrofitWebServices retrofitWebServices;
    FrameLayout frameLayout;
    @BindView(R.id.viewBackground)
    View viewBackground;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        hideLoaders();
        mContext = getApplicationContext();
        toolbar = findViewById(R.id.toolbar);
        appController = (AppController)getApplication();
        toolbar.setTitle("Escanear");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        scannerView = new ZXingScannerView(this);
        frameLayout = findViewById(R.id.container);
        frameLayout.addView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if(currentApiVersion >=  Build.VERSION_CODES.M){
            if(!checkPermission()) {
                requestPermission();
            }
        }
    }



    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }


    @Override
    public void onResume() {
        super.onResume();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
//                    scannerView = findViewById(R.id.scannerView);
                    frameLayout.addView(scannerView);

//                    setContentView(scannerView);

                }

                try{
                    if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
                        scannerView.setFlash(true);
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                }

                scannerView.setAutoFocus(true);
                scannerView.setAspectTolerance(0.5f);
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }else{
            if(scannerView == null) {
                scannerView = new ZXingScannerView(this);
//                    scannerView = findViewById(R.id.scannerView);
                frameLayout.addView(scannerView);

//                    setContentView(scannerView);

            }
            try{
                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
                    scannerView.setFlash(true);
                }
            }catch (RuntimeException e){
                e.printStackTrace();
            }

            scannerView.setAutoFocus(true);
            scannerView.setResultHandler(this);
            scannerView.setAspectTolerance(0.5f);
            scannerView.startCamera();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!cameraAccepted) {
//                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("Necesita otorgar ambos permisos",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.v(LOG_TAG, myResult);
        Log.v(LOG_TAG, result.getBarcodeFormat().toString());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("user_key", result.getText());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        scannerView.stopCamera();
        super.onBackPressed();
    }


    public void hideLoaders(){
        viewBackground.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
    }

    public void displayLoaders(){
        viewBackground.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
    }

}