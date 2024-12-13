package com.example.allgoods2024;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class vendor_signup extends AppCompatActivity {

    private EditText email, password, firstName, lastName, storeName, phone;
    private Spinner province, city, brgy;
    private Button sign_up;
    private ImageView profile, file, btn_back;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_PROFILE_IMAGE_REQUEST = 2;
    private Uri imageUri, profileImageUri;
    private ArrayAdapter<CharSequence> provinceAdapter, cityAdapter, brgyAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_signup);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("vendors");
        storageReference = FirebaseStorage.getInstance().getReference("vendor_images");

        // Initialize UI elements
        profile = findViewById(R.id.profile);
        file = findViewById(R.id.file);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        storeName = findViewById(R.id.storename);
        phone = findViewById(R.id.phone);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        brgy = findViewById(R.id.brgy);
        sign_up = findViewById(R.id.sign_up);
        btn_back = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);

        // Custom animation for EditText fields
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        firstName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        lastName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        storeName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animateView(v);
            }
        });

        // Set up Province Spinner
        provinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.province_array, R.layout.custom_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province.setAdapter(provinceAdapter);

        // Set up City Spinner (initially for Pangasinan)
        cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.city_array_pangasinan, R.layout.custom_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        // Set up Barangay Spinner (Initial setup, assuming default to Dagupan)
        updateBrgySpinner(R.array.brgy_array_dagupan_city);

        // City Spinner listener
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update barangay spinner based on selected city
                switch (position) {
                    case 0:
                        updateBrgySpinner(R.array.brgy_array_agno);
                        break;
                    case 1:
                        updateBrgySpinner(R.array.brgy_array_aguilar);
                        break;
                    case 2:
                        updateBrgySpinner(R.array.brgy_array_alaminos);
                        break;
                    case 3:
                        updateBrgySpinner(R.array.brgy_array_alcala);
                        break;
                    case 4:
                        updateBrgySpinner(R.array.brgy_array_anda);
                        break;
                    case 5:
                        updateBrgySpinner(R.array.brgy_array_asingan);
                        break;
                    case 6:
                        updateBrgySpinner(R.array.brgy_array_balungao);
                        break;
                    case 7:
                        updateBrgySpinner(R.array.brgy_array_bani);
                        break;
                    case 8:
                        updateBrgySpinner(R.array.brgy_array_basista);
                        break;
                    case 9:
                        updateBrgySpinner(R.array.brgy_array_bautista);
                        break;
                    case 10:
                        updateBrgySpinner(R.array.brgy_array_bayambang);
                        break;
                    case 11:
                        updateBrgySpinner(R.array.brgy_array_binalonan);
                        break;
                    case 12:
                        updateBrgySpinner(R.array.brgy_array_binmaley);
                        break;
                    case 13:
                        updateBrgySpinner(R.array.brgy_array_bolinao);
                        break;
                    case 14:
                        updateBrgySpinner(R.array.brgy_array_bugallon);
                        break;
                    case 15:
                        updateBrgySpinner(R.array.brgy_array_burgos);
                        break;
                    case 16:
                        updateBrgySpinner(R.array.brgy_array_calasiao);
                        break;
                    case 17:
                        updateBrgySpinner(R.array.brgy_array_dagupan_city);
                        break;
                    case 18:
                        updateBrgySpinner(R.array.brgy_array_dasol);
                        break;
                    case 19:
                        updateBrgySpinner(R.array.brgy_array_infanta);
                        break;
                    case 20:
                        updateBrgySpinner(R.array.brgy_array_labrador);
                        break;
                    case 21:
                        updateBrgySpinner(R.array.brgy_array_laoac);
                        break;
                    case 22:
                        updateBrgySpinner(R.array.brgy_array_lingayen);
                        break;
                    case 23:
                        updateBrgySpinner(R.array.brgy_array_mabini);
                        break;
                    case 24:
                        updateBrgySpinner(R.array.brgy_array_malasiqui);
                        break;
                    case 25:
                        updateBrgySpinner(R.array.brgy_array_manaoag);
                        break;
                    case 26:
                        updateBrgySpinner(R.array.brgy_array_mangaldan);
                        break;
                    case 27:
                        updateBrgySpinner(R.array.brgy_array_mangatarem);
                        break;
                    case 28:
                        updateBrgySpinner(R.array.brgy_array_mapandan);
                        break;
                    case 29:
                        updateBrgySpinner(R.array.brgy_array_natividad);
                        break;
                    case 30:
                        updateBrgySpinner(R.array.brgy_array_pozorrubio);
                        break;
                    case 31:
                        updateBrgySpinner(R.array.brgy_array_rosales);
                        break;
                    case 32:
                        updateBrgySpinner(R.array.brgy_array_san_carlos);
                        break;
                    case 33:
                        updateBrgySpinner(R.array.brgy_array_san_fabian);
                        break;
                    case 34:
                        updateBrgySpinner(R.array.brgy_array_san_jacinto);
                        break;
                    case 35:
                        updateBrgySpinner(R.array.brgy_array_san_manuel);
                        break;
                    case 36:
                        updateBrgySpinner(R.array.brgy_array_san_nicolas);
                        break;
                    case 37:
                        updateBrgySpinner(R.array.brgy_array_san_quintin);
                        break;
                    case 38:
                        updateBrgySpinner(R.array.brgy_array_santa_barbara);
                        break;
                    case 39:
                        updateBrgySpinner(R.array.brgy_array_santa_maria);
                        break;
                    case 40:
                        updateBrgySpinner(R.array.brgy_array_santo_tomas);
                        break;
                    case 41:
                        updateBrgySpinner(R.array.brgy_array_sison);
                        break;
                    case 42:
                        updateBrgySpinner(R.array.brgy_array_sual);
                        break;
                    case 43:
                        updateBrgySpinner(R.array.brgy_array_tayug);
                        break;
                    case 44:
                        updateBrgySpinner(R.array.brgy_array_umingan);
                        break;
                    case 45:
                        updateBrgySpinner(R.array.brgy_array_urbiztondo);
                        break;
                    case 46:
                        updateBrgySpinner(R.array.brgy_array_urdaneta_city);
                        break;
                    case 47:
                        updateBrgySpinner(R.array.brgy_array_villasis);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle image selection
        file.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        profile.setOnClickListener(v -> openFileChooser(PICK_PROFILE_IMAGE_REQUEST));

        // Handle register button click
        sign_up.setOnClickListener(v -> registerVendor());

        // Handle back button click
        btn_back.setOnClickListener(v -> {
            Intent back = new Intent(vendor_signup.this, buyer_login.class);
            startActivity(back);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        phone.setText("+63");
        // Disable editing the "+63" part
        phone.setSelection(phone.getText().length()); // Set cursor at the end of the text
        phone.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(13) // Limit input length to 13 characters (+63 plus 10 digits)
        });

        // Add a TextWatcher to restrict user input to only numbers after "+63"
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("+63")) {
                    phone.setText("+63");
                    phone.setSelection(phone.getText().length()); // Move cursor to end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if more than 13 characters are entered, if so, trim to 13
                if (s.length() > 13) {
                    s.delete(13, s.length());
                }
            }
        });
    }

    private void animateView(View view) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f);
        alphaAnimator.setDuration(300);
        alphaAnimator.start();

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f);
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f);
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();
    }

    // Method to update barangay spinner based on selected city
    private void updateBrgySpinner(int brgyArrayResource) {
        brgyAdapter = ArrayAdapter.createFromResource(this,
                brgyArrayResource, R.layout.custom_spinner_item);
        brgyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brgy.setAdapter(brgyAdapter);
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == PICK_PROFILE_IMAGE_REQUEST) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                file.setImageURI(imageUri);
            } else if (requestCode == PICK_PROFILE_IMAGE_REQUEST) {
                profileImageUri = data.getData();
                profile.setImageURI(profileImageUri);
            }
        }
    }

    private void registerVendor() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String storeNameText = storeName.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String provinceText = province.getSelectedItem().toString();
        String cityText = city.getSelectedItem().toString();
        String brgyText = brgy.getSelectedItem().toString();

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(firstNameText) ||
                TextUtils.isEmpty(lastNameText) || TextUtils.isEmpty(storeNameText) || TextUtils.isEmpty(phoneText) ||
                imageUri == null || profileImageUri == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(emailText)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(passwordText)) {
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create user with email and password
        auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    // Hide progress bar when the task is complete
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Send verification email
                        sendVerificationEmail();

                        // Get current user
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            StorageReference fileReference = storageReference.child(userId + ".jpg");
                            StorageReference profileFileReference = storageReference.child(userId + "_profile.jpg");

                            fileReference.putFile(imageUri).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        profileFileReference.putFile(profileImageUri).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                profileFileReference.getDownloadUrl().addOnSuccessListener(profileUri -> {
                                                    // Create vendor object with pending status
                                                    // Create vendor object with pending status and password
                                                    Vendor vendor = new Vendor(userId, emailText, firstNameText, lastNameText, storeNameText, phoneText, provinceText, cityText, brgyText, profileUri.toString(), uri.toString(), "pending", passwordText);

                                                    // Save vendor object to database
                                                    databaseReference.child(userId).setValue(vendor)
                                                            .addOnCompleteListener(task3 -> {
                                                                if (task3.isSuccessful()) {
                                                                    Toast.makeText(vendor_signup.this, "Successfully signed up! Please verify your email and wait for admin approval.", Toast.LENGTH_LONG).show();
                                                                    auth.signOut();
                                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(vendor_signup.this, "Failed to save vendor info", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                });
                                            } else {
                                                Toast.makeText(vendor_signup.this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    });
                                } else {
                                    Toast.makeText(vendor_signup.this, "Failed to upload permit image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(vendor_signup.this, "Failed to register", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to send verification email
    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(vendor_signup.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(vendor_signup.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to validate email
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Method to validate password strength
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check for letters and numbers only, no special characters
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        if (!pattern.matcher(password).matches()) {
            Toast.makeText(this, "Password can only contain letters and numbers, without spaces or symbols", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
