package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.StartupTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddress extends AppCompatActivity {

    private Button acceptBtn, skipBtn;
    private EditText streetAddress, province, city, postalCode;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        acceptBtn = findViewById(R.id.acceptBtn);
        skipBtn = findViewById(R.id.skipBtn);
        streetAddress = findViewById(R.id.streetAddress);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        postalCode = findViewById(R.id.postalCode);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        skipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AddAddress.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        acceptBtn.setOnClickListener(v -> {
            String stringStreetAddress = streetAddress.getText().toString();
            String stringProvince = province.getText().toString();
            String stringCity = city.getText().toString();
            String stringPostalCode = postalCode.getText().toString();

            if (stringStreetAddress.isEmpty() || stringProvince.isEmpty() || stringCity.isEmpty() || stringPostalCode.isEmpty()) {
                Toast.makeText(this, "Please enter your Address", Toast.LENGTH_SHORT).show();
            } else {
                String fullAddress = stringStreetAddress + " " + stringProvince + " " + stringCity + " " + stringPostalCode;
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    userID = currentUser.getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);

                    Map<String, Object> user = new HashMap<>();
                    user.put("address", fullAddress);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Intent intent = new Intent(AddAddress.this, Login.class);
                            startActivity(intent);
                            finish();

                            Log.i("AddAddress", "UserID" + userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            acceptBtn.setVisibility(View.VISIBLE);

                            Log.e("AddAddress", e.getMessage());
                        }
                    });

                }
            }

        });
    }
}