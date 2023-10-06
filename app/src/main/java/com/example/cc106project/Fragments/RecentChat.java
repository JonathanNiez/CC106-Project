package com.example.cc106project.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cc106project.Adapter.ChatAdapter;
import com.example.cc106project.Adapter.UserAdapter;
import com.example.cc106project.Model.ChatModel;
import com.example.cc106project.Model.UsersModel;
import com.example.cc106project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecentChat extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<String> stringUsersList;
    private ArrayList<UsersModel> usersModelArrayList;
    private ArrayList<ChatModel> chatModelArrayList;
    private UserAdapter userAdapter;
    private ChatAdapter chatAdapter;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    private String TAG = "Recent Chat";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_chat, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerViewRecentChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        stringUsersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringUsersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);

                    assert chatModel != null;
                    if (chatModel.getSender().equals(currentUser.getUid())) {
                        stringUsersList.add(chatModel.getReceiver());

                        Log.i(TAG, "User List: " + stringUsersList);

                    }
                    if (chatModel.getReceiver().equals(currentUser.getUid())) {
                        stringUsersList.add(chatModel.getSender());

                        Log.i(TAG, "User List: " + stringUsersList);

                    }
                }

                Log.i(TAG, "Reading Message");

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        return view;
    }

    private void readChats() {
        usersModelArrayList = new ArrayList<>();
        fStore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            usersModelArrayList.clear();
            progressBar.setVisibility(View.GONE);

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                //Displays a user that is recently chatted
                for (String ID : stringUsersList) {
                    if (currentUser.getUid().equals(ID)) {
                        if (usersModelArrayList.size() != 0) {
                            for (UsersModel usersModel1 : usersModelArrayList) {
                                if (!currentUser.getUid().equals(usersModel1.getUserID())) {
                                    usersModelArrayList.add(usersModel1);
                                }
                            }
                        } else {
                            usersModelArrayList.add(usersModel);
                        }
                    }
                }
                Log.i(TAG, "Displaying Recent Chat");

            }
            userAdapter = new UserAdapter(getContext(), usersModelArrayList, false);
            recyclerView.setAdapter(userAdapter);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }
}