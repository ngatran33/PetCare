package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.databinding.ActivityEditPostBinding;
import com.example.petcare.model.Status;
import com.example.petcare.model.User;
import com.example.petcare.presenter.IEditPost;
import com.example.petcare.presenter.NetworkChangeListener;
import com.example.petcare.presenter.PresenterEditPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditPostActivity extends AppCompatActivity implements IEditPost {

    private static final int MY_REQUEST_CODE3 = 14;
    ActivityEditPostBinding binding;
    String idPost;
    PresenterEditPost presenterEditPost;
    Uri mUri;
    Boolean flag = false;
    boolean flag1 = true;
    Status status;
    String date;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_post);
        Intent intent = getIntent();
        idPost = intent.getStringExtra("idPost");
        hienThi();
        presenterEditPost = new PresenterEditPost(this, getBaseContext());


        setSupportActionBar(binding.epToolBar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.epToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        binding.epChonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterEditPost.openImages(MY_REQUEST_CODE3);
            }
        });

        binding.epBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loai = binding.epTvTitle.getText().toString();
                String mota = binding.epTvMota.getText().toString();
                presenterEditPost.save(loai, mota, flag, mUri, idPost, status.getImg(), date, flag1);
            }
        });
        binding.closeanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.anhHT.setVisibility(View.GONE);
                flag1 = false;
            }
        });
    }

    private void hienThi() {
        reference.child("Tus").child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status = snapshot.getValue(Status.class);
                if (status != null) {
                    reference.child("users").child(status.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                Glide.with(getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(binding.epImgAvt);

                                if (status.getImg().equals("default")) {
                                    binding.anhHT.setVisibility(View.GONE);
                                } else {
                                    binding.anhHT.setVisibility(View.VISIBLE);
                                    Glide.with(getApplicationContext()).load(status.getImg()).into(binding.epimgHien);
                                }

                                if (status.getLoai().equals("default")) {
                                    binding.epTvTitle.setText("");
                                } else
                                    binding.epTvTitle.setText(status.getLoai());

                                if (status.getMota().equals("default")) {
                                    binding.epTvMota.setText("");
                                } else
                                    binding.epTvMota.setText(status.getMota());
                                date = status.getDate();
                                binding.epUsername.setText(user.getUsername());
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
    public void openGallery(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissionsTus(String[] permission, int code) {
        requestPermissions(permission, code);
    }

    @Override
    public void intentPost() {
        Intent intent = new Intent(getBaseContext(), PostActivity.class);
        intent.putExtra("id", idPost);
        startActivity(intent);
        finish();
    }

    @Override
    public void toast(String post) {
        Toast.makeText(getBaseContext(), post, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            try {
                InputStream inputStream = getBaseContext().getContentResolver().openInputStream(mUri);
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
                binding.epimgHien.setImageBitmap(bitmap);

                binding.anhHT.setVisibility(View.VISIBLE);
                flag = true;
                flag1 = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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