package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class buyer_account extends AppCompatActivity {

    private ImageView profileImage, bckacc;
    private TextView firstName, lastName, email, phone, address;
    private Button editButton, logoutButton, support;
    private DatabaseReference buyerRef;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_account);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        email = findViewById(R.id.login_email);
        phone = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        editButton = findViewById(R.id.edit);
        logoutButton = findViewById(R.id.logout);
        bckacc = findViewById(R.id.bckacc);

        bckacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckacc = new Intent(buyer_account.this, buyer_homepage.class);
                startActivity(bckacc);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        support = findViewById(R.id.support);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent support = new Intent(buyer_account.this, buyer_admin_messages.class);
                support.putExtra("userId", currentUserId); // Replace `currentUserId` with the actual variable holding the vendor's userId
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(support);
            }
        });

        // Initialize Firebase references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        buyerRef = FirebaseDatabase.getInstance().getReference("buyers").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("buyer_images").child(userId);

        // Fetch and display buyer information
        buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Buyer buyer = snapshot.getValue(Buyer.class);
                if (buyer != null) {
                    firstName.setText(buyer.firstName);
                    lastName.setText(buyer.lastName);
                    email.setText(buyer.login_email);
                    phone.setText(buyer.phone);
                    address.setText(buyer.provinces + ", " + buyer.cities + ", " + buyer.barangay);

                    // Load profile image using Glide
                    Glide.with(buyer_account.this)
                            .load(buyer.profileImageUrl)
                            .placeholder(R.drawable.user1) // default image
                            .into(profileImage);
                } else {
                    Toast.makeText(buyer_account.this, "Buyer information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyer_account.this, "Failed to load buyer information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up edit button click listener
        editButton.setOnClickListener(v -> showEditDialog());

        // Set up logout button click listener
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.buyer_dialog_edit_profile, null);
        builder.setView(dialogView);

        ImageView editProfileImage = dialogView.findViewById(R.id.edit_profile_image);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);
        EditText editFirstName = dialogView.findViewById(R.id.edit_firstname);
        EditText editLastName = dialogView.findViewById(R.id.edit_lastname);
        EditText editPhone = dialogView.findViewById(R.id.edit_phone);
        Spinner spinnerProvince = dialogView.findViewById(R.id.spinner_province);
        Spinner spinnerCity = dialogView.findViewById(R.id.spinner_city);
        Spinner spinnerBarangay = dialogView.findViewById(R.id.spinner_barangay);

        // Load existing buyer information
        buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Buyer buyer = snapshot.getValue(Buyer.class);
                if (buyer != null) {
                    // Populate fields with current data
                    editFirstName.setText(buyer.firstName);
                    editLastName.setText(buyer.lastName);
                    editPhone.setText(buyer.phone);

                    // Set up province spinner
                    ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(buyer_account.this,
                            R.array.province_array, android.R.layout.simple_spinner_item);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(provinceAdapter);
                    int provincePosition = provinceAdapter.getPosition(buyer.provinces);
                    spinnerProvince.setSelection(provincePosition);

                    // Set up city spinner based on selected province
                    ArrayAdapter<CharSequence> cityAdapter = getCityAdapter(buyer.provinces);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);
                    int cityPosition = cityAdapter.getPosition(buyer.cities);
                    spinnerCity.setSelection(cityPosition);

                    // Set up barangay spinner based on selected city
                    ArrayAdapter<CharSequence> barangayAdapter = getBarangayAdapter(buyer.cities);
                    barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBarangay.setAdapter(barangayAdapter);
                    int barangayPosition = barangayAdapter.getPosition(buyer.barangay);
                    spinnerBarangay.setSelection(barangayPosition);

                    // Load existing profile image
                    if (buyer.profileImageUrl != null && !buyer.profileImageUrl.isEmpty()) {
                        Glide.with(buyer_account.this)
                                .load(buyer.profileImageUrl)
                                .placeholder(R.drawable.user1) // default image
                                .into(editProfileImage);
                    }
                } else {
                    Toast.makeText(buyer_account.this, "Buyer information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyer_account.this, "Failed to load buyer information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle image selection
        selectImageButton.setOnClickListener(v -> openImageChooser());

        // Flag to check if the city spinner was initialized
        final boolean[] isCitySpinnerInitialized = {false};

        // Set listener for city spinner
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCitySpinnerInitialized[0]) {
                    String selectedCity = (String) parent.getItemAtPosition(position);
                    ArrayAdapter<CharSequence> barangayAdapter = getBarangayAdapter(selectedCity);
                    barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBarangay.setAdapter(barangayAdapter);
                }
                isCitySpinnerInitialized[0] = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newFirstName = editFirstName.getText().toString().trim();
            String newLastName = editLastName.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();
            String selectedProvince = spinnerProvince.getSelectedItem().toString();
            String selectedCity = spinnerCity.getSelectedItem().toString();
            String selectedBarangay = spinnerBarangay.getSelectedItem().toString();

            if (newFirstName.isEmpty() || newLastName.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(buyer_account.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update buyer profile information
            buyerRef.child("firstName").setValue(newFirstName)
                    .addOnSuccessListener(aVoid -> {
                        buyerRef.child("lastName").setValue(newLastName);
                        buyerRef.child("phone").setValue(newPhone);
                        buyerRef.child("provinces").setValue(selectedProvince);
                        buyerRef.child("cities").setValue(selectedCity);
                        buyerRef.child("barangay").setValue(selectedBarangay);
                        if (imageUri != null) {
                            uploadImage();
                        }
                        Toast.makeText(buyer_account.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(buyer_account.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    buyerRef.child("profileImageUrl").setValue(uri.toString());
                })).addOnFailureListener(e -> Toast.makeText(buyer_account.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();

                    // Clear login state from SharedPreferences
                    getSharedPreferences("loginPrefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedInAsVendor", false)
                            .putBoolean("isLoggedInAsBuyer", false)
                            .apply();

                    // Redirect to vendor_login activity
                    Intent intent = new Intent(buyer_account.this, buyer_login.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private ArrayAdapter<CharSequence> getCityAdapter(String province) {
        int arrayId;
        switch (province) {
            case "Pangasinan":
                arrayId = R.array.city_array_pangasinan;
                break;
            default:
                arrayId = R.array.city_array_pangasinan;
                break;
        }
        return ArrayAdapter.createFromResource(this, arrayId, android.R.layout.simple_spinner_item);
    }

    private ArrayAdapter<CharSequence> getBarangayAdapter(String city) {
        int arrayId;
        switch (city) {
            case "Agno":
                arrayId = R.array.brgy_array_agno;
                break;
            case "Aguilar":
                arrayId = R.array.brgy_array_aguilar;
                break;
            case "Alaminos City":
                arrayId = R.array.brgy_array_alaminos;
                break;
            case "Alcala":
                arrayId = R.array.brgy_array_alcala;
                break;
            case "Anda":
                arrayId = R.array.brgy_array_anda;
                break;
            case "Asingan":
                arrayId = R.array.brgy_array_asingan;
                break;
            case "Balungao":
                arrayId = R.array.brgy_array_balungao;
                break;
            case "Bani":
                arrayId = R.array.brgy_array_bani;
                break;
            case "Basista":
                arrayId = R.array.brgy_array_basista;
                break;
            case "Bautista":
                arrayId = R.array.brgy_array_bautista;
                break;
            case "Bayambang":
                arrayId = R.array.brgy_array_bayambang;
                break;
            case "Binalonan":
                arrayId = R.array.brgy_array_binalonan;
                break;
            case "Binmaley":
                arrayId = R.array.brgy_array_binmaley;
                break;
            case "Bolinao":
                arrayId = R.array.brgy_array_bolinao;
                break;
            case "Bugallon":
                arrayId = R.array.brgy_array_bugallon;
                break;
            case "Burgos":
                arrayId = R.array.brgy_array_burgos;
                break;
            case "Calasiao":
                arrayId = R.array.brgy_array_calasiao;
                break;
            case "Dagupan City":
                arrayId = R.array.brgy_array_dagupan_city;
                break;
            case "Dasol":
                arrayId = R.array.brgy_array_dasol;
                break;
            case "Infanta":
                arrayId = R.array.brgy_array_infanta;
                break;
            case "Labrador":
                arrayId = R.array.brgy_array_labrador;
                break;
            case "Laoac":
                arrayId = R.array.brgy_array_laoac;
                break;
            case "Lingayen":
                arrayId = R.array.brgy_array_lingayen;
                break;
            case "Mabini":
                arrayId = R.array.brgy_array_mabini;
                break;
            case "Malasiqui":
                arrayId = R.array.brgy_array_malasiqui;
                break;
            case "Manaoag":
                arrayId = R.array.brgy_array_manaoag;
                break;
            case "Mangaldan":
                arrayId = R.array.brgy_array_mangaldan;
                break;
            case "Mangatarem":
                arrayId = R.array.brgy_array_mangatarem;
                break;
            case "Mapandan":
                arrayId = R.array.brgy_array_mapandan;
                break;
            case "natividad":
                arrayId = R.array.brgy_array_natividad;
                break;
            case "Pozorrubio":
                arrayId = R.array.brgy_array_pozorrubio;
                break;
            case "Rosales":
                arrayId = R.array.brgy_array_rosales;
                break;
            case "San Carlos City":
                arrayId = R.array.brgy_array_san_carlos;
                break;
            case "San Fabian":
                arrayId = R.array.brgy_array_san_fabian;
                break;
            case "San Jacinto":
                arrayId = R.array.brgy_array_san_jacinto;
                break;
            case "San Manuel":
                arrayId = R.array.brgy_array_san_manuel;
                break;
            case "San Nicolas":
                arrayId = R.array.brgy_array_san_nicolas;
                break;
            case "San Quintin":
                arrayId = R.array.brgy_array_san_quintin;
                break;
            case "Santa Barbara":
                arrayId = R.array.brgy_array_santa_barbara;
                break;
            case "Santa Maria":
                arrayId = R.array.brgy_array_santa_maria;
                break;
            case "Santo Tomas":
                arrayId = R.array.brgy_array_santo_tomas;
                break;
            case "Sison":
                arrayId = R.array.brgy_array_sison;
                break;
            case "Sual":
                arrayId = R.array.brgy_array_sual;
                break;
            case "Tayug":
                arrayId = R.array.brgy_array_tayug;
                break;
            case "Umingan":
                arrayId = R.array.brgy_array_umingan;
                break;
            case "Urbiztondo":
                arrayId = R.array.brgy_array_urbiztondo;
                break;
            case "Urdaneta City":
                arrayId = R.array.brgy_array_urdaneta_city;
                break;
            case "Villasis":
                arrayId = R.array.brgy_array_villasis;
                break;
            default:
                arrayId = R.array.brgy_array_agno;
                break;
        }
        return ArrayAdapter.createFromResource(this, arrayId, android.R.layout.simple_spinner_item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(buyer_account.this, buyer_homepage.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
