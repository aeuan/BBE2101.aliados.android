package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.feed.FeedResponseEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 *
 * Created by Eric on 10/03/17.
 * Clase que contiene el recyclerview horizontal de restaurantes
 */

public class RecyclerViewHomeRestaurantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ItemClickListener listener;
    private Activity activity;
    private FeedResponseEntity feedResponseEntity;
    int conta=6;
    List<String> strings;

    public interface ItemClickListener {
        void onClick(View view, String position);
    }

    public RecyclerViewHomeRestaurantsAdapter(Activity activity, Context context, FeedResponseEntity items, ItemClickListener listener,List<String> strings) {
        this.mContext = context;
        this.feedResponseEntity = items;
        this.listener = listener;
        this.activity = activity;
        this.strings=strings;
    }

    class ViewHolderRow extends RecyclerView.ViewHolder {

        @BindView(R.id.my_recycler_view) RecyclerView my_recycler_view;
        @BindView(R.id.text_default)TextView text_default;

        ViewHolderRow(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.category_title_tv)TextView category_title_tv;
        @BindView(R.id.everyone)LinearLayout everyone;
        @BindView(R.id.circle)
        ImageView circle;
        ViewHolderHeader(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,""+category_title_tv.getText().toString());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_restaurants_header_layout, parent, false);
                return new ViewHolderHeader(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_restaurants_layout, parent, false);
                return new ViewHolderRow(v);
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
            default:
                ViewHolderRow vh1 = (ViewHolderRow) viewHolder;
                configureViewHolder1(vh1, position);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    private void configureViewHolder0(ViewHolderHeader vh0, int position) {
        if (position%2==0) {
            vh0.category_title_tv.setText(strings.get(position));
            if (strings.get(position).equals("BENEFICIOS") && feedResponseEntity.isNew_promotions())
                vh0.circle.setVisibility(View.VISIBLE);
            else if (strings.get(position).equals("EVENTOS") && feedResponseEntity.isNew_events())
                vh0.circle.setVisibility(View.VISIBLE);
            else if (strings.get(position).equals("ENCUESTAS") && feedResponseEntity.isNew_surveys())
                vh0.circle.setVisibility(View.VISIBLE);
            else
                vh0.circle.setVisibility(View.GONE);
        }
    }

    private void configureViewHolder1(ViewHolderRow vh1, int position) {
        int viewType = vh1.getItemViewType();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        vh1.my_recycler_view.setLayoutManager(linearLayoutManager);
        switch (strings.get(position)){
            case "promo": {
                if (feedResponseEntity.getPromotions().size()>=1){
                    RecyclerAdapterPromotionsSingleAdapter adapter = new RecyclerAdapterPromotionsSingleAdapter(activity, mContext, feedResponseEntity.getPromotions());
                    vh1.my_recycler_view.setNestedScrollingEnabled(false);
                    vh1.my_recycler_view.setAdapter(adapter);
                    vh1.my_recycler_view.setHasFixedSize(true);
                }else {
                    vh1.text_default.setVisibility(View.VISIBLE);
                    vh1.my_recycler_view.setVisibility(View.GONE);
                    vh1.text_default.setText("Aún no hay datos para mostrar");
                }
                break;
            }case "events": {
                if (feedResponseEntity.getEvents_near().size()>=1){
                    RecyclerAdapterEventsSingleAdapter adapter = new RecyclerAdapterEventsSingleAdapter(activity, mContext, feedResponseEntity.getEvents_near());
                    vh1.my_recycler_view.setNestedScrollingEnabled(false);
                    vh1.my_recycler_view.setAdapter(adapter);
                    vh1.my_recycler_view.setHasFixedSize(true);
                }else {
                    vh1.text_default.setVisibility(View.VISIBLE);
                    vh1.my_recycler_view.setVisibility(View.GONE);
                    vh1.text_default.setText("Aún no hay datos para mostrar");
                }
                break;
            }case "test": {
                if (feedResponseEntity.getSurveys().size()>=1){
                    RecyclerAdapterSurveySingleAdapter adapter = new RecyclerAdapterSurveySingleAdapter(activity, mContext, feedResponseEntity.getSurveys());
                    vh1.my_recycler_view.setNestedScrollingEnabled(false);
                    vh1.my_recycler_view.setAdapter(adapter);
                    vh1.my_recycler_view.setHasFixedSize(true);
                }else {
                    vh1.text_default.setVisibility(View.VISIBLE);
                    vh1.my_recycler_view.setVisibility(View.GONE);
                    vh1.text_default.setText("Aún no hay datos para mostrar");
                }
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        if(strings==null){
            return 0;
        }else {
            return strings.size();
        }
    }
}

