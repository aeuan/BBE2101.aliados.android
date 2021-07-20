package com.dacodes.bepensa.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.NewOportunity;
import com.dacodes.bepensa.activities.OpportunityDetailActivity;
import com.dacodes.bepensa.adapters.BrandsTagsAdapter;
import com.dacodes.bepensa.adapters.MediasAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.TypeEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineMediaOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.dacodes.bepensa.models.NewOpportunityResponse;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.FontSingleton;
import com.dacodes.bepensa.utils.ImagePicker;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.StringPicker;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;


public class NewOportunityStart extends Fragment implements BrandsTagsAdapter.AddNewBrand,
        RetrofitWebServices.DefaultResponseListeners, MediasAdapter.DeleteItem
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{

    private Context context;
    private View view;
    private AppController appController;
    private RetrofitWebServices retrofitWebServices;
    private int opportunityPoints = 0;

    @BindView(R.id.edDescription)
    EditText edDescription;
    @BindView(R.id.edOportunity)
    EditText edOPortunity;
    @BindView(R.id.edLocation)
    EditText edLocation;
    @BindView(R.id.rvMarcas)
    RecyclerView rvMarcas;
    @BindView(R.id.recyclerViewMedias)
    RecyclerView rvMedias;
    @BindView(R.id.cross_iv)
    FloatingActionButton cross_iv;
    @BindView(R.id.media_view)
    ImageView media_view;
    @BindView(R.id.edDivision)
    EditText etDivision;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contenedor)ConstraintLayout contenedor;
    @BindView(R.id.city_label)TextView city_label;
    @BindView(R.id.edCity)EditText edCity;
    @BindView(R.id.location_label)TextView location_label;
    @BindView(R.id.description_text_city)TextView description_text_city;
    @BindView(R.id.description_text_label)TextView description_text_label;
    @BindView(R.id.marcas_label)TextView titleMarcas;
    File file_copy=null;
    Realm realm;

    List <?> divisions = new ArrayList();
    private DivisionEntity divisionEntity;
    String stat;
    int pos;

    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    private int has_media = 0;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 101;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO = 100;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private File imageFile;
    private File videoFile;
    MultipartBody.Part imagePart = null;
    MultipartBody.Part videoPart = null;
    private static final int PICK_IMAGE_ID = 10;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 5;
    private static final int VIDEO_CAPTURE = 101;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 4;
    private int id_opportunity=0;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapterMedias;
    private LinearLayoutManager linearLayoutManager;
    private String LOG_TAG = NewOportunityStart.class.getSimpleName();
    int id_location=0;

    private MultipartBody.Part media_image0 = null;
    private MultipartBody.Part media_image1 = null;
    private MultipartBody.Part media_image2 = null;
    private MultipartBody.Part media_video0 = null;
    private Uri fileUri;
    OpportunityDetailModel opportunityDetailModel;

    @OnClick({R.id.btSave, R.id.edDivision, R.id.edOportunity, R.id.edLocation, R.id.cross_iv, R.id.select_images,R.id.edCity})
    public void submit(View view){
        switch (view.getId()){
            case R.id.btSave:
                if (((NewOportunity)getActivity()).getId_division()!=0){
                    if (((NewOportunity) getActivity()).getBrandsItems() !=null && ((NewOportunity)getActivity()).getBrandsItems()!=""){
                        if(((NewOportunity)getActivity()).getDescription()!=null
                                && ((NewOportunity)getActivity()).getDescription()!=""
                                && edDescription.getText().toString().trim()!=null
                                && edDescription.getText().toString().trim() !="") {
                            if (ValidateNetwork.getNetwork(getContext())) {
                                if (id_location == 1) {
                                    if (((NewOportunity) getActivity()).getLocation() != null) {
                                        /*
                                        for (int i = 0; i < ((NewOportunity) getActivity()).getBrands().size(); i++) {
                                            if (i == 0) {
                                                ((NewOportunity) getActivity()).setBrandsItems(
                                                        String.valueOf(((NewOportunity) getActivity()).getBrands().get(i).getId())
                                                );
                                            }else {
                                                ((NewOportunity) getActivity()).setBrandsItems(
                                                        ((NewOportunity) getActivity()).getBrandsItems() + "," +
                                                                String.valueOf(((NewOportunity) getActivity()).getBrands().get(i).getId())
                                                );
                                            }
                                        }
                                        //VERIFICAR SI EN EL ARRAY SOLO ESTA EL ITEM DE AGREGAR Y QUITARLO
                                        if (((NewOportunity) getActivity()).getBrandsItems().equals("0")) {
                                            ((NewOportunity) getActivity()).setBrandsItems("");
                                        }
*/
                                        //ACORTAMOS LATITUD Y LONGITUD A 8 DECIMALES
                                        double lat = (double) Math.round(((NewOportunity) getActivity()).getLocation().latitude * 1000000.0) / 1000000.0;
                                        double lng = (double) Math.round(((NewOportunity) getActivity()).getLocation().longitude * 1000000.0) / 1000000.0;

                                        //SI ES HAY MEDIAS EN EL ARRAY MANDAMOS 1 EN LA OPPORTUNITY DE LO CONTRARIO MANDAMOS 0
                                        if (((NewOportunity) getActivity()).getMediasFilesEntities().size() > 0) {
                                            has_media = 1;
                                        } else {
                                            has_media = 0;
                                        }

                                        opportunityPoints = ((NewOportunity) getActivity()).getOpportunityPoints();
                                        contenedor.setVisibility(View.INVISIBLE);
                                        progressBar.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(context, "Elija una ubicación", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (id_location == 2) {
                                        if (((NewOportunity) getActivity()).getRegion() > 0) {

                                            //SI ES HAY MEDIAS EN EL ARRAY MANDAMOS 1 EN LA OPPORTUNITY DE LO CONTRARIO MANDAMOS 0
                                            if (((NewOportunity) getActivity()).getMediasFilesEntities().size() > 0) {
                                                has_media = 1;
                                            } else {
                                                has_media = 0;
                                            }

                                            opportunityPoints = ((NewOportunity) getActivity()).getOpportunityPoints();
                                            contenedor.setVisibility(View.INVISIBLE);
                                            progressBar.setVisibility(View.VISIBLE);
                                            try {
                                                int idOppor = Integer.parseInt(null);
                                                if (((NewOportunity) getActivity()).getId_opportunity() > 0) {
                                                    idOppor = ((NewOportunity) getActivity()).getId_opportunity();
                                                } else {
                                                    idOppor = Integer.parseInt(null);
                                                }/*
                                                retrofitWebServices.postOpportunities(
                                                        ((NewOportunity) getActivity()).getId_division(),idOppor,
                                                        ((NewOportunity) getActivity()).getBrandsItems(),
                                                        ((NewOportunity) getActivity()).getDescription(), 0.0, 0.0, null,
                                                        null, has_media, ((NewOportunity) getActivity()).getRegion());*/
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(context, "Selecciona un estado", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            //sin internet
                            else{

                                for (int i = 0; i < ((NewOportunity) getActivity()).getBrands().size(); i++) {
                                    if (i == 0) {
                                        ((NewOportunity) getActivity()).setBrandsItems(
                                                String.valueOf(((NewOportunity) getActivity()).getBrands().get(i).getId())
                                        );
                                    } else if (i == ((NewOportunity) getActivity()).getBrands().size() - 1) {

                                    } else {
                                        ((NewOportunity) getActivity()).setBrandsItems(
                                                ((NewOportunity) getActivity()).getBrandsItems() + "," +
                                                        String.valueOf(((NewOportunity) getActivity()).getBrands().get(i).getId())
                                        );
                                    }
                                }

                                //VERIFICAR SI EN EL ARRAY SOLO ESTA EL ITEM DE AGREGAR Y QUITARLO
                                if (((NewOportunity) getActivity()).getBrandsItems().equals("0")) {
                                    ((NewOportunity) getActivity()).setBrandsItems("");
                                }


                                //SI ES HAY MEDIAS EN EL ARRAY MANDAMOS 1 EN LA OPPORTUNITY DE LO CONTRARIO MANDAMOS 0
                                if (((NewOportunity) getActivity()).getMediasFilesEntities().size() > 0) {
                                    has_media = 1;
                                } else {
                                    has_media = 0;
                                }

                                final int id_division=((NewOportunity) getActivity()).getId_division();
                                final int id_oportunity=((NewOportunity) getActivity()).getId_opportunity();
                                final int id_region=((NewOportunity) getActivity()).getRegion();
                                final String brands=((NewOportunity) getActivity()).getBrandsItems();
                                final String description=((NewOportunity) getActivity()).getDescription();
                                final double lat=0.0;
                                final double lng=0.0;
                                final String state="";
                                final int media=has_media;
                                String des=null;
                                if (edOPortunity.getText().toString()!=null || edOPortunity.getText().toString() !=""){
                                    des=edOPortunity.getText().toString();
                                }
                                final String key_hash= UUID.randomUUID().toString();
                                opportunityDetailModel = new OpportunityDetailModel();

                                DivisionEntity divisionEntity = new DivisionEntity();
                                divisionEntity.setId(id_division);
                                TypeEntity typeEntity = new TypeEntity();
                                typeEntity.setId(id_oportunity);
                                typeEntity.setDivision(divisionEntity);

                                opportunityDetailModel.setDescription(description);
                                opportunityDetailModel.setDivision(divisionEntity);
                                opportunityDetailModel.setType(typeEntity);
                                List<MediaEntity> mediaEntity= new ArrayList<>();
                                mediaEntity.add(new MediaEntity());
                                opportunityDetailModel.setMedia(mediaEntity);
                                String finalDes = des;
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            OflinePostOpportunity postOpportunity = realm.createObject(OflinePostOpportunity.class);
                                            postOpportunity.setId_division(id_division);
                                            postOpportunity.setId_oportunity(id_oportunity);
                                            postOpportunity.setId_region(id_region);
                                            postOpportunity.setBrands(brands);
                                            postOpportunity.setDescription(description);
                                            postOpportunity.setLat(lat);
                                            postOpportunity.setLng(lng);
                                            postOpportunity.setState(state);
                                            if (finalDes !=null){
                                                postOpportunity.setCity(finalDes);
                                            }
                                            postOpportunity.setHas_media(media);
                                            postOpportunity.setKey_hash(key_hash);
                                            postOpportunity.setHas_sync(true);
                                            postOpportunity.setId_colaborator(appController.getUsername());
                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            if (has_media==1){
                                                List<MediasFilesEntity> list=new ArrayList<>();
                                                for (MediasFilesEntity medias:((NewOportunity)getActivity()).getMediasFilesEntities()){
                                                    try{
                                                        File file = new File(Environment.getExternalStorageDirectory()+"/Bepensa/medias/");
                                                        copyFileOrDirectory(medias.getFile().getAbsolutePath(),file.getAbsolutePath());
                                                        list.add(medias);
                                                    }catch (Exception e){
                                                        Log.e(LOG_TAG,"Files:"+e.getLocalizedMessage());
                                                    }
                                                }
                                                if (list.size()>0){
                                                    saveFilesLocal(key_hash,list);
                                                }else{
                                                    Log.i("CatchMedias","Sin archivos");
                                                }

                                            }else{
                                                messageDialog("Gracias por compartir tu oportunidad, hemos detectado que el dispositivo no " +
                                                        "tiene acceso a internet, el reporte ha sido almacenado localmente y lo " +
                                                        "publicaremos cuando encuentre una conexión a internet");
                                            }
                                        }
                                    }, new Realm.Transaction.OnError() {
                                        @Override
                                        public void onError(Throwable error) {
                                            getActivity().onBackPressed();
                                            Log.e(LOG_TAG,"Error media local"+error.getLocalizedMessage());
                                        }
                                    });
                            }
                        }else {
                            Toast.makeText(context, "Complete el campo de descripción", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Elige una marca", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Elija una división", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.edOportunity:
                if(((NewOportunity)getActivity()).getId_division()!=0){
                    if (((NewOportunity) getActivity()).getBrands().size() <1){
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Primero seleccione una división para poder cargar los tipos de oportunidad.");
                        builder.setPositiveButton("Aceptar", null);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(true);
                        alertDialog.show();
                    }else {
                        ((NewOportunity) getActivity()).fragmentChanger(5);
                    }
                }else {
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Primero seleccione una división para poder cargar las marcas");
                    builder.setPositiveButton("Aceptar", null);
                    alertDialog = builder.create();
                    alertDialog.setCancelable(true);
                    alertDialog.show();
                }
                break;
            case R.id.edDivision:
                //((NewOportunity)getActivity()).fragmentChanger(2);
                if (ValidateNetwork.getNetwork(getContext())){
                    if(divisions!=null) {
                        if(divisions.size()>0) {
                            displayDivisionsDialog(divisions);
                        }else{
                            divisions.clear();
                            retrofitWebServices.getDivisions();
                            if (divisions.size()>0){
                                displayDivisionsDialog(divisions);
                            }
                            //Toast.makeText(context, "Cargando divisiones..", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Cargando divisiones..", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Log.e("realm","en realm");
                            RealmResults<OflineDivisionEntity> response= realm.where(OflineDivisionEntity.class)
                                    .equalTo("allow_reports",true).findAll();
                            ArrayList<DivisionEntity> divisionEntities = new ArrayList<>();
                            if (response.size()>0){
                                for (OflineDivisionEntity ofline:response){
                                    DivisionEntity division = new DivisionEntity();
                                    division.setId(ofline.getId());
                                    division.setName(ofline.getName());
                                    divisionEntities.add(division);
                                }
                                displayDivisionsLocalDialog(divisionEntities);
                            }

                        }
                    });
                    //displayDivisionsDialog(divisions);
                }
                break;
            case R.id.edLocation:
                    ((NewOportunity)getActivity()).fragmentChanger(4);
                break;
            case R.id.edCity:
                String [] dis=getResources().getStringArray(R.array.name_location);
                displayLocationsDialog(dis);
                break;
            case R.id.select_images:
                selectMediaDialog();
                break;
            case R.id.cross_iv:
                selectMediaDialog();
                break;
        }
    }

    public void selectMediaDialog(){
        final LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullViewGroup = null;
        final View dialoglayout = inflater.inflate(R.layout.dialog_medias, nullViewGroup, false);
        builder = new AlertDialog.Builder(getActivity());
        RadioButton rbImages = (RadioButton) dialoglayout.findViewById(R.id.rbImage);
        RadioButton rbVideo = (RadioButton) dialoglayout.findViewById(R.id.rbVideo);
        rbImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int con=0;
                for (MediasFilesEntity mediasFilesEntity:((NewOportunity)getActivity()).getMediasFilesEntities()){
                    if (mediasFilesEntity.getType()==1)
                        con++;
                }if (con>=3){
                    Toast.makeText(context,"Límite de imágenes excedido (3)",Toast.LENGTH_LONG).show();
                }else {
                    //verificamos si el permiso para acceder a la camara esta concedido
                    if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getActivity(), permissions[1]) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                        //Si alguno de los permisos no esta concedido lo solicita
                        ActivityCompat.requestPermissions(getActivity(), permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
                    } else {
                        Intent imageIntent = ImagePicker.getPickImageIntent(context);
                        startActivityForResult(imageIntent, PICK_IMAGE_ID);
                    }
                }
                alertDialog.dismiss();
            }
        });

        rbVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int con=0;
                for (MediasFilesEntity mediasFilesEntity:((NewOportunity)getActivity()).getMediasFilesEntities()){
                    if (mediasFilesEntity.getType()==2)
                        con++;
                }if (con>=1){
                    Toast.makeText(context,"Un solo video permitido",Toast.LENGTH_LONG).show();
                }else {
                    if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getActivity(), permissions[1]) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                        //Si alguno de los permisos no esta concedido lo solicita
                        ActivityCompat.requestPermissions(getActivity(), permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO);
                    } else {
                        Intent intent = ImagePicker.getPickVideoIntent(context);
                        startActivityForResult(intent, VIDEO_CAPTURE);
                    }
                }
                alertDialog.dismiss();
            }
        });

        builder.setView(dialoglayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog = builder.create();
        alertDialog.show();
    }

    protected TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                ((NewOportunity)getActivity()).setDescription(edDescription.getText().toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public NewOportunityStart() {
        // Required empty public constructor
    }

    public static NewOportunityStart newInstance() {
        NewOportunityStart fragment = new NewOportunityStart();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        appController = (AppController) getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_oportunity_start, container, false);
        ButterKnife.bind(this, view);
        edOPortunity.setFocusableInTouchMode(false);
        edLocation.setFocusableInTouchMode(false);
        realm =Realm.getDefaultInstance();
        edDescription.addTextChangedListener(textWatcher);
        etDivision.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                etDivision.performClick();
            }
        });
        etDivision.setKeyListener(null);

       Bundle bundle =getArguments();
       if (bundle!=null) {
           id_location = getArguments().getInt("id_location");
           switch (id_location){
               case 2:
                   if (ValidateNetwork.getNetwork(context)){
                       disableSate();
                   }else{
                       enabledState();
                   }
                   break;
               case 1:
                   if (ValidateNetwork.getNetwork(context)){
                       disableSate();
                   }else{
                       enabledState();
                   }
                   break;
               default:
                   break;
           }
       }else{
           if (ValidateNetwork.getNetwork(context)){
               disableSate();
           }else{
               enabledState();
           }
       }
        if(((NewOportunity)getActivity()).getDivision_name()!=null){
            etDivision.setText(((NewOportunity)getActivity()).getDivision_name());
        }

        if(((NewOportunity)getActivity()).getOpportunity_name()!=null){
            edOPortunity.setText(((NewOportunity)getActivity()).getOpportunity_name());
        }

        if(((NewOportunity)getActivity()).getAddress()!=null){
            edLocation.setText(((NewOportunity)getActivity()).getAddress());
        }

        if(((NewOportunity)getActivity()).getDescription()!=null){
            edDescription.setText(((NewOportunity)getActivity()).getDescription());
        }

        return view;
    }
    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null && networkInfo.isConnected()) {
            activeStateNetwork();
        }else {
            enabledState();
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void activeStateNetwork(){
        Bundle bundle =getArguments();
        if (bundle!=null) {
            id_location = getArguments().getInt("id_location");
            switch (id_location){
                case 2:
                    enabledState();
                    break;
                case 1:
                    disableSate();
                    break;
            }
        }
    }

    public void enabledState(){
        location_label.setVisibility(View.GONE);
        edLocation.setVisibility(View.GONE);
        description_text_label.setVisibility(GONE);
        city_label.setVisibility(View.VISIBLE);
        edCity.setVisibility(View.VISIBLE);
        description_text_city.setVisibility(View.VISIBLE);
    }

    public void disableSate(){
        location_label.setVisibility(View.VISIBLE);
        edLocation.setVisibility(View.VISIBLE);
        description_text_label.setVisibility(VISIBLE);
        city_label.setVisibility(View.GONE);
        edCity.setVisibility(View.GONE);
        description_text_city.setVisibility(GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMarcas.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false);
        adapter = new BrandsTagsAdapter(context, ((NewOportunity)getActivity()).getBrands(), this);
        rvMarcas.setLayoutManager(linearLayoutManager);
        rvMarcas.setAdapter(adapter);

        if(((NewOportunity)getActivity()).getMediasFilesEntities().size()!=0){
            //cross_iv.setVisibility(GONE);
            setupRecyclerViewMedias();
        }

        retrofitWebServices.getDivisions();

       etDivision.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(divisions!=null) {
                        if(divisions.size()>0) {
                            displayDivisionsDialog(divisions);
                        }
                    }
                }
            }
        });
       etDivision.setKeyListener(null);

    }


    public void setupRecyclerViewMedias(){
        cross_iv.setVisibility(GONE);
        rvMedias.setVisibility(VISIBLE);

        rvMedias.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false);
        adapterMedias = new MediasAdapter(((NewOportunity)getActivity()).getMediasFilesEntities(),context, getActivity(), this);
        rvMedias.setLayoutManager(linearLayoutManager);
        rvMedias.setAdapter(adapterMedias);
    }

    @Override
    public void onAddBrand() {
        if(((NewOportunity)getActivity()).getId_division()!=0) {
            ((NewOportunity) getActivity()).fragmentChanger(3);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Primero seleccione una división para poder cargar las marcas");
            builder.setPositiveButton("Aceptar", null);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override
    public void onDeleteBrand(int position) {
        ((NewOportunity)getActivity()).getBrands().set(1,new BrandEntity(0,"Agregar Nuevo",""));
        ((NewOportunity)getActivity()).getBrands().remove(position);
        ((NewOportunity)getActivity()).setBrandsItems(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_ID){
                Bitmap bitmap = ImagePicker.getImageFromResult(context, resultCode, data);
                if (bitmap != null) {
                    try {
                        imageFile = convertBitmapToFile(bitmap, context);

                        if(((NewOportunity)getActivity()).getMediasFilesEntities().size()<4){
                            int images_count = 0;
                            for(MediasFilesEntity mediasFilesEntity : ((NewOportunity)getActivity()).getMediasFilesEntities()){
                                if (mediasFilesEntity.getType()==1){
                                    images_count = images_count + 1;
                                }
                            }
                            if(images_count<3){
                                ((NewOportunity) getActivity()).getMediasFilesEntities().add(new MediasFilesEntity(1, "jpg", imageFile));

                                setupRecyclerViewMedias();
                            }else {
                                Toast.makeText(context, "Límite de imágenes excedido (3)",Toast.LENGTH_SHORT).show();

                            }
                        }

                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                        imagePart = MultipartBody.Part.createFormData("media_image0", imageFile.getName(), requestFile);
                    } catch (IOException e) {

                    }
                }
            }
            if (requestCode == VIDEO_CAPTURE){
                Uri contentURI = data.getData();

                String selectedVideoPath = getRealPathFromURI(context, contentURI, "VIDEO");
                videoFile = new File(selectedVideoPath);
                RequestBody videoBody = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
                videoPart = MultipartBody.Part.createFormData("media_video0", videoFile.getName(), videoBody);

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(selectedVideoPath);
                int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                retriever.release();
                long fileSizeInBytes = videoFile.length();
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;

                if(fileSizeInMB>100){//MAX SIZE 100MB
                    Toast.makeText(context, "El archivo que intentas subir es demasiado grande",Toast.LENGTH_SHORT).show();
                }else if(duration>15900){
                    Toast.makeText(context, "El video no puede durar mas de 15 segundos",Toast.LENGTH_SHORT).show();
                } else {
                    if(((NewOportunity)getActivity()).getMediasFilesEntities().size()<4){
                        int video_count = 0;
                        for(MediasFilesEntity mediasFilesEntity : ((NewOportunity)getActivity()).getMediasFilesEntities()){
                            if (mediasFilesEntity.getType() == 2){
                                video_count = video_count + 1;
                            }
                        }
                        if(video_count<1){
                            ((NewOportunity) getActivity()).getMediasFilesEntities().add(new MediasFilesEntity(2, "mp4", videoFile));
                            setupRecyclerViewMedias();
                        }else {
                            Toast.makeText(context, "Un solo video permitido",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        }
    }

    public  void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {
                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void copyFile(File sourceFile, File destFile){
        FileChannel source = null;
        FileChannel destination = null;

        try {
            if (!destFile.getParentFile().exists())
                destFile.getParentFile().mkdirs();

            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            file_copy=destFile;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException e) {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    public static String getRealPathFromURI(Context context,Uri contentURI,String type) {

        String result  = null;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
                Log.d("TAG", "result******************" + result);
            } else {
                cursor.moveToFirst();
                int idx = 0;
                if(type.equalsIgnoreCase("IMAGE")){
                    idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                }else if(type.equalsIgnoreCase("VIDEO")){
                    idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                }else if(type.equalsIgnoreCase("AUDIO")){
                    idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                }
                result = cursor.getString(idx);
                Log.d("TAG", "result*************else*****" + result);
                cursor.close();
            }
        } catch (Exception e){
            Log.e("TAG", "Exception ",e);
        }
        return result;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public static File convertBitmapToFile(Bitmap bitmapFoto, Context context) throws IOException {
        long time= System.currentTimeMillis();
        String name = "avatar_" + time;
        File file = new File(context.getCacheDir(), name + ".png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapFoto.compress(Bitmap.CompressFormat.PNG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        return file;
    }

    private void displayLocationsDialog(final String [] values) {
        final androidx.appcompat.app.AlertDialog alertDialog;
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullViewGroup = null;
        final View dialoglayout = inflater.inflate(R.layout.division_dialog_layout, nullViewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.string_picker);
        StringPicker myStringPicker = dialoglayout.findViewById(R.id.string_picker);
        TextView positiveButton = dialoglayout.findViewById(R.id.positiveButton);
        TextView negativeButton = dialoglayout.findViewById(R.id.negativeButton);
        TextView tvTitulo = dialoglayout.findViewById(R.id.tvTitulo);
        negativeButton.setVisibility(GONE);
        tvTitulo.setText("Selecciona un estado");
        myStringPicker.setWrapSelectorWheel(true);
        myStringPicker.setMaxValue(values.length-1);
        myStringPicker.setMinValue(0);
        myStringPicker.setValue(((NewOportunity)getActivity()).getIndexSelectedState());
        myStringPicker.setDisplayedValues(values);
        myStringPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        myStringPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                stat=values[newVal];
                ((NewOportunity)getActivity()).setIndexSelectedState(newVal);
            }
        });
        builder.setView(dialoglayout);
        alertDialog = builder.create();
        Button positive = dialoglayout.findViewById(R.id.positiveButton);
        Button negative = dialoglayout.findViewById(R.id.negativeButton);
        positive.setTypeface(FontSingleton.getInstance().getTrade18());
        negative.setTypeface(FontSingleton.getInstance().getTrade18());
        positive.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (stat!=null) {
                    edCity.setText("" + stat);
                    ((NewOportunity) getActivity()).setRegion(((NewOportunity)getActivity()).getIndexSelectedState() + 1);
                }else{
                    edCity.setText("" + values[0]);
                    ((NewOportunity) getActivity()).setRegion(1);
                }
                alertDialog.cancel();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.7f);
        int dialogWindowHeight = (int) (displayHeight * 0.5f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    private void displayDivisionsLocalDialog(final ArrayList<DivisionEntity> list) {
        final androidx.appcompat.app.AlertDialog alertDialog;
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullViewGroup = null;
        final View dialoglayout = inflater.inflate(R.layout.division_dialog_layout, nullViewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.string_picker);
        StringPicker myStringPicker = dialoglayout.findViewById(R.id.string_picker);
        TextView positiveButton = dialoglayout.findViewById(R.id.positiveButton);
        TextView negativeButton = dialoglayout.findViewById(R.id.negativeButton);
        negativeButton.setVisibility(GONE);
        myStringPicker.setWrapSelectorWheel(true);
        final String [] values = new String [list.size()];
        for(int i = 0; i<list.size(); i++){
            DivisionEntity divisionEntity = (DivisionEntity) list.get(i);
            values[i] = divisionEntity.getName();
        }
        myStringPicker.setMaxValue(values.length-1);
        myStringPicker.setMinValue(0);
        myStringPicker.setValue(((NewOportunity)getActivity()).getIndexSelected());
        myStringPicker.setDisplayedValues(values);
        myStringPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        myStringPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ((NewOportunity)getActivity()).setIndexSelected(newVal);
                divisionEntity = (DivisionEntity) list.get(newVal);
            }
        });
        //myStringPicker.setValue(parametrosEntity.getGenero());
        builder.setView(dialoglayout);
        alertDialog = builder.create();
        Button positive = dialoglayout.findViewById(R.id.positiveButton);
        Button negative = dialoglayout.findViewById(R.id.negativeButton);
        positive.setTypeface(FontSingleton.getInstance().getTrade18());
        negative.setTypeface(FontSingleton.getInstance().getTrade18());
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divisionEntity!=null) {
                    etDivision.setText(divisionEntity.getName());
                    ((NewOportunity)getActivity()).setId_division(divisionEntity.getId());
                    ((NewOportunity)getActivity()).setDivision_name(divisionEntity.getName());
                    ((NewOportunity)getActivity()).setOpportunity_name(null);
                    ((NewOportunity)getActivity()).setDescription(null);
                    ((NewOportunity)getActivity()).setAddress(null);
                    ((NewOportunity)getActivity()).getBrands().clear();
                    ((NewOportunity)getActivity()).getBrands().add(new BrandEntity(0,"Agregar nuevo",""));
                    ((NewOportunity)getActivity()).fragmentChanger(1);

                }else{
                    divisionEntity = (DivisionEntity) list.get(((NewOportunity)getActivity()).getIndexSelected());
                    etDivision.setText(divisionEntity.getName());
                    ((NewOportunity)getActivity()).setId_division(divisionEntity.getId());
                    ((NewOportunity)getActivity()).setDivision_name(divisionEntity.getName());
                    //}
                }
                alertDialog.cancel();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.7f);
        int dialogWindowHeight = (int) (displayHeight * 0.5f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    private void displayDivisionsDialog(final List<?> divisions) {
        final androidx.appcompat.app.AlertDialog alertDialog;
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullViewGroup = null;
        final View dialoglayout = inflater.inflate(R.layout.division_dialog_layout, nullViewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.string_picker);
        StringPicker myStringPicker = dialoglayout.findViewById(R.id.string_picker);
        TextView positiveButton = dialoglayout.findViewById(R.id.positiveButton);
        TextView negativeButton = dialoglayout.findViewById(R.id.negativeButton);
        negativeButton.setVisibility(GONE);

        myStringPicker.setWrapSelectorWheel(true);

        for(int i = 0; i<divisions.size(); i++){
            DivisionEntity divisionEntity = (DivisionEntity) divisions.get(i);
            if (!divisionEntity.isAllow_reports()) {
                divisions.remove(i);
            }
        }
        final String [] values = new String [divisions.size()];
        for(int i = 0; i<divisions.size(); i++){
            DivisionEntity divisionEntity = (DivisionEntity) divisions.get(i);
            values[i] = divisionEntity.getName();
        }

        myStringPicker.setMaxValue(values.length-1);
        myStringPicker.setMinValue(0);
        myStringPicker.setValue(((NewOportunity)getActivity()).getIndexSelected());
        myStringPicker.setDisplayedValues(values);
        myStringPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        myStringPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ((NewOportunity)getActivity()).setIndexSelected(newVal);
                divisionEntity = (DivisionEntity) divisions.get(newVal);
            }
        });
        //myStringPicker.setValue(parametrosEntity.getGenero());
        builder.setView(dialoglayout);
        alertDialog = builder.create();
        Button positive = dialoglayout.findViewById(R.id.positiveButton);
        Button negative = dialoglayout.findViewById(R.id.negativeButton);
        positive.setTypeface(FontSingleton.getInstance().getTrade18());
        negative.setTypeface(FontSingleton.getInstance().getTrade18());
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divisionEntity!=null) {
                    etDivision.setText(divisionEntity.getName());
                    titleMarcas.setText(divisionEntity.getBrands_title()+ "");
                    ((NewOportunity)getActivity()).setId_division(divisionEntity.getId());
                    ((NewOportunity)getActivity()).setDivision_name(divisionEntity.getName());
                    ((NewOportunity)getActivity()).setOpportunity_name(null);
                    ((NewOportunity)getActivity()).setDescription(null);
                    ((NewOportunity)getActivity()).setAddress(null);
                    ((NewOportunity)getActivity()).getBrands().clear();
                    ((NewOportunity)getActivity()).getBrands().add(new BrandEntity(0,"Agregar nuevo",""));
                    ((NewOportunity)getActivity()).fragmentChanger(1);

                }else{
                        divisionEntity = (DivisionEntity) divisions.get(((NewOportunity)getActivity()).getIndexSelected());
                        titleMarcas.setText(divisionEntity.getBrands_title()+ "");
                        etDivision.setText(divisionEntity.getName());
                        ((NewOportunity)getActivity()).setId_division(divisionEntity.getId());
                        ((NewOportunity)getActivity()).setDivision_name(divisionEntity.getName());
                    //}
                }
                alertDialog.cancel();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.7f);
        int dialogWindowHeight = (int) (displayHeight * 0.5f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                break;
            case RetrofitWebServices.POST_OPPORTUNITY:
                NewOpportunityResponse newOpportunityResponse = (NewOpportunityResponse) object;
                int points = appController.getPoints();
                opportunityPoints=newOpportunityResponse.getPoints();
                points = points + opportunityPoints;
                appController.setPoints(points);
                Toast.makeText(context, "Gracias por reportar tu oportunidad en Aliados Bepensa. En breve nos pondremos en contacto contigo.", Toast.LENGTH_SHORT).show();

                if(newOpportunityResponse.getHasMedia()){
                    id_opportunity=newOpportunityResponse.getId();
                    List<MediasFilesEntity> mediasFilesEntities =((NewOportunity)getActivity()).getMediasFilesEntities();

                    progressBar.setVisibility(GONE);
                    Intent intent2 = new Intent(getContext(),OpportunityDetailActivity.class);
                    intent2.putExtra("id", id_opportunity);
                    intent2.putExtra("files_media",2);
                    intent2.putExtra("networkLocal",1);
                    intent2.putExtra("list_media", (Serializable) mediasFilesEntities);
                    startActivity(intent2);
                    getActivity().finish();
                }else{
                    progressBar.setVisibility(GONE);
                    Intent intent = new Intent(getContext(),OpportunityDetailActivity.class);
                    intent.putExtra("id", newOpportunityResponse.getId());
                    intent.putExtra("files_media",1);
                    intent.putExtra("networkLocal",1);
                    startActivity(intent);
                    getActivity().finish();
                }
                break;
            case RetrofitWebServices.PUT_MEDIA_OPPORTUNITY:
                    progressBar.setVisibility(GONE);
                    Toast.makeText(context, "Archivos multimedia cargados exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),OpportunityDetailActivity.class);
                    intent.putExtra("id", id_opportunity);
                    startActivity(intent);
                    getActivity().finish();
                    break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(GONE);
        switch (status){
            case 400:
                Toast.makeText(context,"Tuvimos un problema con los archivos multimedia,porfavor intente de nuevo",Toast.LENGTH_LONG).show();
                break;
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),appController,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
        }
        switch (id){
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
        contenedor.setVisibility(View.VISIBLE);
        Log.e("status","status"+status+"message"+message);
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
    public void dialogSurvey(int status) {
        this.status_survey=status;
        compareDialog();
    }

    @Override
    public void dialogOpportunity(int status) {
        this.status_opportunity=status;
        compareDialog();
    }

    @Override
    public void dialogLogout(int status) {
        this.status_logout=status;
        compareDialog();
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    @Override
    public void onDeleteItem(int position) {

        ((NewOportunity)getActivity()).getMediasFilesEntities().remove(position);
        adapterMedias.notifyItemRemoved(position);
        adapterMedias.notifyDataSetChanged();

        if( ((NewOportunity)getActivity()).getMediasFilesEntities().size() == 0){
            //cross_iv.setVisibility(VISIBLE);
            rvMedias.setVisibility(GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent imageIntent = ImagePicker.getPickImageIntent(context);
                    startActivityForResult(imageIntent, PICK_IMAGE_ID);
                } else {
                    Toast.makeText(context, "Para poder hacer uso de lo cámara es necesario conceder los permisos", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = ImagePicker.getPickVideoIntent(context);
                    startActivityForResult(intent, VIDEO_CAPTURE);
                } else {
                    Toast.makeText(context, "Es necesario otorgar permisos de almacenamiento", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void saveFilesLocal(final String  key_hash, final List<MediasFilesEntity> mediasFilesEntities){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                List<MediasFilesEntity> medias=mediasFilesEntities;
                RealmList<OflineMediaOpportunity> oflineMediaOpportunities = new RealmList<>();
                OflineListMedia listMedia = bgRealm.createObject(OflineListMedia.class);
                for (MediasFilesEntity mfiles:medias){
                    OflineMediaOpportunity mediaOpportunity = bgRealm.createObject(OflineMediaOpportunity.class);
                    mediaOpportunity.setType(mfiles.getType());
                    mediaOpportunity.setExtension(mfiles.getExtension());
                    mediaOpportunity.setPath(mfiles.getFile().getAbsolutePath());
                    oflineMediaOpportunities.add(mediaOpportunity);
                }
                listMedia.setOflineMediaOpportunities(oflineMediaOpportunities);
                listMedia.setHas_sync(false);
                listMedia.setKey_hash(key_hash);
                listMedia.setId_colaborator(appController.getUsername());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                messageDialog("Gracias por compartir tu oportunidad, hemos detectado q" +
                        "ue el dispositivo no tiene acceso a internet, el reporte ha sido almacenado localmente " +
                        "y lo publicaremos cuando encuentre una conexión a internet");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                getActivity().onBackPressed();

            }
        });
    }

    public void messageDialog(String message){
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), OpportunityDetailActivity.class);
                        if (((NewOportunity)getActivity()).getMediasFilesEntities().size()>0) {
                            List<MediasFilesEntity> medias=((NewOportunity)getActivity()).getMediasFilesEntities();
                            List<MediaEntity> mediaEntities=new ArrayList<>();
                            for (int i=0;i<medias.size();i++){
                                MediaEntity mediaEntity = new MediaEntity();
                                mediaEntity.setType(medias.get(i).getType());
                                mediaEntity.setArchive(medias.get(i).getFile().getAbsolutePath());
                            }
                            opportunityDetailModel.setMedia(mediaEntities);
                        }else{

                        }
                        intent.putExtra("networkLocal",2);
                        intent.putExtra("dataNetworLocal",opportunityDetailModel);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .show();
    }
}
