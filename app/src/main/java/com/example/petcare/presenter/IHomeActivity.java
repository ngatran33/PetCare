package com.example.petcare.presenter;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.petcare.adapter.AdapterMenu;

public interface IHomeActivity {
    void addItem(AHBottomNavigationItem item);
    void setAdapterDM(AdapterMenu adapterMenu);
}
