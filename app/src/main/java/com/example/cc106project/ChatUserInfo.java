package com.example.cc106project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cc106project.Adapter.MyProductsAdapter;
import com.example.cc106project.Fragments.MyProducts;
import com.example.cc106project.Model.MyProductsModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ChatUserInfo extends AppCompatActivity {

    private TextView chatInfoFirstName, chatInfoLastName, chatInfoEmail, chatInfoAddress;
    private ImageView chatInfoProfilePic, chatInfoCoverPic;
    private FirebaseFirestore fStore;
    private DocumentReference documentReference;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private String TAG = "ChatUserInfo";
    private Intent intent;
    private MyProductsAdapter myProductsAdapter;
    private ArrayList<MyProductsModel> myProductsModelArrayList;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_info);
        Log.i(TAG, "onCreate");

        chatInfoFirstName = findViewById(R.id.chatInfoFirstName);
        chatInfoLastName = findViewById(R.id.chatInfoLastName);
        chatInfoAddress = findViewById(R.id.chatInfoAddress);
        chatInfoEmail = findViewById(R.id.chatInfoEmail);
        chatInfoProfilePic = findViewById(R.id.chatInfoProfilePic);
        chatInfoCoverPic = findViewById(R.id.chatInfoCoverPhoto);

        recyclerView = findViewById(R.id.chatUserInfoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        toolbar = findViewById(R.id.toolBar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        fStore = FirebaseFirestore.getInstance();

        intent = getIntent();
        //retrieve the string from user
        String getUserID = intent.getStringExtra("userID");

        loadUserChatInfo(getUserID);
        loadUserChatProductsInfo(getUserID);


    }

    private void loadUserChatInfo(String xGetUserID) {

        documentReference = fStore.collection("users").document(xGetUserID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String getProfilePicUrl = value.getString("profilePicUrl");
                    String getCoverPicUrl = value.getString("getCoverPicUrl");

                    chatInfoFirstName.setText(value.getString("firstName"));
                    chatInfoLastName.setText(value.getString("lastName"));
                    chatInfoAddress.setText(value.getString("address"));
                    chatInfoEmail.setText(value.getString("email"));

                    if (getProfilePicUrl != null ) {
                        Glide.with(ChatUserInfo.this).load(getProfilePicUrl).centerCrop().into(chatInfoProfilePic);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.i(TAG, error.getMessage());

                }
            }
        });

    }

    private void loadUserChatProductsInfo(String xGetUserID) {

        myProductsModelArrayList  = new ArrayList<>();

        CollectionReference collectionReference = fStore.collection("products");
        Query query = collectionReference.whereEqualTo("sellerID", xGetUserID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);

                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    MyProductsModel myProductsModel = queryDocumentSnapshot.toObject(MyProductsModel.class);
                    myProductsModelArrayList.add(myProductsModel);
                }
                myProductsAdapter = new MyProductsAdapter(this, myProductsModelArrayList);
                recyclerView.setAdapter(myProductsAdapter);
                Log.i(TAG, "Retrieving and Displaying Products");

            } else {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Products Failed to Load");

            }
        });
    }
}