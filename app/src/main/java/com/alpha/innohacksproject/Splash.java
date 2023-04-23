package com.alpha.innohacksproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Splash extends AppCompatActivity {


    DatabaseReference user_reference;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window = Splash.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Splash.this, R.color.use_bg));

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        user_reference= FirebaseDatabase.getInstance().getReference().child("users");

    /*    if(RootUtil.isDeviceRooted()){
            Toast.makeText(this, "Device Rooted", Toast.LENGTH_SHORT).show();
            Splash.this.finish();
        }*/

        new Handler(Looper.myLooper()).postDelayed(() -> {
            if(user!=null){

                String user_is=getSharedPreferences("useris?",MODE_PRIVATE)
                        .getString("the_user_is?","");

                boolean auth_entry=  getSharedPreferences("authorized_entry",MODE_PRIVATE)
                        .getBoolean("entry_done",false);
                Log.e("user_is : ",user_is);
                Log.e("auth_entry : ",auth_entry+"");
                if(user_is.equals("p_home") && auth_entry){
                    Intent i = new Intent(Splash.this, Dashboard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                else if(auth_entry){
                    TinyDB tinyDB=new TinyDB(Splash.this);
                    boolean tiny_sel_dist=tinyDB.getBoolean("entered_select_district");
                    if(tiny_sel_dist) {
                        Intent i = new Intent(Splash.this, Dashboard.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                    else{
                        //send to select districts
                    }
                }
                else{
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }
            else {
                Intent intent = new Intent(Splash.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        },1000);//2100
    }

}