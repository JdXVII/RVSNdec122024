package com.example.allgoods2024;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class vendor_archive extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button uploadPermitButton, uploadFinalButton;
    private PhotoView pendingPermitImage, approvedPermitImage;
    private TextView pendingPermitDate, approvedPermitDate;
    private ProgressBar uploadProgressBar;
    private LinearLayout pendingPermitContainer;
    private ImageView bckperms;

    private RecyclerView uploadHistoryRecyclerView;
    private ArchiveAdapter archiveAdapter;
    private List<ArchivedPermit> archivedPermitList;

    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_archive);

        // Set status bar color to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bckperms = findViewById(R.id.bckperms);

        bckperms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckperms = new Intent(vendor_archive.this, vendor_account.class);
                startActivity(bckperms);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        uploadPermitButton = findViewById(R.id.uploadPermitButton);
        uploadFinalButton = findViewById(R.id.uploadFinalButton);
        pendingPermitImage = findViewById(R.id.pendingPermitImage);
        pendingPermitDate = findViewById(R.id.pendingPermitDate);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        pendingPermitContainer = findViewById(R.id.pendingPermitContainer);
        approvedPermitDate = findViewById(R.id.approvedPermitDate);
        approvedPermitImage = findViewById(R.id.approvedPermitImage);

        // Initialize Firebase
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("permits_pending");
        storageReference = FirebaseStorage.getInstance().getReference("permits_pending");

        // Hide the pending container initially
        pendingPermitContainer.setVisibility(View.GONE);

        // Fetch and display the pending permit data
        fetchPendingPermit();

        // Fetch and display the approved permit details
        fetchApprovedPermit();

        approvedPermitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current user ID
                String userId = currentUserId; // Assuming you have the current user ID set up already

                // Reference to the vendors node in the Firebase database
                DatabaseReference vendorReference = FirebaseDatabase.getInstance().getReference("vendors").child(userId);

                // Fetch the permitImageUrl for the current user
                vendorReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Retrieve the permitImageUrl from the database
                            String permitImageUrl = dataSnapshot.child("permitImageUrl").getValue(String.class);

                            if (permitImageUrl != null) {
                                // Start the image preview activity with the retrieved URL
                                Intent intent = new Intent(vendor_archive.this, image_preview.class);
                                intent.putExtra("imageUrl", permitImageUrl);
                                startActivity(intent);
                            } else {
                                // Handle the case where the URL is not available
                                Toast.makeText(vendor_archive.this, "Permit image URL not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle the case where the user data does not exist
                            Toast.makeText(vendor_archive.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors while fetching data
                        Toast.makeText(vendor_archive.this, "Failed to retrieve image: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Select image from storage
        uploadPermitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        // Upload image to Firebase
        uploadFinalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase();
            }
        });

        // Initialize RecyclerView
        uploadHistoryRecyclerView = findViewById(R.id.uploadHistoryRecyclerView);
        uploadHistoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        archivedPermitList = new ArrayList<>();
        archiveAdapter = new ArchiveAdapter(archivedPermitList, this);
        uploadHistoryRecyclerView.setAdapter(archiveAdapter);

        fetchArchivedPermits();
    }

    // Open file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Preview the selected image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                pendingPermitImage.setImageBitmap(bitmap);

                // Show the container for preview
                pendingPermitContainer.setVisibility(View.VISIBLE);

                // Update date for preview
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                pendingPermitDate.setText("Date: " + currentDate);

                // Show the upload button after the preview
                uploadFinalButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Show progress bar
            uploadProgressBar.setVisibility(View.VISIBLE);

            // Unique ID for the image (optional, if you need it for image storage)
            String fileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageReference.child(fileName);

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get URL of uploaded image
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Get current date
                                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                    // Store image details in database under the current userId
                                    String status = "pending";
                                    Map<String, Object> permitData = new HashMap<>();
                                    permitData.put("userId", currentUserId);
                                    permitData.put("imageUrl", imageUrl);
                                    permitData.put("date", currentDate);
                                    permitData.put("status", status);

                                    // Update the permits_pending node under current user's ID
                                    databaseReference.child(currentUserId).setValue(permitData);

                                    // Update UI
                                    pendingPermitDate.setText("Date: " + currentDate);
                                    Glide.with(vendor_archive.this).load(imageUrl).into(pendingPermitImage);

                                    uploadProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(vendor_archive.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    uploadFinalButton.setVisibility(View.GONE);  // Hide upload button after upload
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadProgressBar.setVisibility(View.GONE);
                            Toast.makeText(vendor_archive.this, "Failed to upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch pending permit from Firebase based on userId
    private void fetchPendingPermit() {
        databaseReference.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot permitSnapshot : dataSnapshot.getChildren()) {
                        String imageUrl = permitSnapshot.child("imageUrl").getValue(String.class);
                        String date = permitSnapshot.child("date").getValue(String.class);

                        // Show pending permit data in UI
                        pendingPermitContainer.setVisibility(View.VISIBLE);
                        pendingPermitDate.setText("Date: " + date);
                        Glide.with(vendor_archive.this).load(imageUrl).into(pendingPermitImage);
                    }
                } else {
                    // No pending permits found for the current user
                    pendingPermitContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_archive.this, "Failed to load pending permit: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // Fetch approved permit from Firebase
    private void fetchApprovedPermit() {
        DatabaseReference vendorReference = FirebaseDatabase.getInstance().getReference("vendors").child(currentUserId);

        vendorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve permitImageUrl and expirationDate from the database
                    String approvedImageUrl = dataSnapshot.child("permitImageUrl").getValue(String.class);
                    String expirationDate = dataSnapshot.child("expirationDate").getValue(String.class);

                    // Update the UI with the fetched data
                    if (approvedImageUrl != null) {
                        Glide.with(vendor_archive.this).load(approvedImageUrl).into(approvedPermitImage);
                    }
                    if (expirationDate != null) {
                        approvedPermitDate.setText("Date: " + expirationDate);
                    }
                } else {
                    Toast.makeText(vendor_archive.this, "No approved permit found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_archive.this, "Failed to load approved permit: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchArchivedPermits() {
        DatabaseReference archiveReference = FirebaseDatabase.getInstance().getReference("archive");

        // Query based on the userId
        archiveReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                archivedPermitList.clear(); // Clear existing permits

                if (dataSnapshot.exists()) {
                    for (DataSnapshot permitSnapshot : dataSnapshot.getChildren()) {
                        // Fetch data from each unique permit entry under userId
                        String oldPermitImageUrl = permitSnapshot.child("oldPermitImageUrl").getValue(String.class);
                        String oldExpirationDate = permitSnapshot.child("oldExpirationDate").getValue(String.class);

                        // Add to the list if both fields exist
                        if (oldPermitImageUrl != null && oldExpirationDate != null) {
                            ArchivedPermit archivedPermit = new ArchivedPermit(oldPermitImageUrl, oldExpirationDate);
                            archivedPermitList.add(archivedPermit); // Add the permit to the list
                        }
                    }

                    // Reverse the list to display the most recent permits first
                    Collections.reverse(archivedPermitList);

                    archiveAdapter.notifyDataSetChanged(); // Notify adapter to refresh the list
                } else {
                    Toast.makeText(vendor_archive.this, "No archived permits found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_archive.this, "Failed to load archived permits: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_archive.this, vendor_account.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
