package com.example.petcare.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.petcare.R;
import com.example.petcare.adapter.BannerAdapter;
import com.example.petcare.model.ThuY;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class QuangCaoFragment extends Fragment {

    View view;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    BannerAdapter adapter;
    List<ThuY> quangCaoList;
    Runnable runnable;
    Handler handler;
    int current;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_quang_cao, container, false);
        anhxa();
        getData();
        return view;
    }

    private void getData() {
        quangCaoList = new ArrayList<>();
        reference.child("veterinarys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quangCaoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ThuY thuY = dataSnapshot.getValue(ThuY.class);
                    quangCaoList.add(thuY);
                }
                adapter = new BannerAdapter(getContext(), quangCaoList);
                viewPager.setAdapter(adapter);
                circleIndicator.setViewPager(viewPager);
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        current = viewPager.getCurrentItem();
                        current++;
                        if (current >= 3) {
                            current = 0;
                        }
                        viewPager.setCurrentItem(current, true);
                        handler.postDelayed(runnable, 4500);
                    }
                };
                handler.postDelayed(runnable, 4500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void anhxa() {
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.indicatorDefault);
    }

}