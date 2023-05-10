package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Sell extends AppCompatActivity {

    private Button cancelBtn, sellBtn;
    private EditText itemName, itemPrice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private Spinner itemCategory;

    String userID;

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("Sell", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Sell", "onStop");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        cancelBtn = findViewById(R.id.cancelBtn);
        sellBtn = findViewById(R.id.sellBtn);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);


        itemCategory = findViewById(R.id.itemCategory);


        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        sellBtn.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                Random random = new Random();
                int productID = random.nextInt(99999);

                userID = currentUser.getUid();

                String stringItemName = itemName.getText().toString();
                String stringItemPrice = itemPrice.getText().toString();

                fStore = FirebaseFirestore.getInstance();


                if (stringItemName.isEmpty() || stringItemPrice.isEmpty()) {
                    Toast.makeText(this, "Please Enter the product the description", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = fStore.collection("products").document(String.valueOf(productID));

                    Map<String, Object> product = new HashMap<>();
                    product.put("itemName", stringItemName);
                    product.put("itemPrice", stringItemPrice);
                    product.put("sellerID", userID);
                    documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();

                            Log.i("Sell", "UserID" + userID);
                            Toast.makeText(Sell.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
//                            DocumentReference documentReference = fStore.collection("user").document(userID);
//
//                            documentReference.addSnapshotListener(Sell.this, new EventListener<DocumentSnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                                    if (value != null) {
//                                        String getUserID = (value.getString("userID"));
//
//                                        Log.i("Sell", "UserID: " + getUserID);
//
//                                    } else {
//                                        Log.e("Sell", error.getMessage());
//                                    }
//
//                                }
//                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Sell.this, "Product Failed to Add", Toast.LENGTH_SHORT).show();
                            Log.e("Sell", e.getMessage());
                        }
                    });

                }

            }
        });
    }
}