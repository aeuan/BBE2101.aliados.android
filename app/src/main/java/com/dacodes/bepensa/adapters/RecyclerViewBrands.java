package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.models.DivisionTemporal;
import com.dacodes.bepensa.utils.FontSingleton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;


/**
 *
 * Created by Eric on 10/03/17.
 * Clase que contiene el recyclerview horizontal de restaurantes
 */

public class RecyclerViewBrands extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<DivisionTemporal> items;
    private ItemClickListener listener;
    private Activity activity;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public RecyclerViewBrands(Activity activity, Context context, List<DivisionTemporal> items,
                              ItemClickListener listener) {
        this.mContext = context;
        this.items = items;
        this.listener = listener;
        this.activity = activity;
    }

    class ViewHolderRow extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.my_recycler_view)
        RecyclerView my_recycler_view;

        ViewHolderRow(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

    }

    class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.category_title_tv)TextView category_title_tv;
        @BindView(R.id.tvShowAll)TextView tvShowAll;
        @BindView(R.id.ibArrow)ImageButton ibArrow;

        ViewHolderHeader(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            category_title_tv.setTypeface(FontSingleton.getInstance().getThirstyScript());
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(mContext, "Click", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_restaurants_layout, parent, false);
                return new ViewHolderRow(v);

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_restaurants_header_layout, parent, false);
                return new ViewHolderHeader(v);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case 0:
                ViewHolderHeader vh0 = (ViewHolderHeader) viewHolder;
                configureViewHolder0(vh0, position);
                break;

            case 1:
                ViewHolderRow vh1 = (ViewHolderRow) viewHolder;
                configureViewHolder1(vh1, position);
                break;
        }


    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    private void configureViewHolder0(ViewHolderHeader vh0, int position) {
        vh0.category_title_tv.setText(items.get(position).getName_division());
        vh0.tvShowAll.setVisibility(GONE);
        vh0.ibArrow.setVisibility(GONE);
    }

    private void configureViewHolder1(ViewHolderRow vh1, int position) {
        List<BrandEntity> restaurants = items.get(position).getBrands();

        if (restaurants != null) {
            GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 3);
            vh1.my_recycler_view.setLayoutManager(linearLayoutManager);
            RecyclerAdapterHomeSingle adapter =
                    new RecyclerAdapterHomeSingle(activity, mContext, items.get(position).getName_division(),
                            restaurants, null);
            vh1.my_recycler_view.setNestedScrollingEnabled(false);
            vh1.my_recycler_view.setAdapter(adapter);
            vh1.my_recycler_view.setHasFixedSize(true);
        }
    }

    @Override
    public int getItemCount() {
        if(items==null){
            return 0;
        }else {
            return items.size();
        }
    }
}

