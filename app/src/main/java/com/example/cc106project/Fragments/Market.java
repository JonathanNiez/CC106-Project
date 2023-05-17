package com.example.cc106project.Fragments;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cc106project.Adapter.UserAdapter;
import com.example.cc106project.Model.Products;
import com.example.cc106project.Adapter.ProductsAdapter;
import com.example.cc106project.Model.UsersModel;
import com.example.cc106project.R;
import com.example.cc106project.Sell;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Market extends Fragment {
    private TextView noResult;
    private EditText searchProduct;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProductsAdapter productsAdapter;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;
    private String TAG = "Market";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        noResult = view.findViewById(R.id.noResult);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        searchProduct = view.findViewById(R.id.searchProduct);
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        loadProducts();

        return view;
    }

    private void searchProducts(String s) {
        productsArrayList = new ArrayList<>();
        Query query = fStore.collection("products").orderBy("itemName")
                .startAt(s).endAt(s + "\uf0ff");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                productsArrayList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Products products = documentSnapshot.toObject(Products.class);
                        productsArrayList.add(products);
                    }
                    productsAdapter = new ProductsAdapter(getContext(), productsArrayList);
                    recyclerView.setAdapter(productsAdapter);
                }

            }
        });
    }


    private void loadProducts() {
        fStore.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressBar.setVisibility(View.GONE);

                if (searchProduct.getText().toString().isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Products products = documentSnapshot.toObject(Products.class);
                        if (!products.isSold()){
                            productsArrayList.add(products);
                        }
                    }
                    Log.i(TAG, "Retrieving and Displaying Products");
                    productsAdapter = new ProductsAdapter(getContext(), productsArrayList);
                    recyclerView.setAdapter(productsAdapter);
                }else {
                    noResult.setVisibility(View.VISIBLE);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);

                Log.e(TAG, e.getMessage());
            }
        });
    }

}