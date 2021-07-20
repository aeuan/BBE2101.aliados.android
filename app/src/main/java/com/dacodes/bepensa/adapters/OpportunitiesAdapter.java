package com.dacodes.bepensa.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunitiesAdapter extends RecyclerView.Adapter<OpportunitiesAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<DivisionEntity> divisions = new ArrayList<>();
    private List<OpportunityType> oportunities = new ArrayList<>();
    private OpportunitiesAdapter.AddDivisionType addDivisionType;
    private AddOpportunityType addOpportunityType;
    private int type;//1 Division 2 Opportunity


    public OpportunitiesAdapter(Context context, Activity activity, List<DivisionEntity> divisions,
                                OpportunitiesAdapter.AddDivisionType addDivisionType) {
        this.context = context;
        this.activity = activity;
        this.divisions = divisions;
        this.addDivisionType = addDivisionType;
        type = 1;
    }

    public OpportunitiesAdapter(Context context, Activity activity, List<OpportunityType> opportunities,
                                AddOpportunityType addOpportunityType) {
        this.context = context;
        this.activity = activity;
        this.oportunities = opportunities;
        this.addOpportunityType = addOpportunityType;
        type = 2;
    }



    public interface AddDivisionType{
        void onAddDivisionType(DivisionEntity division);
    }

    public interface AddOpportunityType{
        void onAddOportunityType(OpportunityType opportunity);
    }



    @NonNull
    @Override
    public OpportunitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_division_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpportunitiesAdapter.ViewHolder holder, final int position) {

        switch (type){
            case 1:

                holder.tvDivision.setText(divisions.get(position).getName());

                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addDivisionType.onAddDivisionType(divisions.get(position));

                      /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("Se agregará la división");
                        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addDivisionType.onAddDivisionType(divisions.get(position));
                            }
                        });

                        builder.setNegativeButton("CANCELAR", null);

                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();*/

                    }
                });
                break;
            case 2:
                holder.tvDivision.setText(oportunities.get(position).getName());

                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addOpportunityType.onAddOportunityType(oportunities.get(position));

                      /*  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("Se agregará la oportunidad");
                        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addOpportunityType.onAddOportunityType(oportunities.get(position));
                            }
                        });

                        builder.setNegativeButton("CANCELAR", null);

                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();*/
                    }
                });
                break;
        }



    }

    @Override
    public int getItemCount() {
        int size = 0;
        switch (type){
            case 1:
                size = divisions.size();
            break;
            case 2:
                size = oportunities.size();
            break;
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvDivision)
        TextView tvDivision;
        @BindView(R.id.container)
        ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
