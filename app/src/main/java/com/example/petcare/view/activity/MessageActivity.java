package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.adapter.Util;
import com.example.petcare.databinding.ActivityMessageBinding;
import com.example.petcare.model.Chats;
import com.example.petcare.model.User;
import com.example.petcare.adapter.MessageAdapter;
import com.example.petcare.presenter.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    Intent intent;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    MessageAdapter adapter;
    List<Chats> chatsList;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_message);

        intent=getIntent();
        String userID=intent.getStringExtra("userId");


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("users").child(userID);


        binding.messRecyc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.messRecyc.setLayoutManager(linearLayoutManager);

        setSupportActionBar(binding.messToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.messToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                binding.messUser.setText(user.getUsername());
                if(user.getAvt().equals("default")){
                    binding.messAvt.setImageResource(R.drawable.users);
                }else
                    Glide.with(getApplicationContext()).load(user.getAvt()).into(binding.messAvt);
                readMessages(firebaseUser.getUid(), userID, user.getAvt());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.messBtnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=binding.messEtSend.getText().toString();
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(), userID, message);
                }else {
                    Toast.makeText(getBaseContext(), "You can't send empty message", Toast.LENGTH_LONG).show();
                }
                binding.messEtSend.setText("");
            }
        });
    }
    private  void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference();
        Chats chats=new Chats(sender, receiver, message);
        reference1.child("Chats").push().setValue(chats);

    }

    private  void readMessages(final String myId,final String userId, String imgAvt){
        chatsList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chats= dataSnapshot.getValue(Chats.class);
                    if((chats.getReceiver().equals(myId) && chats.getSender().equals(userId))
                    ||(chats.getReceiver().equals(userId) && chats.getSender().equals(myId))){
                        chatsList.add(chats);
                    }
                    adapter=new MessageAdapter(MessageActivity.this, chatsList, imgAvt);
                    binding.messRecyc.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> map=new HashMap<>();
        map.put("status", status);

        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
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