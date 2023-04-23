package com.alpha.innohacksproject.Home;


import static android.content.Context.MODE_PRIVATE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.alpha.innohacksproject.DB.TinyDB;
import com.alpha.innohacksproject.Home.Fragments.Mcrc_Rm_Coll;
import com.alpha.innohacksproject.Home.Fragments.Mcrc_Rm_Return;
import com.alpha.innohacksproject.Home.Fragments.today;
import com.alpha.innohacksproject.Home.Fragments.urgent_data;
import com.alpha.innohacksproject.Home.Model.Excel_data;
import com.alpha.innohacksproject.R;
import com.alpha.mylib.v4.FragmentPagerItemAdapter;
import com.alpha.mylib.v4.FragmentPagerItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import soup.neumorphism.NeumorphCardView;


public class Frag_Home extends Fragment {

    List<Excel_data> pending_return=new ArrayList<>();
    long total_coll,total_return;
    Query query_coll,query_return;
    TextView coll_text,text_return;
    TextView welcome,check_todays;
    NeumorphCardView blue,back;
    int c=0;
    private Context contextNullSafe;
    View view;
    String sp_of;
    TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_frag__home, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        tinyDB=new TinyDB(getContextNullSafety());
        welcome=view.findViewById(R.id.textView3);
        check_todays=view.findViewById(R.id.textView4);
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                offanimate(welcome);
                offanimate(check_todays);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        welcome.setVisibility(View.GONE);
                        check_todays.setVisibility(View.GONE);
                    }
                },500);
            }
        },3000);
        blue=view.findViewById(R.id.blue);
        back=view.findViewById(R.id.back);
       /* blue.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new pending_coll())
                    .addToBackStack(null)
                    .commit();
        });
        back.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new pending_return())
                    .addToBackStack(null)
                    .commit();
        });*/
        coll_text=view.findViewById(R.id.textView6);
        text_return=view.findViewById(R.id.textView8);

        query_coll = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM CALL");
        query_return = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM RETURN");

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                ((FragmentActivity)getContextNullSafety()).getSupportFragmentManager(), FragmentPagerItems.with(getContextNullSafety())
                .add("Urgent diary", urgent_data.class)
                .add("Today", today.class)
                .add("MCRC Call", Mcrc_Rm_Coll.class)
                .add("MCRC Return", Mcrc_Rm_Return.class)
                .create());

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        if(sp_of.equals("none")) {
            if (tinyDB.getInt("num_station") == 0) {
                getDataForIG();
            } else if (tinyDB.getInt("num_station") == 10) {
                getDataForSDOP();
            } else {
                get_pending();
            }
        }
        else
            getdata_for_sp();

        return view;
    }

    private void getdata_for_sp() {
        query_coll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(snapshot.child(ds.getKey()).child("C").getValue(String.class).equals(sp_of)) {
                            total_coll++;
                        }
                    }
                }
                String text=total_coll+"";
                coll_text.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(snapshot.child(ds.getKey()).child("C").getValue(String.class).equals(sp_of)) {
                            total_return++;
                            pending_return.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                String text=total_return+"";
                text_return.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getDataForSDOP() {
        query_coll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list")
                                .contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))
                                && tinyDB.getListString("stations_list").contains("PS "+snapshot.child(Objects.requireNonNull(ds.getKey())).child("B").getValue(String.class))) {
                            total_coll++;
                        }
                    }
                }
                String text=total_coll+"";
                coll_text.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list")
                                .contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))
                                && tinyDB.getListString("stations_list").contains("PS "+snapshot.child(Objects.requireNonNull(ds.getKey())).child("B").getValue(String.class))) {
                            total_return++;
                            pending_return.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                String text=total_return+"";
                text_return.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getDataForIG() {
        query_coll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list").contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))) {
                            total_coll++;
                        }
                    }
                }
                String text=total_coll+"";
                coll_text.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list").contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))) {
                            total_return++;
                            pending_return.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                String text=total_return+"";
                text_return.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
    private void get_pending() {
        query_coll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        total_coll++;
                    }
                }
                String text=total_coll+"";
                coll_text.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        total_return++;
                        pending_return.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                    }
                }
                String text=total_return+"";
                text_return.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }

}