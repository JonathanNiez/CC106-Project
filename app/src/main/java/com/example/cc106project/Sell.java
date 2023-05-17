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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cc106project.Model.ItemCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Sell extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button cancelBtn, sellBtn, uploadImageBtn;
    private Toolbar sellToolbar;
    private ImageButton itemImage;
    private EditText itemName, itemPrice, itemStock;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private DocumentReference documentReference;
    private Spinner itemCategorySpinner;
    private String userID;
    private String TAG = "Sell";
    private int REQUEST_CODE = 69;
    private Uri imageUri;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");

        if (requestCode == REQUEST_CODE && data != null && data.getData() != null) {
            imageUri = data.getData();

            itemImage.setImageURI(imageUri);
            Log.i(TAG, "Image Uploaded: " + imageUri);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        Log.i(TAG, "onCreate");

        cancelBtn = findViewById(R.id.cancelBtn);
        sellBtn = findViewById(R.id.sellBtn);
        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemStock = findViewById(R.id.itemStock);

        sellToolbar = findViewById(R.id.sellToolbar);
        sellToolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        itemCategorySpinner = findViewById(R.id.itemCategorySpinner);
        ArrayAdapter<CharSequence> arrayAdapter =
                ArrayAdapter.createFromResource(this, R.array.item_category,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        itemCategorySpinner.setAdapter(arrayAdapter);
        itemCategorySpinner.setOnItemSelectedListener(this);

        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        itemImage.setOnClickListener(v -> {
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
                    documentReference = fStore.collection("products").document(String.valueOf(productID));

                    Map<String, Object> product = new HashMap<>();
                    product.put("productID", productID);
                    product.put("itemName", stringItemName);
                    product.put("itemPrice", doubleItemPrice);
                    product.put("sellerID", userID);
                    product.put("itemStock", intItemStock);
                    product.put("itemCategory", ItemCategoryModel.itemCategory);
                    product.put("isSold", false);
                    product.put("soldDate", null);

                    uploadImageToFirebaseStorage(imageUri, productID);

                    documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();

                            ItemCategoryModel.itemCategory = null;

                            Log.i(TAG, "UserID" + userID);
                            Toast.makeText(Sell.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ItemCategoryModel.itemCategory = null;

                            Toast.makeText(Sell.this, "Product Failed to Add", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
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

                        Log.i(TAG, "Image Uploaded URL: " + uri);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Sell.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Image Failed to Upload");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sell.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ItemCategoryModel.itemCategory = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}