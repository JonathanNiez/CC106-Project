package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView navFirstname, navLastname, navEmail;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String userID;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);

        if (userID == null) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            //displays Home as default landing page
            toolbar.setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new Home()).commit();

            // Fetches the data of the user's firstname and lastname
            navFirstname = navHeader.findViewById(R.id.navFirstname);
            navLastname = navHeader.findViewById(R.id.navLastName);
            navEmail = navHeader.findViewById(R.id.navEmail);

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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
}