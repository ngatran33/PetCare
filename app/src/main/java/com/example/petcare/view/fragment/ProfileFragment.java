package com.example.petcare.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.Status;
import com.example.petcare.model.User;
import com.example.petcare.adapter.AdapterTus;
import com.example.petcare.presenter.IProfile;
import com.example.petcare.presenter.PresenterProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements IProfile {

    View view;
    CircleImageView imgAvt;
    TextView tvUser;
    ImageView imgBgr;
    RecyclerView prfPost;

    AdapterTus adapterTus;
    DatabaseReference databaseReference;
    FirebaseUser userFire;

    PresenterProfile obj;
    StorageReference storageReference;
    private static final int MY_REQUEST_CODE =10 ;
    private static final int MY_REQUEST_CODE1 =11 ;
    Uri mUri;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_profile, container, false);
        anhxa(view);
        obj=new PresenterProfile(this, getContext());

        userFire = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(userFire.getUid());
        storageReference=FirebaseStorage.getInstance().getReference("images");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                tvUser.setText(user.getUsername());
                if(user.getAvt().equals("default")){
                    imgAvt.setImageResource(R.drawable.users);
                }else{
                    if(getActivity()!=null)
                        Glide.with(getActivity().getApplicationContext()).load(user.getAvt()).into(imgAvt);
                }

                if(user.getBgr().equals("default")){
                    imgBgr.setImageResource(R.drawable.meomeo);
                }else{
                    if(getActivity()!=null)
                    Glide.with(getActivity().getApplicationContext()).load(user.getBgr()).into(imgBgr);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.openImages(MY_REQUEST_CODE);
            }
        });

        obj.hienThi();

        imgBgr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.openImages(MY_REQUEST_CODE1);
            }
        });



        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(getContext());
                dialog.setTitle("Change Username");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.customdialog);
                dialog.show();
                EditText cusEtUser=dialog.findViewById(R.id.cusEtUser);
                Button cusBtnUpdate=dialog.findViewById(R.id.cusBtnUpdate);
                Button cusBtnHuy=dialog.findViewById(R.id.cusBtnHuy);

                String userold= (String) tvUser.getText();
                cusEtUser.setText(userold);

                cusBtnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userChange=cusEtUser.getText().toString().trim();
                        if(obj.change(userChange, userold)){
                            dialog.cancel();
                        }
                    }
                });
                cusBtnHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });

        return    view;
    }

    private void anhxa(View view) {
        imgAvt =view.findViewById(R.id.frmPrImgavt);
        tvUser =view.findViewById(R.id.frmPrTvuser);
        imgBgr=view.findViewById(R.id.prfImgBgr);
        prfPost=view.findViewById(R.id.prfPost);
        prfPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        prfPost.setFocusable(false);
        prfPost.setNestedScrollingEnabled(false);
        prfPost.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==MY_REQUEST_CODE && resultCode==RESULT_OK && data!= null && data.getData()!=null){
            mUri =data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_LONG).show();
            }
            else {
                obj.uploadImage(mUri, storageReference,uploadTask,"avt");
            }
        }
        if(requestCode==MY_REQUEST_CODE1 && resultCode==RESULT_OK && data!= null && data.getData()!=null){
            mUri =data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_LONG).show();
            }
            else {
                obj.uploadImage(mUri, storageReference,uploadTask,"bgr");
            }
        }
    }


    @Override
    public void onMessage(String s) {
        Toast.makeText(getContext(), s,Toast.LENGTH_LONG).show();
    }


    @Override
    public void openGallery(int code) {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,code);
    }

    @Override
    public void requestPermissionsAnh(String[] s, int code) {
        requestPermissions(s, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(MY_REQUEST_CODE);
            }
        }
        if(requestCode==MY_REQUEST_CODE1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(MY_REQUEST_CODE1);
            }
        }
    }

    @Override
    public void setAdapterPrf(List<Status> statusList) {
        adapterTus=new AdapterTus(statusList, getContext());
        prfPost.setAdapter(adapterTus);
    }

    @Override
    public void toast(String s) {
        Toast.makeText(getContext(), s,Toast.LENGTH_LONG).show();
    }
}