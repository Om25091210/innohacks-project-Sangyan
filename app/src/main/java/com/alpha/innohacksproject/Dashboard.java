package com.alpha.innohacksproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.alpha.innohacksproject.Fragment.DistrictData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    Toolbar toolbar;
    NavigationView navView;
    LinearLayout police_contacts;
    FirebaseAuth auth;
    FirebaseUser  user;
    OnBackPressedListener onBackpressedListener;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarTransparent();

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);
        drawer = findViewById(R.id.drawer1);
        police_contacts = findViewById(R.id.linearLayout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //set default home fragment and its title
        //Objects.requireNonNull(getSupportActionBar()).setTitle("");
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
                       /* fragment = new about();
                        navView.getMenu().getItem(0).setCheckable(false);
                        drawer.closeDrawer(GravityCompat.START);
                        callFragment(fragment);*/
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
                        /*navView.getMenu().getItem(2).setCheckable(false);
                        fragment = new about_dev();
                        drawer.closeDrawer(GravityCompat.START);
                        //  getSupportActionBar().setTitle("About US");
                        callFragment(fragment);*/
                        break;

                    case R.id.nav_privacy:
                        navView.getMenu().getItem(3).setCheckable(false);
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("handles");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("privacy_policy").getValue(String.class);
                                Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                                startActivity(twitterAppIntent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                        break;

                    case R.id.nav_terms:
                        navView.getMenu().getItem(4).setCheckable(false);
                        DatabaseReference reference1 =FirebaseDatabase.getInstance().getReference().child("handles");
                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("terms_condition").getValue(String.class);
                                Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                                startActivity(twitterAppIntent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
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

        police_contacts.setOnClickListener(v -> Dashboard.this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new DistrictData(),"dashboard_frag")
                .addToBackStack(null)
                .commit());
    }
    private void setStatusBarTransparent () {
        Window window = Dashboard.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        Fragment test = getSupportFragmentManager().findFragmentByTag("dashboard_frag");
        if (test != null && test.isVisible()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
            ft.commit();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackpressedListener = onBackPressedListener;
    }

    @Override
    protected void onDestroy() {
        onBackpressedListener = null;
        super.onDestroy();
    }

}