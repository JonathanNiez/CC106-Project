package com.example.cc106project.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cc106project.AddAddress;
import com.example.cc106project.R;
import com.example.cc106project.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Feedback extends Fragment {
    private EditText subjectEditText, feedbackEditText;
    private Button submitBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        subjectEditText = view.findViewById(R.id.subjectEditText);
        feedbackEditText = view.findViewById(R.id.feedbackEditText);
        submitBtn = view.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(v -> {
            submitFeedback();
        });
        return view;
    }

    private void submitFeedback() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        Random random = new Random();
        int feedBackID = random.nextInt(999999999);

        String stringFeedback = feedbackEditText.getText().toString();
        String stringSubject = subjectEditText.getText().toString();

        documentReference = fStore.collection("feedbacks").document(String.valueOf(feedBackID));

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("subject", stringSubject);
        feedback.put("feedback", stringFeedback);
        feedback.put("feedbackID", feedBackID);
        feedback.put("userFeedbackID", currentUser.getUid());

        documentReference.set(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Feedback Submitted", Toast.LENGTH_SHORT).show();

                feedbackEditText.setText("");
                subjectEditText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Feedback Failed to Submit", Toast.LENGTH_SHORT).show();

                feedbackEditText.setText("");
                subjectEditText.setText("");
            }
        });
    }
}