package com.dacodes.bepensa.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.ResponseSettings;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.dacodes.bepensa.AppController.getContext;

public class Contact extends AppCompatActivity implements DCListeners.retrofitResponse {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wtContact)
    AppCompatButton wtContact;
    @BindView(R.id.form) TextView form;
    private AppController appController;
    private String NUMBER_CONTACT="";
    String GOOGLE_FORM = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        appController = (AppController)getApplication();
        setSupportActionBar(toolbar);
        toolbar.setTitle("CONTACTO");
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new DCService().Build(this).setCode(100).setCallResponse(new DCResponse<ResponseSettings>() {
            @Override
            public Call<ResponseSettings> getResponse(DCResponseInterface dcResponseInterface) {
                return appController.getWebServices().getSettings();
            }
        }).init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @OnClick(R.id.wtContact)
    public void sendMessageWhatshap(){
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            String url = "https://wa.me/"+NUMBER_CONTACT;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Toast.makeText(this, "No tienes whatsApp instalado",
                    Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);

        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onFailure(int i, int i1, String s) {

    }

    @Override
    public void onResponse(int i, String s, Object o) {
        switch (i){
            case 100:
                ResponseSettings settings = (ResponseSettings)o;
                NUMBER_CONTACT = settings.getWhatsApp();
                GOOGLE_FORM = settings.getGoogleFormAndroid()+getString(R.string.google_form_3,appController.getDivision(),appController.getUsername());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    form.setText(Html.fromHtml("¿Tienes algún problema?<br>Ingresa <a href="+GOOGLE_FORM+"> aquí</a> para reportarlo.", Html.FROM_HTML_MODE_COMPACT));
                else
                    form.setText(Html.fromHtml("¿Tienes algún problema?<br>Ingresa <a href="+GOOGLE_FORM+"> aquí</a> para reportarlo."));
                form.setMovementMethod(LinkMovementMethod.getInstance());
                if (!TextUtils.isEmpty(settings.getGoogleFormAndroid()))
                    form.setOnClickListener(view ->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(GOOGLE_FORM))));
                else
                    form.setOnClickListener(view -> Toast.makeText(this,"Url incorrecta",Toast.LENGTH_LONG).show());
                break;
        }
    }
}
