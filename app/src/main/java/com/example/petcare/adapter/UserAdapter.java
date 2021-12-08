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
import com.example.petcare.model.User;
import com.example.petcare.view.activity.MessageActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewhoder> {
    private Context context;
    private List<User> userList;
    private  boolean isChat;


    public UserAdapter(Context context, List<User> userList, boolean isChat) {
        this.context = context;
        this.userList = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public Viewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent, false);
        return new Viewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhoder holder, int position) {
        User user=userList.get(position);
        holder.username.setText(user.getUsername());
        if(user.getAvt().equals("default")){
            holder.avt.setImageResource(R.drawable.users);
        }else
            Glide.with(context).load(user.getAvt()).into(holder.avt);

        if(isChat){
            if(user.getStatus().equals("online")){
                holder.img_off.setVisibility(View.GONE);
                holder.img_ol.setVisibility(View.VISIBLE);
            }else {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_ol.setVisibility(View.GONE);
            }
        }
        else {
            holder.img_off.setVisibility(View.GONE);
            holder.img_ol.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class Viewhoder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView avt;
        ImageView img_ol;
        ImageView img_off;
        public Viewhoder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.userTvUser);
            avt=itemView.findViewById(R.id.userPrfile_img);
            img_off=itemView.findViewById(R.id.user_img_off);
            img_ol=itemView.findViewById(R.id.user_img_ol);
        }
    }
}
