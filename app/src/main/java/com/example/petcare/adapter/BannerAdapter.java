package com.example.petcare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.ThuY;
import com.example.petcare.view.activity.ChiTietThuYActivity;

import java.util.List;

public class BannerAdapter extends PagerAdapter {
    Context context;
    List<ThuY> thuYList;

    public BannerAdapter(Context context, List<ThuY> quangCaoList) {
        this.context = context;
        this.thuYList = quangCaoList;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.dong_quang_cao,null);

        //ánh xạ
        ImageView anh=view.findViewById(R.id.imgQc);
        TextView dc=view.findViewById(R.id.tvDiaChi);
        TextView sdt=view.findViewById(R.id.tvSdt);
        TextView name=view.findViewById(R.id.tvTitle);
        //truyền du liệu

        dc.setText(thuYList.get(position).getDiaChi());
        name.setText(thuYList.get(position).getName());
        sdt.setText(thuYList.get(position).getSdt());
        Glide.with(context.getApplicationContext()).load(thuYList.get(position).getAnh()).into(anh);
        container.addView(view);
        //        Picasso.with(context).load().into(imganh);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChiTietThuYActivity.class);
                intent.putExtra("id", thuYList.get(position).getId());
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
