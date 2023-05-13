package com.example.cc106project;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProducts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProducts extends Fragment {

    private TextView firstName, lastName, productsSold;
    private ImageView profilePicture;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    FirebaseUser currentUser;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    String userID;
    private RecyclerView recyclerView;
    ArrayList<MyProductsModel> myProductsModelArrayList = new ArrayList<>();
    private MyProductsAdapter myProductsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyProducts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProducts.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProducts newInstance(String param1, String param2) {
        MyProducts fragment = new MyProducts();
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
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);


        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        productsSold = view.findViewById(R.id.productSold);
        profilePicture = view.findViewById(R.id.profilePicture);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewMyProducts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myProductsAdapter = new MyProductsAdapter(getContext(), myProductsModelArrayList);
        recyclerView.setAdapter(myProductsAdapter);


        displayUserInfo();
        loadMyProducts();

        return view;
    }

    private void loadMyProducts() {

        userID = currentUser.getUid();

        CollectionReference collectionReference = fStore.collection("products");

        Query query = collectionReference.whereEqualTo("sellerID", userID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    MyProductsModel myProductsModel = queryDocumentSnapshot.toObject(MyProductsModel.class);
                    myProductsModelArrayList.add(myProductsModel);
                }
                myProductsAdapter = new MyProductsAdapter(getContext(), myProductsModelArrayList);
                recyclerView.setAdapter(myProductsAdapter);
                Log.i("MyProducts", "Retrieving and Displaying Products");

            } else {
                Log.e("MyProducts", "Products Failed to Load");

            }
        });

    }

    public void displayUserInfo() {

        if (currentUser != null) {
            userID = currentUser.getUid();

            Log.i("MyProducts", "User: " + currentUser);

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
                            Glide.with(getActivity()).load(getProfilePic)
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