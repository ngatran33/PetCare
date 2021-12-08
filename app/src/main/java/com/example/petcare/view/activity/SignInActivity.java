package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petcare.R;
import com.example.petcare.adapter.Util;
import com.example.petcare.databinding.ActivitySignInBinding;
import com.example.petcare.presenter.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        auth=FirebaseAuth.getInstance();
        binding.dnBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=binding.dnEtUser.getText().toString();
                String pass=binding.dnEtPass.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                    Toast.makeText(getBaseContext(), "All fields are required", Toast.LENGTH_LONG).show();
                }else{
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent=new Intent(getBaseContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(getBaseContext(), "You can't Sign In with email and pass", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        binding.dnTvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        binding.dnTvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot();
            }
        });
    }

    private void forgot() {
        Dialog dialog = new Dialog(SignInActivity.this);
        dialog.setTitle("Help");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.reset_pass);
        dialog.show();

        EditText email=dialog.findViewById(R.id.send_email);
        Button reset=dialog.findViewById(R.id.btn_reset);
        ImageView close=dialog.findViewById(R.id.btn_Close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr=email.getText().toString();
                if(emailStr.equals("")){
                    Toast.makeText(getBaseContext(), "All fields are required!", Toast.LENGTH_LONG).show();
                }else{
                    auth.sendPasswordResetEmail(emailStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getBaseContext(), "Please check your Email", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                            else{
                                Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
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