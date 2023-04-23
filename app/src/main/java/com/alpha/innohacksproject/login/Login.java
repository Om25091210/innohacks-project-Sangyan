package com.alpha.innohacksproject.login;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.Dashboard;
import com.alpha.innohacksproject.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.simform.customcomponent.SSCustomEdittextOutlinedBorder;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class Login extends AppCompatActivity {

    LinearLayout linearLayout,logo_layout;
    TextView send_otp,terms_and_condition,didnt,resend;
    ImageView p_back;
    String DeviceToken;
    int downspeed;
    int upspeed;
    PinView pinView;
    SSCustomEdittextOutlinedBorder edtEmail;
    int count=0;
    CountDownTimer countDownTimer;
    private PhoneAuthProvider.ForceResendingToken resendOTPtoken;
    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;
    String station_name,ds_name;
    // string for storing our verification ID
    private String verificationId;
    DatabaseReference user_reference,reference;
    ArrayList<String> emptylist=new ArrayList<>();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSharedPreferences("authorized_entry", MODE_PRIVATE).edit()
                .putBoolean("entry_done", false).apply();

        getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                .putBoolean("authorizing_admin",false).apply();

        getSharedPreferences("valid_version",MODE_PRIVATE).edit()
                .putBoolean("valid_ver",false).apply();

        TinyDB tinyDB=new TinyDB(Login.this);
        Log.e("this cache1",tinyDB.getInt("num_districts")+"");
        deleteCache(this);
        Log.e("this cache2",tinyDB.getInt("num_districts")+"");
        tinyDB.putBoolean("entered_select_district",false);
        tinyDB.putInt("num_districts",-1);
        tinyDB.putInt("num_station",-1);
        tinyDB.putListString("stations_list", emptylist);
        tinyDB.putListString("districts_list", emptylist);

        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth=FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic3")
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.d("topic_log", msg);
                });
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        getting_device_token();
        linearLayout=findViewById(R.id.sign_in);
        logo_layout=findViewById(R.id.logo_layout);
        edtEmail=findViewById(R.id.edtEmail);
        send_otp=findViewById(R.id.textView23);
        p_back=findViewById(R.id.p_back);
        terms_and_condition=findViewById(R.id.textView12);
        didnt=findViewById(R.id.textView13);
        resend=findViewById(R.id.textView14);
        pinView = findViewById(R.id.pin_view);
        upAnimate(logo_layout);

        getSharedPreferences("Is_SP",MODE_PRIVATE).edit()
                .putString("Yes_of","none").apply();

        getSharedPreferences("Is_SDOP",MODE_PRIVATE).edit()
                .putString("Yes_of","none").apply();

        getSharedPreferences("Is_IG",MODE_PRIVATE).edit()
                .putString("Yes_of","none").apply();

        getSharedPreferences("district_name_K",MODE_PRIVATE).edit()
                .putString("the_district_name2002","").apply();

        linearLayout.setOnClickListener(v->{
            if(!send_otp.getText().toString().trim().equals("Verify")) {
                if (edtEmail.getGetTextValue().trim().length() == 10) {
                    offanimate(edtEmail);
                    terms_and_condition.setVisibility(View.GONE);
                    didnt.setVisibility(View.VISIBLE);
                    resend.setVisibility(View.VISIBLE);
                    p_back.setVisibility(View.VISIBLE);
                    send_otp.setText("Verify");
                    onAnimate(pinView);
                    pinView.setVisibility(View.VISIBLE);
                    String phone = "+91" + edtEmail.getGetTextValue();
                    sendVerificationCode(phone);
                    countTimer();
                } else {
                    Toast.makeText(Login.this, "Enter 10 digit mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(pinView.getText().toString().trim().length()==6){
                    String otp_text= Objects.requireNonNull(pinView.getText()).toString().trim();
                    Log.e("pinView","==========");
                    verifyCode(otp_text);
                }
                else{
                    Toast.makeText(this, "Please enter a valid OTP.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resend.setOnClickListener(v->{
            if(resend.getText().toString().equals("RESEND NEW CODE")) {
                String phone = "+91" + edtEmail.getGetTextValue();
                resendVerificationCode(phone, resendOTPtoken);
                countTimer();
            }
        });

        terms_and_condition.setText(Html.fromHtml(getString(R.string.sampleText)));
        terms_and_condition.setMovementMethod(LinkMovementMethod.getInstance());

        p_back.setOnClickListener(v->{
            terms_and_condition.setVisibility(View.VISIBLE);
            didnt.setVisibility(View.GONE);
            resend.setVisibility(View.GONE);
            onAnimate(edtEmail);
            pinView.setText("");
            p_back.setVisibility(View.GONE);
            send_otp.setText("Send OTP");
            offanimate(pinView);
            countDownTimer.cancel();
        });

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ch=s+"";
                if(ch.length()==6){
                    String otp_text= Objects.requireNonNull(pinView.getText()).toString().trim();
                    Log.e("pinView","==========");
                    verifyCode(otp_text);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                pinView.setText(code);
                Log.e("inside code block","==========");
                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Log.e("error",e+"");
        }
    };
    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

            // after getting credential we are
            // calling sign in method.
            signInWithCredential(credential);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Log.e("task successfull","Success");
                            update_ui();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Log.e("task result",task.getException().getMessage());
                            pinView.setError("Wrong Pin");
                        }
                    }
                });
    }
    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir.list() != null) {
                deleteDir2(dir);
            }
        } catch (Exception e) { e.printStackTrace();}
    }

    public boolean deleteDir2(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                boolean success = deleteDir2(child);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    private void update_ui() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        String pkey=user_reference.push().getKey();

        //TODO: Check number whether it exists in our database if yes then take it to home otherwise show him the toast.

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("lohhing","logging");
                for(DataSnapshot ds : snapshot.getChildren()){
                    Log.e("lohhing","logging");
                    for (DataSnapshot ds_1 : ds.getChildren()){
                        if (edtEmail.getGetTextValue().trim().equals(ds_1.getValue(String.class))){
                            count=1;
                            station_name=ds_1.getKey();
                            ds_name=ds.getKey();
                            Log.e("Entered count","Count = 1");
                            if(Objects.requireNonNull(station_name).startsWith("SP")){
                                getSharedPreferences("Is_SP",MODE_PRIVATE).edit()
                                        .putString("Yes_of",ds.getKey()).apply();
                            }
                            getSharedPreferences("station_name_K",MODE_PRIVATE).edit()
                                    .putString("the_station_name2003",station_name).apply();

                            getSharedPreferences("district_name_K",MODE_PRIVATE).edit()
                                    .putString("the_district_name2002",ds_name).apply();
                            break;
                        }
                    }
                    if(count==1) {
                        Log.e("Entered count","break from all loop");
                        break;
                    }
                }
                if(count==1 && station_name.startsWith("PS")){
                    Log.e("police station if","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","p_home").apply();

                    check_device_token(pkey);
                    user=mAuth.getCurrentUser();
                    countDownTimer.cancel();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue(station_name);
                    user_reference.child(user.getUid()).child(Objects.requireNonNull(user.getPhoneNumber()).substring(3)).setValue(user.getPhoneNumber());
                    Intent i = new Intent(Login.this, Dashboard.class);
                    i.putExtra("station_name",station_name);
                    startActivity(i);
                    finish();
                }
                else if(count==1){
                    Log.e("non-admin","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","home").apply();

                    check_device_token(pkey);

                    user = mAuth.getCurrentUser();
                    countDownTimer.cancel();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue(station_name);
                    user_reference.child(user.getUid()).child(Objects.requireNonNull(user.getPhoneNumber()).substring(3)).setValue(user.getPhoneNumber());
                    TinyDB tinyDB=new TinyDB(Login.this);
                    if(Objects.requireNonNull(station_name).startsWith("SDOP")){
                        tinyDB.putInt("num_districts",1);
                        tinyDB.putInt("num_station",10);
                        Intent i = new Intent(Login.this, Select_District.class);
                        i.putExtra("choice_number",1);
                        i.putExtra("choice_station",10);
                        startActivity(i);
                        finish();
                    }
                    else if(Objects.requireNonNull(station_name).startsWith("CSP")){
                        Log.e("Entered count","He's a CSP");
                        tinyDB.putInt("num_districts",1);
                        tinyDB.putInt("num_station",10);
                        Intent i = new Intent(Login.this, Select_District.class);
                        i.putExtra("choice_number",1);
                        i.putExtra("choice_station",10);
                        startActivity(i);
                        finish();
                    }
                    else if(Objects.requireNonNull(station_name).startsWith("IG")){
                        Intent i = new Intent(Login.this, Select_District.class);
                        tinyDB.putInt("num_districts",8);
                        tinyDB.putInt("num_station",0);
                        i.putExtra("choice_number",8);
                        i.putExtra("choice_station",0);
                        startActivity(i);
                        finish();
                    }
                    else {
                        tinyDB.putBoolean("entered_select_district",true);
                        Intent i = new Intent(Login.this, Dashboard.class);
                        startActivity(i);
                        finish();
                    }
                }
                else
                    check_for_admin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void check_device_token(String pkey) {
        if(DeviceToken!=null){
            user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {//.child(user.getUid()).child("token")
                    int c=0;
                    if(snapshot.child(user.getUid()).child("token").exists()) {
                        for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                            Log.e("forloop", "YES");
                            if (Objects.requireNonNull(snapshot.child(user.getUid()).child("token").child(Objects.requireNonNull(ds.getKey())).getValue(String.class)).equals(DeviceToken)) {
                                c = 1;
                                Log.e("loop if", "YES");
                            }
                        }
                        if (c == 0) {
                            user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                        }
                    }
                    else{
                        user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

    }

    private void check_for_admin() {
        Log.e("Entered count","Admin body");
        String pkey=user_reference.push().getKey();
        user=mAuth.getCurrentUser();
        DatabaseReference admin_ref=FirebaseDatabase.getInstance().getReference().child("admin");
        admin_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(user.getPhoneNumber().substring(3))){
                        count=2;
                        break;
                    }
                }
                if(count==2){
                    Log.e("admin entry","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","home").apply();

                    check_device_token(pkey);

                    TinyDB tinyDB=new TinyDB(Login.this);
                    tinyDB.putBoolean("entered_select_district",true);
                    user=mAuth.getCurrentUser();
                    countDownTimer.cancel();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue("admin2.0");
                    user_reference.child(user.getUid()).child(Objects.requireNonNull(user.getPhoneNumber()).substring(3)).setValue(user.getPhoneNumber());
                    Intent i = new Intent(Login.this, Dashboard.class);
                    startActivity(i);
                    finish();
                }
                else{
                    MotionToast.Companion.darkColorToast(Login.this,
                            "Failed ☹️",
                            "Phone No. Is Not In Our Database",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(Login.this, www.sanju.motiontoast.R.font.helvetica_regular));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void countTimer()
    {
        countDownTimer=new CountDownTimer(25000, 1000)
        {
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                resend.setText("Retry after - "+f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                resend.setText("RESEND NEW CODE");
                resend.setVisibility(View.VISIBLE);
                // btnVerify.setEnabled(true);
            }
        };
        countDownTimer.start();
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void offanimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",-800f);
        move.setDuration(1000);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",0);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
    }
    void onAnimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",0f);
        move.setDuration(1000);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",100);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
    }

    void upAnimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationY",-180f);
        move.setDuration(500);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",100);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
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
}