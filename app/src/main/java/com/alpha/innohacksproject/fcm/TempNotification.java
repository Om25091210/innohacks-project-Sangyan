package com.alpha.innohacksproject.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alpha.innohacksproject.Home.Model.Excel_data;
import com.alpha.innohacksproject.R;
import com.alpha.innohacksproject.Splash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class TempNotification extends AppCompatActivity {

    DatabaseReference ref_data,ref_notice,ref_writ;
    String key;
    LinearLayout linearLayout2,linearLayout3;
    ConstraintLayout layout;
    TextView dayLeft,lastDate,stationName,distName,caseNo,nameRm,caseType,personName,crimeNo,receivingDate,message;
    ProgressBar progressBar2;
    ImageView type,share,imageView2,imageView4;
    List<Excel_data> excel_data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_notification);

        linearLayout2=findViewById(R.id.linearLayout2);
        linearLayout3=findViewById(R.id.linearLayout3);
        dayLeft=findViewById(R.id.day_left);
        type=findViewById(R.id.type);
        share=findViewById(R.id.share);
        progressBar2=findViewById(R.id.progressBar2);
        lastDate=findViewById(R.id.last_date);
        stationName=findViewById(R.id.station_name);
        imageView2=findViewById(R.id.imageView2);
        distName=findViewById(R.id.dist_name);
        caseType=findViewById(R.id.case_type);
        caseNo=findViewById(R.id.case_no);
        nameRm=findViewById(R.id.name_rm);
        personName=findViewById(R.id.person_name);
        crimeNo=findViewById(R.id.crime_no);
        imageView4=findViewById(R.id.imageView4);
        receivingDate=findViewById(R.id.receiving_date);
        message=findViewById(R.id.message);
        layout=findViewById(R.id.layout);

        key = getIntent().getStringExtra("sending_msg_data");
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        type.setVisibility(View.GONE);
        dayLeft.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);

        ref_data = FirebaseDatabase.getInstance().getReference().child("data");
        ref_notice = FirebaseDatabase.getInstance().getReference().child("notice");
        ref_writ = FirebaseDatabase.getInstance().getReference().child("writ");

        check_key();
    }

    private void check_key() {

        ref_data.child(key).child("B").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.e("oooo","herer");
                    ref_data.child(key+"").child("seen").setValue("1");
                    populate_data();
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void populate_data() {
        ref_data.child(key+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.add(snapshot.getValue(Excel_data.class));
                show_data();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void show_data() {
        progressBar2.setVisibility(View.GONE);

        linearLayout2.setVisibility(View.VISIBLE);
        linearLayout3.setVisibility(View.VISIBLE);
        type.setVisibility(View.VISIBLE);
        dayLeft.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);

        lastDate.setText(excel_data.get(0).getLl());
        stationName.setText(excel_data.get(0).getBb());
        distName.setText(excel_data.get(0).getCc());
        caseNo.setText(excel_data.get(0).getEe() + "/" + excel_data.get(0).getGg());
        nameRm.setText(excel_data.get(0).getKk());
        caseType.setText(excel_data.get(0).getDd());
        personName.setText(excel_data.get(0).getFf());
        crimeNo.setText(excel_data.get(0).getHh() + "/" + excel_data.get(0).getIi());
        receivingDate.setText(excel_data.get(0).getJj());

        //return call logic
        if (excel_data.get(0).getType().equals("RM CALL")) {
            message.setText("उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getKk() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।");
            type.setVisibility(View.VISIBLE);
            type.setImageResource(R.drawable.ic_submit_type);
        } else if (excel_data.get(0).getType().equals("RM RETURN")) {
            message.setText("उपरोक्त मूल केश डायरी " + excel_data.get(0).getKk() + " से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।");
            type.setVisibility(View.VISIBLE);
            type.setImageResource(R.drawable.ic_return_type);
        } else
            type.setVisibility(View.GONE);

        //red white logic
        if (excel_data.get(0).getJj().equals("None") || excel_data.get(0).getJj().equals("nan"))
            layout.setBackgroundColor(Color.parseColor("#FAD8D9"));
        else
            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //tick logic
        if (excel_data.get(0).getReminded() != null) {
            if (excel_data.get(0).getReminded().equals("once")) {
                imageView2.setVisibility(View.VISIBLE);
                imageView2.setImageResource(R.drawable.ic_blue_tick);
            } else if (excel_data.get(0).getReminded().equals("twice")) {
                imageView2.setVisibility(View.VISIBLE);
                imageView2.setImageResource(R.drawable.ic_green_tick);
            }
        }

        if (excel_data.get(0).getLl() != null) {
            if (!excel_data.get(0).getLl().equals("None")) {
                dayLeft.setVisibility(View.VISIBLE);
                if (nDays_Between_Dates(excel_data.get(0).getLl()) == 0) {
                    dayLeft.setText("0d");
                    dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else if (nDays_Between_Dates(excel_data.get(0).getLl()) <= 5) {
                    dayLeft.setText(nDays_Between_Dates(excel_data.get(0).getLl()) + "d");
                    dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else {
                    dayLeft.setText("--");
                    dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_clock, 0, 0, 0);
                }
            } else
                dayLeft.setVisibility(View.GONE);
        } else
            dayLeft.setVisibility(View.GONE);
        String message;
        if(excel_data.get(0).getType().equals("RM CALL")) {
            message= "हाईकोर्ट अलर्ट:-डायरी माँग" + "\nदिनाँक:- " + excel_data.get(0).getDate() + "\n\n" + "Last Date - " + excel_data.get(0).getLl() + "\n"
                    + "District - " + excel_data.get(0).getCc() + "\n" +
                    "Police Station - " + excel_data.get(0).getBb() + "\n" +
                    excel_data.get(0).getDd() + " No. - " + excel_data.get(0).getEe() + "/" + excel_data.get(0).getGg() + "\n" +
                    "RM Date - " + excel_data.get(0).getKk() + "\n" +
                    "Case Type - " + excel_data.get(0).getDd() + "\n" +
                    "Name - " + excel_data.get(0).getFf() + "\n" +
                    "Crime No. - " + excel_data.get(0).getHh() + "/" + excel_data.get(0).getIi() + "\n" +
                    "Received - " + excel_data.get(0).getJj() + "\n\n"
                    + "उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getLl() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";
        }
        else{
            message = "हाईकोर्ट अलर्ट:-डायरी वापसी"+"\nदिनाँक:- "+ excel_data.get(0).getDate()  +" \n\n" + "Last Date - " + excel_data.get(0).getLl() + "\n"
                    + "District - " + excel_data.get(0).getCc() + "\n" +
                    "Police Station - " + excel_data.get(0).getBb() + "\n"+
                    excel_data.get(0).getDd() + " No. - " + excel_data.get(0).getEe() +"/"+ excel_data.get(0).getGg()+"\n" +
                    "RM Date - " + excel_data.get(0).getKk()+ "\n" +
                    "Case Type - " + excel_data.get(0).getDd() +  "\n" +
                    "Name - " + excel_data.get(0).getFf()+  "\n" +
                    "Crime No. - " + excel_data.get(0).getHh() +"/"+ excel_data.get(0).getIi()+  "\n" +
                    "Received - " + excel_data.get(0).getJj() + "\n\n" + "1)उपरोक्त मूल केश डायरी  महाधिवक्ता कार्यालय द्वारा दी गयी मूल पावती लाने पर ही दी जाएगी।\n"
                    +"2) उपरोक्त मूल केश डायरी "+ excel_data.get(0).getKk() +" से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।";

        }
        share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(TempNotification.this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    public static int nDays_Between_Dates(String date1) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String str = formatter.format(date);
        int diffDays = 0;
        try {
            SimpleDateFormat dates = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date startDate = dates.parse(date1);
            Date endDate = dates.parse(str);
            if(startDate.after(endDate)) {
                long diff = endDate.getTime() - startDate.getTime();
                diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            }
            else if(startDate.equals(endDate))
                return 0;
            else{
                return 6;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(diffDays);
    }
}
