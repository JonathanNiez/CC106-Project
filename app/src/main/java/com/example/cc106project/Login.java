package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private ImageButton phoneNumberBtn, googleBtn;

    private TextView textViewRegister, forgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLE_AUTH";


    @Override
    protected void onStart() {
        super.onStart();

        Log.i("Login", "onStart");

        if (!isConnectedToTheInternet(this)) {
            internetConnectionDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Login", "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Login", "onDestroy");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("Login", "onCreate");

        //Firebase Email
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        //Google
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        callbackManager = CallbackManager.Factory.create();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        textViewRegister = findViewById(R.id.textViewRegister);
        forgotPassword = findViewById(R.id.forgotPassword);
        googleBtn = findViewById(R.id.googleBtn);
        phoneNumberBtn = findViewById(R.id.phoneNumberBtn);
        progressBar = findViewById(R.id.progressBar);

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        googleBtn.setOnClickListener(v -> {

            if (!isConnectedToTheInternet(this)) {
                internetConnectionDialog();
            } else {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);

            }
        });

        phoneNumberBtn.setOnClickListener(v -> {

        });

        forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);

            EditText emailForgotPassword = dialogView.findViewById(R.id.emailForgotPassword);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogView.findViewById(R.id.resetPasswordBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringForgotPasswordEmail = emailForgotPassword.getText().toString();

                    if (stringForgotPasswordEmail.isEmpty()) {
                        Toast.makeText(Login.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.sendPasswordResetEmail(stringForgotPasswordEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Password Reset Success", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(Login.this, "Password Reset Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Reset Password", e.getMessage());
                            }
                        });

                    }
                }
            });
            dialogView.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();

                }
            });

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            }
            alertDialog.show();
        });

        loginBtn.setOnClickListener(v -> {
            if (!isConnectedToTheInternet(this)) {
                internetConnectionDialog();
            } else {
                String stringEmail = email.getText().toString();
                String stringPassword = password.getText().toString();

                if (stringEmail.isEmpty()) {
                    email.setError("Enter your Email");
                    return;
                }
                if (stringPassword.isEmpty()) {
                    password.setError("Enter your Password");
                    return;
                } else {

                    loginBtn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        mAuth.signInWithEmailAndPassword(stringEmail, stringPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                Log.i("Login", "Logging In");

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
                    } catch (Exception e) {
                        loginBtn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(Login.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });

    }

    private void isUserCurrentlyLoggedIn() {
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private boolean isConnectedToTheInternet(Login login) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                login.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiConnection != null && wifiConnection.isConnected() ||
                mobileDataConnection != null && mobileDataConnection.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void internetConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.check_internet_dialog, null);

        Button retryBtn = dialogView.findViewById(R.id.retryBtn);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        alertDialog.show();
    }


    //google login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("Login " + TAG, e.getMessage());
            }
        }

    }
    private void fireBaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fStore = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Login" + " " + TAG, "signInWithCredential:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    String email = account.getEmail();
                                    String name = account.getDisplayName();
                                    String profilePic = String.valueOf(account.getPhotoUrl());

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("firstName", name);
                                    user.put("profilePicUrl", profilePic);
                                    user.put("productsSold", 0);
                                    user.put("userID", userId);
                                    user.put("coverPicUrl", null);
                                    user.put("isOnline", true);
                                    fStore.collection("users").document(userId).set(user);

                                    Log.d("Login" + " " + TAG, "signInWithCredential:success");
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            } else {
                                Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.d(TAG, "signInWithCredential:failed");

//            Intent intent = new Intent(Login.this, MainActivity.class);
//            startActivity(intent);
//            finish();

        }
    }

}