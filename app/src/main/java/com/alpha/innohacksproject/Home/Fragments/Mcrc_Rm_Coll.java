package com.alpha.innohacksproject.Home.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.Home.Adapter.Excel_Adapter;
import com.alpha.innohacksproject.Home.Interface.onAgainClickInterface;
import com.alpha.innohacksproject.Home.Interface.onClickInterface;
import com.alpha.innohacksproject.Home.Model.Excel_data;
import com.alpha.innohacksproject.Home.Model.smsData;
import com.alpha.innohacksproject.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import soup.neumorphism.NeumorphButton;

public class Mcrc_Rm_Coll extends Fragment {

    View view;
    private Context contextNullSafe;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<String> case_data_list=new ArrayList<>();
    List<String> case_data_list_filter=new ArrayList<>();
    DatabaseReference user_ref;
    Query query;
    int onback=0;
    List<Excel_data> excel_data=new ArrayList<>();
    List<Excel_data> mylist=new ArrayList<>();
    List<Excel_data> j_data_list=new ArrayList<>();
    EditText search;
    CheckBox select_all;
    List<String> phone_numbers=new ArrayList<>();
    List<String> station_name_list=new ArrayList<>();
    List<String> district_name_list=new ArrayList<>();

    List<String> not_sent_sms_list=new ArrayList<>();
    List<String> keys_selected=new ArrayList<>();
    List<String> keys_copy_selected_phone=new ArrayList<>();
    List<String> noti_keys_copy_selected_phone=new ArrayList<>();
    List<String> list=new ArrayList<>();
    Excel_Adapter excel_adapter;
    DatabaseReference phone_numbers_ref;
    ArrayList<String> added_list;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    NeumorphButton join;
    String sp_of,gsID="";
    ImageView bulk_delete,cg_logo,j_column;
    boolean isadmin=false;
    Dialog dialog,dialog1,j_dialog,dialogD;
    DatabaseReference reference,gs_ref;
    TextView message, notification,no_data,phone_sms;
    private com.alpha.innohacksproject.Home.Interface.onClickInterface onClickInterface;
    private com.alpha.innohacksproject.Home.Interface.onAgainClickInterface onAgainClickInterface;
    TinyDB tinyDB;
    List<smsData> smsDataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_mcrc__rm__coll, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        added_list=new ArrayList<>();
        smsDataList=new ArrayList<>();
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        search=view.findViewById(R.id.search);
        bulk_delete=view.findViewById(R.id.imageRemoveImage);
        select_all=view.findViewById(R.id.checkBox4);
        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        //filter=view.findViewById(R.id.imageView8);
        join=view.findViewById(R.id.join);
        j_column=view.findViewById(R.id.j_column);
        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        mRecyclerView.setItemViewCacheSize(500);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(mManager);
        excel_adapter= new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
        sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        tinyDB=new TinyDB(getContextNullSafety());
        //Initialize Database
        reference = FirebaseDatabase.getInstance().getReference().child("data");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");
        gs_ref = FirebaseDatabase.getInstance().getReference().child("gskey");
        query = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM CALL");
        phone_numbers_ref=FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        if(sp_of.equals("none")) {
            if(tinyDB.getInt("num_station")==0){
                getDataForIG();
            }
            else if(tinyDB.getInt("num_station")==10){
                getDataForSDOP();
            }
            else{
                getdata();
            }
        }
        else
            getdata_for_sp();
        isadmin=getContextNullSafety().getSharedPreferences("isAdmin_or_not", MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin) {
            join.setVisibility(View.VISIBLE);
            bulk_delete.setVisibility(View.VISIBLE);
            select_all.setVisibility(View.VISIBLE);
            j_column.setVisibility(View.VISIBLE);
        }
        else {
            join.setVisibility(View.GONE);
            bulk_delete.setVisibility(View.GONE);
            select_all.setVisibility(View.GONE);
            j_column.setVisibility(View.GONE);
        }
        onClickInterface = position -> {
            if(search.getText().toString().equals("")) {
                added_list.add(excel_data.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == excel_data.size());
                join.setText(txt);
            }
            else{
                added_list.add(mylist.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == mylist.size());
                join.setText(txt);
            }
        };

