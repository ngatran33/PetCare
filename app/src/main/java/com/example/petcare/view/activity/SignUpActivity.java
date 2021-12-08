package com.example.petcare.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.petcare.R;
import com.example.petcare.databinding.ActivitySignUpBinding;
import com.example.petcare.presenter.ISignUp;
import com.example.petcare.presenter.NetworkChangeListener;
import com.example.petcare.presenter.PresenterSignUp;

public class SignUpActivity extends AppCompatActivity implements ISignUp {

    ActivitySignUpBinding binding;
    ProgressDialog progressDialog;
    PresenterSignUp presenterSignUp;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        presenterSignUp = new PresenterSignUp(getBaseContext(), this);
        progressDialog = new ProgressDialog(this);


        binding.dkBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage(getString(R.string.pross));
                progressDialog.show();

                String email = binding.dkEtUser.getText().toString();
                String pass = binding.dkEtPass.getText().toString();
                String passcf = binding.dkEtPassConfirm.getText().toString();
                presenterSignUp.addUser(email, pass, passcf);
            }
        });
        binding.dkTvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSuccessful() {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), getString(R.string.success), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailed() {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "You can't sign up with this email", Toast.LENGTH_LONG).show();
    }

    @Override
    public void progressDialog() {
        progressDialog.dismiss();
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