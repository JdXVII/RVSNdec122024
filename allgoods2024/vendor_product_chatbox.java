package com.example.allgoods2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class vendor_product_chatbox extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String senderId, receiverId, chatId, firstname, lastname, profileImageUrl;
    private EditText messageEditText;
    private RecyclerView messagesRecyclerView;
    private ImageView sendButton;
    private TextView firstNameTextView;
    private ImageView attachImageView;
    private ImageView selectedImageView;
    private VendorMessagesAdapter vendorMessagesAdapter;
    private List<Message> messagesList = new ArrayList<>();
    private Uri imageUri;
    private ValueEventListener messageEventListener;
    private ProgressBar progressBar;

    private static final List<String> OFFENSIVE_WORDS = Arrays.asList("putangina", "gago", "tangina", "tarantado", "kantot", "burat", "pekpek", "titi", "bobo", "tanga", "ulol", "pangit", "bwisit", "kupal", "engot", "gunggong", "papatayin kita", "sasaksakin kita", "babarilin kita", "bugbugin kita", "unggoy", "baluga");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_product_chatbox);

        // Set status bar color to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        senderId = getIntent().getStringExtra("senderId");
        receiverId = getIntent().getStringExtra("receiverId");
        firstname = getIntent().getStringExtra("firstName");
        lastname = getIntent().getStringExtra("lastName");
        profileImageUrl = getIntent().getStringExtra("profileImageUrl");

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        attachImageView = findViewById(R.id.attachImageView);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        firstNameTextView = findViewById(R.id.firstname);
        selectedImageView = findViewById(R.id.selectedImageView);
        progressBar = findViewById(R.id.progressBar);

        // Set store name
        firstNameTextView.setText(firstname + " " + lastname);

        // Load the profile image
        ImageView profileImageView = findViewById(R.id.profile);
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.user) // Optional placeholder image
                    .into(profileImageView);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Ensure new messages are at the bottom
        messagesRecyclerView.setLayoutManager(layoutManager);
        vendorMessagesAdapter = new VendorMessagesAdapter(this, messagesList, senderId);
        messagesRecyclerView.setAdapter(vendorMessagesAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatId != null) {
                    if (imageUri != null) {
                        uploadImage(); // Handle image upload
                    } else {
                        sendMessage(); // Handle text message sending
                    }
                } else {
                    createNewChatAndSendMessage();
                }
            }
        });

        attachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Fetch the chat ID initially
        fetchChatId();
    }

    private void fetchChatId() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatsRef.orderByChild("users/senderId").equalTo(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String existingReceiverId = snapshot.child("users/receiverId").getValue(String.class);
                    if (receiverId.equals(existingReceiverId)) {
                        chatId = snapshot.getKey();
                        fetchMessages(); // Fetch messages after we have chatId
                        return;
                    }
                }
                // If no existing chat found, set chatId to null
                chatId = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_product_chatbox.this, "Failed to load chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewChatAndSendMessage() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatId = chatsRef.push().getKey();
        Chat chat = new Chat(senderId, receiverId);

        // Only set the users node if it doesn't exist
        chatsRef.child(chatId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatsRef.child(chatId).child("users").setValue(chat).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (imageUri != null) {
                                uploadImage(); // Handle image upload
                            } else {
                                sendMessage(); // Handle text message sending
                            }
                        } else {
                            Toast.makeText(vendor_product_chatbox.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Proceed to send the message if the users node already exists
                    if (imageUri != null) {
                        uploadImage(); // Handle image upload
                    } else {
                        sendMessage(); // Handle text message sending
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_product_chatbox.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected. Ready to send.", Toast.LENGTH_SHORT).show();

            // Show a preview of the selected image
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Show the progress bar
            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_images");
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child(imageName);

            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessage(uri.toString()); // Send the image URL
                            imageUri = null;
                            progressBar.setVisibility(View.GONE); // Hide progress bar after success
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar in case of failure
                    Toast.makeText(vendor_product_chatbox.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessage() {
        // Send message with or without image URL
        sendMessage(null);
    }

    private void sendMessage(String imageUrl) {
        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        if (imageUrl != null) {
            // Send image message
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
            String messageId = messagesRef.push().getKey();

            Message message = new Message(senderId, receiverId, "vendor", null, imageUrl, getCurrentTimestamp(), false);
            messagesRef.child(messageId).setValue(message).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                imageUri = null;
                selectedImageView.setVisibility(View.GONE);
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(vendor_product_chatbox.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Send text message
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
                String messageId = messagesRef.push().getKey();
                messageText = filterOffensiveWords(messageText);

                Message message = new Message(senderId, receiverId, "vendor", messageText, null, getCurrentTimestamp(), false);
                messagesRef.child(messageId).setValue(message).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    messageEditText.setText("");
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(vendor_product_chatbox.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private String filterOffensiveWords(String message) {
        for (String word : OFFENSIVE_WORDS) {
            // Create a string of asterisks with the same length as the offensive word
            String asterisks = new String(new char[word.length()]).replace("\0", "*");
            // Use regex to replace each offensive word with the corresponding asterisks
            String pattern = Pattern.quote(word);
            message = message.replaceAll("(?i)" + pattern, asterisks); // (?i) makes it case insensitive
        }
        return message;
    }

    private void fetchMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");

        messageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        if (message.getCategory().equals("buyer") && !message.getIsRead()) {
                            // Set isRead to true
                            snapshot.getRef().child("isRead").setValue(true);
                        }
                        messagesList.add(message);
                    }
                }
                vendorMessagesAdapter.notifyDataSetChanged();
                // Scroll to the bottom after updating the data
                messagesRecyclerView.post(() -> messagesRecyclerView.scrollToPosition(messagesList.size() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_product_chatbox.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        };

        messagesRef.addValueEventListener(messageEventListener);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clear the image URI and hide the image preview
        imageUri = null;
        selectedImageView.setVisibility(View.GONE);

        // Remove any database listeners to avoid memory leaks
        if (messageEventListener != null) {
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
            messagesRef.removeEventListener(messageEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any database listeners
        if (messageEventListener != null) {
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
            messagesRef.removeEventListener(messageEventListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}