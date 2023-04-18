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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText email, password, confirmPassword, address, firstName, lastName;
    private Button registerBtn;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        registerBtn = findViewById(R.id.registerBtn);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            startActivity(intent);
            finish();
        });

        registerBtn.setOnClickListener(v -> {

            String stringFirstName = firstName.getText().toString();
            String stringLastName = lastName.getText().toString();
            String stringEmail = email.getText().toString().trim();
            String stringPassword = password.getText().toString().trim();
            String stringConPassword = confirmPassword.getText().toString();
            String stringAddress = address.getText().toString();

            if (stringEmail.isEmpty() || stringPassword.isEmpty() ||
                    stringConPassword.isEmpty() || stringAddress.isEmpty()
                    || stringFirstName.isEmpty() || stringLastName.isEmpty()) {
//                Toast.makeText(this, "Please input the fields", Toast.LENGTH_LONG).show();
                email.setError("Enter you Email");
                return;
            } else if (stringPassword.equals(stringConPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.GONE);

                try {
                    mAuth.createUserWithEmailAndPassword(stringEmail, stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);

                                Toast.makeText(Register.this, "Register Success", Toast.LENGTH_SHORT).show();

                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", stringFirstName);
                                user.put("lastName", stringLastName);
                                user.put("address", stringAddress);
                                user.put("userID", userID);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        finish();

                                        Log.i("Register", "UserID" + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        registerBtn.setVisibility(View.VISIBLE);

                                        Log.e("Register", e.getMessage());
                                    }
                                });


                            } else {
                                progressBar.setVisibility(View.GONE);
                                registerBtn.setVisibility(View.VISIBLE);

                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(Register.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show();
            }
        });
    }
}