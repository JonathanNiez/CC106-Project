package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cc106project.Fragments.Account;
import com.example.cc106project.Fragments.Feedback;
import com.example.cc106project.Fragments.Home;
import com.example.cc106project.Fragments.MyProducts;
import com.example.cc106project.Fragments.Market;
import com.example.cc106project.Fragments.Orders;
import com.example.cc106project.Fragments.Settings;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.example.cc106project.Fragments.About;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView navFirstname, navLastname, navEmail;
    private ImageView navProfilePic;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;
    private DocumentReference documentReference;
    private String userID;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("MainActivity", "onStart");

        displayUserInfo();

        if (!isConnectedToTheInternet(this)) {
            internetConnectionDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume");
        setStatus(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause");
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
        Log.i("MainActivity", "onCreate");

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

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setStatus(boolean isOnline) {
        documentReference = fStore.collection("users").document(currentUser.getUid());

        HashMap<String, Object> status = new HashMap<>();
        status.put("isOnline", isOnline);
        documentReference.update(status);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                toolbar.setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Home()).commit();
                break;
            case R.id.market:
                toolbar.setTitle("Market");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Market()).commit();
                break;
            case R.id.myProducts:
                toolbar.setTitle("My Products");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new MyProducts()).commit();
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
                Intent toChatActivity = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(toChatActivity);
                break;
            case R.id.about:
                toolbar.setTitle("About");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new About()).commit();
                break;
            case R.id.feedback:
                toolbar.setTitle("Feedback");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new Feedback()).commit();
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
                        FirebaseAuth.getInstance().signOut();

                        Log.i("MainActivity", "Logged Out");

                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();

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

    public void displayUserInfo() {

        if (currentUser != null) {

            Log.i("MainActivity", "User is Logged-in");

            userID = currentUser.getUid();

            toolbar.setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new Home()).commit();

            Log.i("MainActivity", "User: " + currentUser);

            documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        String getEmail = value.getString("email");
                        String getFirstName = value.getString("firstName");
                        String getLastName = value.getString("lastName");
                        String getProfilePic = value.getString("profilePicUrl");

                        Log.i("MainActivity", "User Full Name: " + getFirstName + "" + getLastName);
                        Log.i("MainActivity", "UserID: " + userID);
                        Log.i("MainActivity", "User Email: " + getEmail);

                        navFirstname.setText(getFirstName);
                        navLastname.setText(getLastName);
                        navEmail.setText(getEmail);

                        //if profile pic is not google
                        if (getProfilePic != null) {
                            Glide.with(MainActivity.this).load(getProfilePic)
                                    .centerCrop().into(navProfilePic);

                            Log.i("MainActivity", "Displaying Profile Picture");

                        } else {
                            navProfilePic.setImageResource(R.drawable.user_icon100);
                        }

                    } else {
                        Log.e("MainActivity", error.getMessage());
                    }

                }
            });
        } else {
            Log.i("MainActivity", "User is not Logged-in");

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}