package com.dacodes.bepensa.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.models.Markers;

import java.util.ArrayList;
import java.util.List;

public class AdapterMarkers extends RecyclerView.Adapter {

    private List<Markers> items = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MarkersViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_marker,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MarkersViewHolder)viewHolder).bindView(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Markers> markers){
        this.items = markers;
        notifyDataSetChanged();
    }
}
