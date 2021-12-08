package com.example.petcare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.ThuY;
import com.example.petcare.view.activity.ChiTietThuYActivity;

import java.util.List;

public class AdapterThuY extends RecyclerView.Adapter<AdapterThuY.Viewhoder>{

    List<ThuY> thuYList;
    Context context;

    public AdapterThuY(List<ThuY> quangCaoList, Context context) {
        this.thuYList = quangCaoList;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.quangcao_recyc, parent, false);

        AdapterThuY.Viewhoder viewhoder = new AdapterThuY.Viewhoder(view);
        return viewhoder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhoder holder, int position) {
        ThuY thuY= thuYList.get(position);
        hienThi(thuY, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChiTietThuYActivity.class);
                intent.putExtra("id", thuY.getId());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return thuYList.size();
    }

    public class Viewhoder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        TextView tvDiaChi;
        TextView tvSdt;
        TextView tvWeb;
        TextView tvTg;
        public Viewhoder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.anhqq);
            tvName=itemView.findViewById(R.id.tvtenqq);
            tvDiaChi=itemView.findViewById(R.id.tvDCqq);
            tvSdt=itemView.findViewById(R.id.tvSdtqq);
            tvWeb=itemView.findViewById(R.id.tvwebqq);
            tvTg=itemView.findViewById(R.id.tvTgqq);
        }
    }

    private void hienThi(ThuY quangCao, Viewhoder holder) {
        holder.tvName.setText(quangCao.getName());
        holder.tvTg.setText(quangCao.getThoigian());
        holder.tvWeb.setText(quangCao.getWebsite());
        holder.tvSdt.setText(quangCao.getSdt());
        holder.tvDiaChi.setText(quangCao.getDiaChi());
        Glide.with(context.getApplicationContext()).load(quangCao.getAnh()).error(R.drawable.ic_baseline_image_not_supported_24).into(holder.img);
    }
}
