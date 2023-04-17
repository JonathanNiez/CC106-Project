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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private EditText email, password, confirmPassword, address;
    private Button registerBtn;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cc106-project-efaaa-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
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

            String stringEmail = email.getText().toString();
            String stringPassword = password.getText().toString();
            String stringConPassword = confirmPassword.getText().toString();
            String stringAddress = address.getText().toString();

            if (stringEmail.isEmpty() || stringPassword.isEmpty() ||
                    stringConPassword.isEmpty() || stringAddress.isEmpty()) {
//                Toast.makeText(this, "Please input the fields", Toast.LENGTH_LONG).show();
                email.setError("Enter you Email");
                return;
            } else if (stringPassword.equals(stringConPassword)) {
                try {

                  mAuth.createUserWithEmailAndPassword(stringEmail, stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()){
                              Toast.makeText(Register.this, "Login Succeed", Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(Register.this, Login.class);
                              startActivity(intent);
                              finish();
                          }
                          else{
                              Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                          }
                      }
                  });

                } catch (Exception e) {
                    Toast.makeText(Register.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show();
            }
        });
    }
}