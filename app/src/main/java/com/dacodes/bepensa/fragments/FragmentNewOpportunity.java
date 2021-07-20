package com.dacodes.bepensa.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.BuildConfig;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.UploadMedias;
import com.dacodes.bepensa.activities.ActivityDetailOportunity;
import com.dacodes.bepensa.activities.ActivityOpportunity;
import com.dacodes.bepensa.adapters.MediasAdapter;
import com.dacodes.bepensa.adapters.RecyclerAdapterMarcas;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.LocationEntity;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineMediaOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.dacodes.bepensa.models.NewOpportunityResponse;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.FillingSuggestion;
import com.dacodes.bepensa.utils.ImagePicker;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.RemoveAccent;
import com.dacodes.bepensa.utils.TextWatcherListener;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.POST_OPPORTUNITY;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewOpportunity extends Fragment implements DialogDivisions.selectDivision
        ,RecyclerAdapterMarcas.brands,DialogStates.selectState
        ,BSImagePicker.OnSingleImageSelectedListener,DialogMedias.pickerMedias
        ,MediasAdapter.DeleteItem, TextWatcherListener.listenerWatcher,
        RetrofitWebServices.DefaultResponseListeners, AlertDIalogLogout.logout,
        AlertDIalogLogout.opportunity,AlertDIalogLogout.survey,RecyclerAdapterMarcas.optionBrand,MediasAdapter.addNewMedia
        , View.OnFocusChangeListener, DCListeners.retrofitResponse {


    @BindView(R.id.etDivision) TextView etDivision;
    @BindView(R.id.etOportunidad)TextView etOportunity;
    @BindView(R.id.etUbicacion)TextView etUbicacion;
    @BindView(R.id.rvMarca) RecyclerView rvMarca;
    @BindView(R.id.llState) LinearLayout llState;
    @BindView(R.id.llUbicacion)LinearLayout llUbicacion;
    @BindView(R.id.etEstado)TextView etEstado;
    @BindView(R.id.rvMedias)RecyclerView rvMedias;
    @BindView(R.id.faMedias) ImageButton faMedias;
    @BindView(R.id.etDescripcion)EditText etDescripcion;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.contenido) ConstraintLayout contenido;
    @BindView(R.id.tvOpcionalMarca)TextView tvOptionalMarca;
    @BindView(R.id.etOpcionalMarca)EditText etOptionalMarca;
    @BindView(R.id.etxUbicacion)EditText etxUbicacion;
    @BindView(R.id.etxEstado)EditText etxEstado;
    @BindView(R.id.ivHelp)ImageView ivHelp;
    @BindView(R.id.titleMarca)TextView titleMarcas;
    @BindView(R.id.titleLocation)TextView titleLocation;
    @BindView(R.id.titleState)TextView titleState;
    @BindView(R.id.titleMedias)TextView titleMedias;

    private RecyclerView.Adapter adapterMedias;
    private DivisionEntity division=null;
    private Activity mActivity;
    private Context mContext;
    private RecyclerAdapterMarcas mAdapterMarca;
    private LinearLayoutManager linearLayoutManager;
    private List<BrandEntity> items;
    private StatesLocalEntity statesLocalEntity=null;
    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int VIDEO_CAPTURE = 101;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO = 100;
    private List<MediasFilesEntity> mediasFilesEntities=new ArrayList<>();
    private RetrofitWebServices retrofitWebServices;
    private AppController appController;
    private int id_opportunity=0;
    private OpportunityDetailModel opportunityDetailModel;
    private Realm realm;
    private BrandEntity itemBrand;
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;
    private Call<List<BrandEntity>> callBrands;


    public FragmentNewOpportunity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mContext=getContext();
        appController=(AppController)getActivity().getApplication();
        retrofitWebServices=new RetrofitWebServices(appController,"Opportunity",mContext,this);
        realm=Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_opportunity, container, false);
        ButterKnife.bind(this,view);
        rvMarca.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
        rvMarca.setLayoutManager(linearLayoutManager);
        etxUbicacion.setVisibility(GONE);
        etxEstado.setVisibility(GONE);
        addMarcas();
        loadDatas();
        mediasFilesEntities=((ActivityOpportunity)mActivity).getMediasFilesEntities();
        addRecyclerMedias();
        etDescripcion.addTextChangedListener(new TextWatcherListener(etDescripcion,this));
        etOptionalMarca.addTextChangedListener(new TextWatcherListener(etOptionalMarca,this));
        String description=((ActivityOpportunity)mActivity).getDescription();
        if (description!=null && !TextUtils.isEmpty(description)){
            etDescripcion.setText(description);
        }
        if (!TextUtils.isEmpty(((ActivityOpportunity)mActivity).getValueOption()))
            etOptionalMarca.setText(((ActivityOpportunity)mActivity).getValueOption());
        opportunityDetailModel = new OpportunityDetailModel();
        etDescripcion.setOnFocusChangeListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm!=null)
            realm.close();
        if (callBrands!= null && callBrands.isExecuted())
            callBrands.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callBrands!= null && callBrands.isExecuted())
            callBrands.cancel();
    }

    @OnClick(R.id.etDivision)
    public void clickDivision(){
/*
        if (ValidateNetwork.getNetwork(mContext)){

        }else{

        }
        realm.executeTransaction(realm -> {
            RealmResults<OflineDivisionEntity> response= realm.where(OflineDivisionEntity.class)
                    .equalTo("allow_reports",true).findAll();

            RealmResults<OflineBrandsEntity> brands = realm.where(OflineBrandsEntity.class).findAll();

            });*/

        int TEMP_STATE = ((ActivityOpportunity)getActivity()).getTEMP_REGION();
        if (division!=null)
            new DialogDivisions().setActivity(getActivity()).setDivisions(this).setIdRegion(TEMP_STATE).setSelected(division.getName()).init();
        else
            new DialogDivisions().setActivity(getActivity()).setDivisions(this).setIdRegion(TEMP_STATE).init();
    }

    @OnClick(R.id.etOportunidad)
    public void clickOportunidad(){
        if (TextUtils.isEmpty(etDivision.getText())){
            showMessage("Primero seleccione una división para poder cargar las marcas");
        }else if (items!=null && items.size()==1 && items.get(0).getType()==1){
            showMessage("Primero seleccione una marca");
        }else{
            changeFragment(ActivityOpportunity.OPORTUNITY_TYPE);
        }
    }

    @OnClick(R.id.etUbicacion)
    public void clickUbucacion(){
        if (TextUtils.isEmpty(etDivision.getText()))
            showMessage("Aun no has agregado una división");
        else if (items!=null && items.size()==1 && items.get(0).getType()==1)
            showMessage("Aun no has agregado una marca");
        else if (TextUtils.isEmpty(etDescripcion.getText()))
            showMessage("Aun no has agregado una descripción");
        else
            changeFragment(ActivityOpportunity.OPORTUNITY_LOCATION);
    }

    @OnClick({R.id.rvMedias,R.id.ivMedias,R.id.faMedias})
    public void btMultimedia(){
        new DialogMedias().setActivity(mActivity).setListenerMedias(this).init();
    }

    @OnClick(R.id.btGuardar)
    public void btGuardar(){
        if (TextUtils.isEmpty(etDivision.getText()))
            showMessage("Aun no has agregado una división");
        else if (items!=null && items.size()==1 && items.get(0).getType()==1)
            showMessage("Aun no has agregado una marca");
        else if(TextUtils.isEmpty(etDescripcion.getText()))
            showMessage("Aun no has agregado una descripción");
        else if (ValidateNetwork.getNetwork(mContext)){
            if (itemBrand.isRequired_location()){
                LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
                if (locationEntity!=null){
                    if (locationEntity.getState()==null && locationEntity.getRegion()==0)
                        showMessage("Aun no has agregado tu ubicación");
                    else if (locationEntity.getRegion()==0){
                        enableState();
                        showMessage("Porfavor selecciona tu estado");
                    } else {
                        if (itemBrand.isRequired_media() && mediasFilesEntities.size()<1)
                            showMessage("Es necesario agregar una foto o video");
                        else
                            postOpportunity();
                    }
                }else {
                    showMessage("Aun no has agregado tu ubicación");
                }
            }else if (itemBrand.isRequired_media() && mediasFilesEntities.size()<1){
                showMessage("Es necesario agregar una foto o video");
            }else
                postOpportunity();
        }else if (!ValidateNetwork.getNetwork(mContext)){
            if (itemBrand.isRequired_location()) {
                LocationEntity locationEntity = ((ActivityOpportunity) mActivity).getLocationEntity();
                if (locationEntity != null) {
                    if (locationEntity.getState() == null && locationEntity.getRegion() == 0) {
                        showMessage("Aun no has agregado tu estado");
                    } else {
                        if (itemBrand.isRequired_media() && mediasFilesEntities.size()<1)
                            showMessage("Es necesario agregar una foto o video");
                        else
                            postOpportunity();
                    }
                } else {
                    showMessage("Aun no has agregado tu estado");
                }
            }else if (itemBrand.isRequired_media() && mediasFilesEntities.size()<1){
                showMessage("Es necesario agregar una foto o video");
            }else
                postOpportunity();
        }
    }

    @OnClick(R.id.etEstado)
    public void clickEstado(){
        if (TextUtils.isEmpty(etDivision.getText()))
            showMessage("Aun no has agregado una división");
        else if (items!=null && items.size()==1 && items.get(0).getType()==1)
            showMessage("Aun no has agregado una marca");
        else if (TextUtils.isEmpty(etDescripcion.getText()))
            showMessage("Aun no has agregado una descripción");
        else{
            DivisionEntity divisionEntity = ((ActivityOpportunity)mActivity).getDivisionEntity();
            if (divisionEntity.getStates().size() ==0){
                showAlertDivision();
            }else{
                if (statesLocalEntity != null)
                    new DialogStates().setActivity(mActivity).setDivisions(this).setStates(divisionEntity.getStates()).isSelect(statesLocalEntity.getId()).init();
                else
                    new DialogStates().setActivity(mActivity).setDivisions(this).setStates(divisionEntity.getStates()).init();
            }
        }
    }

    private void showAlertDivision(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false)
                .setMessage("No aplica para la división seleccionada")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void addBrand(int position) {
        if (items!=null){
            if (items.get(position).getType()==1){
                DivisionEntity divisionEntity=((ActivityOpportunity)mActivity).getDivisionEntity();
                if (divisionEntity!=null && divisionEntity.getId()>0)
                    changeFragment(ActivityOpportunity.OPORTUNITY_BRANDS);
                else{
                    showMessage("Primero seleccione una división para poder cargar las marcas");
                }
            }
        }
    }

    @Override
    public void removeBrand(int position) {
        if (items!=null){
            items.remove(position);
            items.get(0).setName("Agregar Nuevo");
            ((ActivityOpportunity)mActivity).setBrandEntities(items);
            ((ActivityOpportunity)mActivity).setOpportunityType(null);
            etOportunity.setText(null);
            mAdapterMarca.notifyDataSetChanged();
        }
    }

    @Override
    public void idDivision(DivisionEntity divisionEntity) {
        if (divisionEntity!=null){
            titleMarcas.setText(divisionEntity.getBrands_title() + " *");
            if (division!=null && division.getId()!=divisionEntity.getId()){
                removeBrands();
            }
            etDivision.setText(divisionEntity.getName());
            ((ActivityOpportunity)mActivity).setDivisionEntity(divisionEntity);
            division=divisionEntity;
            if (ValidateNetwork.getNetwork(mContext)) {
                if (callBrands != null && callBrands.isExecuted())
                    callBrands.cancel();
                callBrands = retrofitWebServices.getBrands(appController.getAccessToken(), division.getId());
                progressBar.setVisibility(View.VISIBLE);
            }else
                selectBrandLocal();
        }
    }

    @Override
    public void idState(StatesLocalEntity statesLocal) {
        if (statesLocal!=null){
            ((ActivityOpportunity)mActivity).setStatusState(true);
            LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
            if (locationEntity==null)
                locationEntity=new LocationEntity();
            locationEntity.setRegion(statesLocal.getId());
            locationEntity.setState(statesLocal.getName());
            locationEntity.setCity("");
            ((ActivityOpportunity)mActivity).setLocationEntity(locationEntity);
            statesLocalEntity=statesLocal;
            etEstado.setText(statesLocalEntity.getName());
            etxEstado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        if (uri!=null){
            try {
                File originFile=new File(getPatchDirectory(mContext,uri,"IMAGE"));
                File compressedImageFile=null;
                try {
                     compressedImageFile =
                            new Compressor(mContext).compressToFile(originFile);
                    long bytes = compressedImageFile.length();
                    long KB = bytes / 1024;
                    long MB = KB / 1024;
                }catch (Exception e){
                    compressedImageFile =new File(getPatchDirectory(mContext,uri,"IMAGE"));
                }
                //File file =new File(getRealPathFromURI(mContext,uri,"IMAGE"));
                if (compressedImageFile!=null) {
                    if (mediasFilesEntities.size()==0){
                        mediasFilesEntities.add(new MediasFilesEntity(1, "jpg", compressedImageFile));
                        mediasFilesEntities.add(new MediasFilesEntity(3,"drawable",null));
                    }else{
                        if (mediasFilesEntities.size()>1)
                            mediasFilesEntities.add(mediasFilesEntities.size()-1,new MediasFilesEntity(1,"jpg",compressedImageFile));
                        else
                            mediasFilesEntities.add(new MediasFilesEntity(1, "jpg", compressedImageFile));
                    }
                }

                ((ActivityOpportunity)mActivity).setMediasFilesEntities(mediasFilesEntities);
                faMedias.setVisibility(View.GONE);
                adapterMedias.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case VIDEO_CAPTURE:
                    Uri contentURI = data.getData();
                    String selectedVideoPath = getRealPathFromURI(mContext,contentURI,"VIDEO");
                    if (selectedVideoPath != null && !TextUtils.isEmpty(selectedVideoPath)) {
                        File videoFile = new File(selectedVideoPath);
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(selectedVideoPath);
                        int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                        retriever.release();
                        long fileSizeInBytes = videoFile.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;
                        if (fileSizeInMB > 100) {
                            showMessage("El archivo que intentas subir es demasiado grande");
                        } else if (duration > 15900) {
                            showMessage("El video no puede durar mas de 15 segundos");
                        } else {
                            if (mediasFilesEntities.size()==0){
                                mediasFilesEntities.add(new MediasFilesEntity(2, "mp4", videoFile));
                                mediasFilesEntities.add(new MediasFilesEntity(3,"drawable",null));
                            }else{
                                if (mediasFilesEntities.size()>1)
                                    mediasFilesEntities.add(mediasFilesEntities.size()-1,new MediasFilesEntity(2, "mp4", videoFile));
                                else
                                    mediasFilesEntities.add(new MediasFilesEntity(2, "mp4", videoFile));
                            }

                            ((ActivityOpportunity) mActivity).setMediasFilesEntities(mediasFilesEntities);
                            faMedias.setVisibility(View.GONE);
                            adapterMedias.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("null", "nulo");
                    }
                    Log.e("Video", "aqui");
                    break;
            }
        }
    }

    @Override
    public void idMedia(int position) {
        if (position==2){
            if (mediasFilesEntities.size()>0){
                int conMedias=0;
                for (MediasFilesEntity medias:mediasFilesEntities){
                    if (medias.getType()==2)
                        conMedias++;
                }
                if (conMedias>=1){
                    showMessage("Un solo video permitido");
                }else
                    openVideoCapture();
            }else
                openVideoCapture();
        }else{
            if (mediasFilesEntities.size()>0){
                int conImage=0;
                for (MediasFilesEntity medias:mediasFilesEntities){
                    if (medias.getType()==1)
                        conImage++;
                }
                if (conImage>=3)
                    showMessage("Límite de imágenes excedido (3)");
                else
                    new BSImagePicker.Builder("com.dacodes.bepensa.utils.provider").build().show(getChildFragmentManager(),"Medias");
            }else
                new BSImagePicker.Builder("com.dacodes.bepensa.utils.provider").build().show(getChildFragmentManager(),"Medias");
        }
    }

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
    public void onDeleteItem(int position) {
        if (mediasFilesEntities.size()==2)
            mediasFilesEntities.clear();
        else
            mediasFilesEntities.remove(position);
        adapterMedias.notifyDataSetChanged();
        ((ActivityOpportunity)mActivity).setMediasFilesEntities(mediasFilesEntities);
        if (mediasFilesEntities.size()==0)
            faMedias.setVisibility(View.VISIBLE);
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {

    }

    @Override
    public void beforeTextChanged(EditText editText, CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(EditText editText, CharSequence charSequence, int i, int i1, int i2) {
        switch (editText.getId()){
            case R.id.etDescripcion:
                String text=charSequence.toString();
                ((ActivityOpportunity)mActivity).setDescription(text);
                break;
            case R.id.etOpcionalMarca:
                String value=charSequence.toString();
                ((ActivityOpportunity)mActivity).setValueOption(value);
                break;
        }
    }

    @Override
    public void addNewMedia() {
        btMultimedia();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case RetrofitWebServices.POST_OPPORTUNITY:
                NewOpportunityResponse newOpportunityResponse = (NewOpportunityResponse) object;
                int points = appController.getPoints();
                int opportunityPoints=newOpportunityResponse.getPoints();
                points = points + opportunityPoints;
                appController.setPoints(points);
                Toast.makeText(mContext, "Gracias por reportar tu oportunidad en Aliados Bepensa. En breve nos pondremos en contacto contigo.", Toast.LENGTH_SHORT).show();

                if(newOpportunityResponse.getHasMedia()){
                    id_opportunity=newOpportunityResponse.getId();
                    //List<MediasFilesEntity> mediasFiles =((ActivityOpportunity)mActivity).getMediasFilesEntities();
                    if (mediasFilesEntities!=null && mediasFilesEntities.size()>0) {
                        for (int i=0;i<mediasFilesEntities.size();i++){
                            if (mediasFilesEntities.get(i).getType()==3)
                                mediasFilesEntities.remove(i);
                        }
                        serviceMedias(mediasFilesEntities, newOpportunityResponse.getId());
                    }

                    Intent intent2 = new Intent(mContext, ActivityDetailOportunity.class);
                    intent2.putExtra("id", id_opportunity);
                    intent2.putExtra("files_media",2);
                    intent2.putExtra("networkLocal",1);
                    intent2.putExtra("list_media", (Serializable) mediasFilesEntities);
                    if (mActivity!=null){
                        mActivity.startActivity(intent2);
                        mActivity.finish();
                    }else{
                        try {
                            startActivity(intent2);
                            getActivity().finish();
                        }catch (Exception e){

                        }
                    }
                }else{
                    Intent intent = new Intent(mContext, ActivityDetailOportunity.class);
                    intent.putExtra("id", newOpportunityResponse.getId());
                    intent.putExtra("files_media",1);
                    intent.putExtra("networkLocal",1);
                    if (mActivity!=null){
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }else{
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
            case RetrofitWebServices.GET_BRANDS:
                progressBar.setVisibility(GONE);
                List<? >list = ObjectToList.ObjectToList(object);
                if (list != null && list.size()>0){
                    ((ActivityOpportunity)mActivity).setOpportunityType(null);
                    BrandEntity brand=(BrandEntity)list.get(0);
                    ((ActivityOpportunity)mActivity).setIdMarca(brand.getId());
                    List<BrandEntity> brandEntities=((ActivityOpportunity)mActivity).getBrandEntities();
                    brandEntities.clear();
                    brandEntities.add(brand);
                    brandEntities.add(new BrandEntity("Modificar",1));
                    ((ActivityOpportunity)mActivity).setBrandEntities(brandEntities);
                    if (brand.getOpportunity_types().size()>0) {
                        ((ActivityOpportunity) mActivity).setOpportunityType(brand.getOpportunity_types().get(0));
                    }
                    addMarcas();
                    loadDatas();
                }
                break;
            case RetrofitWebServices.GET_OPPORTUNITIES_TYPE: {
                List<?> listType = ObjectToList.ObjectToList(object);
                if (listType != null && listType.size()>0){
                    BrandEntity brandType = (BrandEntity)listType.get(0);
                    if (brandType.getOpportunity_types().size()>0) {
                        ((ActivityOpportunity) mActivity).setOpportunityType(brandType.getOpportunity_types().get(0));
                        loadDatas();
                    }
                }
                break;
            }
        }
    }

    /*
    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case RetrofitWebServices.POST_OPPORTUNITY:
                NewOpportunityResponse newOpportunityResponse = (NewOpportunityResponse) object;
                int points = appController.getPoints();
                int opportunityPoints=newOpportunityResponse.getPoints();
                points = points + opportunityPoints;
                appController.setPoints(points);
                Toast.makeText(mContext, "Gracias por reportar tu oportunidad en Aliados Bepensa. En breve nos pondremos en contacto contigo.", Toast.LENGTH_SHORT).show();

                if(newOpportunityResponse.getHasMedia()){
                    id_opportunity=newOpportunityResponse.getId();
                    //List<MediasFilesEntity> mediasFiles =((ActivityOpportunity)mActivity).getMediasFilesEntities();
                    if (mediasFilesEntities!=null && mediasFilesEntities.size()>0) {
                        for (int i=0;i<mediasFilesEntities.size();i++){
                            if (mediasFilesEntities.get(i).getType()==3)
                                mediasFilesEntities.remove(i);
                        }
                        serviceMedias(mediasFilesEntities, newOpportunityResponse.getId());
                    }

                    Intent intent2 = new Intent(mContext, ActivityDetailOportunity.class);
                    intent2.putExtra("id", id_opportunity);
                    intent2.putExtra("files_media",2);
                    intent2.putExtra("networkLocal",1);
                    intent2.putExtra("list_media", (Serializable) mediasFilesEntities);
                    if (mActivity!=null){
                        mActivity.startActivity(intent2);
                        mActivity.finish();
                    }else{
                        try {
                            startActivity(intent2);
                            getActivity().finish();
                        }catch (Exception e){

                        }
                    }
                }else{
                    Intent intent = new Intent(mContext, ActivityDetailOportunity.class);
                    intent.putExtra("id", newOpportunityResponse.getId());
                    intent.putExtra("files_media",1);
                    intent.putExtra("networkLocal",1);
                    if (mActivity!=null){
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }else{
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
            case RetrofitWebServices.GET_BRANDS:
                List<? >list = ObjectToList.ObjectToList(object);
                if (list != null && list.size()>0){
                    ((ActivityOpportunity)mActivity).setOpportunityType(null);
                    BrandEntity brand=(BrandEntity)list.get(0);
                    ((ActivityOpportunity)mActivity).setIdMarca(brand.getId());
                    List<BrandEntity> brandEntities=((ActivityOpportunity)mActivity).getBrandEntities();
                    brandEntities.clear();
                    brandEntities.add(brand);
                    brandEntities.add(new BrandEntity("Modificar",1));
                    ((ActivityOpportunity)mActivity).setBrandEntities(brandEntities);
                    if (brand.getOpportunity_types().size()>0) {
                        ((ActivityOpportunity) mActivity).setOpportunityType(brand.getOpportunity_types().get(0));
                    }
                    addMarcas();
                    loadDatas();
                }
                break;
            case RetrofitWebServices.GET_OPPORTUNITIES_TYPE: {
                List<?> listType = ObjectToList.ObjectToList(object);
                if (listType != null && listType.size()>0){
                    BrandEntity brandType = (BrandEntity)listType.get(0);
                    if (brandType.getOpportunity_types().size()>0) {
                        ((ActivityOpportunity) mActivity).setOpportunityType(brandType.getOpportunity_types().get(0));
                        loadDatas();
                    }
                }
                break;
            }
        }
    }*/

    private void selectBrandLocal(){
        progressBar.setVisibility(View.GONE);
        realm.executeTransactionAsync(realm -> {
            if (division!=null) {
                RealmResults<OflineBrandsEntity> response = realm.where(OflineBrandsEntity.class)
                        .equalTo("divisionEntity.id", division.getId())
                        .equalTo("divisionEntity.allow_reports", true).findAll();
                if (response.size()>0){
                    BrandEntity brand = new Gson().fromJson(response.get(0).getOpportunity_types(),BrandEntity.class);
                    ((ActivityOpportunity)mActivity).setOpportunityType(null);

                    ((ActivityOpportunity)mActivity).setIdMarca(brand.getId());
                    List<BrandEntity> brandEntities=((ActivityOpportunity)mActivity).getBrandEntities();
                    brandEntities.clear();
                    brandEntities.add(brand);
                    brandEntities.add(new BrandEntity("Modificar",1));
                    ((ActivityOpportunity)mActivity).setBrandEntities(brandEntities);
                    if (brand.getOpportunity_types().size()>0) {
                        ((ActivityOpportunity) mActivity).setOpportunityType(brand.getOpportunity_types().get(0));
                    }
                }
            }
        }, () -> {
            addMarcas();
            loadDatas();
            }
        , error -> { });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (TextUtils.isEmpty(etDivision.getText())) {
            etDescripcion.setFocusable(false);
            showMessage("Aun no has agregado una división");
        }else if (items!=null && items.size()==1 && items.get(0).getType()==1) {
            etDescripcion.setFocusable(false);
            showMessage("Aun no has agregado una marca");
        }else if (hasFocus && TextUtils.isEmpty(etDescripcion.getText().toString())){
            if (((ActivityOpportunity)mActivity).getOpportunityType()!=null) {
                if (appController.getIdBrands() != null) {
                    String[] ids = appController.getIdBrands().split(",");
                    int tempId = ((ActivityOpportunity) mActivity).getOpportunityType().getId();
                    if (ids.length > 0) {
                        boolean status = false;
                        for (String temp : ids) {
                            if (temp.equalsIgnoreCase(String.valueOf(tempId))) {
                                status = true;
                                break;
                            }
                        }
                        if (!status) {
                            appController.setIdBrands(appController.getIdBrands() + "," + tempId);
                            OpportunityType opportunityType = ((ActivityOpportunity) mActivity).getOpportunityType();
                            if (opportunityType != null && !TextUtils.isEmpty(opportunityType.getHelp_text())) {
                                FillingSuggestion fillingSuggestion = new FillingSuggestion().setMessage(opportunityType.getHelp_text());
                                fillingSuggestion.show(getChildFragmentManager(), "Filling");
                            }
                        }
                    } else {
                        appController.setIdBrands(appController.getIdBrands() + "," + tempId);
                        OpportunityType opportunityType = ((ActivityOpportunity) mActivity).getOpportunityType();
                        if (opportunityType != null && !TextUtils.isEmpty(opportunityType.getHelp_text())) {
                            FillingSuggestion fillingSuggestion = new FillingSuggestion().setMessage(opportunityType.getHelp_text());
                            fillingSuggestion.show(getChildFragmentManager(), "Filling");
                        }
                    }
                } else {
                    if (((ActivityOpportunity) mActivity).getOpportunityType() != null) {
                        appController.setIdBrands("" + ((ActivityOpportunity) mActivity).getOpportunityType().getId());
                        OpportunityType opportunityType = ((ActivityOpportunity) mActivity).getOpportunityType();
                        if (opportunityType != null && !TextUtils.isEmpty(opportunityType.getHelp_text())) {
                            FillingSuggestion fillingSuggestion = new FillingSuggestion().setMessage(opportunityType.getHelp_text());
                            fillingSuggestion.show(getChildFragmentManager(), "Filling");
                        }
                    }
                }
            }
        }
    }

    @OnClick(R.id.ivHelp)
    public void HelpText(){
        OpportunityType opportunityType=((ActivityOpportunity)mActivity).getOpportunityType();
        if (opportunityType!=null && !TextUtils.isEmpty(opportunityType.getHelp_text())){
            FillingSuggestion fillingSuggestion= new FillingSuggestion().setMessage(opportunityType.getHelp_text());
            fillingSuggestion.show(getChildFragmentManager(),"Filling");
        }
    }

    private static String getPatchDirectory(Context context,Uri uri,String type){
        String imagePath = "";
        if (uri.toString().contains("content:")) {
            imagePath = getRealPathFromURI(context,uri,type);
        } else if (uri.toString().contains("file:")) {
            imagePath = uri.getPath();
        } else {
            imagePath = null;
        }
        return imagePath;
    }

    private void serviceMedias(List<MediasFilesEntity> mediasFilesEntities,int id){
        if (mediasFilesEntities.size()>0){
            String object="";
            Gson gson=new Gson();
            if (mediasFilesEntities.size()>1) {
                for (MediasFilesEntity medias : mediasFilesEntities) {
                    String media = gson.toJson(medias);
                    object = object + media + ">>";
                }
            }else
                object=gson.toJson(mediasFilesEntities.get(0));
            if (!TextUtils.isEmpty(object))
                appController.setLocalMedias(object);
        }
        appController.setIdOpportunityMedias(id);
        Intent intent=new Intent(mContext, UploadMedias.class);
        intent.putExtra("id",id);
        intent.putExtra("medias",(Serializable) mediasFilesEntities);
        mContext.startService(intent);
    }

    @Override
    public void onFailError(int id, int status, String message) {
        contenido.setVisibility(View.VISIBLE);
        progressBar.setVisibility(GONE);
        switch (status){
            case 400:
                Toast.makeText(mContext,"Tuvimos un problema con los archivos multimedia,porfavor intente de nuevo",Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onFailure(int i, int i1, String s) {
        contenido.setVisibility(View.VISIBLE);
        progressBar.setVisibility(GONE);
        if (i1 == 401){
            AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),appController,this,this,this);
            alertDIalogLogout.sesionTerminate();
        }else
            Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
        switch (i){
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
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

    @Override
    public void enableOptionBrand(boolean status,String title) {
        if (status){
            ((ActivityOpportunity)mActivity).setTitleOption(title);
            tvOptionalMarca.setVisibility(View.VISIBLE);
            etOptionalMarca.setVisibility(View.VISIBLE);
            tvOptionalMarca.setText(title);
        }else{
            ((ActivityOpportunity)mActivity).setTitleOption(null);
            ((ActivityOpportunity)mActivity).setValueOption(null);
            tvOptionalMarca.setVisibility(GONE);
            etOptionalMarca.setVisibility(GONE);
        }
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            s+=getRandomString();
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String ALLOWED_CHARACTERS ="0123456klaskLLCHKKNKJAKJCK789qwertyjkhagsuaslnlvuiopasdfghjklzxcvbnm";

    private static String getRandomString()
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(32);
        for(int i=0;i<32;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void postOpportunity(){
        if (mediasFilesEntities.size()>0){
            for (int i=0;i<mediasFilesEntities.size();i++){
                if (mediasFilesEntities.get(i).getType()==3)
                    mediasFilesEntities.remove(i);
            }
        }
        String description=((ActivityOpportunity)mActivity).getDescription();
        String titleOption=((ActivityOpportunity)mActivity).getTitleOption();
        String valueOption=((ActivityOpportunity)mActivity).getValueOption();
        DivisionEntity divisionEntity=((ActivityOpportunity)mActivity).getDivisionEntity();
        LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
        OpportunityType opportunityType=((ActivityOpportunity)mActivity).getOpportunityType();
        List<BrandEntity> brandEntities=((ActivityOpportunity)mActivity).getBrandEntities();

        int idDivision=0;
        int has_media=0;
        int idType=0;
        int idRegion=0;
        double lat=0.0;
        double lgn=0.0;
        String state="Yucatán";
        String city="";
        String brands="";
        String address=etxUbicacion.getText().toString();
        String version = BuildConfig.VERSION_NAME;

        if (divisionEntity!=null)
            idDivision=divisionEntity.getId();

        if (opportunityType!=null && opportunityType.getId()>0)
            idType=opportunityType.getId();

        if (locationEntity!=null && locationEntity.getRegion()>0)
            idRegion=locationEntity.getRegion();
        else
            idRegion=31;

        if (locationEntity!=null && locationEntity.getState()!=null)
            state=locationEntity.getState();

        if (locationEntity!=null)
            lat=locationEntity.getLat();

        if (locationEntity!=null)
            lgn=locationEntity.getLgn();

        if (mediasFilesEntities!=null && mediasFilesEntities.size()>0)
            has_media=1;

        if (brandEntities==null) {
            if (items == null)
                showMessage("Tuvimos un problema con las marcas");
            else{
                for (BrandEntity br:brandEntities){
                    if (br.getType()==0)
                        brands=""+br.getId();
                }
            }
        }else{
            for (BrandEntity br:brandEntities){
                if (br.getType()==0)
                    brands=""+br.getId();
            }
        }
        String key_hash= UUID.randomUUID().toString();
        String temp_hash=md5(key_hash);
        if (temp_hash!=null)
            key_hash=temp_hash;

        final int tempDiv = idDivision;
        final String tempBrands = brands;
        final int tempMedia= has_media;
        final int tempType = idType;
        final double temLat= lat;
        final double temLgn = lgn;
        final String tempState = state;
        final int tempRegion = idRegion;
        if (ValidateNetwork.getNetwork(mContext)){
           if (idType==0) {
               contenido.setVisibility(GONE);
               progressBar.setVisibility(View.VISIBLE);
               if (!TextUtils.isEmpty(titleOption) && !TextUtils.isEmpty(valueOption) && (itemBrand.isRequired_location() || locationEntity != null)){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv, tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion,titleOption,valueOption);
                       }
                   }).init();
                  /* retrofitWebServices.postOpportunities(address,version,idDivision, brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion,titleOption,valueOption);*/
               } else if (!TextUtils.isEmpty(titleOption) && !TextUtils.isEmpty(valueOption) && !itemBrand.isRequired_location()){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunityNu(appController.getAccessToken(),version,tempDiv,tempBrands,description,tempMedia,titleOption,valueOption);
                       }
                   }).init();
               }
               else if (!TextUtils.isEmpty(titleOption) && (itemBrand.isRequired_location() || locationEntity != null )){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv, tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion,titleOption);
                       }
                   }).init();
                  /* retrofitWebServices.postOpportunities(address,version,idDivision, brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion,titleOption);*/
               }else if (!TextUtils.isEmpty(titleOption) && !itemBrand.isRequired_location()){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunityTitleOption(appController.getAccessToken(),version,tempDiv,tempBrands,description,tempMedia,titleOption);
                       }
                   }).init();
               }
               else if (itemBrand.isRequired_location() || locationEntity != null){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv, tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion);
                       }
                   }).init();
                   /*retrofitWebServices.postOpportunities(address,version,idDivision, brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion);*/
               }else{
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),version,tempDiv,tempBrands,description,tempMedia);
                       }
                   }).init();
               }
           } else {
               contenido.setVisibility(GONE);
               progressBar.setVisibility(View.VISIBLE);
               if (!TextUtils.isEmpty(titleOption) && !TextUtils.isEmpty(valueOption) && (itemBrand.isRequired_location() || locationEntity != null)){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv, tempType,tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion,titleOption,valueOption);
                       }
                   }).init();
                   /*retrofitWebServices.postOpportunities(address,version,idDivision, idType,brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion,titleOption,valueOption);*/
               }else if (!TextUtils.isEmpty(titleOption) && !TextUtils.isEmpty(valueOption) && !itemBrand.isRequired_location()){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunityNu(appController.getAccessToken(),version,tempDiv,tempType,tempBrands,description,tempMedia,titleOption,valueOption);
                       }
                   }).init();
               }else if (!TextUtils.isEmpty(titleOption) && (itemBrand.isRequired_location() || locationEntity != null)){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv,tempType ,tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion,titleOption);
                       }
                   }).init();
                   /*retrofitWebServices.postOpportunities(address,version,idDivision,idType ,brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion,titleOption);*/
               }else if (!TextUtils.isEmpty(titleOption) && !itemBrand.isRequired_location()){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunityTitleOption(appController.getAccessToken(),version,tempDiv,tempType,tempBrands,description,tempMedia,titleOption);
                       }
                   }).init();
               }else if (itemBrand.isRequired_location() || locationEntity != null){
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),address,version,tempDiv, tempType,tempBrands, description, roundDecimal(temLat),
                                   roundDecimal(temLgn), tempState, city, tempMedia, tempRegion);
                       }
                   }).init();
                  /* retrofitWebServices.postOpportunities(address,version,idDivision, idType,brands, description, roundDecimal(lat),
                           roundDecimal(lgn), state, city, has_media, idRegion);*/
               }else{
                   new DCService().Build(this).setCode(POST_OPPORTUNITY).setCallResponse(new DCResponse<NewOpportunityResponse>() {
                       @Override
                       public Call<NewOpportunityResponse> getResponse(DCResponseInterface dcResponseInterface) {
                           return appController.getWebServices().postOpportunity(appController.getAccessToken(),version,tempDiv,tempType,tempBrands,description,tempMedia);
                       }
                   }).init();
               }
               /*retrofitWebServices.postOpportunities(idDivision, idType, brands, description, roundDecimal(lat),
                       roundDecimal(lgn), state, city, has_media, idRegion);*/
           }
        }else {
            boolean container = false;
            if (locationEntity != null)
                container = true;
            saveDataLocal(itemBrand.isRequired_location(),container,idDivision, idType, idRegion, brands, description, roundDecimal(lat), roundDecimal(lgn),
                    state, city, has_media, key_hash,titleOption,valueOption,etxEstado.getText().toString());
        }
    }

    private void openVideoCapture(){
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), permissions[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), permissions[2]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            ActivityCompat.requestPermissions(getActivity(), permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO);
        } else {
            Intent intent = ImagePicker.getPickVideoIntent(mContext);
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            changeNetworkLocation(ni);
        }
    };

    private void saveDataLocal(boolean location,boolean container,int id_division,int id_oportunity,int id_region,String brands
            ,String description,double lat,double lng,String state,String city
            ,int has_media,String key_hash,String extra_field_title,String extra_field_value,String address){
        realm.executeTransactionAsync(realm -> {
            OflinePostOpportunity postOpportunity = realm.createObject(OflinePostOpportunity.class);
            postOpportunity.setId_division(id_division);
            postOpportunity.setId_oportunity(id_oportunity);
            postOpportunity.setId_region(id_region);
            postOpportunity.setBrands(brands);
            postOpportunity.setDescription(description);
            postOpportunity.setLat(lat);
            postOpportunity.setLng(lng);
            postOpportunity.setState(state);
            postOpportunity.setCity(city);
            postOpportunity.setHas_media(has_media);
            postOpportunity.setKey_hash(key_hash);
            postOpportunity.setHas_sync(true);
            postOpportunity.setAddress(address);
            postOpportunity.setId_colaborator(appController.getUsername());
            postOpportunity.setExtra_field_title(extra_field_title);
            postOpportunity.setExtra_field_value(extra_field_value);
            postOpportunity.setRequire_location(location);
            postOpportunity.setContainer_location(container);
        }, () -> {
            if (has_media==1){
                List<MediasFilesEntity> list=new ArrayList<>();
                for (MediasFilesEntity medias:((ActivityOpportunity)getActivity()).getMediasFilesEntities()){
                    try{
                        if (medias.getType()==1 || medias.getType()==2)
                            list.add(medias);
                    }catch (Exception e){
                        e.printStackTrace();
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
                        "publicaremos cuando encuentre una conexión a internet",key_hash);
            }
        }, error -> {
            mActivity.onBackPressed();
        });
    }

    private void saveFilesLocal(final String  key_hash, final List<MediasFilesEntity> mediasFiles){
        realm.executeTransactionAsync(bgRealm -> {
            List<MediasFilesEntity> medias=mediasFiles;
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
        }, () -> messageDialog("Gracias por compartir tu oportunidad, hemos detectado q" +
                "ue el dispositivo no tiene acceso a internet, el reporte ha sido almacenado localmente " +
                "y lo publicaremos cuando encuentre una conexión a internet",key_hash),
                error -> mActivity.onBackPressed());
    }

    public void messageDialog(String message,String keyhash){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    Intent intent = new Intent(getContext(), ActivityDetailOportunity.class);
                    intent.putExtra("networkLocal",2);
                    intent.putExtra("keyhash",keyhash);
                    startActivity(intent);
                    getActivity().finish();
                })
                .show();
    }

    public void addRecyclerMedias(){
        if (mediasFilesEntities.size()>0)
            faMedias.setVisibility(View.GONE);
        rvMedias.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        adapterMedias = new MediasAdapter(mediasFilesEntities,mContext, getActivity(), this,this);
        rvMedias.setLayoutManager(linearLayoutManager);
        rvMedias.setAdapter(adapterMedias);
        adapterMedias.notifyDataSetChanged();
    }

    private void addMarcas(){
        division=((ActivityOpportunity)mActivity).getDivisionEntity();
        items=((ActivityOpportunity)mActivity).getBrandEntities();
        mAdapterMarca = new RecyclerAdapterMarcas(mContext, items, this, this);
        if (items.size()>1) {
            for (int i=0;i<items.size();i++){
                if (items.get(i).getId() != 0){
                    itemBrand = items.get(i);
                    if (((ActivityOpportunity)mActivity).getOpportunityType() == null) {
                        if (items.get(i).getOpportunity_types().size() > 0)
                            ((ActivityOpportunity) mActivity).setOpportunityType(items.get(i).getOpportunity_types().get(0));
                    }
                }
            }
        }
        rvMarca.setAdapter(mAdapterMarca);
        mAdapterMarca.notifyDataSetChanged();
    }

    private void loadDatas(){
        division=((ActivityOpportunity)mActivity).getDivisionEntity();
        if (division!=null) {
            etDivision.setText(division.getName());
            titleMarcas.setText(division.getBrands_title() + "*");
        }
        if (itemBrand != null){
            if (itemBrand.isRequired_media())
                titleMedias.setText("Fotos y/o video*");
            else
                titleMedias.setText("Fotos y/o video");
            if (itemBrand.isRequired_location()) {
                titleLocation.setText("Ubicación de la oportunidad*");
                titleState.setText("Estado*");
            }else{
                titleLocation.setText("Ubicación de la oportunidad");
                titleState.setText("Estado");
            }
        }
        if (((ActivityOpportunity)mActivity).getOpportunityType()!=null) {
            etOportunity.setText(((ActivityOpportunity) mActivity).getOpportunityType().getName());
            if (!TextUtils.isEmpty(((ActivityOpportunity)mActivity).getOpportunityType().getHelp_text())){
                ivHelp.setVisibility(View.VISIBLE);
            }else{
                ivHelp.setVisibility(GONE);
            }
        }else{
            ivHelp.setVisibility(GONE);
        }
    }

    private void changeNetworkLocation(NetworkInfo networkInfo){
        boolean statusLocation=((ActivityOpportunity)mActivity).isStatusLocation();
        if (statusLocation){
            validateState();
        }else{
            if (networkInfo!=null && networkInfo.isConnected()){
                boolean statusState=((ActivityOpportunity)mActivity).isStatusState();
                LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
                if (statusState){
                    if (locationEntity!=null){
                        if (locationEntity.getState()!=null){
                            enableState();
                            etEstado.setText(locationEntity.getState());
                        }
                    }
                }else{
                    disableState();
                }
            }else{
                boolean statusState=((ActivityOpportunity)mActivity).isStatusState();
                LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
                if (statusState){
                    if (locationEntity!=null){
                        if (locationEntity.getState()!=null){
                            enableState();
                            etEstado.setText(locationEntity.getState());
                        }
                    }
                }else{
                    enableState();
                }
            }
        }
    }

    private static double roundDecimal(double number){
        return Math.round(number * 1000000.0) / 1000000.0;
    }

    private void validateState(){
        boolean statusState=((ActivityOpportunity)mActivity).isStatusState();
        LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
        if (statusState){
            enableState();
            if (locationEntity!=null)
                etEstado.setText(locationEntity.getState());
        }else {
            if (locationEntity == null) {
                DivisionEntity divisionEntity = ((ActivityOpportunity)mActivity).getDivisionEntity();
                if (divisionEntity.getStates().size()==0)
                    disableState();
                else
                    enableState();
                //enableState();
            } else {
                if (locationEntity.getState() != null) {
                    if (getRegion(locationEntity.getState())) {
                        etxUbicacion.setVisibility(View.VISIBLE);
                        etxUbicacion.setText(locationEntity.getAddress());
                        etUbicacion.setText("Cambiar Ubicación");
                    } else {
                        DivisionEntity divisionEntity = ((ActivityOpportunity)mActivity).getDivisionEntity();
                        if (divisionEntity.getStates().size()==0)
                            disableState();
                        else
                            enableState();
                    }
                } else {
                    DivisionEntity divisionEntity = ((ActivityOpportunity)mActivity).getDivisionEntity();
                    if (divisionEntity.getStates().size()==0)
                        disableState();
                    else
                        enableState();
                    //enableState();
                }
            }
        }
    }

    private void enableState(){
        llUbicacion.setVisibility(View.GONE);
        llState.setVisibility(View.VISIBLE);
    }

    private void disableState(){
        llUbicacion.setVisibility(View.VISIBLE);
        llState.setVisibility(View.GONE);
    }

    private boolean getRegion(String state){
        String[] states= getResources().getStringArray(R.array.name_location);
        String stateUnaccent= RemoveAccent.unaccent(state);
        LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
        if (locationEntity==null) {
            locationEntity = new LocationEntity();
            ((ActivityOpportunity)mActivity).setLocationEntity(locationEntity);
        }
        for (int i=0;i<states.length;i++){
            String localState=RemoveAccent.unaccent(states[i]);
            if (localState.equalsIgnoreCase(stateUnaccent)){
                ((ActivityOpportunity)mActivity).getLocationEntity().setRegion(i+1);
                return true;
            }
        }

        return false;
    }

    private void changeFragment(int position){
        if (mActivity!=null){
            ((ActivityOpportunity)mActivity).fragmentChanger(position);
        }
    }

    private void removeBrands(){
        List<BrandEntity> items=((ActivityOpportunity)mActivity).getBrandEntities();
        if (items!=null){
            items.clear();
            items.add(new BrandEntity("Agregar Nuevo",1));
            etOportunity.setText(null);
            ((ActivityOpportunity)mActivity).setBrandEntities(items);
            ((ActivityOpportunity)mActivity).setOpportunityType(null);
            ((ActivityOpportunity)mActivity).setDescription(null);
            etDescripcion.setText(null);
            ivHelp.setVisibility(GONE);
            mAdapterMarca.notifyDataSetChanged();
        }
    }

    private void showMessage(String mesaje){
        Alerter.create(mActivity)
                .setDuration(2000)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setText(mesaje)
                .enableSwipeToDismiss()
                .show();
    }

    public static String getRealPathFromURI(Context context,Uri contentURI,String type) {
        String result  = null;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
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
                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e("ErrorMedia",""+e.getLocalizedMessage());
        }
        return result;
    }

}
