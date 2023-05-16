package com.example.cc106project.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cc106project.Login;
import com.example.cc106project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Account extends Fragment {

    private TextView firstName, lastName, email, address;
    private ImageView profilePicture, coverPhoto;
    private ImageButton editAddress, editProfilePicBtn, clearProfilePicBtn, editCoverPhotoBtn, clearCoverPhotoBtn;
    private EditText streetAddress, province, city, postalCode;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private StorageReference fileRef;
    private String userID;
    private int REQUEST_CODE_FOR_PROFILE_PIC = 69;
    private int REQUEST_CODE_FOR_COVER_PIC = 420;
    private Intent intent;
    private Uri imageUri;
    private String TAG = "Account";

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i(TAG, "onStop");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");

        userID = currentUser.getUid();

        if (requestCode == REQUEST_CODE_FOR_PROFILE_PIC && data != null && data.getData() != null) {
            imageUri = data.getData();

            Log.i(TAG, "REQUEST_CODE_FOR_PROFILE_PIC");

            uploadImageToFirebaseStorage(imageUri, userID);
        }
        if (requestCode == REQUEST_CODE_FOR_COVER_PIC && data != null && data.getData() != null) {
            imageUri = data.getData();

            Log.i(TAG, "REQUEST_CODE_FOR_COVER_PIC");

            uploadCoverPhotoToFirebaseStorage(imageUri, userID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        profilePicture = view.findViewById(R.id.profilePic);
        coverPhoto = view.findViewById(R.id.coverPhoto);
        editAddress = view.findViewById(R.id.editAddress);
        editProfilePicBtn = view.findViewById(R.id.editProfilePicBtn);
        clearProfilePicBtn = view.findViewById(R.id.clearProfilePicBtn);
        editCoverPhotoBtn = view.findViewById(R.id.editCoverPhotoBtn);
        clearCoverPhotoBtn = view.findViewById(R.id.clearCoverPhotoBtn);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        displayUserInfo();

        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("images");

        editAddress.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);

            streetAddress = dialogView.findViewById(R.id.streetAddress);
            province = dialogView.findViewById(R.id.province);
            city = dialogView.findViewById(R.id.city);
            postalCode = dialogView.findViewById(R.id.postalCode);
            clearProfilePicBtn.setVisibility(View.GONE);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            dialogView.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringStreetAddress = streetAddress.getText().toString();
                    String stringProvince = province.getText().toString();
                    String stringCity = city.getText().toString();
                    String stringPostalCode = postalCode.getText().toString();

                    if (stringStreetAddress.isEmpty() || stringProvince.isEmpty() || stringCity.isEmpty() || stringPostalCode.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter your Address", Toast.LENGTH_SHORT).show();
                    } else {
                        String fullAddress = stringStreetAddress + " " + stringProvince + " " + stringCity + " " + stringPostalCode;
                        auth = FirebaseAuth.getInstance();
                        currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            userID = currentUser.getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            Map<String, Object> user = new HashMap<>();
                            user.put("address", fullAddress);
                            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    alertDialog.dismiss();

                                    Toast.makeText(getContext(), "Address Successfully Edited", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "Address Successfully Edited " + "UserID" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getContext(), "Address Failed to Edit", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Address Failed to Edit " + "UserID" + userID);
                                }
                            });

                        }
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

        clearProfilePicBtn.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Account");
            alert.setMessage("Remove Profile Picture?");
            alert.setCancelable(true);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userID = auth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);

                    Map<String, Object> user = new HashMap<>();
                    user.put("profilePicUrl", null);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.i(TAG, "Profile Picture Removed Successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                            dialog.cancel();
                        }
                    });


                }
            });
            alert.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert.show();

        });

        editProfilePicBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_FOR_PROFILE_PIC);
        });

        editCoverPhotoBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_FOR_COVER_PIC);

        });

        return view;
    }

    public void displayUserInfo() {

        if (currentUser != null) {
            userID = currentUser.getUid();

            Log.i(TAG, "User: " + currentUser);

            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    progressBar.setVisibility(View.GONE);

                    if (value != null) {
                        String getProfilePic = value.getString("profilePicUrl");
                        String getCoverPhoto = value.getString("coverPicUrl");

                        firstName.setText(value.getString("firstName"));
                        lastName.setText(value.getString("lastName"));
                        email.setText(value.getString("email"));
                        address.setText(value.getString("address"));


                        //TODO: nalibug ko sa conditioning
                        if (getProfilePic != null && getCoverPhoto == null) {
                            Glide.with(getContext()).load(getProfilePic)
                                    .centerCrop().into(profilePicture);

                            Log.i(TAG, "Displaying Profile Picture");
                            clearProfilePicBtn.setVisibility(View.VISIBLE);
                        } else if (getCoverPhoto != null) {
                            Glide.with(getContext()).load(getCoverPhoto)
                                    .centerCrop().into(coverPhoto);
                            clearCoverPhotoBtn.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Displaying Cover Picture");

                        }


                    } else {
                        Log.e(TAG, error.getMessage());
                        clearProfilePicBtn.setVisibility(View.GONE);
                        clearCoverPhotoBtn.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                    }

                }
            });

        } else {
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String xUserID) {
        fileRef = imagesRef.child(imageUri.getLastPathSegment());
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
                        data.put("profilePicUrl", uri.toString());
                        fStore.collection("users").document(String.valueOf(xUserID)).update(data);
                        profilePicture.setImageURI(imageUri);

//                        Toast.makeText(getContext(), "Profile Picture Changed Successfully", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Profile Picture Changed Successfully");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile Picture Failed to Upload", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Profile Picture Failed to Upload");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Profile Picture Failed to Upload", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void uploadCoverPhotoToFirebaseStorage(Uri imageUri, String xUserID) {
        fileRef = imagesRef.child(imageUri.getLastPathSegment());
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // Save the download URL in Firestore
                        Map<String, Object> data = new HashMap<>();
                        data.put("coverPicUrl", uri.toString());
                        fStore.collection("users").document(String.valueOf(xUserID)).update(data);
                        coverPhoto.setImageURI(imageUri);

//                        Toast.makeText(getContext(), "Profile Picture Changed Successfully", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Cover Picture Changed Successfully");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Cover Picture Failed to Upload", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Cover Picture Failed to Upload");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Profile Picture Failed to Upload", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        });

    }

}

