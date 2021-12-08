package com.example.petcare.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.model.Comment;
import com.example.petcare.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCmt extends RecyclerView.Adapter<AdapterCmt.Viewhoder> {
    List<Comment> comments;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Context context;
    String idUserPost;
    FirebaseUser userFb = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterCmt(List<Comment> comments, Context context, String idUserPost) {
        this.comments = comments;
        this.context = context;
        this.idUserPost = idUserPost;
    }

    @NonNull
    @Override
    public Viewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_cmt, parent, false);

        AdapterCmt.Viewhoder viewhoder = new AdapterCmt.Viewhoder(view);
        return viewhoder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhoder holder, int position) {
        Comment comment = comments.get(position);
        reference.child("users").child(comment.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(context.getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(holder.imgAvt);
                holder.tvUser.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.tvCmt.setText(comment.getComment());

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCmt(comment.getId(), comment.getUserId(), comment.getComment(), comment.getIdPost());
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCmt(comment.getId(), comment.getUserId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class Viewhoder extends RecyclerView.ViewHolder {
        CircleImageView imgAvt;
        TextView tvUser;
        TextView tvCmt;
        TextView tvDelete;
        TextView tvEdit;

        public Viewhoder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.item_cmtImgAvt);
            tvCmt = itemView.findViewById(R.id.item_cmtTvCmt);
            tvUser = itemView.findViewById(R.id.item_cmtTvUsername);
            tvDelete = itemView.findViewById(R.id.item_cmtDelete);
            tvEdit = itemView.findViewById(R.id.item_cmtEdit);
        }
    }

    private void editCmt(String id, String userId, String cmt, String idPost) {
        if (userFb.getUid().equals(userId)) {
            Dialog dialog = new Dialog(context);
            dialog.setTitle("Edit Comment");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.edit_cmt);
            dialog.show();
            ImageView imgAvt = dialog.findViewById(R.id.ecImgAvt);
            TextView username = dialog.findViewById(R.id.ecUsername);
            EditText etCmt = dialog.findViewById(R.id.ecTvCmt);
            Button btnSave = dialog.findViewById(R.id.ecBtnSave);
            ImageView btnClose = dialog.findViewById(R.id.ecBtnClose);
            reference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    Glide.with(context.getApplicationContext()).load(user.getAvt()).error(R.drawable.users).into(imgAvt);
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            etCmt.setText(cmt);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = new Comment(userId, idPost, etCmt.getText().toString(), id);
                    reference.child("comments").child(id).setValue(comment);
                    dialog.cancel();
                }
            });

        } else {
            Toast.makeText(context, "You don't edit Cmt", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteCmt(String id, String idUser) {
        if (userFb.getUid().equals(idUser) || userFb.getUid().equals(idUserPost)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Are you sure you want to delete this comment?");
            builder.setMessage("");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete(id);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        } else {
            Toast.makeText(context, "You don't delete", Toast.LENGTH_LONG).show();
        }
    }

    private void delete(String id) {
        reference.child("comments").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Successful delete", Toast.LENGTH_LONG).show();
            }
        });
    }
}
