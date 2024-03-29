package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cc106project.Adapter.ChatAdapter;
import com.example.cc106project.Model.ChatModel;
import com.example.cc106project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatWithProduct extends AppCompatActivity {
    private TextView chatFirstName, chatLastName, chatProductName,
            chatProductPrice, chatProductStock, chatProductCategory;
    private ImageView chatProfilePic, chatProductImage;
    private ImageButton chatUserInfoBtn, sendMsgBtn;
    private EditText message;
    private Toolbar chatToolBar;
    private ProgressBar progressBar;
    private RelativeLayout messageInputWrapper;
    private Intent intent;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    private DocumentReference documentReference;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private ArrayList<ChatModel> chatModelArrayList;
    private ChatAdapter chatAdapter;
    private RecyclerView chatWithProductRecyclerView;
    private String TAG = "ChatWithProduct";
    private ValueEventListener valueEventListener;
    private String UserID;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isFinishing()){
            Glide.with(this).clear(chatProfilePic);
            Glide.with(this).clear(chatProductImage);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_product);

        chatFirstName = findViewById(R.id.chatFirstName);
        chatLastName = findViewById(R.id.chatLastName);
        chatProfilePic = findViewById(R.id.chatProfilePic);
        chatUserInfoBtn = findViewById(R.id.chatUserInfoBtn);
        message = findViewById(R.id.message);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        progressBar = findViewById(R.id.progressBar);

        messageInputWrapper = findViewById(R.id.messageInputWrapper);

        chatProductImage = findViewById(R.id.chatProductImage);
        chatProductName = findViewById(R.id.chatProductName);
        chatProductCategory = findViewById(R.id.chatProductCategory);
        chatProductPrice = findViewById(R.id.chatProductPrice);
        chatProductStock = findViewById(R.id.chatProductStock);

        progressBar.setVisibility(View.VISIBLE);

        chatWithProductRecyclerView = findViewById(R.id.chatWithProductRecyclerView);
        chatWithProductRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatWithProductRecyclerView.setLayoutManager(linearLayoutManager);

        chatToolBar = findViewById(R.id.chatToolBar);
        getSupportActionBar();
        chatToolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        intent = getIntent();
        //Getting the productID from ProductsAdapter
        String getChatProductID = intent.getStringExtra("productID");

        //Getting the userID from UserAdapter
        String getChatUserID = intent.getStringExtra("sellerID");

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        sendMsgBtn.setOnClickListener(v -> {
            String stringMessage = message.getText().toString();

            if (stringMessage.isEmpty()) {
                return;
            } else {
                sendMessage(currentUser.getUid(), getChatUserID, stringMessage);
                message.setText("");
            }
        });

        fStore = FirebaseFirestore.getInstance();

//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("chats").child(getChatUserID);


        chatUserInfoBtn.setOnClickListener(v -> {
            Intent sendToChatUserInfo = new Intent(ChatWithProduct.this, ChatUserInfo.class);
            sendToChatUserInfo.putExtra("userID", getChatProductID);
            startActivity(sendToChatUserInfo);
        });

        loadChatUser(getChatUserID);
        seenMessage(getChatUserID);
        loadSelectedProduct(getChatProductID);

    }

    private void loadChatUser(String xUserID) {

        if (currentUser.getUid().equals(xUserID)) {
            chatFirstName.setText("Your");
            chatLastName.setText("Product");
            messageInputWrapper.setVisibility(View.GONE);
            chatProfilePic.setVisibility(View.GONE);
            chatWithProductRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            chatUserInfoBtn.setVisibility(View.GONE);
        } else {
            documentReference = fStore.collection("users").document(xUserID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {

                        String getProfilePicUrl = value.getString("profilePicUrl");

                        chatFirstName.setText(value.getString("firstName"));
                        chatLastName.setText(value.getString("lastName"));

                        readMessage(currentUser.getUid(), xUserID, getProfilePicUrl);

                        if (getProfilePicUrl != null) {
                            Glide.with(getApplicationContext()).load(getProfilePicUrl).centerCrop().into(chatProfilePic);
                        } else {
                            chatProfilePic.setImageResource(R.drawable.user_icon100);
                        }
                    }
                }
            });
        }


    }

    private void loadSelectedProduct(String xProductID) {
        documentReference = fStore.collection("products").document(xProductID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    chatProductName.setText(value.getString("itemName"));
                    chatProductCategory.setText(value.getString("itemCategory"));
                    chatProductPrice.setText("₱" + String.valueOf(value.getDouble("itemPrice")));
                    chatProductStock.setText("Stock: " + String.valueOf(value.getLong("itemStock")));
                    Glide.with(ChatWithProduct.this).load(value.getString("itemImage"))
                            .centerCrop().into(chatProductImage);

                } else {
                    Log.e(TAG, error.getMessage());
                }
            }
        });
    }

    private void seenMessage(final String xUserID) {

        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "seenMessage");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);

                    assert chatModel != null;
                    if (chatModel.getReceiver().equals(currentUser.getUid())
                            && chatModel.getSender().equals(xUserID)
                    ) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendMessage(String sender, String receiver, String message) {

        Log.i(TAG, "Message Sent");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> chars = new HashMap<>();
        chars.put("sender", sender);
        chars.put("receiver", receiver);
        chars.put("message", message);
        chars.put("isSeen", false);
        databaseReference.child("chatWithProduct").push().setValue(chars);
    }

    private void readMessage(String senderID, String receiverID, String imageURL) {

        chatModelArrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("chatWithProduct");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);
                chatModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);

                    assert chatModel != null;
                    if (chatModel.getReceiver().equals(receiverID) && chatModel.getSender().equals(senderID)
                            || chatModel.getReceiver().equals(senderID) && chatModel.getSender().equals(receiverID)
                    ) {
                        chatModelArrayList.add(chatModel);

                    }
                }
                chatAdapter = new ChatAdapter(ChatWithProduct.this, chatModelArrayList, imageURL);
                chatWithProductRecyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}