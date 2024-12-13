package com.example.allgoods2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class vendor_admin_messages extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String senderId, receiverId = "admin";
    private EditText messageEditText;
    private RecyclerView messagesRecyclerView;
    private ImageView sendButton, attachImageView, selectedImageView;
    private ProgressBar progressBar;
    private VendorAdminMessageAdapter vendorAdminMessageAdapter;
    private List<Message> messagesList = new ArrayList<>();
    private Uri imageUri;
    private String chatId;
    private ValueEventListener messageEventListener;

    private static final List<String> OFFENSIVE_WORDS = Arrays.asList("putangina", "gago", "tangina", "tarantado", "kantot", "burat", "pekpek", "titi", "bobo", "tanga", "ulol", "pangit", "bwisit", "kupal", "engot", "gunggong", "papatayin kita", "sasaksakin kita", "babarilin kita", "bugbugin kita", "unggoy", "baluga");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_admin_messages);

        // Set status bar color to black
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        attachImageView = findViewById(R.id.attachImageView);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        selectedImageView = findViewById(R.id.selectedImageView);
        progressBar = findViewById(R.id.progressBar);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        vendorAdminMessageAdapter = new VendorAdminMessageAdapter(this, messagesList, senderId);
        messagesRecyclerView.setAdapter(vendorAdminMessageAdapter);

        // Set senderId to the logged-in vendor's userId
        senderId = getIntent().getStringExtra("userId");

        // Fetch the chat ID
        fetchChatId();

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            if (chatId != null) {
                if (imageUri != null) {
                    uploadImage(); // Handle image upload
                } else {
                    sendMessage(); // Handle text message sending
                }
            } else {
                createNewChatAndSendMessage();
            }
        });

        // Attach image click listener
        attachImageView.setOnClickListener(v -> openImagePicker());
    }

    private void fetchChatId() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats_admin");
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
                Toast.makeText(vendor_admin_messages.this, "Failed to load chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewChatAndSendMessage() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats_admin");
        chatId = chatsRef.push().getKey();
        Chat chat = new Chat(senderId, receiverId);

        chatsRef.child(chatId).child("users").setValue(chat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (imageUri != null) {
                    uploadImage(); // Handle image upload
                } else {
                    sendMessage(); // Handle text message sending
                }
            } else {
                Toast.makeText(vendor_admin_messages.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
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
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_vendor_images");
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child(imageName);

            progressBar.setVisibility(View.VISIBLE);
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                sendMessage(uri.toString()); // Send the image URL
                progressBar.setVisibility(View.GONE);
                imageUri = null; // Clear the image URI after uploading
            })).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(vendor_admin_messages.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void sendMessage() {
        sendMessage(null); // Send message without image URL
    }

    private void sendMessage(String imageUrl) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats_admin").child(chatId).child("messages");
        String messageId = messagesRef.push().getKey();

        String messageText = messageEditText.getText().toString().trim();
        messageText = filterOffensiveWords(messageText);
        Message message = new Message(senderId, receiverId, "vendor", messageText, imageUrl, getCurrentTimestamp(), false);

        messagesRef.child(messageId).setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageEditText.setText("");
                selectedImageView.setVisibility(View.GONE);
            } else {
                Toast.makeText(vendor_admin_messages.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
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
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats_admin").child(chatId).child("messages");

        messageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        if (message.getCategory().equals("admin") && !message.getIsRead()) {
                            // Set isRead to true
                            snapshot.getRef().child("isRead").setValue(true);
                        }
                        messagesList.add(message);
                    }
                }
                vendorAdminMessageAdapter.notifyDataSetChanged();
                // Scroll to the bottom after updating the data
                messagesRecyclerView.post(() -> messagesRecyclerView.scrollToPosition(messagesList.size() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_admin_messages.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        };

        messagesRef.addValueEventListener(messageEventListener);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageEventListener != null) {
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats_admin").child(chatId).child("messages");
            messagesRef.removeEventListener(messageEventListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_admin_messages.this, vendor_account.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}