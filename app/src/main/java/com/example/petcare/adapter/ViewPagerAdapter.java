package com.example.petcare.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.petcare.view.fragment.ChatFragment;
import com.example.petcare.view.fragment.VeterinaryFragment;
import com.example.petcare.view.fragment.HomeFragment;
import com.example.petcare.view.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new VeterinaryFragment();
            case 2:
                return new ChatFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment();

        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
