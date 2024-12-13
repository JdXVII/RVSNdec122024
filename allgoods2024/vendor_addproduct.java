package com.example.allgoods2024;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class vendor_addproduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_IMAGES = 7;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private Button addImageButton;
    private ProgressBar progressBar;
    private ImageView bckpu;

    private TextView displayPrice;
    private EditText description, price, stock, sale,gcash_number, gcash_name;
    private Spinner productNameSpinner, categorySpinner, productTypeSpinner, productDeliverySpinner, productPaymentSpinner, typeSpinner;
    private Button publishButton;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private ArrayList<String> productNamesList;

    private CheckBox gcashCheckbox, paymayaCheckbox;
    private EditText mayaNumber, mayaName, voucherCodeEditText, voucherAmountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_addproduct);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bckpu = findViewById(R.id.bckpu);

        bckpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckpu = new Intent(vendor_addproduct.this, vendor_homepage.class);
                startActivity(bckpu);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        stock = findViewById(R.id.stock);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        productNameSpinner = findViewById(R.id.productNameSpinner);
        categorySpinner = findViewById(R.id.category);
        displayPrice = findViewById(R.id.display_price);
        publishButton = findViewById(R.id.publishButton);
        productTypeSpinner = findViewById(R.id.productTypeSpinner);
        sale = findViewById(R.id.sale_quantity);
        productDeliverySpinner = findViewById(R.id.productDeliverySpinner);
        productPaymentSpinner = findViewById(R.id.productPaymentSpinner);
        gcash_number = findViewById(R.id.gcash_number);
        gcash_name = findViewById(R.id.gcash_name);
        gcashCheckbox = findViewById(R.id.gcash_checkbox);
        paymayaCheckbox = findViewById(R.id.paymaya_checkbox);
        mayaNumber = findViewById(R.id.maya_number);
        mayaName = findViewById(R.id.maya_name);
        typeSpinner = findViewById(R.id.TypeSpinner);

        voucherCodeEditText = findViewById(R.id.voucher_code);
        voucherAmountEditText = findViewById(R.id.voucher_amount);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(imageAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("upload_products");
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        productNamesList = new ArrayList<>();

        // Set up the Product Type Spinner
        ArrayAdapter<CharSequence> typesAdapter = ArrayAdapter.createFromResource(this, R.array.product_type_options, R.layout.custom_spinner_item);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typesAdapter);

        // Set up the Product Type Spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.product_type_array, R.layout.custom_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productTypeSpinner.setAdapter(typeAdapter);

        // Set up the Product Delivery Spinner
        ArrayAdapter<CharSequence> deliveryAdapter = ArrayAdapter.createFromResource(this, R.array.delivery_options_array, R.layout.custom_spinner_item);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productDeliverySpinner.setAdapter(deliveryAdapter);

        // Set up the Product Payment Spinner
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this, R.array.payment_options_array, R.layout.custom_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productPaymentSpinner.setAdapter(paymentAdapter);

        addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUris.size() < MAX_IMAGES) {
                    openFileChooser();
                } else {
                    Toast.makeText(vendor_addproduct.this, "You can only upload up to 7 images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });

        // Fetch categories from Firebase and set the spinner
        fetchCategories();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                productNameSpinner.setVisibility(View.VISIBLE); // Show product name spinner
                fetchProductNames(selectedCategory); // Fetch products based on selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here now
            }
        });

        // Handle product name selection
        productNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update display price based on selected product name
                String selectedProductName = parent.getItemAtPosition(position).toString();
                updateDisplayPrice(selectedProductName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                displayPrice.setText("");
            }
        });

        // Handle product type selection
        productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if (selectedType.equals("Wholesale")) {
                    sale.setVisibility(View.VISIBLE);
                    findViewById(R.id.sale_label).setVisibility(View.VISIBLE);
                } else {
                    sale.setVisibility(View.GONE);
                    findViewById(R.id.sale_label).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sale.setVisibility(View.GONE);
                findViewById(R.id.sale_label).setVisibility(View.GONE);
            }
        });

        // Fetch product names based on initial category selection
        String initialCategory = categorySpinner.getSelectedItem().toString();
        fetchProductNames(initialCategory);

        productPaymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paymentOption = parent.getItemAtPosition(position).toString();
                if (paymentOption.equals("Available")) {
                    gcashCheckbox.setVisibility(View.VISIBLE);
                    paymayaCheckbox.setVisibility(View.VISIBLE);
                } else {
                    gcashCheckbox.setVisibility(View.GONE);
                    paymayaCheckbox.setVisibility(View.GONE);
                    gcash_number.setVisibility(View.GONE);
                    gcash_name.setVisibility(View.GONE);
                    mayaNumber.setVisibility(View.GONE);
                    mayaName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        gcashCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gcash_number.setVisibility(View.VISIBLE);
                gcash_name.setVisibility(View.VISIBLE);
            } else {
                gcash_number.setVisibility(View.GONE);
                gcash_name.setVisibility(View.GONE);
            }
        });

        paymayaCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mayaNumber.setVisibility(View.VISIBLE);
                mayaName.setVisibility(View.VISIBLE);
            } else {
                mayaNumber.setVisibility(View.GONE);
                mayaName.setVisibility(View.GONE);
            }
        });

    }

    private void fetchCategories() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("product_category");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> categories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getValue(String.class);
                    categories.add(category);  // Add each category to the list
                }

                // Create a new ArrayAdapter with the dynamically loaded categories
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(vendor_addproduct.this, R.layout.custom_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);

                // Optional: Fetch products based on the initially selected category
                String initialCategory = categorySpinner.getSelectedItem().toString();
                fetchProductNames(initialCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_addproduct.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch product names based on selected category
    private void fetchProductNames(final String selectedCategory) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.orderByChild("category").equalTo(selectedCategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productNamesList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productName = productSnapshot.child("name").getValue(String.class);
                    productNamesList.add(productName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(vendor_addproduct.this, R.layout.custom_spinner_item, productNamesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productNameSpinner.setAdapter(adapter);

                // Optionally, update display price for the first product in the list if available
                if (!productNamesList.isEmpty()) {
                    updateDisplayPrice(productNamesList.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_addproduct.this, "Failed to load product names", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDisplayPrice(final String productName) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");
        productRef.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        String priceMin = productSnapshot.child("price").child("min").getValue(String.class);
                        String priceMax = productSnapshot.child("price").child("max").getValue(String.class);
                        String quantity = productSnapshot.child("quantity").getValue(String.class);

                        if (priceMin != null && priceMax != null && quantity != null) {
                            displayPrice.setText("Price Range (min - max): " + priceMin + " - " + priceMax + "\nQuantity: " + quantity);
                        } else {
                            displayPrice.setText("Price or quantity not available");
                        }
                    }
                } else {
                    displayPrice.setText("Price information not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_addproduct.this, "Failed to load price information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Multiple images selected
                int count = data.getClipData().getItemCount();
                if (count > MAX_IMAGES) {
                    Toast.makeText(this, "You can only upload up to 7 images", Toast.LENGTH_SHORT).show();
                    return;
                }

                imageUris.clear(); // Clear existing URIs if you want to replace them
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (imageUri != null) {
                        imageUris.add(imageUri);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                if (imageUris.size() < MAX_IMAGES) {
                    imageUris.add(imageUri);
                    imageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "You can only upload up to 7 images", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadProduct() {
        final String productName;
        final String productStock = stock.getText().toString().trim();
        final String productDescription = description.getText().toString().trim();
        final String productPrice = price.getText().toString().trim();
        final String selectedCategory = categorySpinner.getSelectedItem().toString();
        final String productType = productTypeSpinner.getSelectedItem().toString();
        final String saleInput = sale.getText().toString().trim();
        final String deliveryOption = productDeliverySpinner.getSelectedItem().toString();
        final String paymentOption = productPaymentSpinner.getSelectedItem().toString();
        final String gcashNumber = gcash_number.getText().toString().trim();
        final String gcashName = gcash_name.getText().toString().trim();
        final String mayaNumberValue = mayaNumber.getText().toString().trim();
        final String mayaNameValue = mayaName.getText().toString().trim();
        final String type = typeSpinner.getSelectedItem().toString();

        // Get voucher details
        final String voucherCode = voucherCodeEditText.getText().toString().trim();
        final String voucherAmount = voucherAmountEditText.getText().toString().trim();

        // Use the selected category product name
        productName = productNameSpinner.getSelectedItem().toString();

        if (productName == null || productName.isEmpty()) {
            Toast.makeText(this, "Product name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate all required fields and image selection
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productStock.isEmpty()) {
            Toast.makeText(this, "Please enter the stock quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productDescription.isEmpty()) {
            Toast.makeText(this, "Please enter a product description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productPrice.isEmpty()) {
            Toast.makeText(this, "Please enter the product price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productType.equals("Wholesale") && saleInput.isEmpty()) {
            Toast.makeText(this, "Please enter sale quantity for wholesale products", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryOption.isEmpty()) {
            Toast.makeText(this, "Please select a delivery option", Toast.LENGTH_SHORT).show();
            return;
        }

        if (paymentOption.equals("Available")) {
            if (!gcashCheckbox.isChecked() && !paymayaCheckbox.isChecked()) {
                Toast.makeText(this, "Please check at least one payment method (GCash or PayMaya)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gcashCheckbox.isChecked()) {
                if (gcashNumber.isEmpty()) {
                    Toast.makeText(this, "Please enter your GCash number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gcashName.isEmpty()) {
                    Toast.makeText(this, "Please enter your GCash name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (paymayaCheckbox.isChecked()) {
                if (mayaNumberValue.isEmpty()) {
                    Toast.makeText(this, "Please enter your PayMaya number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mayaNameValue.isEmpty()) {
                    Toast.makeText(this, "Please enter your PayMaya name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Proceed with the upload if all validations pass
        validateAndUploadProduct(productName, productStock, productDescription, productPrice, selectedCategory, productType, saleInput, deliveryOption, paymentOption, gcashNumber, gcashName, mayaNumberValue, mayaNameValue, voucherCode, voucherAmount, type);
    }

    private void validateAndUploadProduct(final String productName, final String productStock, final String productDescription, final String productPrice, final String selectedCategory, final String productType, final String saleInput, final String deliveryOption, final String paymentOption, final String gcashNumber, final String gcashName, final String mayaNumberValue, final String mayaNameValue, final String voucherCode, final String voucherAmount, final String type) {
        DatabaseReference pricesRef = FirebaseDatabase.getInstance().getReference("products");
        pricesRef.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        double minPrice = Double.parseDouble(productSnapshot.child("price").child("min").getValue(String.class));
                        double maxPrice = Double.parseDouble(productSnapshot.child("price").child("max").getValue(String.class));

                        double priceValue = Double.parseDouble(productPrice);
                        if (priceValue < minPrice) {
                            Toast.makeText(vendor_addproduct.this, "Price is too low", Toast.LENGTH_SHORT).show();
                            // Hide ProgressBar
                            progressBar.setVisibility(View.GONE);
                        } else if (priceValue > maxPrice) {
                            Toast.makeText(vendor_addproduct.this, "Price is too high", Toast.LENGTH_SHORT).show();
                            // Hide ProgressBar
                            progressBar.setVisibility(View.GONE);
                        } else {
                            uploadProductDetails(
                                    productName, productStock, productDescription, productPrice, selectedCategory,
                                    productType, saleInput, deliveryOption, paymentOption, gcashNumber, gcashName,
                                    mayaNumberValue, mayaNameValue, voucherCode, voucherAmount, type );
                        }
                    }
                } else {
                    Toast.makeText(vendor_addproduct.this, "Price range not available for this product", Toast.LENGTH_SHORT).show();
                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_addproduct.this, "Failed to validate price", Toast.LENGTH_SHORT).show();
                // Hide ProgressBar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void uploadProductDetails(final String productName, final String productStock, final String productDescription, final String productPrice, final String selectedCategory, final String productType, final String saleInput, final String deliveryOption, final String paymentOption, final String gcashNumber, final String gcashName, final String mayaNumberValue, final String mayaNameValue, final String voucherCode, final String voucherAmount, final String type) {
        final List<String> imageUrls = new ArrayList<>();
        final StorageReference[] storageReferences = new StorageReference[imageUris.size()];

        for (int i = 0; i < imageUris.size(); i++) {
            final Uri uri = imageUris.get(i);
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            storageReferences[i] = fileReference;

            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            if (imageUrls.size() == imageUris.size()) {
                                // All images uploaded
                                String productId = databaseRef.push().getKey();
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = currentUser != null ? currentUser.getUid() : "unknown";
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("vendors").child(userId);
                                userRef.child("storeName").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String storeName = dataSnapshot.getValue(String.class);
                                        storeName = storeName != null ? storeName : "No Store Name";
                                        String[] priceDetails = displayPrice.getText().toString().split("\n");
                                        String unit = priceDetails.length > 1 ? priceDetails[1].replace("Quantity: ", "").trim() : "unknown";

                                        String saleValue = productType.equals("Wholesale") ? saleInput : "0";
                                        String voucherCodeValue = voucherCode.isEmpty() ? "Not Available" : voucherCode;
                                        String voucherAmountValue = voucherAmount.isEmpty() ? "Not Available" : voucherAmount;
                                        Product product = new Product(productId, productName, productStock, productDescription, productPrice,
                                                selectedCategory, imageUrls, "pending", userId, productType, saleValue, storeName, deliveryOption,
                                                "0", paymentOption, 0, 0, 0.0f, gcashNumber, gcashName, mayaNumberValue, mayaNameValue, unit, voucherCodeValue, voucherAmountValue, false, type);
                                        databaseRef.child(productId).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Start the transition activity
                                                Intent intent = new Intent(vendor_addproduct.this, success_publish_transition.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                // Hide ProgressBar
                                                progressBar.setVisibility(View.GONE);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(vendor_addproduct.this, "Failed to upload product", Toast.LENGTH_SHORT).show();
                                                // Hide ProgressBar
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(vendor_addproduct.this, "Failed to get store name", Toast.LENGTH_SHORT).show();
                                        // Hide ProgressBar
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(vendor_addproduct.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_addproduct.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}