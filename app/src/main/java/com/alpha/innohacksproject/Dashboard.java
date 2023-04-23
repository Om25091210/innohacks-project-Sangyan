package com.alpha.innohacksproject;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.alpha.innohacksproject.Home.Home;
import com.alpha.innohacksproject.login.Login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    LinearLayout case_diary, police_contacts, notice_victim, writ_police;
    //LottieAnimationView toolbar;
    DatabaseReference ref_version,reference;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String ver="2.0";
    boolean valid_ver=false;
    boolean isadmin=false;
    String redirect_to="";
    Toolbar toolbar;
    NavigationView navView;
    DrawerLayout drawer;
    int downspeed;
    int upspeed;
    String DeviceToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarTransparent();

        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();

        valid_ver=getSharedPreferences("valid_version",MODE_PRIVATE)
                .getBoolean("valid_ver",false);

        redirect_to=getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        reference=FirebaseDatabase.getInstance().getReference().child("users");
        ref_version=FirebaseDatabase.getInstance().getReference().child("version");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("admin");
        check_version();
        police_contacts = findViewById(R.id.linearLayout);
        case_diary = findViewById(R.id.linearLayout8);
        notice_victim = findViewById(R.id.linearLayout7);
        writ_police = findViewById(R.id.linearLayout9);

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);
        drawer = findViewById(R.id.drawer1);

        setSupportActionBar(toolbar);

        getting_device_token();
        get_status_of_admin();

        //set default home fragment and its title
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        navView.setCheckedItem(R.id.nav_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.use_orange));
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        navView.getMenu().getItem(0).setCheckable(false);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_share:
                        navView.getMenu().getItem(1).setCheckable(false);
                        String title ="*CG High Court Alert*"+"\n\n"+"*उच्च न्यायालय की केश डायरी मंगाने और जमा करने संबंधित सूचना तथा डायरी की स्थिति पता करने के लिए नीचे दिए गए लिंक से Android App download करें।*\n\nDownload this app to stay alerted and notified for the case diaries for both submission and return. Tap on the below link to download"; //Text to be shared
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        break;

                    case R.id.nav_developer:
                        navView.getMenu().getItem(2).setCheckable(false);
                        drawer.closeDrawer(GravityCompat.START);
                        //  getSupportActionBar().setTitle("About US");
                        break;

                    case R.id.nav_privacy:
                        navView.getMenu().getItem(3).setCheckable(false);
                        break;

                    case R.id.nav_terms:
                        navView.getMenu().getItem(4).setCheckable(false);
                        break;
                    case R.id.nav_logout:
                        navView.getMenu().getItem(5).setCheckable(false);
                        auth.signOut();
                        startActivity(new Intent(Dashboard.this , Splash.class));
                        finish();
                        break;

                }
                return true;
            }
        });

      /*  notice_victim.setOnClickListener(v->{
            if(valid_ver) {
                Intent intent = new Intent(Dashboard.this, NoticemainAdmin.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Please Update the app", Toast.LENGTH_SHORT).show();
            }
        });*/

        writ_police.setOnClickListener(v -> {
            if(valid_ver) {
                Toast.makeText(this, "Section yet to be developed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please Update the app", Toast.LENGTH_SHORT).show();
            }
        });

       /* police_contacts.setOnClickListener(v -> Dashboard.this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new DistrictData(),"dashboard_frag")
                .addToBackStack(null)
                .commit());*/

        case_diary.setOnClickListener(v -> {
            if(valid_ver) {
                if (redirect_to.equals("home")) {
                    Intent intent = new Intent(Dashboard.this, Home.class);
                    startActivity(intent);
                } else if (redirect_to.equals("p_home")) {
                    Log.e("users","user login");
                   /* Intent intent = new Intent(Dashboard.this, p_Home.class);
                    startActivity(intent);*/
                }
            }else{
                Toast.makeText(this, "Please Update the app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void check_version() {
        ref_version.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(Objects.equals(snapshot.getValue(String.class), ver)){
                        Log.e("valid",valid_ver+"");
                        valid_ver=true;
                        getSharedPreferences("valid_version",MODE_PRIVATE).edit()
                                .putBoolean("valid_ver",true).apply();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setStatusBarTransparent () {
        Window window = Dashboard.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void check_if_token(String DeviceToken) {
        String pkey=reference.push().getKey();
        if(DeviceToken!=null){
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {//
                    int c=0;
                    if(snapshot.child(user.getUid()).child("token").exists()) {
                        for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                            Log.e("token check", "YES");
                            if (snapshot.child(user.getUid()).child("token").child(ds.getKey()).getValue(String.class).equals(DeviceToken)) {
                                c = 1;
                                Log.e("device token exists", "YES");
                            }
                        }
                        if (c == 0) {
                            Log.e("setting deivce token","Discarding");
                            reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                        }
                    }
                    else{
                        Log.e("setting deivce token","Creating");
                        reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void getting_device_token() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(nc!=null) {
            downspeed = nc.getLinkDownstreamBandwidthKbps()/1000;
            upspeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        }else{
            downspeed=0;
            upspeed=0;
        }

        if((upspeed!=0 && downspeed!=0) || getWifiLevel()!=0) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d("token", "retrieve token successful : " + token);
                } else {
                    Log.w("token121", "token should not be null...");
                }
            }).addOnFailureListener(e -> {
                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task ->
            {
                try {
                    DeviceToken = task.getResult();
                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            check_if_token(DeviceToken);
                        }
                    },1500);
                    Log.e("token",DeviceToken+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public int getWifiLevel()
    {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int linkSpeed = wifiManager.getConnectionInfo().getRssi();
            return WifiManager.calculateSignalLevel(linkSpeed, 5);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private void get_status_of_admin() {
        if (!redirect_to.equals("p_home")) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    isadmin = snapshot.child(user.getPhoneNumber().substring(3) + "").exists();
                    if (isadmin) {
                        getSharedPreferences("isAdmin_or_not", MODE_PRIVATE).edit()
                                .putBoolean("authorizing_admin", true).apply();
                    } else {
                        getSharedPreferences("isAdmin_or_not", MODE_PRIVATE).edit()
                                .putBoolean("authorizing_admin", false).apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(Dashboard.this, Login.class);
            startActivity(i);
            finish();
        }
    }

}