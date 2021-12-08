package com.example.petcare.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.adapter.AdapterThuY;
import com.example.petcare.model.ThuY;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VeterinaryFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    List<ThuY> thuYList;
    AdapterThuY adapterThuY;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_find_pet, container, false);

        recyclerView=view.findViewById(R.id.recyclerViewThuY);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        thuYList =new ArrayList<>();
        hienThi();
        return view;
    }

    private void hienThi() {
        thuYList.clear();
        reference.child("veterinarys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ThuY quangCao=dataSnapshot.getValue(ThuY.class);
                    thuYList.add(quangCao);
                }
                adapterThuY =new AdapterThuY(thuYList, getActivity());
                recyclerView.setAdapter(adapterThuY);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}