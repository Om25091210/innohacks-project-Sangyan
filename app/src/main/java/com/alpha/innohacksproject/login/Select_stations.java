package com.alpha.innohacksproject.login;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.Dashboard;
import com.alpha.innohacksproject.databinding.ActivitySelectStationsBinding;
import com.alpha.innohacksproject.login.Adapter.SelectStationAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Select_stations extends AppCompatActivity {

    ActivitySelectStationsBinding binding;
    String district;
    int num_of_station;
    DatabaseReference reference_phone;
    List<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivitySelectStationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        district=getIntent().getStringExtra("district");
        num_of_station=getIntent().getIntExtra("Station_choice_num",-1);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rec.setItemViewCacheSize(500);
        binding.rec.setDrawingCacheEnabled(true);
        binding.rec.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.rec.setLayoutManager(gridLayoutManager);
        get_districts();
        TinyDB tinydb=new TinyDB(Select_stations.this);
        binding.next.setOnClickListener(v->{
            tinydb.putBoolean("entered_select_district",true);
            Intent intent=new Intent(Select_stations.this, Dashboard.class);
            startActivity(intent);
            finish();
        });
    }


    public void get_districts(){
        reference_phone.child(district).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().startsWith("PS"))
                        list.add(ds.getKey());
                }
                SelectStationAdapter selectStationAdapter=new SelectStationAdapter(list,Select_stations.this,num_of_station);
                selectStationAdapter.notifyDataSetChanged();
                binding.rec.setAdapter(selectStationAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}