package com.example.petcare.presenter;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.petcare.model.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PresenterHomeFragment {
    IHomeFragment iHomeFragment;
    Context context;

    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storage = FirebaseStorage.getInstance().getReference("images");

    public PresenterHomeFragment(IHomeFragment iHomeFragment, Context context) {
        this.iHomeFragment = iHomeFragment;
        this.context = context;
    }

    public void openImages(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            iHomeFragment.openGallery(code);
            return;
        }
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            iHomeFragment.openGallery(code);
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            iHomeFragment.requestPermissionsTus(permission, code);
        }
    }


    public String getFileExtention(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void post(String loai, String mota, boolean flag, Uri mUri) {//loai.getText().toString(), mota.getText().toString()
        reference = FirebaseDatabase.getInstance().getReference("Tus");
        String id = FirebaseDatabase.getInstance().getReference("Tus").push().getKey();

        Calendar calendar=Calendar.getInstance();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd");
        Status status = new Status(id, "default", loai, mota, firebaseUser.getUid(),dateFormat.format(calendar.getTime()));
        if (loai.length() <= 0) {
            status.setLoai("default");
        }
        if (mota.length() <= 0) {
            status.setMota("default");
        }
        if (loai.length() <= 0 && mota.length() <= 0 && !flag) {
            iHomeFragment.toast("Empty data! failed");
            iHomeFragment.dismissDialog();
            return;
        }
        reference.child(id).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (mUri != null) {
                        if (uploadTask != null && uploadTask.isInProgress()) {
                            iHomeFragment.toast("Upload in progress");
                        } else {
                            uploadImage(id, mUri);
                        }
                    }
                    iHomeFragment.toast("Post");
                    iHomeFragment.dismissDialog();
                } else {
                    iHomeFragment.toast("You can't create post");
                    iHomeFragment.dismissDialog();
                }
            }
        });
    }

    public void uploadImage(String key, Uri mUri) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Uploading");
        pd.show();
        if (mUri != null) {
            final StorageReference storageReference1 = storage.child(System.currentTimeMillis() + "." + getFileExtention(mUri));
            uploadTask = storageReference1.putFile(mUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String urim = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Tus").child(key);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("img", urim);
                        reference.updateChildren(map);
                        pd.cancel();
                    } else {
                        iHomeFragment.toast("Failed");
                        pd.cancel();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iHomeFragment.toast(e.getMessage());
                    pd.cancel();
                }
            });
        } else {
            iHomeFragment.toast("No image selected");
        }

    }

    public void showTus() {
        List<Status> statusList = new ArrayList<>();
        reference.child("Tus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Status status = dataSnapshot.getValue(Status.class);
                    statusList.add(status);
                }
                Collections.sort(statusList);
                iHomeFragment.setAdapterTus(statusList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
