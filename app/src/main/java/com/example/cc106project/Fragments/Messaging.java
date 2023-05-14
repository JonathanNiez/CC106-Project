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

import com.example.cc106project.Adapter.UserAdapter;
import com.example.cc106project.Model.UsersModel;
import com.example.cc106project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Messaging extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<UsersModel> usersModelList;
    private UserAdapter userAdapter;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    FirebaseFirestore fStore;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        userAdapter = new UserAdapter(getContext(), usersList);
//        recyclerView.setAdapter(userAdapter);
        usersModelList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                usersModelList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                    if (!usersModel.getUserID().equals(currentUser.getUid())){
                        usersModelList.add(usersModel);
                    }
                }
                userAdapter = new UserAdapter(getContext(), usersModelList);
                recyclerView.setAdapter(userAdapter);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Messaging", e.getMessage());
            }
        });
        return view;
    }

    private void loadUsers() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);
                    usersModelList.add(usersModel);


                }
                userAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Messaging", e.getMessage());
            }
        });

//        fStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//
//                    Log.e("Messaging", error.getMessage());
//                    return;
//                }
//
//
//                for (DocumentChange documentChange : value.getDocumentChanges()) {
//                    if (documentChange.getType() == DocumentChange.Type.ADDED ||
//                            documentChange.getType() == DocumentChange.Type.MODIFIED ||
//                            documentChange.getType() == DocumentChange.Type.REMOVED) {
//                        usersList.add(documentChange.getDocument().toObject(Users.class));
//                    }
//
//                    userAdapter = new UserAdapter(getContext(), usersList);
//                    recyclerView.setAdapter(userAdapter);
//                    userAdapter.notifyDataSetChanged();
//                }
//            }
//        });

    }
}