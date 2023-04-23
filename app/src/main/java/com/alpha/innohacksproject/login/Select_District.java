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
import com.alpha.innohacksproject.databinding.ActivitySelectDistrictBinding;
import com.alpha.innohacksproject.login.Adapter.SelectDistrictAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Select_District extends AppCompatActivity {

    ActivitySelectDistrictBinding binding;
    int num_of_station,num_of_districts;
    DatabaseReference reference_phone;
    List<String> list=new ArrayList<>();
    ArrayList<String> emptylist=new ArrayList<>();

    //TODO: 1. Set on click on the layout of card for selecting districts in case of SDOP its value of selecting will be 1 then.
    //TODO: will change layout for Thane selection in case of SDOP.
    //TODO: 2. Same for IG but only districts.
    //TODO: SharedPreferences for Select_Districts in case of back button.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivitySelectDistrictBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();
        TinyDB tinydb=new TinyDB(Select_District.this);
        tinydb.putListString("stations_list", emptylist);
        num_of_districts=tinydb.getInt("num_districts");
        num_of_station=tinydb.getInt("num_station");
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rec.setItemViewCacheSize(500);
        binding.rec.setDrawingCacheEnabled(true);
        binding.rec.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.rec.setLayoutManager(gridLayoutManager);
        //This part of code will fix the clear cache problem after every login.
        if(num_of_districts==0){
            Intent intent = new Intent(Select_District.this, Login.class);
            startActivity(intent);
            finish();
        }
        get_districts();

        binding.next.setOnClickListener(v->{
            if(num_of_station!=0){
                if(tinydb.getListString("districts_list").size()==1) {
                    //for sdop its not true ->Entered select district
                    String district = tinydb.getListString("districts_list").get(0);
                    Intent intent = new Intent(Select_District.this, Select_stations.class);
                    intent.putExtra("Station_choice_num", 10);
                    intent.putExtra("district", district);
                    startActivity(intent);
                    finish();
                }
            }
            else{
                if(tinydb.getListString("districts_list").size()>=1) {
                    //User opened the screen or not.
                    tinydb.putBoolean("entered_select_district",true);
                    String district = tinydb.getListString("districts_list").get(0);
                    Intent intent = new Intent(Select_District.this, Dashboard.class);
                    intent.putExtra("Station_choice_num", 10);
                    intent.putExtra("district", district);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public void get_districts(){
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    list.add(ds.getKey());
                }
                SelectDistrictAdapter selectDistrictAdapter=new SelectDistrictAdapter(list,Select_District.this,num_of_districts);
                selectDistrictAdapter.notifyDataSetChanged();
                binding.rec.setAdapter(selectDistrictAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}