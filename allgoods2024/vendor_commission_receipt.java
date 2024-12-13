package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class vendor_commission_receipt extends AppCompatActivity {

    private RecyclerView receiptRecyclerView;
    private ReceiptAdapter receiptAdapter;
    private List<Receipt> receiptList;
    private TextView noReceiptTextView;
    private ImageView back;

    private DatabaseReference receiptRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_commission_receipt);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        // Initialize views
        receiptRecyclerView = findViewById(R.id.receiptRecyclerView);
        noReceiptTextView = findViewById(R.id.noReceiptTextView);
        receiptList = new ArrayList<>();
        receiptAdapter = new ReceiptAdapter(receiptList);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(vendor_commission_receipt.this, vendor_commission.class);
                startActivity(back);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Set up RecyclerView
        receiptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receiptRecyclerView.setAdapter(receiptAdapter);

        // Initialize Firebase reference
        receiptRef = FirebaseDatabase.getInstance().getReference("receipt");

        // Retrieve userId from the intent
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            fetchReceipts(userId);
        }
    }

    private void fetchReceipts(String userId) {
        receiptRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiptList.clear(); // Clear existing receipts
                for (DataSnapshot receiptSnapshot : snapshot.getChildren()) {
                    Receipt receipt = receiptSnapshot.getValue(Receipt.class);
                    if (receipt != null) {
                        receiptList.add(receipt);
                    }
                }

                // Check if receipts are found and update UI accordingly
                if (receiptList.isEmpty()) {
                    noReceiptTextView.setVisibility(View.VISIBLE);
                    receiptRecyclerView.setVisibility(View.GONE);
                } else {
                    noReceiptTextView.setVisibility(View.GONE);
                    receiptRecyclerView.setVisibility(View.VISIBLE);
                    receiptAdapter.notifyDataSetChanged(); // Notify adapter of changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_commission_receipt.this, vendor_commission.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}