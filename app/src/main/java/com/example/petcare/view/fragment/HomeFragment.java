package com.example.petcare.view.fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.petcare.presenter.IHomeFragment;
import com.example.petcare.presenter.PresenterHomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements IHomeFragment {

    View view;
    RecyclerView frmHomeLv;
    LinearLayout frmHomeBtnFloat;
    RelativeLayout rc;
    CircleImageView homeImgAvt;
    TextView username;
    ImageView imgHien;

    DatabaseReference reference;
    FirebaseUser firebaseUser;


    AdapterTus adapterTus;
    Boolean flag = false;


    Dialog dialog;
    private static final int MY_REQUEST_CODE3 = 13;
    PresenterHomeFragment presenterHomeFragment;
    Uri mUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        frmHomeLv = view.findViewById(R.id.frmHomeLv);
        frmHomeBtnFloat = view.findViewById(R.id.frmHomeBtnFloat);
        username=view.findViewById(R.id.homeTvUsername);
        homeImgAvt=view.findViewById(R.id.homeImgAvt);
        presenterHomeFragment = new PresenterHomeFragment(this, getContext());

        frmHomeLv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        frmHomeLv.setFocusable(false);
        frmHomeLv.setNestedScrollingEnabled(false);
        frmHomeLv.setLayoutManager(linearLayoutManager);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        hienThi();

        presenterHomeFragment.showTus();

        frmHomeBtnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setTitle("Create Post");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_dang_bai);
                dialog.show();

                TextView username = dialog.findViewById(R.id.bdUsername);
                EditText mota = dialog.findViewById(R.id.bdTvMota);
                EditText loai = dialog.findViewById(R.id.bdTvTitle);
                ImageView avt = dialog.findViewById(R.id.bdImgAvt);
                ImageView chon = dialog.findViewById(R.id.dbChonImg);
                ImageView btnClose = dialog.findViewById(R.id.bdBtnClose);
                ImageView btnCloseanh = dialog.findViewById(R.id.closeanh);
                rc=dialog.findViewById(R.id.anhHT);
                imgHien = dialog.findViewById(R.id.bdimgHien);
                Button btnPost = dialog.findViewById(R.id.bdBtnPost);


                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        username.setText(user.getUsername());
                        if (getActivity() != null) {
                            Glide.with(getActivity().getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(avt);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                btnCloseanh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUri=null;
                        flag=false;
                        rc.setVisibility(View.GONE);
                    }
                });
                chon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenterHomeFragment.openImages(MY_REQUEST_CODE3);
                    }
                });
                btnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String loaipr = loai.getText().toString();
                        String moTa = mota.getText().toString();
                        presenterHomeFragment.post(loaipr, moTa, flag, mUri);
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        mUri=null;
                        flag=false;
                    }
                });

            }
        });

        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MY_REQUEST_CODE3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(mUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                int newHeight = 1000;
                float scaleFactor = ((float) newHeight) / height;
                float newWidth = width * scaleFactor;

                float scaleWidth = scaleFactor;
                float scaleHeight = scaleFactor;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                imgHien.setImageBitmap(bitmap);

                rc.setVisibility(View.VISIBLE);
                flag = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void hienThi(){
        reference=FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if(isAdded())
                    Glide.with(getContext()).load(user.getAvt()).error(R.drawable.users).into(homeImgAvt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void openGallery(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, code);
    }

    @Override
    public void requestPermissionsTus(String[] permission, int code) {
        requestPermissions(permission, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(MY_REQUEST_CODE3);
            }
        }
    }

    @Override
    public void toast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismissDialog() {
        dialog.cancel();
    }

    @Override
    public void setAdapterTus(List<Status> statusList) {
        if (getActivity() != null ) {
            if(getActivity().isDestroyed() ) {
                adapterTus = new AdapterTus(statusList, getActivity().getApplicationContext());
            }else {
                adapterTus = new AdapterTus(statusList, getContext());
            }
        } else {
            adapterTus = new AdapterTus(statusList, getContext());
        }
        frmHomeLv.setAdapter(adapterTus);
    }

}