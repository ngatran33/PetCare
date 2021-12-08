package com.example.petcare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.Comment;
import com.example.petcare.model.Like;
import com.example.petcare.model.Status;
import com.example.petcare.model.User;
import com.example.petcare.view.activity.EditPostActivity;
import com.example.petcare.view.activity.PostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterTus extends RecyclerView.Adapter<AdapterTus.Viewhoder> {
    List<Status> statusList;
    Context context;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser userFb = FirebaseAuth.getInstance().getCurrentUser();
    List<Like> likes = new ArrayList<>();

    public AdapterTus(List<Status> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_status, parent, false);

        AdapterTus.Viewhoder viewhoder = new AdapterTus.Viewhoder(view);
        return viewhoder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhoder holder, int position) {
        Status status = statusList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chiTiet(status.getId());
            }
        });


        holder.imglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(holder, status.getId());
            }
        });

        holder.imgliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liked(holder, status.getId());
            }
        });
        holder.itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopuMenu(v, status.getId(), status.getUserId());
            }
        });

        hienthi(holder, status);
        showLike(holder, status.getId());
        showCmt(holder, status.getId());
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class Viewhoder extends RecyclerView.ViewHolder {
        ImageView itemAvt;
        ImageView itemAnh;
        TextView itemloai;
        TextView itemTvUser;
        TextView itemTvMota;
        TextView itemTvLuotLike;
        TextView itemdate;
        TextView itemTvLuotCmt;
        ImageView imglike;
        ImageView imgliked;
        ImageView itemMenu;

        public Viewhoder(@NonNull View itemView) {
            super(itemView);
            itemAvt = itemView.findViewById(R.id.itemAvt);
            itemAnh = itemView.findViewById(R.id.itemAnh);
            itemloai = itemView.findViewById(R.id.itemloai);
            itemTvUser = itemView.findViewById(R.id.itemTvUser);
            itemTvMota = itemView.findViewById(R.id.itemTvMota);
            itemdate = itemView.findViewById(R.id.itemdate);
            itemTvLuotLike = itemView.findViewById(R.id.itemTvLuotLike);
            itemTvLuotCmt = itemView.findViewById(R.id.itemTvLuotCmt);
            imglike = itemView.findViewById(R.id.imglike);
            imgliked = itemView.findViewById(R.id.imgliked);
            itemMenu = itemView.findViewById(R.id.itemMenu);
        }
    }

    private void chiTiet(String id) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void hienthi(Viewhoder holder, Status status) {
        reference.child("users").child(status.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && context != null) {
                    Glide.with(context.getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(holder.itemAvt);
                    if (status.getImg().equals("default")) {
                        holder.itemAnh.setVisibility(View.GONE);
                    } else {
                        Glide.with(context.getApplicationContext()).load(status.getImg()).into(holder.itemAnh);
                    }

                    if (status.getLoai().equals("default")) {
                        holder.itemloai.setText("");
                    } else
                        holder.itemloai.setText(status.getLoai());

                    if (status.getMota().equals("default")) {
                        holder.itemTvMota.setText("");
                    } else
                        holder.itemTvMota.setText(status.getMota());

                    holder.itemdate.setText(status.getDate());
                    holder.itemTvUser.setText(user.getUsername());
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showLike(Viewhoder holder, String id) {
        reference.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dem = 0;
                boolean flag = true;
                likes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Like like = dataSnapshot.getValue(Like.class);
                    likes.add(like);
                    if (like.getIdPost().equals(id) && like.getUserId().equals(userFb.getUid())) {
                        holder.imgliked.setVisibility(View.VISIBLE);
                        holder.imglike.setVisibility(View.GONE);
                        flag = false;
                    }
                    if (like.getIdPost().equals(id)) {
                        dem++;
                    }
                }
                if (flag) {
                    holder.imgliked.setVisibility(View.GONE);
                    holder.imglike.setVisibility(View.VISIBLE);
                }
                if (dem > 0) {
                    holder.itemTvLuotLike.setText(dem + " like");
                } else {
                    holder.itemTvLuotLike.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "loi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void like(Viewhoder holder, String id) {
        boolean flag = true;
        if (likes.size() == 0) {
            String idLike = reference.push().getKey();//String userId, String idPost,String id
            Like likea = new Like(userFb.getUid(), id, idLike);
            reference.child("likes").child(idLike).setValue(likea).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        holder.imgliked.setVisibility(View.VISIBLE);
                        holder.imglike.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            for (Like like : likes) {
                if (like.getIdPost().equals(id) && like.getUserId().equals(userFb.getUid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                String idLike = reference.push().getKey();
                Like likea = new Like(userFb.getUid(), id, idLike);
                reference.child("likes").child(idLike).setValue(likea).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            holder.imgliked.setVisibility(View.VISIBLE);
                            holder.imglike.setVisibility(View.GONE);
                        }
                    }
                });
            }

        }
    }

    private void liked(Viewhoder holder, String id) {
        for (Like like : likes) {
            if (like.getIdPost().equals(id) && like.getUserId().equals(userFb.getUid())) {
                reference.child("likes").child(like.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            holder.imgliked.setVisibility(View.GONE);
                            holder.imglike.setVisibility(View.VISIBLE);
                            showLike(holder, id);
                        }
                    }
                });
                break;
            }
        }
    }

    private void showCmt(Viewhoder holder, String id) {
        reference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dem = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment cmt = dataSnapshot.getValue(Comment.class);
                    if (cmt.getIdPost().equals(id)) {
                        dem++;
                    }
                }
                if (dem > 0) {
                    holder.itemTvLuotCmt.setText(dem + " comment");
                } else
                    holder.itemTvLuotCmt.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPopuMenu(View v, String idPost, String idUser) {
        PopupMenu popupMenu=new PopupMenu(context,v);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mnuEdit:
                        if(userFb.getUid().equals(idUser)){
                            editTus(idPost);
                        }else{
                            Toast.makeText(context, "You don't edit Post", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.mnuDelete:
                        if(userFb.getUid().equals(idUser)){
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("Notification");
                            builder.setCancelable(false);
                            builder.setMessage("Are you sure you want to delete the post?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteTus(idPost);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();

                        }else{
                            Toast.makeText(context, "You don't delete", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteTus(String idPost) {
        deleteCmt(idPost);
        deleteLike(idPost);
        reference.child("Tus").child(idPost).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void deleteLike(String idPost) {
        reference.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Like like = dataSnapshot.getValue(Like.class);
                    if (like.getIdPost().equals(idPost)) {
                        reference.child("likes").child(like.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteCmt(String idPost) {
        reference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    if (comment.getIdPost().equals(idPost)) {
                        reference.child("comments").child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Delete", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editTus(String idPost) {
        Intent intent = new Intent(context, EditPostActivity.class);
        intent.putExtra("idPost", idPost);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
