package com.example.petcare.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.petcare.R;
import com.example.petcare.adapter.AdapterMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PresenterHomeActivity {
    IHomeActivity homeActivity;
    Context context;

    DatabaseReference reference;

    public PresenterHomeActivity(IHomeActivity homeActivity, Context context) {
        this.homeActivity = homeActivity;
        this.context = context;
    }

    public void init() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.home, R.color.purple_200);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.thuy, R.color.purple_200);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.chat, R.color.purple_200);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.user, R.color.teal_200);
        homeActivity.addItem(item1);
        homeActivity.addItem(item2);
        homeActivity.addItem(item3);
        homeActivity.addItem(item5);
    }
    public void ActionMenu(){
        List<String> list=new ArrayList<>();
        list.add("Change password");
        list.add("Help");
        list.add("Log Out");

        AdapterMenu adapterMenu=new AdapterMenu(list);
        homeActivity.setAdapterDM(adapterMenu);
    }
    public void status(String status, String userId){
        reference= FirebaseDatabase.getInstance().getReference("users").child(userId);

        HashMap<String, Object> map=new HashMap<>();
        map.put("status", status);

        reference.updateChildren(map);
    }

    public void gotoUrl(String s) {
        Uri uri=Uri.parse(s);
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
