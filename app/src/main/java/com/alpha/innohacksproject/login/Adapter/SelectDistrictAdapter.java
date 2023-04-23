package com.alpha.innohacksproject.login.Adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.R;

import java.util.ArrayList;
import java.util.List;

public class SelectDistrictAdapter extends RecyclerView.Adapter<SelectDistrictAdapter.ViewHolder> {

    List<String> list;
    Context context;
    int num_of_districts;
    ArrayList<String> selection_list=new ArrayList<>();
    TinyDB tinydb;

    public SelectDistrictAdapter(List<String> list, Context context, int num_of_districts) {
        this.list = list;
        this.context=context;
        this.num_of_districts=num_of_districts;
        tinydb=new TinyDB(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_district_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        selection_list=tinydb.getListString("districts_list");
        Log.e("selection_list",selection_list+"");
        if (selection_list.contains(list.get(position))) {
            holder.tick.setVisibility(View.VISIBLE);
        }
        holder.district.setText(list.get(position));
        holder.layout.setOnClickListener(v->{
            if (holder.tick.getVisibility() == View.VISIBLE) {
                holder.tick.setVisibility(View.GONE);
                selection_list.remove(list.get(position));
            } else {
                if(selection_list.size()<=num_of_districts-1) {
                    holder.tick.setVisibility(View.VISIBLE);
                    selection_list.add(list.get(position));
                }
                else{
                    Toast.makeText(context, "You can only select "+num_of_districts+" district/s", Toast.LENGTH_SHORT).show();
                }
            }
            tinydb.putListString("districts_list", selection_list);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView district;
        ConstraintLayout layout;
        ImageView tick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            district=itemView.findViewById(R.id.dis);
            layout=itemView.findViewById(R.id.layout);
            tick=itemView.findViewById(R.id.imageView5);
        }
    }
}
