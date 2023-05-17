package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cc106project.Adapter.UserAdapter;
import com.example.cc106project.Model.ItemCategoryModel;
import com.example.cc106project.Model.UsersModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewProductInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private ImageView productImage;
    private ImageButton undoBtn, editProductName, editProductPrice, editProductStock, editProductCategory, editProductImage;
    private EditText dialogEditProductName, dialogEditProductPrice, dialogEditProductStock;
    private Button setAsSoldBtn;
    private TextView productName, productPrice, productStock, productCategory, productAvailability;
    private Spinner dialogEditProductCategory;
    private ProgressBar progressBar;
    private RecyclerView usersInterestedRecyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UsersModel> usersModelArrayList;
    private FirebaseAuth mAuth;
    private Intent intent;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private DocumentReference documentReference;
    private Spinner itemCategorySpinner;
    private String userID;
    private String TAG = "Sell";
    private int REQUEST_CODE = 69;
    private Uri imageUri;
    private AlertDialog.Builder builder;
    private View dialogView;

    // Get the current timestamp
    private long timestamp = System.currentTimeMillis();

    // Convert the timestamp to a Date object
    private Date currentDate = new Date(timestamp);

    // Format the date as desired
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy-dd");
    private String dateString = dateFormat.format(currentDate);

    // Format the time as desired
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private String timeString = timeFormat.format(currentDate);


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        if (!isFinishing()) {
            Glide.with(this).clear(productImage);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_info);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        productCategory = findViewById(R.id.productCategory);
        productAvailability = findViewById(R.id.productAvailability);

        setAsSoldBtn = findViewById(R.id.setAsSoldBtn);
        undoBtn = findViewById(R.id.undoBtn);
        editProductCategory = findViewById(R.id.editProductCategory);
        editProductImage = findViewById(R.id.editProductImage);
        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        editProductStock = findViewById(R.id.editProductStock);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        intent = getIntent();
        String getProductID = intent.getStringExtra("productID");
        String getSellerID = intent.getStringExtra("sellerID");

        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("products").document(getProductID);

        editProductName.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(this);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product_name, null);

            dialogEditProductName = dialogView.findViewById(R.id.dialogEditProductName);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogView.findViewById(R.id.dialogEditProductNameEditBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringProductName = dialogEditProductName.getText().toString();

                    if (stringProductName.isEmpty()) {
                        Toast.makeText(ViewProductInfo.this, "Please enter Product Name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (currentUser != null) {
                            Map<String, Object> product = new HashMap<>();
                            product.put("itemName", stringProductName);
                            documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Product Failed to Edit " + e.getMessage());
                                }
                            });

                        }
                    }

                }
            });
            dialogView.findViewById(R.id.dialogEditProductNameCancelBtn).setOnClickListener(new View.OnClickListener() {
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

        editProductPrice.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(this);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product_price, null);

            dialogEditProductPrice = dialogView.findViewById(R.id.dialogEditProductPrice);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogView.findViewById(R.id.dialogEditProductPriceEditBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double doubleProductPrice = Double.parseDouble(dialogEditProductPrice.getText().toString());

                    if (dialogEditProductPrice.getText().toString().isEmpty()) {
                        Toast.makeText(ViewProductInfo.this, "Please enter Product Price", Toast.LENGTH_SHORT).show();
                    } else {
                        if (currentUser != null) {
                            Map<String, Object> product = new HashMap<>();
                            product.put("itemPrice", doubleProductPrice);
                            documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Product Failed to Edit " + e.getMessage());
                                }
                            });

                        }
                    }

                }
            });
            dialogView.findViewById(R.id.dialogEditProductPriceCancelBtn).setOnClickListener(new View.OnClickListener() {
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

        editProductStock.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(this);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product_stock, null);

            dialogEditProductStock = dialogView.findViewById(R.id.dialogEditProductStock);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogView.findViewById(R.id.dialogEditProductStockEditBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int intProductStock = Integer.parseInt(dialogEditProductStock.getText().toString());

                    if (dialogEditProductPrice.getText().toString().isEmpty()) {
                        Toast.makeText(ViewProductInfo.this, "Please enter Product Stock", Toast.LENGTH_SHORT).show();
                    } else {
                        if (currentUser != null) {
                            Map<String, Object> product = new HashMap<>();
                            product.put("itemStock", intProductStock);
                            documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Product Failed to Edit " + e.getMessage());
                                }
                            });

                        }
                    }

                }
            });
            dialogView.findViewById(R.id.dialogEditProductStockCancelBtn).setOnClickListener(new View.OnClickListener() {
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

        editProductCategory.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(this);
            dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product_category, null);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogEditProductCategory = dialogView.findViewById(R.id.dialogEditProductCategory);
            ArrayAdapter<CharSequence> arrayAdapter =
                    ArrayAdapter.createFromResource(this, R.array.item_category,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            dialogEditProductCategory.setAdapter(arrayAdapter);
            dialogEditProductCategory.setOnItemSelectedListener(this);

            dialogView.findViewById(R.id.dialogEditProductCategoryEditBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser != null) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("itemCategory", ItemCategoryModel.itemCategory);
                        documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ItemCategoryModel.itemCategory = null;
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ItemCategoryModel.itemCategory = null;
                                Log.e(TAG, "Product Failed to Edit " + e.getMessage());
                            }
                        });

                    }
                }
            });
            dialogView.findViewById(R.id.dialogEditProductCategoryCancelBtn).setOnClickListener(new View.OnClickListener() {
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

        editProductImage.setOnClickListener(v -> {

        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        usersInterestedRecyclerView = findViewById(R.id.usersInterestedRecyclerView);
        usersInterestedRecyclerView.setHasFixedSize(true);
        usersInterestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.toolBar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setAsSoldBtn.setOnClickListener(v -> {
            setAsProductSold();
        });

        undoBtn.setOnClickListener(v -> {
            undoSetAsProductSold();
        });

        userID = currentUser.getUid();
        loadSelectedProduct(getSellerID);
        loadInterestedUsers();
    }

    private void loadInterestedUsers() {
        usersModelArrayList = new ArrayList<>();
        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressBar.setVisibility(View.GONE);

                usersModelArrayList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);


                    if (!usersModel.getUserID().equals(currentUser.getUid())) {
                        usersModelArrayList.add(usersModel);
                    }

                    userAdapter = new UserAdapter(ViewProductInfo.this, usersModelArrayList, true);
                    usersInterestedRecyclerView.setAdapter(userAdapter);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });

    }

    private void loadSelectedProduct(String xSellerID) {
        if (userID.equals(xSellerID)) {
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {
                        productName.setText(value.getString("itemName"));
                        productCategory.setText("Category: " + value.getString("itemCategory"));
                        productPrice.setText("₱" + String.valueOf(value.getDouble("itemPrice")));
                        productStock.setText("Stock: " + String.valueOf(value.getLong("itemStock")));

                        if (Boolean.TRUE.equals(value.getBoolean("isSold"))) {
                            productAvailability.setText("Sold");
                            productAvailability.setTextColor(Color.RED);
                            productStock.setText("Stock: 0");
                        } else {
                            productAvailability.setText("Available");
                            productAvailability.setTextColor(Color.GREEN);

                        }

                        Glide.with(ViewProductInfo.this).load(value.getString("itemImage"))
                                .centerCrop().into(productImage);
                    } else {
                        Log.e(TAG, error.getMessage());
                    }
                }
            });
        } else {
            finish();
        }

    }

    private void setAsProductSold() {
        Map<String, Object> product = new HashMap<>();
        product.put("isSold", true);
        product.put("soldDate", timeString + " " + dateString);

        documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                productAvailability.setText("Sold");
                productAvailability.setTextColor(Color.RED);
                productStock.setText("Stock: 0");
                countProductsSold();
                Toast.makeText(ViewProductInfo.this, "Product has been marked as Sold", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void undoSetAsProductSold() {
        Map<String, Object> product = new HashMap<>();
        product.put("isSold", false);
        product.put("soldDate", null);

        documentReference.update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                productAvailability.setText("Available");
                productAvailability.setTextColor(Color.GREEN);
                undoCountProductsSold();
                Toast.makeText(ViewProductInfo.this, "Product undo marked as Sold", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void undoCountProductsSold() {
        documentReference = fStore.collection("users").document(userID);

        Map<String, Object> user = new HashMap<>();
        user.put("productsSold", 0);

        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void countProductsSold() {
        documentReference = fStore.collection("users").document(userID);

        Map<String, Object> user = new HashMap<>();
        user.put("productsSold", 1);

        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
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