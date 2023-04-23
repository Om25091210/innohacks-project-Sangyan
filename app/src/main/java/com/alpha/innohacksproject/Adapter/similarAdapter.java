package com.alpha.innohacksproject.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.innohacksproject.Fragment.Similar_Collection;
import com.alpha.innohacksproject.Home.Model.Excel_data;
import com.alpha.innohacksproject.Home.Model.filterdata;
import com.alpha.innohacksproject.R;

import java.io.Serializable;
import java.util.List;

public class similarAdapter extends RecyclerView.Adapter<similarAdapter.ViewHolder> {

    List<filterdata> filtered_data;
    Context context;
    List<Excel_data> excel_data_duplicates;

    public similarAdapter(Context contextNullSafety, List<filterdata> filtered_data, List<Excel_data> excel_data_duplicates) {
        this.context=contextNullSafety;
        this.filtered_data=filtered_data;
        this.excel_data_duplicates= excel_data_duplicates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.case_no.setText(filtered_data.get(position).getCn()+"/"+filtered_data.get(position).getYear());
        holder.station_name.setText(filtered_data.get(position).getStn());
        holder.dist_name.setText(filtered_data.get(position).getDis_n());

        holder.layout.setOnClickListener(v->{
            Similar_Collection showing_similar_coll=new Similar_Collection();
            Bundle args=new Bundle();
            args.putString("data_case_number",filtered_data.get(position).getCn());
            args.putString("data_case_year",filtered_data.get(position).getYear());
            args.putString("data_station_name",filtered_data.get(position).getStn());
            args.putString("data_district_name",filtered_data.get(position).getDis_n());
            args.putSerializable("data_array_list", (Serializable) excel_data_duplicates);
            showing_similar_coll.setArguments(args);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,showing_similar_coll)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public int getItemCount() {
        return filtered_data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView station_name,dist_name,case_no,case_type;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            station_name=itemView.findViewById(R.id.station_name);
            dist_name=itemView.findViewById(R.id.dist_name);
            case_no=itemView.findViewById(R.id.case_no);
            case_type=itemView.findViewById(R.id.case_type);
            layout=itemView.findViewById(R.id.layout);
        }

    }
}
