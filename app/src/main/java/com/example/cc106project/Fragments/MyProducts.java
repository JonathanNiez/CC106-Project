package com.example.cc106project.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.cc106project.Adapter.MyProductsAdapter;
import com.example.cc106project.Login;
import com.example.cc106project.Model.MyProductsModel;
import com.example.cc106project.R;
import com.example.cc106project.Sell;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyProducts extends Fragment {
    private TextView firstName, lastName, productsSold;
    private ImageView profilePicture;
    private ProgressBar progressBar;
    private Button sellBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private String userID;
    private String TAG = "MyProducts";
    private RecyclerView recyclerView;
    private ArrayList<MyProductsModel> myProductsModelArrayList;
    private MyProductsAdapter myProductsAdapter;
    private RequestManager requestManager;

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");

        if (requestManager != null){
            requestManager.clear(profilePicture);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);
        Log.i(TAG, "onCreateView");

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        productsSold = view.findViewById(R.id.productSold);
        profilePicture = view.findViewById(R.id.profilePicture);
        sellBtn = view.findViewById(R.id.sellBtn);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        requestManager = Glide.with(this);

        sellBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Sell.class);
            startActivity(intent);
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewMyProducts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        displayUserInfo();
        loadMyProducts();

        return view;
    }

    private void loadMyProducts() {

        myProductsModelArrayList = new ArrayList<>();

        userID = currentUser.getUid();

        CollectionReference collectionReference = fStore.collection("products");

        Query query = collectionReference.whereEqualTo("sellerID", userID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);

                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    MyProductsModel myProductsModel = queryDocumentSnapshot.toObject(MyProductsModel.class);
                    myProductsModelArrayList.add(myProductsModel);
                }
                myProductsAdapter = new MyProductsAdapter(getContext(), myProductsModelArrayList);
                recyclerView.setAdapter(myProductsAdapter);
                Log.i("MyProducts", "Retrieving and Displaying Products");

            } else {
                progressBar.setVisibility(View.GONE);
                Log.e("MyProducts", "Products Failed to Load");

            }
        });

    }

    public void displayUserInfo() {

        if (currentUser != null) {
            userID = currentUser.getUid();

            Log.i("MyProducts", "User: " + currentUser);
            if (getActivity() != null) {
                DocumentReference documentReference = fStore.collection("users").document(userID);
                documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            String getProfilePic = value.getString("profilePicUrl");

                            firstName.setText(value.getString("firstName"));
                            lastName.setText(value.getString("lastName"));
                            productsSold.setText("Products Sold: " + value.getLong("productsSold"));

                            if (getProfilePic != null) {
                                requestManager.load(getProfilePic)
                                        .centerCrop().into(profilePicture);
                            } else {
                                profilePicture.setImageResource(R.drawable.user_icon100);
                            }

                        } else {
                            Log.e("MyProducts", error.getMessage());
                        }

                    }
                });

            } else {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        }


    }

}