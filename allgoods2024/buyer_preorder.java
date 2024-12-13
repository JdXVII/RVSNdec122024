package com.example.allgoods2024;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class buyer_preorder extends AppCompatActivity {

    private RecyclerView preorderRecyclerView;
    private BuyerProductAdapter buyerProductAdapter;
    private List<Product> productList;

    private List<Product> filteredProductList;
    private SearchView searchBar;
    private Spinner categorySpinner;
    private ImageView btn_back;

    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_preorder);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        preorderRecyclerView = findViewById(R.id.preorderrecyclerView);
        preorderRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btn_back = new Intent(buyer_preorder.this, buyer_homepage.class);
                startActivity(btn_back);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        searchBar = findViewById(R.id.search_bar);
        categorySpinner = findViewById(R.id.productStatusSpinner);

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        categoryList = new ArrayList<>();

        setupSearchBar();
        fetchCategories();
        fetchPreOrderProducts();

        searchBar.setQueryHint("Search products");
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = findViewById(searchTextId);
        if (searchTextView != null) {
            searchTextView.setTextColor(Color.BLACK);
        }
    }

    private void setupSearchBar() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, categorySpinner.getSelectedItem().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, categorySpinner.getSelectedItem().toString());
                return false;
            }
        });
    }

    private void fetchCategories() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("product_category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryList.add("All"); // Default option for all categories

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(buyer_preorder.this, R.layout.custom_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCategory = categoryList.get(position);
                        filterProducts(searchBar.getQuery().toString(), selectedCategory);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action needed
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyer_preorder.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPreOrderProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("upload_products");
        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");

        vendorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot vendorSnapshot) {
                Map<String, Vendor> approvedVendors = new HashMap<>();

                // Loop through vendors to find "approved" ones
                for (DataSnapshot vendorData : vendorSnapshot.getChildren()) {
                    Vendor vendor = vendorData.getValue(Vendor.class);
                    if (vendor != null && "approved".equalsIgnoreCase(vendor.getStatus())) {
                        approvedVendors.put(vendor.getUserId(), vendor);
                    }
                }

                productsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                        productList.clear();

                        // Loop through products and filter by "Pre Order" and approved vendor
                        for (DataSnapshot productData : productSnapshot.getChildren()) {
                            Product product = productData.getValue(Product.class);
                            if (product != null
                                    && "approved".equalsIgnoreCase(product.getStatus()) // Approved product
                                    && "Pre Order".equalsIgnoreCase(product.getProductType())) { // Pre Order type
                                String productUserId = product.getUserId();

                                // Check if the product's userId is in the approved vendor list
                                if (approvedVendors.containsKey(productUserId)) {
                                    productList.add(product);
                                }
                            }
                        }

                        filteredProductList.clear();
                        filteredProductList.addAll(productList); // Initialize filtered list
                        updateRecyclerView();

                        // Update RecyclerView with the filtered product list
                        buyerProductAdapter = new BuyerProductAdapter(productList, new BuyerProductAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Product product) {
                                Intent intent = new Intent(buyer_preorder.this, buyer_buy_product.class);
                                intent.putExtra("productId", product.getProductId());
                                intent.putExtra("userId", product.getUserId());
                                startActivity(intent);
                            }
                        });

                        preorderRecyclerView.setAdapter(buyerProductAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_preorder.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_preorder.this, "Failed to load vendor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query, String category) {
        filteredProductList.clear();

        for (Product product : productList) {
            boolean matchesQuery = product.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category.equals("All") || product.getCategory().equalsIgnoreCase(category);

            if (matchesQuery && matchesCategory) {
                filteredProductList.add(product);
            }
        }

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        buyerProductAdapter = new BuyerProductAdapter(filteredProductList, new BuyerProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(buyer_preorder.this, buyer_buy_product.class);
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("userId", product.getUserId());
                startActivity(intent);
            }
        });

        preorderRecyclerView.setAdapter(buyerProductAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(buyer_preorder.this, buyer_homepage.class);
        startActivity(back);
        // Apply fade in and fade out animations
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
