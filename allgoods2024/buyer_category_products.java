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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class buyer_category_products extends AppCompatActivity implements BuyerProductAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private BuyerProductAdapter adapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private TextView storeNameTextView;
    private SearchView searchView;
    private Spinner productStatusSpinner;
    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_category_products);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.vendorstorerecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BuyerProductAdapter(filteredList, this); // Pass the listener
        recyclerView.setAdapter(adapter);

        storeNameTextView = findViewById(R.id.store_name);
        searchView = findViewById(R.id.search_bar);
        productStatusSpinner = findViewById(R.id.productStatusSpinner);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(buyer_category_products.this, buyer_category.class);
                startActivity(back);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productStatusSpinner.setAdapter(spinnerAdapter);

        // Fetch the passed storeName and userId from the intent
        String storeName = getIntent().getStringExtra("storeName");
        String userId = getIntent().getStringExtra("userId");
        storeNameTextView.setText(storeName);

        // Fetch products from the database based on the userId
        fetchProducts(userId);
        // Fetch categories for spinner
        fetchCategories();

        // Implement the search feature
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, productStatusSpinner.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, productStatusSpinner.getSelectedItem().toString());
                return true;
            }
        });

        searchView.setQueryHint("Search products");
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = findViewById(searchTextId);
        if (searchTextView != null) {
            searchTextView.setTextColor(Color.BLACK);
        }

        // Implement the spinner filter feature
        productStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts(searchView.getQuery().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Fetch categories from the database
    private void fetchCategories() {
        FirebaseDatabase.getInstance().getReference("product_category")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> categories = new ArrayList<>();
                        categories.add("All"); // Default option for showing all products
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String category = snapshot.getValue(String.class);
                            if (category != null) {
                                categories.add(category);
                            }
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(buyer_category_products.this,
                                R.layout.custom_spinner_item, categories);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productStatusSpinner.setAdapter(spinnerAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(buyer_category_products.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(buyer_category_products.this, buyer_buy_product.class);
        intent.putExtra("productId", product.getProductId());
        intent.putExtra("userId", product.getUserId());
        startActivity(intent);
    }

    private void fetchProducts(String userId) {
        FirebaseDatabase.getInstance().getReference("upload_products")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null && "approved".equals(product.getStatus())) {
                                productList.add(product);
                            }
                        }
                        filterProducts(searchView.getQuery().toString(), productStatusSpinner.getSelectedItem().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(buyer_category_products.this, "Failed to load products. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterProducts(String query, String category) {
        filteredList.clear();
        for (Product product : productList) {
            boolean matchesName = product.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category.equals("All") || product.getCategory().equals(category);

            if (matchesName && matchesCategory) {
                filteredList.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
