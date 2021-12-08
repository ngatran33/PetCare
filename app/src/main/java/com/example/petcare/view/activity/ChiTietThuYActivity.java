package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.databinding.ActivityChiTietThuYactivityBinding;
import com.example.petcare.model.ThuY;
import com.example.petcare.presenter.NetworkChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChiTietThuYActivity extends AppCompatActivity {

    ActivityChiTietThuYactivityBinding binding;
    String id;
    List<ThuY> thuYList;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chi_tiet_thu_yactivity);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        setSupportActionBar(binding.cttyToolBar);
        getSupportActionBar().setTitle("Veterinary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        thuYList = new ArrayList<>();

        binding.cttyToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        hienThi();

        binding.cttyFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thuYList.get(0).getFanpage().equals("//")) {
                    gotoUrl(thuYList.get(0).getFanpage());
                }
            }
        });

        binding.cttyWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thuYList.get(0).getWebsite().equals("//")) {
                    gotoUrl(thuYList.get(0).getWebsite());
                } else {
                    Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.cttyTvDC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thuYList.size() > 0) {
                    Intent intent1 = new Intent(ChiTietThuYActivity.this, MapsActivity.class);
                    intent1.putExtra("latitude", thuYList.get(0).getLatitude());
                    intent1.putExtra("longitude", thuYList.get(0).getLongitude());
                    intent1.putExtra("name", thuYList.get(0).getName());
                    startActivity(intent1);
                }
            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void hienThi() {
        thuYList.clear();
        reference.child("veterinarys").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ThuY thuY = snapshot.getValue(ThuY.class);
                thuYList.add(thuY);
                binding.cttyTen.setText(thuY.getName());
                binding.cttyFb.setText(thuY.getFanpage());
                binding.cttyMoTa.setText(thuY.getMota());
                binding.cttySDT.setText(thuY.getSdt());
                binding.cttyTg.setText(thuY.getThoigian());
                binding.cttyTvDC.setText(thuY.getDiaChi());
                binding.cttyWeb.setText(thuY.getWebsite());
                Glide.with(getApplicationContext()).load(thuY.getAnh()).into(binding.cttyAnh);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}