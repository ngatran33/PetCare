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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PresenterEditPost {
    IEditPost iEditPost;
    Context context;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storage = FirebaseStorage.getInstance().getReference("images");
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public PresenterEditPost(IEditPost iEditPost, Context context) {
        this.iEditPost = iEditPost;
        this.context = context;
    }

    public void openImages(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            iEditPost.openGallery(code);
            return;
        }
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            iEditPost.openGallery(code);
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            iEditPost.requestPermissionsTus(permission, code);
        }
    }

    public void save(String loai, String mota, boolean flag, Uri mUri, String idPost, String uri, String date) {//loai.getText().toString(), mota.getText().toString()

        Status status = new Status(idPost, uri, loai, mota, firebaseUser.getUid(), date);
        if (loai.length() <= 0) {
            status.setLoai("default");
        }
        if (mota.length() <= 0) {
            status.setMota("default");
        }
        if (loai.length() <= 0 && mota.length() <= 0 && !flag) {
            iEditPost.intentPost();
            return;
        }
        reference.child("Tus").child(idPost).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (mUri != null) {
                        if (uploadTask != null && uploadTask.isInProgress()) {
                            iEditPost.toast("Upload in progress");
                        } else {
                            uploadImage(idPost, mUri);
                        }
                    }
                    iEditPost.toast("Post");
                    iEditPost.intentPost();
                } else {
                    iEditPost.toast("You can't create post");
                    iEditPost.intentPost();
                }
            }
        });
    }

    private void uploadImage(String idPost, Uri mUri) {
//        final ProgressDialog pd = new ProgressDialog(context);
//        pd.setMessage("Uploading");
//        pd.show();
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


                        HashMap<String, Object> map = new HashMap<>();
                        map.put("img", urim);
                        reference.child("Tus").child(idPost).updateChildren(map);
//                        pd.dismiss();
                    } else {
                        iEditPost.toast("Failed");
//                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iEditPost.toast(e.getMessage());
//                    pd.dismiss();
                }
            });
        } else {
            iEditPost.toast("No image selected");
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
