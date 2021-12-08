package com.example.petcare.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.model.User;
import com.example.petcare.presenter.IChat;
import com.example.petcare.presenter.PresenterChat;
import com.example.petcare.adapter.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatFragment extends Fragment implements IChat {

    RecyclerView recyclerView;
    RecyclerView recyclerViewEmail;
    EditText etSearch;
    FloatingActionButton btnEmail;

    PresenterChat presenterChat;
    UserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        presenterChat = new PresenterChat(this, getContext());

        btnEmail = view.findViewById(R.id.btnemail);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));



        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setTitle("User");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.user_list);
                dialog.show();

                recyclerViewEmail = dialog.findViewById(R.id.recyclerViewEmail);
                recyclerViewEmail.setHasFixedSize(true);
                recyclerViewEmail.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                ImageButton close = dialog.findViewById(R.id.btnclose);
                etSearch = dialog.findViewById(R.id.etSearch);

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        presenterChat.searchUser(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                presenterChat.readUsers(etSearch.getText().toString());

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

            }
        });
        presenterChat.readUsersId();
        return view;
    }



    @Override
    public void setAdapterChat(List<User> userList) {
        UserAdapter userAdapter = new UserAdapter(getActivity(), userList, true);
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    public void setAdapterReadUser(List<User> userList) {
        UserAdapter userAdapter = new UserAdapter(getActivity(), userList, false);
        recyclerViewEmail.setAdapter(userAdapter);
    }

}