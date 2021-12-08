package com.example.petcare.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.petcare.R;
import com.example.petcare.model.User;
import com.example.petcare.view.activity.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PresenterSignUp {
    Context context;//getBaseContext()
    ISignUp iSignUp;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    public PresenterSignUp(Context context, ISignUp iSignUp) {
        this.context = context;
        this.iSignUp = iSignUp;
    }

    public void addUser(String email, String pass, String passcf) {
        if (email.trim().length() <= 0) {
            Toast.makeText(context, context.getString(R.string.email), Toast.LENGTH_LONG).show();
            iSignUp.progressDialog();
        } else if (pass.trim().length() <= 0) {
            Toast.makeText(context, context.getString(R.string.passnull), Toast.LENGTH_LONG).show();
            iSignUp.progressDialog();
        } else if (!passcf.equals(pass)) {
            Toast.makeText(context, context.getString(R.string.passequa), Toast.LENGTH_LONG).show();
            iSignUp.progressDialog();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        User user = new User(email, userId, "default", "default", "offline", email.toLowerCase());
                        databaseReference.child("users").child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    iSignUp.onSuccessful();
                                }
                            }
                        });
                    } else {
                        iSignUp.onFailed();
                    }
                }

            });

        }

    }
}

