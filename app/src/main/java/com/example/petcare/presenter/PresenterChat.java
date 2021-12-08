package com.example.petcare.presenter;

import android.content.Context;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.model.Chats;
import com.example.petcare.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PresenterChat {
    IChat iChat;
    Context context;
    List<String> userId;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public PresenterChat(IChat iChat, Context context) {
        this.iChat = iChat;
        this.context = context;
    }
    private void readUserChat() {
        final boolean[] flag = {true};
        List<User> userList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    for(String s: userId){
                        if(user.getId().equals(s)){
                            if(userList.size()!=0){
                                for(User user1: userList){
                                    flag[0]=true;
                                    if(user.getId().equals(user1.getId())){
                                        flag[0] =false;
                                        break;
                                    }
                                }
                                if(flag[0]){
                                    userList.add(user);
                                }
                            }else{
                                userList.add(user);
                            }
                        }
                    }
                }
                iChat.setAdapterChat(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public   void readUsersId(){
        userId=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userId.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chats=dataSnapshot.getValue(Chats.class);
                    if(chats.getSender().equals(firebaseUser.getUid())){
                        userId.add(chats.getReceiver());
                    }
                    if(chats.getReceiver().equals(firebaseUser.getUid())){
                        userId.add(chats.getSender());
                    }
                }
                readUserChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void readUsers(String s) {
        List<User> userList=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (s.equals("")) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            userList.add(user);
                        }
                    }
                    iChat.setAdapterReadUser(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void searchUser(String s) {
        List<User> userList=new ArrayList<>();
        final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if(!user.getId().equals(fuser.getUid())){
                        userList.add(user);
                    }
                }
                iChat.setAdapterReadUser(userList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
