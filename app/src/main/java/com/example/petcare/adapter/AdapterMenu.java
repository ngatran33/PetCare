package com.example.petcare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.petcare.R;

import java.util.List;

public class AdapterMenu extends BaseAdapter {
    List<String> list;

    public AdapterMenu(List<String> list) {
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.item_meu, parent, false);

        TextView tvMenu=view.findViewById(R.id.tvMenu);

        tvMenu.setText(list.get(position));
        return view;
    }
}
