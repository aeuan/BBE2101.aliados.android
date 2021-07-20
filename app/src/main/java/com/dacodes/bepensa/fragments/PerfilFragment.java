package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.FragmentGraph;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EditProfile;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_PROFILE_USER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PerfilFragment extends Fragment implements RetrofitWebServices.DefaultResponseListeners{

    private static final String LOG_TAG = PerfilFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    public PerfilFragment() {
        // Required empty public constructor
    }
    Context mContext;
    AppController appController;
    @BindView(R.id.profile_image)CircleImageView profile_image;
    @BindView(R.id.name_tv)TextView name_tv;
    @BindView(R.id.id_tv)TextView id_tv;
    @BindView(R.id.division_tv)TextView division_tv;
    @BindView(R.id.email_tv)TextView email_tv;
    @BindView(R.id.points_number_tv)TextView point_number_tv;
    @BindView(R.id.constraintLayout)ConstraintLayout constraintLayout;
    @BindView(R.id.contact_tv)TextView contact_tv;
    RetrofitWebServices retrofitWebServices;
    @BindView(R.id.linearLayout2)
    LinearLayout prueba;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        appController = (AppController)getActivity().getApplication();
        final View view = inflater.inflate(R.layout.perfil_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        loadProfile();
        loadGraph();
        ((MainActivity)getActivity()).setToolbar(8);

        return view;
    }

    public void loadGraph(){
        getChildFragmentManager().beginTransaction().replace(R.id.content,new FragmentGraph()).commit();
    }

    @OnClick(R.id.constraintLayout)
    public void editc(){
        Intent intent = new Intent(getContext(),EditProfile.class);
        startActivity(intent);
    }

    public void loadProfile(){
        Glide.with(mContext)
                .load(appController.getImageUrl())
                .into(profile_image);
        name_tv.setText(""+appController.getName()+" "+appController.getLastName());
        email_tv.setText(""+appController.getEmail());
        division_tv.setText(appController.getDivision());
        point_number_tv.setText(""+appController.getPoints());
        id_tv.setText("ID:"+appController.getUsername());
        contact_tv.setText(appController.getPhone());
    }


    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
        ((MainActivity)getActivity()).refreshTotalPoint();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDatosPersonalesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
           case PUT_PROFILE_USER:
               ProfileEntity response = (ProfileEntity) object;
               appController.setPhone(response.getContact());
               appController.setEmail(response.getEmail());
               appController.setImageUrl(response.getAvatarUrl());
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String newUrl, String name);
    }

}
