package com.example.petcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Viewhoder> {
    private Context context;
    private List<Chats> chatsList;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;
    private String imgAvt;

    public MessageAdapter(Context context, List<Chats> chatsList, String imgAvt) {
        this.context = context;
        this.chatsList = chatsList;
        this.imgAvt = imgAvt;
    }

    @NonNull
    @Override
    public MessageAdapter.Viewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        }
        return new Viewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Viewhoder holder, int position) {
        Chats chats=chatsList.get(position);

        holder.TvMess.setText(chats.getMessage());

        if(imgAvt.equals("default")){
            holder.avt.setImageResource(R.drawable.users);
        }else{
            Glide.with(context).load(imgAvt).into(holder.avt);
        }

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class Viewhoder extends RecyclerView.ViewHolder {
        TextView TvMess;
        ImageView avt;
        public Viewhoder(@NonNull View itemView) {
            super(itemView);
            TvMess=itemView.findViewById(R.id.TvMess);
            avt=itemView.findViewById(R.id.imgAvt);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