        onAgainClickInterface=removePosition -> {
            if(search.getText().toString().equals("")) {
                added_list.remove(excel_data.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
            }
            else{
                added_list.remove(mylist.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
            }
        };
        select_all.setOnClickListener(v->{
            if (select_all.isChecked()){
                if(search.getText().toString().equals("")) {
                    for (int i = 0; i < excel_data.size(); i++) {
                        added_list.add(excel_data.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
                else{
                    for (int i = 0; i < mylist.size(); i++) {
                        added_list.add(mylist.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
            }
            else{
                added_list.clear();
                j_data_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
            }
            excel_adapter.notifyDataSetChanged();
            Log.e("added_peeps",added_list+"");
        });
        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(sp_of.equals("none")) {
                if(tinyDB.getInt("num_station")==0){
                    getDataForIG();
                }
                else if(tinyDB.getInt("num_station")==10){
                    getDataForSDOP();
                }
                else{
                    getdata();
                }
            }
            else
                getdata_for_sp();
        });
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                select_all.setChecked(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                search(s+"");
            }
        });
        gs_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gsID=snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        bulk_delete.setOnClickListener(v->{
            dialogD= new Dialog(getContextNullSafety());
            dialogD.setCancelable(true);
            dialogD.setContentView(R.layout.dialog_for_sure);
            dialogD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialogD.findViewById(R.id.textView96);
            TextView text=dialogD.findViewById(R.id.textView94);
            text.setText("Delete All?");
            TextView yes=dialogD.findViewById(R.id.textView95);
            dialogD.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogD.show();
            cancel.setOnClickListener(vi-> dialogD.dismiss());
            yes.setOnClickListener(vi-> {
                List<Excel_data> delete_list=new ArrayList<>();
                if(added_list!=null){
                    if(added_list.size()!=0) {
                        for (int i = 0; i < added_list.size(); i++) {
                            for (int j = 0; j < excel_data.size(); j++) {
                                if (excel_data.get(j).getPushkey().equals(added_list.get(i))) {
                                    delete_list.add(excel_data.get(j));
                                }
                            }
                        }
                        Log.e("delete_list", delete_list.size() + "");
                        dialog1 = new Dialog(getContextNullSafety());
                        dialog1.setCancelable(false);
                        dialog1.setContentView(R.layout.loading_dialog);
                        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        LottieAnimationView lottieAnimationView = dialog1.findViewById(R.id.animate);
                        lottieAnimationView.setAnimation("done.json");
                        dialog1.show();
                        delete_data(delete_list);
                    }
                    else{
                        Snackbar.make(mRecyclerView,"Please add data to delete.",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        dialogD.dismiss();
                    }
                }
            });

        });

        join.setOnClickListener(v->{

            dialog = new Dialog(getContextNullSafety());
            dialog.setContentView(R.layout.message_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            message = dialog.findViewById(R.id.message);
            notification = dialog.findViewById(R.id.notification);
            phone_sms = dialog.findViewById(R.id.phone_sms);

            message.setOnClickListener(v1->{
                dialog.dismiss();
                gather_number("sms");
            });

            notification.setOnClickListener(v2->{
                dialog.dismiss();
                gather_number("notify");
            });

            phone_sms.setOnClickListener(v3->{
                dialog.dismiss();
                gather_number("phonesms");
            });

        });


        j_column.setOnClickListener(v->{
            if(added_list!=null){
                if(added_list.size()!=0) {
                    j_dialog = new Dialog(getContextNullSafety());
                    j_dialog.setCancelable(true);
                    j_dialog.setContentView(R.layout.j_column_dialog);
                    TextView dates=j_dialog.findViewById(R.id.diary);
                    j_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    j_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    j_dialog.show();
                    dates.setOnClickListener(view -> {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(
                                getActivity(),
                                mDateSetListener,
                                year,month,day);

                        dialog.show();
                    });

                    mDateSetListener = (datePicker, year, month, day) -> {

                        String d=String.valueOf(day);
                        String m=String.valueOf(month+1);
                        Log.e("month",m+"");
                        month = month + 1;
                        Log.e("month",month+"");
                        if(String.valueOf(day).length()==1)
                            d="0"+ day;
                        if(String.valueOf(month).length()==1)
                            m="0"+ month;
                        String date = d + "." + m + "." + year;
                        dates.setText(date);
                    };

                    TextView cancel=j_dialog.findViewById(R.id.textView96);
                    TextView yes=j_dialog.findViewById(R.id.textView95);
                    cancel.setOnClickListener(vi-> j_dialog.dismiss());
                    yes.setOnClickListener(vi-> {
                        Snackbar.make(mRecyclerView,"Gathering data...",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (int i = 0; i < added_list.size(); i++) {
                                    j_data_list.add(snapshot.child(added_list.get(i)).getValue(Excel_data.class));
                                }

                                dialog1 = new Dialog(getContextNullSafety());
                                dialog1.setCancelable(false);
                                dialog1.setContentView(R.layout.loading_dialog);
                                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                                lottieAnimationView.setAnimation("done.json");
                                dialog1.show();
                                update_J_Excel(j_data_list,dates.getText().toString());

                                Log.e("dates",j_data_list.size()+"");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    });
                }
            }
        });
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((FragmentActivity) getContextNullSafety()).finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    public void gather_number(String type) {
        dialog1 = new Dialog(getContextNullSafety());
        dialog1.setCancelable(true);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.show();
        Snackbar.make(mRecyclerView,"Gathering number of stations...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        station_name_list.clear();
        district_name_list.clear();
        case_data_list.clear();
        not_sent_sms_list.clear();
        keys_selected.clear();
        keys_copy_selected_phone.clear();
        case_data_list_filter.clear();
        phone_numbers.clear();
        Log.e("added_pushkey",added_list+"");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(added_list.size()!=0) {
                    for (int h = 0; h < added_list.size(); h++) {
                        String station_name = "PS " + snapshot.child(added_list.get(h)).child("B").getValue(String.class).toUpperCase().trim();
                        String district_name = snapshot.child(added_list.get(h)).child("C").getValue(String.class).toUpperCase().trim();

                        String K = snapshot.child(added_list.get(h)).child("K").getValue(String.class).trim();
                        String L = snapshot.child(added_list.get(h)).child("L").getValue(String.class).trim();
                        String C = snapshot.child(added_list.get(h)).child("C").getValue(String.class).trim();
                        String D = snapshot.child(added_list.get(h)).child("D").getValue(String.class).trim();
                        String E = snapshot.child(added_list.get(h)).child("E").getValue(String.class).trim();
                        String G = snapshot.child(added_list.get(h)).child("G").getValue(String.class).trim();
                        String H = snapshot.child(added_list.get(h)).child("H").getValue(String.class).trim();
                        String I = snapshot.child(added_list.get(h)).child("I").getValue(String.class).trim();
                        String B = snapshot.child(added_list.get(h)).child("B").getValue(String.class).trim();
                        String type = snapshot.child(added_list.get(h)).child("type").getValue(String.class).trim();

                        case_data_list.add(type+"~"+K+"~"+C+"~"+D+"~"+E+"~"+G+"~"+H+"~"+I+"~"+B+"~"+L);
                        district_name_list.add(district_name);
                        station_name_list.add(station_name);
                        keys_selected.add(added_list.get(h));

                    }
                    Log.e("district_name_list = ",district_name_list+"");
                    Log.e("station_name_list = ",station_name_list+"");
                    filter_by_district(type);
                }
                else{
                    Snackbar.make(mRecyclerView,"Zero selections...",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#ea4a1f"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setBackgroundTint(Color.parseColor("#D9F5F8"))
                            .show();
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filter_by_district(String type) {
        Snackbar.make(mRecyclerView,"Filtering data...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        phone_numbers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<district_name_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.getChildren()){
                        if(district_name_list.get(i).toLowerCase().trim().equals(ds_key.getKey().toLowerCase().trim())){
                            if(snapshot.child(ds_key.getKey().trim()).child(station_name_list.get(i)).exists()){
                                phone_numbers.add(snapshot.child(ds_key.getKey()).child(station_name_list.get(i)).getValue(String.class));
                                keys_copy_selected_phone.add(keys_selected.get(i));
                                case_data_list_filter.add(case_data_list.get(i));
                            }
                            else{
                                not_sent_sms_list.add(keys_selected.get(i));
                            }
                        }
                    }
                }
                Log.e("phone_numbers = ",phone_numbers+"");
                if(phone_numbers.size()!=0) {
                    for (int pos = 0; pos < phone_numbers.size(); pos++) {
                        if (type.equals("sms")) {
                            //httpCall("https://2factor.in/API/R1/?module=TRANS_SMS&apikey=89988543-35b9-11ec-a13b-0200cd936042&to="+phone_numbers.get(pos)+"&from=OMSAIT&templatename=TESTING&var1="+"Himanshi"+"&var2="+"OM is Love");
                        } else if (type.equals("phonesms")) {
                            send_phone_sms(phone_numbers);
                            break;
                        } else {
                            send_notification(phone_numbers);
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(getContextNullSafety(), "No data found.", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void send_phone_sms(List<String> phone_numbers) { //TODO : The logic here is written for number which does not exist in users node. Need to write code for notification too and then copy paste it to other files.
        SmsManager sms = SmsManager.getDefault();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<phone_numbers.size();i++) {
                    int check=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.child(ds.getKey()).child(phone_numbers.get(i)).exists()) {
                            check=1;
                            //reference.child(keys_copy_selected_phone.get(i)).child("reminded").setValue("once");
                            extract_data(i,keys_copy_selected_phone.get(i),phone_numbers.get(i));
                            //String body = extract_data(i,phone_numbers.get(i));
                            //ArrayList<String> list = sms.divideMessage(body);
                            //sms.sendMultipartTextMessage(phone_numbers.get(i), null, list, null, null);
                        }
                    }
                    if(check==0)
                        not_sent_sms_list.add(keys_copy_selected_phone.get(i));
                }
                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                getContextNullSafety().getSharedPreferences("saving_RM_Call_not_sms", MODE_PRIVATE).edit()
                        .putString("RM_CALL_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Sending sms...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();

                send_sms_api();

                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void send_sms_api() {
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        if (smsDataList.size() != 0) {
            String jsonExcelList = gson.toJson(smsDataList);
            // print your generated json
            Log.e("jsonCartList: ", jsonExcelList);
            String prev_keygen = smsDataList.get(0).getTid() + "-" + smsDataList.get(0).getMob_no() + "-" + smsDataList.get(0).getCrime_no();
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("data", jsonExcelList);
                jsonBody.put("keygen", hashGenerator(prev_keygen));
                Log.e("body", "httpCall_collect: " + hashGenerator(prev_keygen));
            } catch (Exception e) {
                Log.e("Error", "JSON ERROR");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(getContextNullSafety());
            String URL = "http://sangyan.co.in/sendmsg";

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // enjoy your response
                            String code = response.optString("code") + "";
                            if (code.equals("202")) {
                                for (int i = 0; i < smsDataList.size(); i++) {
                                    reference.child(smsDataList.get(i).getPushkey()).child("reminded").setValue("once");
                                }
                                smsDataList.clear();
                                Snackbar.make(join, "SMS sent Successfully.", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                                dialog1.dismiss();
                            } else {
                                Snackbar.make(join, "Failed to send sms", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#000000"))
                                        .setTextColor(Color.parseColor("#000000"))
                                        .setBackgroundTint(Color.parseColor("#FF5252"))
                                        .show();
                            }
                            Log.e("BULK code", code + "");
                            Log.e("response", response.toString());
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // enjoy your error status
                    Log.e("Status of code = ", "Wrong " + error);
                    Snackbar.make(join, "Failed to send sms.", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#000000"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setBackgroundTint(Color.parseColor("#FF5252"))
                            .show();
                }
            });
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 15000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 15000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            Log.d("string", stringRequest.toString());
            requestQueue.add(stringRequest);
        }
    }

    private void send_notification(List<String> phone_numbers) {
        noti_keys_copy_selected_phone.clear();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<phone_numbers.size();i++){
                    int check=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.child(ds.getKey()).child(phone_numbers.get(i)).exists()) {
                            check=1;
                            String body=extract_data(i, keys_copy_selected_phone.get(i), phone_numbers.get(i));
                            if(snapshot.child(ds.getKey()).child("token").exists()) {
                                reference.child(keys_copy_selected_phone.get(i)).child("reminded").setValue("once");
                                for (DataSnapshot dd : snapshot.child(ds.getKey()).child("token").getChildren()) {
                                    String token = snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(dd.getKey())).getValue(String.class);
                                    if (token != null) {
                                     /*   Specific specific = new Specific();
                                        Log.e("This",keys_copy_selected_phone.get(i));
                                        specific.noti("CG Sangyan", body, token,keys_copy_selected_phone.get(i),"data");*/
                                    }
                                }
                            }
                            else{
                                noti_keys_copy_selected_phone.add(keys_copy_selected_phone.get(i));
                            }
                        }
                    }
                    if(check==0)
                        not_sent_sms_list.add(keys_copy_selected_phone.get(i));
                }
                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                Log.e("keys copy selected phone = ",noti_keys_copy_selected_phone+"");

                getContextNullSafety().getSharedPreferences("saving_RM_Call_not_noti", MODE_PRIVATE).edit()
                        .putString("RM_CALL_list",noti_keys_copy_selected_phone+"").apply();

                getContextNullSafety().getSharedPreferences("saving_RM_Call_not_sms", MODE_PRIVATE).edit()
                        .putString("RM_CALL_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Notified successfully...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();
                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String extract_data(int index, String pushkey, String ph_number) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date date = new Date();
        String format=case_data_list_filter.get(index);
        String C=null,B=null,K=null,D=null,E=null,G=null,H=null,I=null,type=null,L=null;
        int temp=0;
        Log.e("Format",format);
        for(int i=0;i<format.length();i++){
            if(format.charAt(i)=='~'){
                if(type==null){
                    type=format.substring(0,i);
                    temp=i+1;
                }
                else if(K==null){
                    K=format.substring(temp,i);
                    temp=i+1;
                }
                else if (C==null){
                    C=format.substring(temp,i);
                    temp=i+1;
                }
                else if(D==null){
                    D=format.substring(temp,i);
                    temp=i+1;
                }
                else if(E==null){
                    E=format.substring(temp,i);
                    temp=i+1;
                }
                else if(G==null){
                    G=format.substring(temp,i);
                    temp=i+1;
                }
                else if(H==null){
                    H=format.substring(temp,i);
                    temp=i+1;
                }
                else if(I==null){
                    I=format.substring(temp,i);
                    temp=i+1;
                }
                else if(B==null){
                    B=format.substring(temp,i);
                    Log.e("Last date Format",format.substring(i+1)+"");
                    L=format.substring(i+1);
                }
            }
        }
        if(type.equals("RM RETURN")){
            String current=formatter.format(date);
            smsData smsData=new smsData(current,E+"/"+G,H+"/"+I,B,K,"1107166841984501076",ph_number,D+" No.",pushkey);
            smsDataList.add(smsData);
            return "हाईकोर्ट अलर्ट:-डायरी वापसी"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"1)उपरोक्त मूल केश डायरी  महाधिवक्ता कार्यालय द्वारा दी गयी मूल पावती लाने पर ही दी जाएगी।\n"
                    +"2) उपरोक्त मूल केश डायरी "+K+" से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।";
        }
        else{
            String current=formatter.format(date);
            smsData smsData=new smsData(current,E+"/"+G,H+"/"+I,B,K,"1107166842005504102",ph_number,D+" No.",pushkey);
            Log.e("pushkey",pushkey+"");
            smsDataList.add(smsData);
            return "हाईकोर्ट अलर्ट:-डायरी माँग"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"उपरोक्त मूल केश डायरी तथा पूर्व अपराधिक रिकॉर्ड, दिनाँक "+L+" तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";
        }
    }

    private void search(String str) {
        if(str.equals("")){
            excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
            excel_adapter.notifyDataSetChanged();
            if(mRecyclerView!=null)
                mRecyclerView.setAdapter(excel_adapter);
        }
        else {
            String[] str_Args = str.toLowerCase().split(" ");
            mylist.clear();
            int count = 0;
            boolean not_once = true;
            List<Integer> c_list = new ArrayList<>();
            for (Excel_data object : excel_data) {
                convert_to_list(object);
                for (String s : list) {
                    for (String str_arg : str_Args) {
                        if (str_arg.contains("/") && not_once) {
                            String sub1 = str_arg.substring(0, str_arg.indexOf("/"));
                            String sub2 = str_arg.substring(str_arg.indexOf("/") + 1);
                            if (list.get(4).contains(sub1) && list.get(6).contains(sub2)) {
                                count++;
                                not_once = false;
                            } else if (list.get(7).contains(sub1) && list.get(8).contains(sub2)) {
                                count++;
                                not_once = false;
                            }
                        } else if (s.contains(str_arg)) {
                            count++;
                        }
                    }
                }
                c_list.add(count);
                System.out.println(c_list + "");
                if (count == str_Args.length)
                    mylist.add(object);
                count = 0;
            }
            excel_adapter = new Excel_Adapter(getContextNullSafety(), mylist, onClickInterface, onAgainClickInterface,gsID);
            excel_adapter.notifyDataSetChanged();
            if (mRecyclerView != null)
                mRecyclerView.setAdapter(excel_adapter);
        }
    }
    private void convert_to_list(Excel_data object) {
        list.clear();
        try{
            list.add(object.getAa().toLowerCase());
            list.add(object.getBb().toLowerCase());
            list.add(object.getCc().toLowerCase());
            list.add(object.getDd().toLowerCase());
            list.add(object.getEe().toLowerCase());
            list.add(object.getFf().toLowerCase());
            list.add(object.getGg().toLowerCase());
            list.add(object.getHh().toLowerCase());
            list.add(object.getIi().toLowerCase());
            list.add(object.getJj().toLowerCase());
            list.add(object.getKk().toLowerCase());
            list.add(object.getLl().toLowerCase());
            list.add(object.getMm().toLowerCase());
            list.add(object.getNn().toLowerCase());
            list.add(object.getDate().toLowerCase());
            list.add(object.getType().toLowerCase());
            list.add(object.getPushkey().toLowerCase());
            list.add(object.getReminded().toLowerCase());
            list.add(object.getSeen().toLowerCase());
            list.add(object.getDate_of_alert().toLowerCase());
            list.add(object.getSent().toLowerCase());
            list.add(object.getNumber().toLowerCase());
        }
        catch (NullPointerException e){
            System.out.println("Error");
        }
    }
    private void getdata() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        mSwipeRefreshLayout.setRefreshing(true);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void getDataForIG() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        mSwipeRefreshLayout.setRefreshing(true);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(tinyDB.getListString("districts_list").contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))) {
                        excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getDataForSDOP() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        mSwipeRefreshLayout.setRefreshing(true);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(tinyDB.getListString("districts_list")
                            .contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))
                            && tinyDB.getListString("stations_list").contains("PS "+snapshot.child(Objects.requireNonNull(ds.getKey())).child("B").getValue(String.class)))
                        excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getdata_for_sp() {
        Log.e("sp","triggered");
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        mSwipeRefreshLayout.setRefreshing(true);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("C").getValue(String.class).equals(sp_of)) {
                        excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,gsID);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /*private void update_J_Excel(List<Excel_data> j_dates_list,String j_date) {
        Snackbar.make(mRecyclerView,"Updating dates...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        j_dialog.dismiss();
        Log.e("Sheet",j_date+"");
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        String jsonExcelList = gson.toJson(j_dates_list);
        // print your generated json
        Log.e("jsonCartList: " , jsonExcelList);
        String prev_keygen=j_dates_list.get(0).getB()+"-"+j_dates_list.get(0).getE();
        JSONObject jsonBody = new JSONObject();
        try
        {
            jsonBody.put("data", jsonExcelList);
            jsonBody.put("j_column", j_date);
            jsonBody.put("keygen",hashGenerator(prev_keygen));
            Log.d("body", "httpCall_collect: "+jsonBody);
        }
        catch (Exception e)
        {
            Log.e("Error","JSON ERROR");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContextNullSafety());
        String URL = "https://sangyan.co.in/bulk_j_column";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // enjoy your response
                        String code=response.optString("code")+"";
                        if(code.equals("202")){
                            for(int i=0;i<added_list.size();i++){
                                reference.child(added_list.get(i)).child("J").setValue(j_date);
                            }
                            Snackbar.make(join,"Data Uploaded to Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            dialog1.dismiss();
                        }
                        else{
                            Snackbar.make(join,"Failed to Upload in Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                  dialog1.dismiss();
                                }
                            },500);
                        }
                        Log.e("BULK code",code+"");
                        Log.e("response",response.toString());
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong "+error);
                Snackbar.make(join,"Failed to Upload in Excel.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#000000"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#FF5252"))
                        .show();
                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("error.json");
                dialog1.show();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },500);
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 15000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);

    }*/
    private void update_J_Excel(List<Excel_data> j_dates_list,String j_date) {
        Snackbar.make(mRecyclerView,"Updating dates...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        j_dialog.dismiss();
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        String jsonExcelList = gson.toJson(j_dates_list);
        // print your generated json
        Log.e("jsonCartList: " , jsonExcelList);
        Log.e("ps case",j_dates_list.get(0).getBb());
        String prev_keygen=j_dates_list.get(0).getBb()+"-"+j_dates_list.get(0).getEe();
        Log.e("GS",gsID);
        String URL = "http://script.google.com/macros/s/"
                + gsID+"/exec?"
                +"data="+jsonExcelList
                +"&j_column="+j_date
                +"&keygen="+hashGenerator(prev_keygen)
                +"&action=bulkjColumn";

        RequestQueue queue = Volley.newRequestQueue(getContextNullSafety());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String code="";
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            code=jsonObj.get("code")+"";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(code.equals("202")){
                            for(int i=0;i<added_list.size();i++){
                                reference.child(added_list.get(i)).child("J").setValue(j_date);
                            }
                            Snackbar.make(join,"Data Uploaded.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            dialog1.dismiss();
                        }
                        else{
                            Snackbar.make(join,"Failed to Upload.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                }
                            },2000);
                        }
                        Log.e("BULK code", response +"");
                        Log.e("BULK response",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
                Snackbar.make(join,"Failed to Upload.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#000000"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#FF5252"))
                        .show();
                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("error.json");
                dialog1.show();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },2000);
            }
        });

        queue.add(stringRequest);
    }
    /*private void delete_data(List<Excel_data> delete_list) {
        Snackbar.make(mRecyclerView,"Deleting selected data...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        dialogD.dismiss();
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        String jsonExcelList = gson.toJson(delete_list);
        // print your generated json
        Log.e("jsonCartList: " , jsonExcelList);
        String prev_keygen=delete_list.get(0).getB()+"-"+delete_list.get(0).getE();
        JSONObject jsonBody = new JSONObject();
        try
        {
            jsonBody.put("data", jsonExcelList);
            jsonBody.put("keygen",hashGenerator(prev_keygen));
            Log.d("body", "httpCall_collect: "+jsonBody);
        }
        catch (Exception e)
        {
            Log.e("Error","JSON ERROR");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContextNullSafety());
        //String URL = "http://sangyan.co.in/bulk_j_column";
        String URL = "http://high-court-alertsystem.herokuapp.com/delete_data";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // enjoy your response
                        String code=response.optString("code")+"";
                        if(code.equals("201")){
                            for(int i=0;i<added_list.size();i++){
                                reference.child(added_list.get(i)).removeValue();
                                for(int j=0;j<excel_data.size();j++){
                                    if(excel_data.get(j).getPushkey().equals(added_list.get(i))) {
                                        excel_adapter.remove(excel_data.get(j));
                                    }
                                }
                            }
                            Snackbar.make(join,"Data Uploaded to Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            dialog1.dismiss();
                        }
                        else{
                            Snackbar.make(join,"Failed to Upload in Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                }
                            },500);
                        }
                        Log.e("BULK code",code+"");
                        Log.e("response",response.toString());
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong "+error);
                Snackbar.make(join,"Failed to Upload in Excel.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#000000"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#FF5252"))
                        .show();
                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("error.json");
                dialog1.show();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },500);
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 15000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);

    }*/

    private void delete_data(List<Excel_data> delete_list) {
        dialogD.dismiss();
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        String jsonExcelList = gson.toJson(delete_list);
        // print your generated json
        Log.e("jsonCartList: " , jsonExcelList);

        String prev_keygen=delete_list.get(0).getBb()+"-"+delete_list.get(0).getEe();

        String URL = "http://script.google.com/macros/s/"
                + gsID+"/exec?"
                +"data="+jsonExcelList
                +"&keygen="+hashGenerator(prev_keygen)
                +"&action=deleteData";

        RequestQueue queue = Volley.newRequestQueue(getContextNullSafety());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String code="";
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            code=jsonObj.get("code")+"";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(code.equals("202")){
                            for(int i=0;i<added_list.size();i++){
                                reference.child(added_list.get(i)).removeValue();
                                for(int j=0;j<excel_data.size();j++){
                                    if(excel_data.get(j).getPushkey().equals(added_list.get(i))) {
                                        excel_adapter.remove(excel_data.get(j));
                                    }
                                }
                            }
                            Snackbar.make(join,"Data Deleted.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            dialog1.dismiss();
                        }
                        else{
                            Snackbar.make(join,"Failed to Delete.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                }
                            },2000);
                        }
                        Log.e("BULK code", response +"");
                        Log.e("BULK response",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
                Snackbar.make(join,"Failed to Delete.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#000000"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#FF5252"))
                        .show();
                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("error.json");
                dialog1.show();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },2000);
            }
        });

        queue.add(stringRequest);

    }


    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
    protected String hashGenerator(String str_hash) {
        // TODO Auto-generated method stub
        StringBuffer finalString=new StringBuffer();
        finalString.append(str_hash);
        //		logger.info("Parameters for SHA-512 : "+finalString);
        String hashGen=finalString.toString();
        StringBuffer sb = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(hashGen.getBytes());
            byte byteData[] = md.digest();
            //convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }


}