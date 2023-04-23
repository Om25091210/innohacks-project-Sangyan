package com.alpha.innohacksproject.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.innohacksproject.Adapter.NumberAdapter;
import com.alpha.innohacksproject.Model.NumberModel;
import com.alpha.innohacksproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NumberData extends Fragment {


    View view;
    TextView text;
    String district;
    EditText inputSearch;
    DatabaseReference reference;
    List<NumberModel> list;
    Context contextNullSafe;
    RecyclerView recyclerView;
    ImageView back_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_number_data, container, false);

        list = new ArrayList<>();
        text = view.findViewById(R.id.text);
        inputSearch = view.findViewById(R.id.search);

        try {
            assert getArguments() != null;
            district = getArguments().getString("DistrictName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        NumberAdapter adapter = new NumberAdapter(getContextNullSafety(), list, district);
        text.setText(district);

        recyclerView = view.findViewById(R.id.rv2);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers").child(district);
        back_btn = view.findViewById(R.id.back);


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputSearch.getText().toString().equals("")) {
                    adapter.setTasks(list);
                }
                else if(list.size() != 0){
                    adapter.searchNotes(s.toString());
                }
            }
        });

        back_btn.setOnClickListener(v -> {
            FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            if(fm.getBackStackEntryCount()>0) {
                fm.popBackStack();
            }
            ft.commit();
        });
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    NumberModel numberModel = new NumberModel(ds.getKey(), snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(String.class));
                    list.add(numberModel);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
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