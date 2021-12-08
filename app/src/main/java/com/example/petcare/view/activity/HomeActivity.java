package com.example.petcare.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.adapter.Util;
import com.example.petcare.databinding.ActivityHomeBinding;
import com.example.petcare.model.User;
import com.example.petcare.adapter.AdapterMenu;
import com.example.petcare.presenter.IHomeActivity;
import com.example.petcare.presenter.NetworkChangeListener;
import com.example.petcare.presenter.PresenterHomeActivity;
import com.example.petcare.adapter.ViewPagerAdapter;
import com.example.petcare.presenter.PresenterHomeFragment;
import com.example.petcare.view.fragment.ChatFragment;
import com.example.petcare.view.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements IHomeActivity {

    ActivityHomeBinding binding;
    ViewPagerAdapter adapter;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    PresenterHomeActivity presenterHomeActivity;
    String userId;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        presenterHomeActivity = new PresenterHomeActivity(this, getBaseContext());
        init();
        presenterHomeActivity.ActionMenu();
        ActionBAr();
        userId = firebaseUser.getUid();

        binding.lvDanhMuc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                danhMuc(position);
            }
        });
//        if(!Util.isNetworkAvailable(this)){// check internet
//            Toast.makeText(this,"Network disconnected", Toast.LENGTH_LONG).show();
//        }

        hienThi();
        binding.imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewPager.setCurrentItem(3);
                binding.navigation.setCurrentItem(3);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void danhMuc(int position) {
        if (position == 2) {
            help();
        }
        if (position == 0) {
            changeEmail();
        }
        if (position == 1) {
            changePass();
        }
        if (position == 3) {
            startActivity(new Intent(getBaseContext(), StartAppActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            FirebaseAuth.getInstance().signOut();
        }
    }


    private void hienThi() {
        reference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.tvTenUS.setText(user.getUsername());
                binding.tvEmail.setText(firebaseUser.getEmail());
                Glide.with(getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(binding.imgAvt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setPagingEnabled(true);
        presenterHomeActivity.init();

        binding.navigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                binding.viewPager.setCurrentItem(position);
                return true;
            }
        });

        binding.homeImgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });


        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.navigation.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void ActionBAr() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterHomeActivity.status("online", userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenterHomeActivity.status("offline", userId);
    }

    @Override
    public void addItem(AHBottomNavigationItem item) {
        binding.navigation.addItem(item);
    }

    @Override
    public void setAdapterDM(AdapterMenu adapterMenu) {
        binding.lvDanhMuc.setAdapter(adapterMenu);
    }


    private void help() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setTitle("Help");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.help);
        dialog.show();

        ImageView close = dialog.findViewById(R.id.helpBtnClose);
        ImageView search = dialog.findViewById(R.id.helpSearch);
        EditText etSearch = dialog.findViewById(R.id.helpTvSearch);
        TextView cau1 = dialog.findViewById(R.id.helpcau1);
        TextView cau2 = dialog.findViewById(R.id.helpcau2);
        TextView cau3 = dialog.findViewById(R.id.helpcau3);

        cau1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterHomeActivity.gotoUrl("https://10vancauhoivisao.com");
            }
        });
        cau2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterHomeActivity.gotoUrl("https://10vancauhoivisao.com");
            }
        });
        cau3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterHomeActivity.gotoUrl("https://10vancauhoivisao.com");
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterHomeActivity.gotoUrl("https://10vancauhoivisao.com");
            }
        });
    }

    private void changeEmail() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setTitle("Help");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.change_email);
        dialog.show();

        EditText email = dialog.findViewById(R.id.changeEmail);
        ImageView close = dialog.findViewById(R.id.emailBtnClose);
        Button save = dialog.findViewById(R.id.btnSave);

        email.setText(firebaseUser.getEmail());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail(email.getText().toString());
                dialog.cancel();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void updateEmail(String s) {
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setMessage("Uploading");
        pd.show();
        firebaseUser.updateEmail(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    Toast.makeText(getBaseContext(), "User email address updated.", Toast.LENGTH_LONG).show();
                    hienThi();
                } else {
                    pd.dismiss();
                    Toast.makeText(getBaseContext(), "User email address don't updated.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void changePass() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setTitle("Help");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_change_pass);
        dialog.show();

        EditText passOld = dialog.findViewById(R.id.passPassOld);
        ImageView close = dialog.findViewById(R.id.passBtnClose);
        Button Ok = dialog.findViewById(R.id.passBtnOk);
        EditText passNew = dialog.findViewById(R.id.passEtNewPass);
        EditText confirmPass = dialog.findViewById(R.id.passEtXacNhan);

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passNew.getText().toString().equals(confirmPass.getText().toString())) {
                    clickOK(passNew.getText().toString(), passOld.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

    private void clickOK(String newpass, String oldpass) {
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setMessage("Uploading");
        pd.show();
        String email=firebaseUser.getEmail();
        AuthCredential credential= EmailAuthProvider.getCredential(email, oldpass);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getBaseContext(), "Password updated", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        pd.cancel();
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