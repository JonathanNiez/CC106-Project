package com.example.cc106project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ChatUserInfo extends AppCompatActivity {

    private TextView chatInfoFirstName, chatInfoLastName, chatInfoEmail, chatInfoAddress;
    private ImageView chatInfoProfilePic;
    private FirebaseFirestore fStore;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ChatUserInfo", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ChatUserInfo", "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_info);
        Log.i("ChatUserInfo", "onCreate");

        chatInfoFirstName = findViewById(R.id.chatInfoFirstName);
        chatInfoLastName = findViewById(R.id.chatInfoLastName);
        chatInfoAddress = findViewById(R.id.chatInfoAddress);
        chatInfoEmail = findViewById(R.id.chatInfoEmail);
        chatInfoProfilePic = findViewById(R.id.chatInfoProfilePic);

        loadUserChatInfo();


    }

    private void loadUserChatInfo() {
        Intent intent = getIntent();
        String getUserID = intent.getStringExtra("userID");

        fStore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = fStore.collection("users").document(getUserID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    String getProfilePicUrl = value.getString("profilePicUrl");

                    chatInfoFirstName.setText(value.getString("firstName"));
                    chatInfoLastName.setText(value.getString("lastName"));
                    chatInfoAddress.setText(value.getString("address"));
                    chatInfoEmail.setText(value.getString("email"));

                    if (getProfilePicUrl != null) {
                        Glide.with(ChatUserInfo.this).load(getProfilePicUrl).centerCrop().into(chatInfoProfilePic);
                    }
                }
            }
        });

    }
}