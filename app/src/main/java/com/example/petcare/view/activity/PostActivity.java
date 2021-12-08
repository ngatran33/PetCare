package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.adapter.Util;
import com.example.petcare.databinding.ActivityPostBinding;
import com.example.petcare.model.Comment;
import com.example.petcare.model.Status;
import com.example.petcare.model.User;
import com.example.petcare.adapter.AdapterCmt;
import com.example.petcare.presenter.IPost;
import com.example.petcare.presenter.NetworkChangeListener;
import com.example.petcare.presenter.PresenterPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostActivity extends AppCompatActivity implements IPost {

    ActivityPostBinding binding;
    Intent intent;
    String idPost;
    String idUser;
    DatabaseReference reference;
    FirebaseUser userFb;

    PresenterPost presenterPost;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_post);

        intent=getIntent();
        idPost=intent.getStringExtra("id");
        presenterPost=new PresenterPost(this,PostActivity.this, idPost);

        reference= FirebaseDatabase.getInstance().getReference();
        userFb=FirebaseAuth.getInstance().getCurrentUser();

        binding.postRcCmt.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.postRcCmt.setNestedScrollingEnabled(false);
        binding.postRcCmt.setFocusable(false);
        binding.postRcCmt.setLayoutManager(linearLayoutManager);

        setSupportActionBar(binding.postToolBar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.postToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
                    finish();
            }
        });

        showTus();
        presenterPost.showLike();
        showSendCmt();
        presenterPost.showCmt();


        binding.postBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=binding.postEtCmt.getText().toString();
                presenterPost.sendCmt(s);

            }
        });

        binding.postImgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterPost.like();
            }
        });


        binding.postImgLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterPost.unLiked();
            }
        });

    }

    private void showSendCmt() {// hien thi username va avt cua user hien tai
        reference.child("users").child(userFb.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                    Glide.with(getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(binding.postAvtCmt);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showTus(){//hien thi tus duoc chon
        reference.child("Tus").child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Status status=snapshot.getValue(Status.class);
                if(status!=null){
                    reference.child("users").child(status.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            assert user != null;
                            idUser=user.getId();
                            if(user!=null){
                                Glide.with(getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(binding.postAvt);

                                if(status.getImg().equals("default")){
                                    binding.postAnh.setVisibility(View.GONE);
                                }else
                                    Glide.with(getApplicationContext()).load(status.getImg()).into(binding.postAnh);

                                if(status.getLoai().equals("default")){
                                    binding.postloai.setText("");
                                }else
                                    binding.postloai.setText(status.getLoai());

                                if(status.getMota().equals("default")){
                                    binding.postTvMota.setText("");
                                }else
                                    binding.postTvMota.setText(status.getMota());

                                binding.date.setText(status.getDate());
                                binding.postTvUser.setText(user.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void liked() {//hien tim do
        binding.postImgLiked.setVisibility(View.VISIBLE);
        binding.postImgLike.setVisibility(View.GONE);
    }

    @Override
    public void unLiked() {//tat tim do
        binding.postImgLiked.setVisibility(View.GONE);
        binding.postImgLike.setVisibility(View.VISIBLE);
    }

    @Override//lenh toast
    public void toast(String s) {
        Toast.makeText(getBaseContext(),s, Toast.LENGTH_LONG).show();
    }

    @Override//set adapter cho RecyclerView
    public void adapterCmt(List<Comment> comments) {
        AdapterCmt adapterCmt=new AdapterCmt(comments, PostActivity.this,idUser);
        binding.postRcCmt.setAdapter(adapterCmt);
    }

    @Override
    public void sendCmt() {
        binding.postEtCmt.setText("");
        Toast.makeText(getBaseContext(),"Success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void luotLike(String i) {
        binding.postTvLuotLike.setText(i);
    }

    @Override
    public void luotCmt(String i) {
        binding.postTvLuotCmt.setText(i);
    }

    @Override
    public void deleteTus() {
        binding.postln.setVisibility(View.GONE);
        binding.postExist.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuEdit:
                if (userFb.getUid().equals(idUser)) {
                    Intent intent=new Intent(getBaseContext(), EditPostActivity.class);
                    intent.putExtra("idPost", idPost);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "You don't edit Post", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mnuDelete:
                if (userFb.getUid().equals(idUser)) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(PostActivity.this);
                    builder.setTitle("Notification");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to delete the post?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenterPost.deleteTus(idPost);
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "You don't delete", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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