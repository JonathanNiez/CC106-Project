package com.example.cc106project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class Sell extends AppCompatActivity {

    private Button cancelBtn, sellBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        cancelBtn = findViewById(R.id.cancelBtn);
        sellBtn = findViewById(R.id.sellBtn);

        cancelBtn.setOnClickListener(v -> {
            finish();
        });
    }
}