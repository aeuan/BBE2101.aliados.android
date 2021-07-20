package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.HomeCategoriesEntity;
import com.dacodes.bepensa.entities.RestaurantsEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapterHomeSingleRestaurantAdapter extends
        RecyclerView.Adapter<RecyclerAdapterHomeSingleRestaurantAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantsEntity> items;
    private ItemClickListener listener;
    private Activity activity;
    private String sectionTitle;

    public interface ItemClickListener {
        void onClick(View view, int position, HomeCategoriesEntity items);
    }

    public RecyclerAdapterHomeSingleRestaurantAdapter(Activity activity,
                                                      Context context, String sectionTitle,
                                                      List<RestaurantsEntity> items,
                                                      ItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.sectionTitle = sectionTitle;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.restaurant_iv) ImageView restaurant_iv;
       /* @BindView(R.id.circleImageView)CircleImageView circleImageView;
        @BindView(R.id.restaurant_tv)TextView restaurant_tv;
        @BindView(R.id.restaurant_type_tv)TextView restaurant_type_tv;
*/
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
           /* restaurant_tv.setTypeface(FontSingleton.getInstance().getThirstyScript());
            restaurant_type_tv.setTypeface(FontSingleton.getInstance().getThirstyScript());*/
        }


        @Override
        public void onClick(View v) {
/*            if(sectionTitle.contentEquals("Descuentos")){
                DiscountOverlayView overlayView = new DiscountOverlayView(activity, context, items.get(getAdapterPosition()).getId());
                List<String> images = new ArrayList<>();
                images.add(items.get(getAdapterPosition()).getBannerUrl());
                new ImageViewer.Builder(activity, images)
                        .setStartPosition(0)
                        .hideStatusBar(false)
                        .setOverlayView(overlayView)
                        .setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranslucentBlack))
                        .show();
            }else{
                Intent intent = new Intent(context, RestaurantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", items.get(getAdapterPosition()).getId());
                intent.putExtra("sponsored", true);
                context.startActivity(intent);
            }*/

            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_home_restaurant, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
       /* Glide.with(context).load(items.get(i).getLogoUrl()).into(viewHolder.circleImageView);
        viewHolder.restaurant_tv.setText("Nombre");
        viewHolder.restaurant_type_tv.setText("Tipo");*/
        Uri imageUri;
/*        if(sectionTitle.contentEquals("Descuentos")) {
            imageUri = Uri.parse(items.get(i).getBannerUrl());
        }else{
            imageUri = Uri.parse("http://degusta.dacodes.mx/img/dyn/sm/" +
                    items.get(i).getBanner());
        }*/
        imageUri = Uri.parse("https://cdn-a.william-reed.com/var/wrbm_gb_food_pharma/storage/images/publications/food-beverage-nutrition/foodmanufacture.co.uk/article/2018/02/01/coca-cola-site-closures-not-down-to-any-one-factor/7812725-1-eng-GB/Coca-Cola-site-closures-not-down-to-any-one-factor_wrbm_large.jpg");
        viewHolder.restaurant_iv.setImageURI(imageUri);
    }
}