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

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBar);

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        loginBtn.setOnClickListener(v -> {

            String stringEmail = email.getText().toString();
            String stringPassword = password.getText().toString();

            if (stringEmail.isEmpty() || stringPassword.isEmpty()){
                Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                return;
            }
            else{

                loginBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                try {
                    mAuth.signInWithEmailAndPassword(stringEmail, stringPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                                Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            loginBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            Log.e("Login", e.getMessage());

                        }
                    });
                }catch (Exception e){
                    Toast.makeText(Login.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}