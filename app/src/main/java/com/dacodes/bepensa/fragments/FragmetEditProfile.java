package com.dacodes.bepensa.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EditProfile;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.utils.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_PROFILE_USER;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_VALIDATE_CONTACT;

public class FragmetEditProfile extends Fragment implements RetrofitWebServices.DefaultResponseListeners{

    @BindView(R.id.label)TextView label;
    @BindView(R.id.login_description_tv)TextView login_description_tv;
    @BindView(R.id.profile_image)ImageView profile;
    @BindView(R.id.email_et)EditText email_et;
    @BindView(R.id.password_til)TextInputLayout password_til;
    @BindView(R.id.confirm_password_til)TextInputLayout confirm_password_til;
    @BindView(R.id.phone_et)EditText phone_et;
    @BindView(R.id.end_bt)Button end_btn;
    @BindView(R.id.rela_term)
    RelativeLayout rela_term;

    AppController app;
    Context mContext;
    private File imageFile=null;
    MultipartBody.Part avatar_url = null;
    private static final int PICK_IMAGE_ID = 10;
    String LOG_TAG=FragmetEditProfile.class.getSimpleName();
    RetrofitWebServices retrofitWebServices;
    private String[] permissions = new String[]{android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 101;
    boolean status_email=false;
    boolean status_contact=false;
    boolean status_image=false;
    byte[] b;
    RequestBody emailBody;
    private Activity mActivity;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app =(AppController)getActivity().getApplicationContext();
        mContext = getContext();
        mActivity=getActivity();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment_two_layout, container, false);
        ButterKnife.bind(this,rootView);
        label.setText("No es necesario ingresar el correo institucional de Bepensa");
        rela_term.setVisibility(View.GONE);
        hiddenOption();
        loadData();
        end_btn.setText("Guardar");
        phone_et.addTextChangedListener(textWatcher);
        email_et.addTextChangedListener(textWatcher2);
        return rootView;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (app.getPhone().equals("+52"+phone_et.getText().toString())){
                end_btn.setText("Guardar");
                status_contact=false;
            }else{
                status_contact=true;
                end_btn.setText("Siguente");
            }
        }
    };

    private final TextWatcher textWatcher2 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (app.getEmail().equals(email_et.getText().toString())){
                status_email=false;
            }else{
                status_email=true;
            }
        }
    };

    @OnClick(R.id.profile_image)
    public void image(){
        if (ActivityCompat.checkSelfPermission(mContext, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(mContext, permissions[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }else {
            Intent imageIntent = ImagePicker.getPickImageIntent(mContext);
            startActivityForResult(imageIntent, PICK_IMAGE_ID);
        }
    }

    @OnClick(R.id.end_bt)
    public void last(){
        emailBody = RequestBody.create(MediaType.parse("text/plain"), email_et.getText().toString());
        RequestBody phoneBody =RequestBody.create(MediaType.parse("text/plain"),phone_et.getText().toString());
        String email =email_et.getText().toString();
        //email=email.replace("\"","");
        String contact=phone_et.getText().toString().trim();
        if (validateInput()){
            end_btn.setEnabled(false);
             if (status_email && !status_contact && !status_image){
                 retrofitWebServices.putProfile(null,emailBody,null,null);
            }else if (status_email && status_image && !status_contact){
                 new Upload().execute();
            }else if (!status_email && status_image && !status_contact){
                 new Upload().execute();
            }else if (status_contact && !status_email && !status_image){
                 Bundle bundle = new Bundle();
                 bundle.putString("type","1");
                 bundle.putString("contact",contact);
                 ((EditProfile)getActivity()).setBundle(bundle);
                 retrofitWebServices.ValidateContact("+52"+contact);
            }else if ( status_email && status_contact && !status_image){
                 Bundle bundle = new Bundle();
                 bundle.putString("type","2");
                 bundle.putString("contact",""+phone_et.getText());
                 bundle.putString("email",""+email);
                 ((EditProfile)getActivity()).setBundle(bundle);
                 retrofitWebServices.ValidateContact("+52"+phone_et.getText().toString());
            }else if ( !status_email && status_contact && status_image){
                 MediasFilesEntity mediasFilesEntity = new MediasFilesEntity(1,"png",imageFile);
                 Bundle bundle = new Bundle();
                 bundle.putString("type","3");
                 bundle.putString("contact",""+phone_et.getText());
                 bundle.putSerializable("file",mediasFilesEntity);
                 ((EditProfile)getActivity()).setBundle(bundle);
                 retrofitWebServices.ValidateContact("+52"+phone_et.getText().toString());
             }
             else if (status_email && status_contact && status_image){
                 MediasFilesEntity mediasFilesEntity = new MediasFilesEntity(1,"png",imageFile);
                 Bundle bundle = new Bundle();
                 bundle.putString("type","4");
                 bundle.putString("contact",""+phone_et.getText());
                 bundle.putString("email",""+email_et.getText());
                 bundle.putSerializable("file",mediasFilesEntity);
                 ((EditProfile)getActivity()).setBundle(bundle);
                 retrofitWebServices.ValidateContact("+52"+phone_et.getText().toString());
             }else{
                 if(mActivity!=null) {
                     mActivity.onBackPressed();
                 }else{
                     try {
                         Intent intent=new Intent(mContext, MainActivity.class);
                         intent.putExtra("intentTypeBack",35);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         getActivity().startActivity(intent);
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                 }
             }
        }
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        end_btn.setEnabled(true);
        switch (id) {
            case PUT_PROFILE_USER:
                ProfileEntity profileEntity = (ProfileEntity) object;
                app.setEmail(profileEntity.getEmail());
                app.setPhone(profileEntity.getContact());
                app.setUsername(profileEntity.getUsername());
                app.setImageUrl(profileEntity.getAvatarUrl());
                app.setDivision(profileEntity.getDivision().getName());
                if (mActivity!=null) {
                    mActivity.onBackPressed();
                }else{
                    try {
                        Intent intent=new Intent(mContext, MainActivity.class);
                        intent.putExtra("intentTypeBack",35);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mActivity.startActivity(intent);
                    }catch (Exception e){

                    }
                }
                Toast.makeText(mContext,"Datos actualizados",Toast.LENGTH_LONG).show();
                break;
            case PUT_VALIDATE_CONTACT:
                if (((EditProfile)getActivity()).getBundle()!=null){
                    Bundle bundle = ((EditProfile)getActivity()).getBundle();
                    ((EditProfile)getActivity()).fragmentEditContact(bundle);
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        end_btn.setEnabled(true);
        switch (status){
            case 500:
                Toast.makeText(mContext,"Es posible que no tengas una conexión a internet",Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(mContext,""+message,Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onWebServiceStart() {

    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent imageIntent = ImagePicker.getPickImageIntent(mContext);
                    startActivityForResult(imageIntent, PICK_IMAGE_ID);
                } else {
                    Toast.makeText(getActivity(), "Para poder hacer uso de lo cámara es necesario conceder los permisos", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_ID){
                Bitmap bitmap = ImagePicker.getImageFromResult(mContext, resultCode, data);
                if (bitmap != null) {
                    Bitmap original = bitmap;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.PNG, 100, out);
                    //profile.setImageBitmap(original);
                    Glide.with(mContext)
                            .load(original)
                            .apply(new RequestOptions().centerCrop().circleCrop())
                            .into(profile);
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                     b= out.toByteArray();
                     status_image=true;
                    imageFile = convertBitmapToFile(bitmap, mContext);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    avatar_url = MultipartBody.Part.createFormData("avatar_url", imageFile.getName(), requestFile);

                }
            }
        }else{
            Log.e("RESULT_OK","NO RESULT");
        }
    }


    public static File convertBitmapToFile(Bitmap bitmapFoto, Context context){
        File file=null;
        try {
            long time= System.currentTimeMillis();
            String name = "avatar_" + time;
            file = new File(context.getCacheDir(), name + ".png");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmapFoto.compress(Bitmap.CompressFormat.PNG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch (Exception e){
            Log.e("Error",""+e.getLocalizedMessage());
        }
        return file;
    }

    public void loadData(){
        String telefono=app.getPhone();
        telefono = telefono.replace("+52","");
        Glide.with(mContext)
                .load(app.getImageUrl()).apply(new RequestOptions().centerCrop().circleCrop())
                .into(profile);
        email_et.setText(app.getEmail());
        phone_et.setText(telefono);
    }

    public void hiddenOption(){
        profile.setVisibility(View.VISIBLE);
        login_description_tv.setVisibility(View.GONE);
        password_til.setVisibility(View.GONE);
        confirm_password_til.setVisibility(View.GONE);
    }

    private boolean validateInput(){
        phone_et.setError(null);
        email_et.setError(null);

        if(TextUtils.isEmpty(email_et.getText().toString())){
            email_et.setError("El correo es obligatorio");
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email_et.getText().toString()).matches()) {
            email_et.setError("El formato de correo es incorrecto");
            return false;
        }

        if(TextUtils.isEmpty(phone_et.getText().toString())){
            phone_et.setError("Campo vacío");
            return false;
        }

        if(!Patterns.PHONE.matcher(phone_et.getText().toString()).matches()) {
            phone_et.setError("El número celular debe contener sólo números");
            return false;
        }

        if(phone_et.getText().toString().length()!=10) {
            phone_et.setError("El número celular debe contener 10 dígitos");
            return false;
        }

        return true;
    }

    private class Upload extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (status_email && status_image && !status_contact){
                retrofitWebServices.putProfile(null,emailBody,null,avatar_url);
            } else {
                if (!status_email && status_image && !status_contact) {
                    retrofitWebServices.putProfile(null, null, null, avatar_url);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }
}
