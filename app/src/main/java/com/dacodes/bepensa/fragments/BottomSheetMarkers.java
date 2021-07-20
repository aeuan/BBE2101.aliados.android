package com.dacodes.bepensa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.adapters.AdapterMarkers;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetMarkers extends BottomSheetDialogFragment {

    private AdapterMarkers adapterMarkers = new AdapterMarkers();
    private PromotionDataEntity dataEntity;

    @BindView(R.id.recycler)RecyclerView recyclerView;

    public static BottomSheetMarkers getInstance(PromotionDataEntity dataEntity){
        BottomSheetMarkers markers = new BottomSheetMarkers();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",dataEntity);
        markers.setArguments(bundle);
        return markers;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_markers,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataEntity = (PromotionDataEntity) getArguments().getSerializable("data");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterMarkers);
        adapterMarkers.setItems(dataEntity.getMarkers());
    }
}
