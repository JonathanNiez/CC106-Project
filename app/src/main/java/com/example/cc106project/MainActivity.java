package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.net.NetworkInterface;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView navFirstname, navLastname, navEmail;
    private ImageView navProfilePic;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    FirebaseFirestore fStore;
    String userID;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isConnectedToTheInternet(this)){
            internetConnectionDialog();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Email
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        //Firebase Google sign in
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);

        navFirstname = navHeader.findViewById(R.id.navFirstname);
        navLastname = navHeader.findViewById(R.id.navLastName);
        navEmail = navHeader.findViewById(R.id.navEmail);
        navProfilePic = navHeader.findViewById(R.id.navProfilePic);

        //Checks if either login method tokens are null
        if (user == null) {
            checkIfGoogleSignedIn();

        }
        if (googleSignInAccount == null) {
            checkIfEmailSignedIn();
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private boolean isConnectedToTheInternet(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiConnection != null && wifiConnection.isConnected() ||
                mobileDataConnection != null && mobileDataConnection.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void checkIfEmailSignedIn() {
        if (user == null) {
            FirebaseAuth.getInstance().signOut();
            Log.i("MainActivity", "Token Cleared by Email Sign in");

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            //displays Home as default landing page
            toolbar.setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new Home()).commit();

            // Fetches the data of the user's firstname and lastname
            userID = auth.getCurrentUser().getUid();

            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {
                        navFirstname.setText(value.getString("firstName"));
                        navLastname.setText(value.getString("lastName"));
                        navEmail.setText(user.getEmail());
                    } else {
                        FirebaseAuth.getInstance().signOut();

                    }

                }
            });
        }
    }

    private void checkIfGoogleSignedIn() {
        if (googleSignInAccount == null) {
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i("MainActivity", "Google Token Cleared by Google sign in");

                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("MainActivity", e.getMessage());
                }
            });
        } else {
            toolbar.setTitle("Home");
            String getEmail = googleSignInAccount.getEmail();
            String getFirstName = googleSignInAccount.getGivenName();
            String getLastName = googleSignInAccount.getFamilyName();

            Glide.with(this).load(googleSignInAccount.getPhotoUrl())
                    .centerCrop().into(navProfilePic);

            navFirstname.setText(getFirstName);
            navLastname.setText(getLastName);
            navEmail.setText(getEmail);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                toolbar.setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Home()).commit();
                break;
            case R.id.orderParts:
                toolbar.setTitle("Order Parts");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new OrderParts()).commit();
                break;
            case R.id.account:
                toolbar.setTitle("Account");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Account()).commit();
                break;
            case R.id.settings:
                toolbar.setTitle("Settings");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Settings()).commit();
                break;
            case R.id.messaging:
                toolbar.setTitle("Messaging");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Messaging()).commit();
                break;
            case R.id.logout:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Logout");
                alert.setMessage("Do you want to Logout?");
                alert.setCancelable(true);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Logout
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();

                            Log.i("MainActivity", "Logged Out");

                            Intent intent = new Intent(MainActivity.this, Login.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        }
                        if (googleSignInAccount != null) {
                            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("MainActivity", "Logged Out from Google");

                                    Intent intent = new Intent(MainActivity.this, Login.class);
                                    startActivity(intent);
                                    finish();

                                    Toast.makeText(MainActivity.this, "Logged Out from Google", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("MainActivity", e.getMessage());
                                }
                            });
                        }
                    }
                });
                alert.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                alert.show();

                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Body");
                intent.putExtra(Intent.EXTRA_TEXT, "Subject");
                startActivity(Intent.createChooser(intent, "Share with"));
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}