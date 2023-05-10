package com.example.cc106project;

import static android.app.Activity.RESULT_OK;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Account extends Fragment {

    private TextView firstName, lastName, email, address;
    private ImageView profilePicture;
    private ImageButton editAddress, editProfilePicBtn;
    private EditText streetAddress, province, city, postalCode;

    FirebaseAuth auth;
    FirebaseFirestore fStore;
    FirebaseUser currentUser;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    String userID;
    private static final int REQUEST_OPEN_GALLERY = 1;

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Account", "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i("Account", "onStop");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        auth = FirebaseAuth.getInstance();
//        currentUser = auth.getCurrentUser();

        //Firebase Google sign in
//        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail().build();
//        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
//        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

    }

    private void checkIfGoogleSignedIn() {
        if (googleSignInAccount != null) {
            Log.i("Account", "Displaying Google Account Info");
            String getEmail = googleSignInAccount.getEmail();
            String getFirstName = googleSignInAccount.getGivenName();
            String getLastName = googleSignInAccount.getFamilyName();

            Log.i("Account", "Email: " + getEmail);
            Log.i("Account", "FullName: " + getFirstName + " " + getLastName);

            Glide.with(getContext()).load(googleSignInAccount.getPhotoUrl())
                    .centerCrop().into(profilePicture);

            firstName.setText(getFirstName);
            lastName.setText(getLastName);
            email.setText(getEmail);
        }

    }

    private void checkIfEmailSignedIn() {
        if (currentUser != null) {
            Log.i("Account", "Displaying Account Info");

//            userID = auth.getCurrentUser().getUid();

//            DocumentReference documentReference = fStore.collection("users").document(userID);
//            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                    if (value != null) {
//                        firstName.setText(value.getString("firstName"));
//                        lastName.setText(value.getString("lastName"));
//                        email.setText(value.getString(currentUser.getEmail()));
//                        address.setText(value.getString("address"));
//
//                        Log.i("Account", currentUser.getEmail());
//                    } else {
//                        FirebaseAuth.getInstance().signOut();
//                    }
//                }
//            });
        }
    }

    public void displayUserInfo() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            userID = currentUser.getUid();

            Log.i("Account", "User: " + currentUser);

            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {
                        String getProfilePic = value.getString("profilePicUrl");

                        firstName.setText(value.getString("firstName"));
                        lastName.setText(value.getString("lastName"));
                        email.setText(value.getString("email"));
                        address.setText(value.getString("address"));

                        if (getProfilePic != null) {
                            Glide.with(getActivity()).load(currentUser.getPhotoUrl())
                                    .centerCrop().into(profilePicture);

                        }

                    } else {
                        Log.e("Account", error.getMessage());
                    }

                }
            });

        } else {
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            Toast.makeText(getActivity(), "Selected image: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        profilePicture = view.findViewById(R.id.profilePic);
        editAddress = view.findViewById(R.id.editAddress);
        editProfilePicBtn = view.findViewById(R.id.editProfilePicBtn);

        displayUserInfo();

        editAddress.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);

//            EditText emailForgotPassword = dialogView.findViewById(R.id.emailForgotPassword);
            streetAddress = dialogView.findViewById(R.id.streetAddress);
            province = dialogView.findViewById(R.id.province);
            city = dialogView.findViewById(R.id.city);
            postalCode = dialogView.findViewById(R.id.postalCode);

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
                                    Log.i("Account",  "Address Successfully Edited " + "UserID" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getContext(), "Address Failed to Edit", Toast.LENGTH_SHORT).show();
                                    Log.e("Account",  "Address Failed to Edit " + "UserID" + userID);
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

        editProfilePicBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_OPEN_GALLERY);
        });

        return view;
    }
}

