package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.petcare.R;
import com.example.petcare.adapter.AdapterTus;
import com.example.petcare.adapter.Util;
import com.example.petcare.databinding.ActivitySearchBinding;
import com.example.petcare.model.Status;
import com.example.petcare.presenter.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    List<String> stringList = new ArrayList<>();
    AdapterTus adapterTus;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);


        setSupportActionBar(binding.searchToolBar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.searchToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.searchLv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        binding.searchLv.setLayoutManager(linearLayoutManager);
        binding.searchLv.setFocusable(false);
        binding.searchLv.setNestedScrollingEnabled(false);


        docHistory();
        docTus();
        binding.searchBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });


        binding.searchImgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tim();

            }
        });


    }

    private void tim() {
        List<Status> statusList = new ArrayList<>();
        if (binding.searchTvSearch.getText().toString().trim().length() > 0) {
            reference.child("history").child(firebaseUser.getUid())
                    .child(binding.searchTvSearch.getText().toString()).setValue(binding.searchTvSearch.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
            reference.child("Tus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Status status = dataSnapshot.getValue(Status.class);
                        if (status.getLoai().toLowerCase().contains(binding.searchTvSearch.getText().toString().toLowerCase())
                                || status.getMota().toLowerCase().contains(binding.searchTvSearch.getText().toString().toLowerCase()))
                            statusList.add(status);
                    }
                    if (statusList.size() == 0) {
                        binding.searchTb.setVisibility(View.VISIBLE);
                        binding.searchTb.setText("No results were found");
                  } else {
                        binding.searchTb.setVisibility(View.GONE);
                    }
                    Collections.sort(statusList);
                    adapterTus = new AdapterTus(statusList, SearchActivity.this);
                    binding.searchLv.setAdapter(adapterTus);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(getBaseContext(), "Enter your key", Toast.LENGTH_LONG).show();
        }
    }

    private void docTus() {
        List<Status> statusList = new ArrayList<>();
        reference.child("Tus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding.searchTvSearch.getText().toString().length() == 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Status status = dataSnapshot.getValue(Status.class);
                        statusList.add(status);
                    }
                }
                Collections.sort(statusList);
                adapterTus = new AdapterTus(statusList, SearchActivity.this);
                binding.searchLv.setAdapter(adapterTus);
                binding.searchTb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void closeSearch() {
        List<Status> statusList = new ArrayList<>();
        reference.child("Tus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding.searchTvSearch.getText().toString().length() == 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Status status = dataSnapshot.getValue(Status.class);
                        statusList.add(status);
                    }
                }
                Collections.sort(statusList);
                adapterTus = new AdapterTus(statusList, SearchActivity.this);
                binding.searchLv.setAdapter(adapterTus);
                binding.searchTb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.searchTvSearch.setText("");
    }

    private void docHistory() {
        reference.child("history").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String s = dataSnapshot.getValue(String.class);
                    stringList.add(s);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.select_dialog_item, stringList);
                binding.searchTvSearch.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}