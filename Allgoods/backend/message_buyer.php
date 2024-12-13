<script type="module">
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-app.js";
  import { getDatabase, ref, onValue, get, push, set } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-database.js";
  import { getStorage, ref as storageRef, uploadBytesResumable, getDownloadURL } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-storage.js";

  const firebaseConfig = {
    apiKey: "AIzaSyCSx9Dx9f6fbwBvf8b02Wo8W6py5mFkpzI",
    authDomain: "allgoods2024-5163a.firebaseapp.com",
    databaseURL: "https://allgoods2024-5163a-default-rtdb.firebaseio.com",
    projectId: "allgoods2024-5163a",
    storageBucket: "allgoods2024-5163a.appspot.com",
    messagingSenderId: "980525971714",
    appId: "1:980525971714:web:78849efcc5ac2325a7ea1b"
  };

  const app = initializeApp(firebaseConfig);
  const database = getDatabase(app);
  const storage = getStorage(app);

  document.addEventListener("DOMContentLoaded", () => {
    const chatbox = document.getElementById("buyer-chatbox");
    const chatboxUser = document.getElementById("buyer-chatbox-user");
    const chatboxClose = document.getElementById("buyer-chatbox-close");
    const chatboxImg = document.getElementById("buyer-chatbox-img");
    const chatboxContent = document.querySelector(".buyer-chatbox-content");
    const chatboxMessageInput = document.getElementById("buyer-chatbox-message");
    const chatboxSendButton = document.getElementById("buyer-chatbox-send");
    const attachImageButton = document.getElementById("buyer-attach-image-icon");
    const attachImageInput = document.getElementById("buyer-attach-image");

    let selectedImageFile = null; 
    let currentBuyerId = null;
    let sessionId = ""; 

    let unreadMessages = {}; 
    let vendorsData = [];

    // Fetch vendors and set up listeners for new messages
    function fetchVendors() {
        const chatsRef = ref(database, "chats_admin");
        const vendorsFetched = new Set();

        onValue(chatsRef, (snapshot) => {
            snapshot.forEach((chatSnapshot) => {
                chatSnapshot.child("messages").forEach((messageSnapshot) => {
                    const messageData = messageSnapshot.val();
                    if (messageData.senderId && !vendorsFetched.has(messageData.senderId)) {
                        vendorsFetched.add(messageData.senderId);
                        const vendorRef = ref(database, "buyers/" + messageData.senderId);

                        get(vendorRef).then((vendorSnapshot) => {
                            const vendorData = vendorSnapshot.val();
                            if (vendorData) {
                                vendorsData.push(vendorData); // Add vendor data to the array
                                const nameItem = document.createElement("li");
                                nameItem.classList.add("buyer-name-item");
                                nameItem.setAttribute("data-name", vendorData.firstName + " " + vendorData.lastName);

                                // Initialize unread message count
                                unreadMessages[messageData.senderId] = 0;

                                nameItem.innerHTML = `
                                    <img src="${vendorData.profileImageUrl}" alt="${vendorData.storeName}" class="buyer-profile-img">
                                    <div class="user-info">
                                        <span class="buyer-user-name">${vendorData.firstName} ${vendorData.lastName}</span>
                                        <span class="buyer-user-type">Buyer</span>
                                        <span class="unread-count" style="display: none; color: red;">0</span>
                                    </div>
                                `;

                                // Append the vendor item to the list
                                document.querySelector(".buyer-name-list").appendChild(nameItem);

                                // Click event for the vendor
                                nameItem.addEventListener("click", () => {
                                    currentBuyerId = messageData.senderId; // Set the current vendorId
                                    sessionId = chatSnapshot.key; // Set the current sessionId
                                    chatboxUser.textContent = `${vendorData.firstName} ${vendorData.lastName}`;
                                    chatboxImg.src = vendorData.profileImageUrl;

                                    // Reset unread count when the chat is opened
                                    unreadMessages[currentBuyerId] = 0;
                                    updateUnreadCount(nameItem, unreadMessages[currentBuyerId]); 

                                    // Update isRead to true in database
                                    markMessagesAsRead(sessionId);

                                    fetchMessagesForUser(currentBuyerId, vendorData.profileImageUrl);
                                    chatbox.style.display = "block";
                                });

                                // Set up real-time listener for unread messages
                                setupUnreadMessageListener(chatSnapshot.key, messageData.senderId, nameItem);
                            }
                        });
                    }
                });
            });
        });
    }

    // Function to set up a listener for unread messages
    function setupUnreadMessageListener(sessionId, vendorId, nameItem) {
        const messagesRef = ref(database, "chats_admin/" + sessionId + "/messages");
        
        onValue(messagesRef, (snapshot) => {
            let unreadCount = 0;

            snapshot.forEach((messageSnapshot) => {
                const messageData = messageSnapshot.val();
                if (messageData.senderId === vendorId && !messageData.isRead) {
                    unreadCount++;
                }
            });

            // Update unread message count
            if (unreadCount > 0) {
                unreadMessages[vendorId] = unreadCount;
                updateUnreadCount(nameItem, unreadMessages[vendorId]);
                moveToTop(nameItem);
            } else {
                unreadMessages[vendorId] = 0;
                updateUnreadCount(nameItem, unreadMessages[vendorId]);
            }
        });
    }

    // Function to update the unread count display
    function updateUnreadCount(nameItem, count) {
        const unreadCountElement = nameItem.querySelector('.unread-count');
        unreadCountElement.textContent = count;
        unreadCountElement.style.display = count > 0 ? 'block' : 'none'; // Show count if > 0
    }

    // Move the vendor item to the top of the list if they have unread messages
    function moveToTop(nameItem) {
        const nameList = document.querySelector(".buyer-name-list");
        if (nameItem !== nameList.firstChild) {
            nameList.insertBefore(nameItem, nameList.firstChild); // Move to top
        }
    }

    // Update the messages as read
    function markMessagesAsRead(sessionId) {
        const messagesRef = ref(database, "chats_admin/" + sessionId + "/messages");

        onValue(messagesRef, (snapshot) => {
            snapshot.forEach((messageSnapshot) => {
                const messageData = messageSnapshot.val();
                if (messageData.receiverId === "admin" && messageData.isRead === false) {
                    // Update isRead status in the database
                    const messageKey = messageSnapshot.key;
                    set(ref(database, "chats_admin/" + sessionId + "/messages/" + messageKey), {
                        ...messageData,
                        isRead: true, // Mark as read
                    });
                }
            });
        });
    }

    function fetchMessagesForUser(vendorId, profileImageUrl) {
      chatboxContent.innerHTML = ""; // Clear previous messages
      const messagesRef = ref(database, "chats_admin/" + sessionId + "/messages");

      onValue(messagesRef, (snapshot) => {
        chatboxContent.innerHTML = ""; // Reset content to avoid duplication
        snapshot.forEach((messageSnapshot) => {
          const messageData = messageSnapshot.val();

          const messageWrapper = document.createElement("div");
          const messageContent = document.createElement("div");
          const messageDate = document.createElement("div");

          // Check if it's a vendor's message
          if (messageData.senderId === vendorId && messageData.category === "buyer") {
            messageWrapper.classList.add("buyer-message-wrapper", "received");
            const profileImg = document.createElement("img");
            profileImg.src = profileImageUrl;
            profileImg.alt = "Profile Image";
            profileImg.classList.add("buyer-profile-img");
            messageWrapper.appendChild(profileImg);
          } else {
            // Admin's message
            messageWrapper.classList.add("buyer-message-wrapper", "sent");
          }

          messageContent.classList.add("buyer-message-content");
          messageContent.innerText = messageData.text;
          messageWrapper.appendChild(messageContent);

          messageDate.classList.add("buyer-message-date");
          messageDate.innerText = new Date(messageData.timestamp).toLocaleString();
          messageWrapper.appendChild(messageDate);

          if (messageData.image) {
            const messageImg = document.createElement("img");
            messageImg.src = messageData.image;
            messageImg.alt = "Message Image";
            messageImg.classList.add("buyer-message-img");

            messageImg.addEventListener("click", () => {
              openFullScreen(messageImg.src);
            });

            messageContent.appendChild(messageImg);
          }

          chatboxContent.appendChild(messageWrapper);
        });

        // Scroll to the latest message after messages are loaded
        setTimeout(() => {
          chatboxContent.scrollTop = chatboxContent.scrollHeight;
        }, 100); // Timeout to ensure all messages are rendered before scrolling
      });
    }

    // Open image in fullscreen on click
    function openFullScreen(imageSrc) {
      const fullScreenImg = document.createElement("img");
      fullScreenImg.src = imageSrc;
      fullScreenImg.style.width = "100vw";
      fullScreenImg.style.height = "100vh";
      fullScreenImg.style.objectFit = "contain";
      fullScreenImg.style.position = "fixed";
      fullScreenImg.style.top = "0";
      fullScreenImg.style.left = "0";
      fullScreenImg.style.zIndex = "1000";
      fullScreenImg.style.cursor = "pointer";

      fullScreenImg.addEventListener("click", () => {
        document.body.removeChild(fullScreenImg);
      });

      document.body.appendChild(fullScreenImg);
    }

    // Event listener for sending messages 
    chatboxSendButton.addEventListener("click", () => {
      const messageText = chatboxMessageInput.value.trim();
      if (messageText || selectedImageFile) {
        sendMessage(messageText, selectedImageFile);
        chatboxMessageInput.value = ""; 
        selectedImageFile = null; 
      }
    });

    // Handle image attachment
    attachImageButton.addEventListener("click", () => {
      attachImageInput.click();
    });

    attachImageInput.addEventListener("change", (event) => {
      selectedImageFile = event.target.files[0];
    });

    // Function to send the message
    function sendMessage(messageText, imageFile) {
      const messagesRef = ref(database, "chats_admin/" + sessionId + "/messages");
      const newMessageRef = push(messagesRef);
      const messageId = newMessageRef.key;

      const messageData = {
        messageId: messageId,
        category: "admin",
        isRead: false,
        receiverId: currentBuyerId,
        senderId: "admin", // Sender is admin
        text: messageText || "",
        timestamp: new Date().toISOString(),
      };

      if (imageFile) {
        uploadImage(imageFile, messageId).then((imageUrl) => {
          messageData.image = imageUrl;
          set(newMessageRef, messageData); // Save the message with image URL
        });
      } else {
        set(newMessageRef, messageData); // Save the message without an image
      }
    }

    function uploadImage(imageFile, messageId) {
      const storageRefInstance = storageRef(storage, "chat_buyer_images/" + messageId);
      const uploadTask = uploadBytesResumable(storageRefInstance, imageFile);

      return new Promise((resolve, reject) => {
        uploadTask.on(
          "state_changed",
          null,
          (error) => {
            console.error("Error uploading image:", error);
            reject(error);
          },
          () => {
            getDownloadURL(uploadTask.snapshot.ref).then((downloadURL) => {
              resolve(downloadURL);
            });
          }
        );
      });
    }

    chatboxClose.addEventListener("click", () => {
      chatbox.style.display = "none";
    });

    // Search functionality
    const searchInput = document.getElementById("buyer-search-input");
    searchInput.addEventListener("input", (event) => {
      const searchTerm = event.target.value.toLowerCase();
      filterVendors(searchTerm);
    });

    function filterVendors(searchTerm) {
      const buyerNameList = document.querySelector(".buyer-name-list");
      buyerNameList.innerHTML = ""; // Clear the list before filtering

      vendorsData.forEach(vendor => {
        const fullName = `${vendor.firstName} ${vendor.lastName}`.toLowerCase();
        if (fullName.includes(searchTerm)) {
          const nameItem = document.createElement("li");
          nameItem.classList.add("buyer-name-item");
          nameItem.setAttribute("data-name", fullName);

          nameItem.innerHTML = `
            <img src="${vendor.profileImageUrl}" alt="${vendor.storeName}" class="buyer-profile-img">
            <div class="buyer-user-info">
              <span class="buyer-user-name">${vendor.firstName} ${vendor.lastName}</span>
              <span class="buyer-user-type">Buyer</span>
            </div>
          `;
          buyerNameList.appendChild(nameItem);

          // Add click event listener to open chat with the selected vendor
          nameItem.addEventListener("click", () => {
            currentBuyerId = vendor.id; // Set the current vendorId
            chatboxUser.textContent = `${vendor.firstName} ${vendor.lastName}`;
            chatboxImg.src = vendor.profileImageUrl;
            fetchMessagesForUser(currentBuyerId, vendor.profileImageUrl);
            chatbox.style.display = "block";
          });
        }
      });
    }

    fetchVendors();
  });
</script>