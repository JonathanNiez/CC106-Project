package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Sell extends AppCompatActivity {

    private Button cancelBtn, sellBtn, uploadImageBtn;
    private ImageView itemImage;
    private EditText itemName, itemPrice, itemStock;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private Spinner itemCategory;
    String userID;
    int REQUEST_CODE = 69;
    Uri imageUri;


    @Override
    protected void onStart() {
        super.onStart();

        Log.i("Sell", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Sell", "onStop");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && data != null && data.getData() != null) {
            imageUri = data.getData();

            itemImage.setImageURI(imageUri);

//            StorageReference fileRef = imagesRef.child("image.jpg");
//            UploadTask uploadTask = fileRef.putFile(imageUri);
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("Sell", e.getMessage());
//                }
//            });


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        cancelBtn = findViewById(R.id.cancelBtn);
        sellBtn = findViewById(R.id.sellBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemStock = findViewById(R.id.itemStock);

        itemCategory = findViewById(R.id.itemCategory);


        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        uploadImageBtn.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, REQUEST_CODE);

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE);
        });

        sellBtn.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                Random random = new Random();
                int productID = random.nextInt(99999);

                userID = currentUser.getUid();

                String stringItemName = itemName.getText().toString();
                double doubleItemPrice = Double.parseDouble(itemPrice.getText().toString());
                int intItemStock = Integer.parseInt(itemStock.getText().toString());

                fStore = FirebaseFirestore.getInstance();

                if (stringItemName.isEmpty() || itemPrice.getText().toString().isEmpty()
                        || itemStock.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please Enter the product the description", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = fStore.collection("products").document(String.valueOf(productID));

//                    imagesRef.child("image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            // Save the download URL in Firestore
//                            Map<String, Object> productImage = new HashMap<>();
//                            productImage.put("image_url", uri.toString());
//                            fStore.collection("products").document("image1").set(productImage);
//                        }
//                    });

                    Map<String, Object> product = new HashMap<>();
                    product.put("itemName", stringItemName);
                    product.put("itemPrice", doubleItemPrice);
                    product.put("sellerID", userID);
                    product.put("itemStock", intItemStock);

                    uploadImageToFirebaseStorage(imageUri, productID);

//                    uploadImage(productID);

                    documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();

                            Log.i("Sell", "UserID" + userID);
                            Toast.makeText(Sell.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
//                            DocumentReference documentReference = fStore.collection("user").document(userID);
//
//                            documentReference.addSnapshotListener(Sell.this, new EventListener<DocumentSnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                                    if (value != null) {
//                                        String getUserID = (value.getString("userID"));
//
//                                        Log.i("Sell", "UserID: " + getUserID);
//
//                                    } else {
//                                        Log.e("Sell", error.getMessage());
//                                    }
//
//                                }
//                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Sell.this, "Product Failed to Add", Toast.LENGTH_SHORT).show();
                            Log.e("Sell", e.getMessage());
                        }
                    });

                }

            }
        });


    }

    private void uploadImageToFirebaseStorage(Uri imageUri, int xProductID) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference fileRef = imagesRef.child(imageUri.getLastPathSegment());
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageFormat = ".png";

                        // Save the download URL in Firestore
                        Map<String, Object> data = new HashMap<>();
                        data.put("itemImage", uri.toString());
                        fStore.collection("products").document(String.valueOf(xProductID)).update(data);

//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
//                                new OrderParts()).commit();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Sell.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                        Log.e("Sell", "Image Failed to Upload");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sell.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                Log.e("Sell", e.getMessage());
            }
        });
    }

    private void uploadImage(int xProductID) {

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Uploading File....");
//        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.JAPAN);
        Date now = new Date();
        String fileName = formatter.format(now);
//        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        DocumentReference documentReference = fStore.collection("products").document(String.valueOf(xProductID));

        Map<String, Object> product = new HashMap<>();
        product.put("itemImage", fileName);
        documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();

                Log.i("Sell", "UserID" + userID);
                Toast.makeText(Sell.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Sell.this, "Product Failed to Add", Toast.LENGTH_SHORT).show();
                Log.e("Sell", e.getMessage());
            }
        });

    }
}