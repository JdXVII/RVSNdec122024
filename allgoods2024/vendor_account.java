package com.example.allgoods2024;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

public class vendor_account extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {

            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(vendor_account.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ImageView profileImage, archive, admin, commission, UG, bck_btn;
    private TextView firstName, lastName, email, phone, address, storename;
    private Button editButton, logoutButton;
    private DatabaseReference vendorRef;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_account);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        editButton = findViewById(R.id.edit);
        logoutButton = findViewById(R.id.logout);
        UG = findViewById(R.id.UG);
        storename = findViewById(R.id.storename);

        bck_btn = findViewById(R.id.bck_btn);
        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(vendor_account.this, vendor_homepage.class);
                startActivity(back);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        admin = findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(vendor_account.this, vendor_admin_messages.class);
                // Pass the userId as an extra
                intent.putExtra("userId", currentUserId); // Replace `currentUserId` with the actual variable holding the vendor's userId
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
                finish();
            }
        });

        commission = findViewById(R.id.commission);
        commission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commission = new Intent(vendor_account.this, vendor_commission.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(commission);
                finish();
            }
        });

        archive = findViewById(R.id.archive);
        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent archive = new Intent(vendor_account.this, vendor_archive.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(archive);
                finish();
            }
        });

        UG.setOnClickListener(view -> {
            Intent UG = new Intent(vendor_account.this, User_Guide.class);
            startActivity(UG);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        // Initialize Firebase references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        vendorRef = FirebaseDatabase.getInstance().getReference("vendors").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("vendor_images").child(userId);

        // Fetch and display vendor information
        vendorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vendor vendor = snapshot.getValue(Vendor.class);
                if (vendor != null) {
                    firstName.setText(vendor.firstName);
                    lastName.setText(vendor.lastName);
                    storename.setText(vendor.storeName);
                    email.setText(vendor.email);
                    phone.setText(vendor.phone);
                    address.setText(vendor.province + ", " + vendor.city + ", " + vendor.barangay);

                    // Load profile image using Glide
                    Glide.with(vendor_account.this)
                            .load(vendor.profileImageUrl)
                            .placeholder(R.drawable.user1) // default image
                            .into(profileImage);
                } else {
                    Toast.makeText(vendor_account.this, "Vendor information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(vendor_account.this, "Failed to load vendor information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up edit button click listener
        editButton.setOnClickListener(v -> showEditDialog());

        // Set up logout button click listener
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    // Method to check internet availability
    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.vendor_dialog_edit_profile, null);
        builder.setView(dialogView);

        ImageView editProfileImage = dialogView.findViewById(R.id.edit_profile_image);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);
        EditText editFirstName = dialogView.findViewById(R.id.edit_firstname);
        EditText editLastName = dialogView.findViewById(R.id.edit_lastname);
        EditText editPhone = dialogView.findViewById(R.id.edit_phone);
        Spinner spinnerProvince = dialogView.findViewById(R.id.spinner_province);
        Spinner spinnerCity = dialogView.findViewById(R.id.spinner_city);
        Spinner spinnerBarangay = dialogView.findViewById(R.id.spinner_barangay);

        // Load existing vendor information
        vendorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vendor vendor = snapshot.getValue(Vendor.class);
                if (vendor != null) {
                    // Populate fields with current data
                    editFirstName.setText(vendor.firstName);
                    editLastName.setText(vendor.lastName);
                    editPhone.setText(vendor.phone);

                    // Set up province spinner
                    ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(vendor_account.this,
                            R.array.province_array, android.R.layout.simple_spinner_item);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(provinceAdapter);
                    int provincePosition = provinceAdapter.getPosition(vendor.province);
                    spinnerProvince.setSelection(provincePosition);

                    // Set up city spinner based on selected province
                    ArrayAdapter<CharSequence> cityAdapter = getCityAdapter(vendor.province);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);
                    int cityPosition = cityAdapter.getPosition(vendor.city);
                    spinnerCity.setSelection(cityPosition);

                    // Set up barangay spinner based on selected city
                    ArrayAdapter<CharSequence> barangayAdapter = getBarangayAdapter(vendor.city);
                    barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBarangay.setAdapter(barangayAdapter);
                    int barangayPosition = barangayAdapter.getPosition(vendor.barangay);
                    spinnerBarangay.setSelection(barangayPosition);

                    // Load existing profile image
                    if (vendor.profileImageUrl != null && !vendor.profileImageUrl.isEmpty()) {
                        Glide.with(vendor_account.this)
                                .load(vendor.profileImageUrl)
                                .placeholder(R.drawable.user1) // default image
                                .into(editProfileImage);
                    }
                } else {
                    Toast.makeText(vendor_account.this, "Vendor information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(vendor_account.this, "Failed to load vendor information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(vendor_account.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update vendor profile information
            vendorRef.child("firstName").setValue(newFirstName)
                    .addOnSuccessListener(aVoid -> {
                        vendorRef.child("lastName").setValue(newLastName);
                        vendorRef.child("phone").setValue(newPhone);
                        vendorRef.child("province").setValue(selectedProvince);
                        vendorRef.child("city").setValue(selectedCity);
                        vendorRef.child("barangay").setValue(selectedBarangay);
                        if (imageUri != null) {
                            // Upload new profile image
                            uploadImage(imageUri);
                        }
                        Toast.makeText(vendor_account.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(vendor_account.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
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


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        } else if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri uri) {
        if (uri != null) {
            StorageReference fileReference = storageRef;
            fileReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        String imageUrl = downloadUrl.toString();
                        vendorRef.child("profileImageUrl").setValue(imageUrl)
                                .addOnSuccessListener(aVoid -> Toast.makeText(vendor_account.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(vendor_account.this, "Failed to update image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(vendor_account.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(vendor_account.this, "No image selected", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(vendor_account.this, vendor_login.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_account.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
