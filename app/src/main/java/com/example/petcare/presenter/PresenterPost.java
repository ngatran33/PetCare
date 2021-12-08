package com.example.petcare.presenter;

import android.app.Dialog;
import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.example.petcare.R;
import com.example.petcare.adapter.AdapterCmt;
import com.example.petcare.model.Comment;
import com.example.petcare.model.Like;
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

public class PresenterPost {
    IPost iPost;
    Context context;
    String idPost;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
    List<Like> likes=new ArrayList<>();
    FirebaseUser userFb= FirebaseAuth.getInstance().getCurrentUser();

    public PresenterPost(IPost iPost, Context context,String idPost) {
        this.iPost = iPost;
        this.context = context;
        this.idPost = idPost;
    }

    public  void showLike(){
        reference.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag=true;
                int dem=0;
                likes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Like like = dataSnapshot.getValue(Like.class);
                    likes.add(like);
                    if(like.getIdPost().equals(idPost)&& like.getUserId().equals(userFb.getUid())){
                        iPost.liked();
                        flag=false;
                    }
                    if(like.getIdPost().equals(idPost)){
                        dem++;
                    }
                }
                if(flag){
                    iPost.unLiked();
                }
                if(dem>0){
                    iPost.luotLike(dem + " Like");
                }else
                    iPost.luotLike("");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iPost.toast("Don't connect");
            }
        });

    }
    public void unLiked(){
        for(Like like:likes){
            if(like.getIdPost().equals(idPost)&& like.getUserId().equals(userFb.getUid())){
                reference.child("likes").child(like.getId()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            iPost.unLiked();
                            showLike();
                        }
                    }
                });
                break;
            }
        }
    }
    public void like(){
            String id= reference.push().getKey();
            Like likea=new Like(userFb.getUid(),idPost,id);
            reference.child("likes").child(id).setValue(likea).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        iPost.liked();
                    }
                }
            });
    }

    public  void showCmt(){
        List<Comment> comments=new ArrayList<>();
        reference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment cmt=dataSnapshot.getValue(Comment.class);
                    if(cmt.getIdPost().equals(idPost)){
                        comments.add(cmt);
                    }
                }
                iPost.adapterCmt(comments);
                if(comments.size()>0){
                    iPost.luotCmt(comments.size()+" Comment");
                }
                else
                    iPost.luotCmt("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public  void sendCmt(String s){
        if(s.length()>0){
            String id=reference.child("comments").push().getKey();
            Comment comment=new Comment(userFb.getUid(),idPost,s,id);
            reference.child("comments").child(id).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        iPost.sendCmt();
                    }
                }
            });
        }
    }


    public void editTus(String idPost) {

    }

    public void deleteTus(String idPost) {
        deleteCmt(idPost);
        deleteLike(idPost);
        reference.child("Tus").child(idPost).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                iPost.toast("Success");
                iPost.deleteTus();
            }
        });

    }

    public void deleteLike(String idPost) {
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

    public void deleteCmt(String idPost) {
        reference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment comment=dataSnapshot.getValue(Comment.class);
                    if(comment.getIdPost().equals(idPost)){
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
}
