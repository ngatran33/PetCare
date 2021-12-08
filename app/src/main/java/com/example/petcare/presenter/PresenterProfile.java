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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PresenterProfile {
    IProfile itf;
    Context context;
    List<Status> statusList;
    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
    FirebaseUser userFire= FirebaseAuth.getInstance().getCurrentUser();

    public PresenterProfile(IProfile itf, Context context) {
        this.itf = itf;
        this.context = context;
    }

    public void openImages(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            itf.openGallery(code);
            return;
        }
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            itf.openGallery(code);
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            itf.requestPermissionsAnh(permission, code);
        }
    }

    public void hienThi() {
        statusList = new ArrayList<>();
        databaseReference.child("Tus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Status status = dataSnapshot.getValue(Status.class);
                    if (status.getUserId().equals(userFire.getUid())) {
                        statusList.add(status);
                    }
                }
                if (statusList.size() > 0) {
                    Collections.sort(statusList);
                    itf.setAdapterPrf(statusList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String getFileExtention(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage(Uri muri, StorageReference storageReference, StorageTask<UploadTask.TaskSnapshot> uploadTask, String s) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Uploading");
        pd.show();

        if (muri != null) {
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(muri));
            uploadTask = storageReference1.putFile(muri);

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

                        HashMap<String, Object> map = new HashMap<>();
                        map.put(s, urim);
                        databaseReference.child("users").child(userFire.getUid()).updateChildren(map);
                        pd.dismiss();
                    } else {
                        itf.onMessage("Failed");
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    itf.onMessage(e.getMessage());
                    pd.dismiss();
                }
            });
        } else {
            itf.onMessage("No image selected");
        }

    }

    public boolean change(String userChange, String userOld) {
        if (!userChange.equals(userOld)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", userChange);
            map.put("search", userChange.toLowerCase());
            databaseReference.child("users").child(userFire.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        itf.toast("Success");
                    } else {
                        itf.toast("Failed");
                    }
                }
            });
        } else {
            itf.toast("Old userName same new userName");
        }
        return true;
    }

}
