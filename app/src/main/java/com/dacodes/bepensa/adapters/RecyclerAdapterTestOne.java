package com.dacodes.bepensa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.CategoriesEntity;

import java.util.ArrayList;
import java.util.List;

public  class RecyclerAdapterTestOne  extends RecyclerView.Adapter<RecyclerAdapterTestOne.ViewHolder> {
    public int mSelectedItem = -1;
    public List<CategoriesEntity> mItems;
    private Context mContext;
    private addItem addItem;

    public RecyclerAdapterTestOne(Context context, List<CategoriesEntity> items,addItem addItem) {
        this.mContext = context;
        this.mItems = items;
        this.addItem=addItem;
    }

    public interface addItem{
        void addIdItem(int id);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.mText.setText(""+options().get(i)+mItems.get(i).getName());
        viewHolder.mRadio.setChecked(i == mSelectedItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public RadioButton mRadio;
        public TextView mText;

        public ViewHolder(final View inflate) {
            super(inflate);
            mText = (TextView) inflate.findViewById(R.id.text);
            mRadio = (RadioButton) inflate.findViewById(R.id.radio);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    addItem.addIdItem(mItems.get(getAdapterPosition()).getId());
                    notifyItemRangeChanged(0, mItems.size());
                }
            };
            itemView.setOnClickListener(clickListener);
            mRadio.setOnClickListener(clickListener);
        }
    }

    public List<String> options(){
        List<String> options=new ArrayList<>();
        options.add("a)");
        options.add("b)");
        options.add("c)");
        options.add("d)");
        options.add("e)");
        options.add("f)");
        options.add("g)");
        options.add("h)");
        options.add("i)");
        options.add("j)");

        return options;
    }
}
