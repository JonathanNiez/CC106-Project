package com.example.cc106project.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cc106project.Model.Products;
import com.example.cc106project.Adapter.ProductsAdapter;
import com.example.cc106project.R;
import com.example.cc106project.Sell;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderParts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderParts extends Fragment {

    private Button sellBtn;
    private RecyclerView recyclerView;
    private ProductsAdapter productsAdapter;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    FirebaseFirestore fStore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderParts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderParts.
     */
    public static OrderParts newInstance(String param1, String param2) {
        OrderParts fragment = new OrderParts();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_parts, container, false);

        sellBtn = view.findViewById(R.id.sellBtn);

        recyclerView = view.findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

//        productsArrayList.add(new Products("test", "12323", "waala"));
        productsAdapter = new ProductsAdapter(getContext(), productsArrayList);
        recyclerView.setAdapter(productsAdapter);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Products products = documentSnapshot.toObject(Products.class);
                    productsArrayList.add(products);
                }
                Log.i("OrderParts", "Retrieving and Displaying Products");
                productsAdapter = new ProductsAdapter(getContext(), productsArrayList);
                recyclerView.setAdapter(productsAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderParts", e.getMessage());
            }
        });

        sellBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Sell.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadProducts() {
        fStore.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Products products = documentSnapshot.toObject(Products.class);
                    productsArrayList.add(products);
                }
                productsAdapter = new ProductsAdapter(getContext(), productsArrayList);
                recyclerView.setAdapter(productsAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderParts", e.getMessage());
            }
        });


    }

}