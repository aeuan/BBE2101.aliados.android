package com.dacodes.bepensa.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.LoginUser;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.entities.ResponseSettings;
import com.dacodes.bepensa.models.ValidateRegistrationEntity;
import com.dacodes.bepensa.utils.BottomSheetDivisions;
import com.dacodes.bepensa.utils.ObjectToList;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_CONTACT_WHATS;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;
import static com.dacodes.bepensa.api.RetrofitWebServices.LOGIN;
import static com.dacodes.bepensa.api.RetrofitWebServices.VALIDATE_USER;

public class LoginActivity extends AppCompatActivity implements RetrofitWebServices.DefaultResponseListeners
,BottomSheetDivisions.selectDivision{
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    int mHeight;
    int mWidth;
    Context mContext;
    AppController appController;
    Toast mToast;
    List <?> divisions = new ArrayList();
    private DivisionEntity divisionEntity;

    @BindView(R.id.login_bt) Button login_bt;
    //@BindView(R.id.register_bt)Button register_bt;
    @BindView(R.id.clave_empleado_et)EditText clave_empleado_et;
    @BindView(R.id.password_et)TextInputEditText password_et;
    @BindView(R.id.division_et)TextInputEditText division_et;
    RetrofitWebServices retrofitWebServices;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.edit_password)Button edit_password;
    @BindView(R.id.form)TextView form;
    String NUMBER_CONTACT = "";
    String GOOGLE_FORM = "";

    BottomSheetDivisions bottomSheetDivisions;

    @OnClick(R.id.edit_password)
    public void editPassword(){
        Intent intent = new Intent(mContext,EditPassword.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        setContentView(R.layout.activity_login_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
        appController = (AppController)getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        retrofitWebServices.getSettings();
        retrofitWebServices.getDivisions();
        bottomSheetDivisions= new BottomSheetDivisions(this,true);
        division_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (divisions.size()>0) {
                    if(!bottomSheetDivisions.isAdded()) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("divisions", (ArrayList<String>) divisions);
                        bottomSheetDivisions.setArguments(bundle);
                        bottomSheetDivisions.show(getSupportFragmentManager(), "example");
                    }
                }else{
                    if (!bottomSheetDivisions.isAdded()) {
                        bottomSheetDivisions.show(getSupportFragmentManager(), "example");
                    }
                }

            }
        });


        division_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    division_et.performClick();
                }
            }
        });
        division_et.setKeyListener(null);

        password_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    login_bt.performClick();
                    return true;
                }
                return false;
            }
        });
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

    private void displayToast(String message){
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext,message ,Toast.LENGTH_SHORT);
        mToast.show();
    }


    private boolean validateLoginData(){
        String password = password_et.getText().toString();
        String username = clave_empleado_et.getText().toString();

        if(divisionEntity ==null){
            division_et.setError("Selecciona una división");
            clave_empleado_et.setFocusable(true);
            return false;
        }else{
            division_et.setError(null);
        }

        if(TextUtils.isEmpty(username)){
            clave_empleado_et.setError("Usuario vacío");
            clave_empleado_et.setFocusable(true);
            return false;
        }else{
            clave_empleado_et.setError(null);
            clave_empleado_et.setFocusable(true);
        }

        if(TextUtils.isEmpty(password)){
            password_et.setError("Contraseña vacía");
            return false;
        }else{
            password_et.setError(null);
        }

        return true;
    }
    @OnClick(R.id.login_bt)
    public void submit(View view) {
        if(validateLoginData()){
            LoginUser loginUser = new LoginUser();
            loginUser.setClose_all_conections(0);
            loginUser.setPassword(password_et.getText().toString());
            loginUser.setDivision(divisionEntity.getId());
            loginUser.setUsername(clave_empleado_et.getText().toString());
            retrofitWebServices.loginUser(loginUser);
        }
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case LOGIN:
                ProfileEntity response = (ProfileEntity) object;
                appController.setAccessToken(response.getToken().getHash());
                appController.setUserId(response.getUuid());
                appController.setUsername(response.getUsername());
                appController.setImageUrl(response.getAvatarUrl());
                appController.setEmail(response.getEmail());
                appController.setPhone(response.getContact());
                String name = response.getName();
                String lastName = response.getLast_name();
                appController.setName(name);
                appController.setDivision(response.getDivision().getName());
                appController.setIdDivision(response.getDivision().getId());
                appController.setLastName(lastName);
                appController.setLoggedIn(true);
                appController.setRank(response.getRank());
                if (!response.isHidden_ranking()){
                    appController.setRankingPrivacy(true);
                }else{
                    appController.setRankingPrivacy(false);
                }
                appController.setImageDivision(response.getDivision().getImage());
                appController.setPoints(((ProfileEntity) object).getPoints());
                appController.setPointAvailable1(response.getPoints_available());
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("profile", response);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                List<DivisionEntity> list = new ArrayList<>();
                for (int i = 0; i < divisions.size(); i++) {
                    DivisionEntity division = (DivisionEntity)divisions.get(i);
                    if (division.isAllow_login())
                        list.add(division);
                }
                divisions = list;
                break;

            case VALIDATE_USER:
                ValidateRegistrationEntity validateRegistrationEntity = (ValidateRegistrationEntity) object;
                if(validateRegistrationEntity.getSuccess().contentEquals("ok")){
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    registerIntent.putExtra("username", clave_empleado_et.getText().toString());
                    registerIntent.putExtra("password", password_et.getText().toString());
                    registerIntent.putExtra("division", divisionEntity.getId());
                    startActivity(registerIntent);
                    }
                break;
            case GET_CONTACT_WHATS:
                ResponseSettings settings = (ResponseSettings)object;
                NUMBER_CONTACT = settings.getWhatsApp();
                GOOGLE_FORM = settings.getGoogleFormAndroid()+getString(R.string.google_form_4);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    form.setText(Html.fromHtml("¿Tienes algún problema?<br>Ingresa <a href="+GOOGLE_FORM+"> aquí</a> para reportarlo.", Html.FROM_HTML_MODE_COMPACT));
                else
                    form.setText(Html.fromHtml("¿Tienes algún problema?<br>Ingresa <a href="+GOOGLE_FORM+"> aquí</a> para reportarlo."));
                form.setMovementMethod(LinkMovementMethod.getInstance());
                form.setOnClickListener(view ->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(GOOGLE_FORM))));
                break;
        }

    }

    @Override
    public void onFailError(int id, int code, String message) {
        switch (code){
            case 1030:
                //displayToast(message);
                retrofitWebServices.validateUser(clave_empleado_et.getText().toString(), String.valueOf(divisionEntity.getId()), password_et.getText().toString());
                break;
            case 1060:
                alertAccess(message);
                break;
            case 1009:
                displayToast(message);
                break;
            default:
                displayToast(message);
                break;
        }
    }

    @Override
    public void onWebServiceStart() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onWebServiceEnd() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onExpireToken() {

    }

    public void alertAccess(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginUser loginUser= new LoginUser();
                        loginUser.setClose_all_conections(1);
                        loginUser.setPassword(password_et.getText().toString());
                        loginUser.setDivision(divisionEntity.getId());
                        loginUser.setUsername(clave_empleado_et.getText().toString());
                        retrofitWebServices.loginUser(loginUser);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void idDivision(DivisionEntity div) {
        if (div!=null){
            divisionEntity =div;
            division_et.setText(divisionEntity.getName());
        }
    }
}
