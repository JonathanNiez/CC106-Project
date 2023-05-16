package com.example.cc106project.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.cc106project.Adapter.UserAdapter;
import com.example.cc106project.Model.UsersModel;
import com.example.cc106project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatUsers extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchChatUsers;
    private ProgressBar progressBar;
    private ArrayList<UsersModel> usersModelArrayList;
    private UserAdapter userAdapter;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;
    private String TAG = "ChatUsers";

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_users, container, false);

        Log.i(TAG, "onCreateView");

        searchChatUsers = view.findViewById(R.id.searchChatUsers);
        searchChatUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        usersModelArrayList = new ArrayList<>();
        loadUsers();

        return view;
    }

    private void searchUsers(String s) {
        Query query = fStore.collection("users").orderBy("firstName")
                .startAt(s).endAt(s + "\uf0ff");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                usersModelArrayList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                        if (!usersModel.getUserID().equals(currentUser.getUid())) {
                            usersModelArrayList.add(usersModel);
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), usersModelArrayList, false);
                    recyclerView.setAdapter(userAdapter);
                }

            }
        });
    }

    private void loadUsers() {
        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressBar.setVisibility(View.GONE);
                if (searchChatUsers.getText().toString().isEmpty()) {
                    usersModelArrayList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                        if (!usersModel.getUserID().equals(currentUser.getUid())) {
                            usersModelArrayList.add(usersModel);
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), usersModelArrayList, true);
                    recyclerView.setAdapter(userAdapter);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

}

