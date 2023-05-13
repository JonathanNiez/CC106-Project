package com.example.cc106project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Messaging extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Users> usersList = new ArrayList<>();
    private UserAdapter userAdapter;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    FirebaseFirestore fStore;
    String userID;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Messaging() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Messaging.
     */
    // TODO: Rename and change types and number of parameters
    public static Messaging newInstance(String param1, String param2) {
        Messaging fragment = new Messaging();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(getContext(), usersList);
        recyclerView.setAdapter(userAdapter);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                userID = currentUser.getUid();
//
//                if (){
//
//                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Users users = documentSnapshot.toObject(Users.class);
                    usersList.add(users);
                }
                userAdapter = new UserAdapter(getContext(), usersList);
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
                    Users users = documentSnapshot.toObject(Users.class);
                    usersList.add(users);


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