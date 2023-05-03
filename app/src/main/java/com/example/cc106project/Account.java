package com.example.cc106project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class Account extends Fragment {

    private TextView firstName, lastName, email, address;
    private ImageView profilePicture;
    private ImageButton editAddress;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Firebase Google sign in
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

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
        if (user != null) {
            Log.i("Account", "Displaying Account Info");

            userID = auth.getCurrentUser().getUid();

            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {
                        firstName.setText(value.getString("firstName"));
                        lastName.setText(value.getString("lastName"));
                        email.setText(value.getString(user.getEmail()));
                        address.setText(value.getString("address"));

                        Log.i("Account", user.getEmail());
                    } else {
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    public void displayUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            Log.i("Account", "User: " + currentUser);

            String getEmail = currentUser.getEmail();
            String getFirstName = currentUser.getDisplayName();

            Glide.with(getContext()).load(currentUser.getPhotoUrl())
                    .centerCrop().into(profilePicture);

            firstName.setText(getFirstName);
//            navLastname.setText(getLastName);
            email.setText(getEmail);

            userID = auth.getCurrentUser().getUid();

            if (userID != null) {
                DocumentReference documentReference = fStore.collection("users").document(userID);
                documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            firstName.setText(value.getString("firstName"));
                            lastName.setText(value.getString("lastName"));
                            email.setText(getEmail);
                            address.setText(value.getString("address"));
                        }

                    }
                });
            }


        } else {
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
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

        displayUserInfo();

        editAddress.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddAddress.class);
            startActivity(intent);
        });

        return view;
    }
}