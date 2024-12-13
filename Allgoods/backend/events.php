<script type="module">
    // Import necessary Firebase modules
    import { initializeApp } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-app.js";
    import { getDatabase, ref, set, push, onValue, remove, update, query, orderByChild, equalTo } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-database.js";
    import { getStorage, ref as storageRef, uploadBytes, getDownloadURL, deleteObject } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-storage.js";

    // Firebase Configuration
    const firebaseConfig = {
        apiKey: "AIzaSyCSx9Dx9f6fbwBvf8b02Wo8W6py5mFkpzI",
        authDomain: "allgoods2024-5163a.firebaseapp.com",
        databaseURL: "https://allgoods2024-5163a-default-rtdb.firebaseio.com",
        projectId: "allgoods2024-5163a",
        storageBucket: "allgoods2024-5163a.appspot.com",
        messagingSenderId: "980525971714",
        appId: "1:980525971714:web:72de5c59d2c84d28a7ea1b"
    };

    // Initialize Firebase
    const app = initializeApp(firebaseConfig);
    const db = getDatabase(app);
    const storage = getStorage(app);

    // Select form elements
    const eventForm = document.getElementById("eventForm");
    const imageInput = document.getElementById("images");
    const imagePreviewContainer = document.getElementById("imagePreviewContainer");
    const eventList = document.getElementById("eventList");

    // Select buttons
    const uploadButton = document.getElementById("uploadButton");
    const updateButton = document.getElementById("updateButton");
    const cancelButton = document.getElementById("cancelButton");

    const progressBarContainer = document.getElementById("progressBarContainer");
    const progressBar = document.getElementById("progressBar");

    let currentEditingEventId = null;

    // Show progress bar
    function showProgressBar() {
        progressBarContainer.style.display = "block";
        progressBar.style.width = "0%";
    }

    // Update progress bar
    function updateProgressBar(progress) {
        progressBar.style.width = `${progress}%`;
    }

    // Hide progress bar
    function hideProgressBar() {
        progressBarContainer.style.display = "none";
    }

    // Image preview functionality
    imageInput.addEventListener("change", () => {
        const files = imageInput.files;

        // Clear previous previews
        imagePreviewContainer.innerHTML = "";

        // Limit images to a maximum of 5
        if (files.length > 5) {
            alert("You can upload a maximum of 5 images.");
            imageInput.value = ""; // Reset input
            return;
        }

        // Generate previews for selected images
        Array.from(files).forEach((file) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = document.createElement("img");
                img.src = e.target.result;
                img.alt = "Image Preview";
                img.className = "preview-image";
                imagePreviewContainer.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
    });

    async function uploadImages(images) {
        const imageUrls = [];
        const totalImages = images.length;

        for (let i = 0; i < totalImages; i++) {
            const image = images[i];
            const imageRef = storageRef(storage, `event_images/${Date.now()}_${image.name}`);
            const uploadTask = await uploadBytes(imageRef, image);
            const downloadUrl = await getDownloadURL(uploadTask.ref);
            imageUrls.push(downloadUrl);

            const progress = Math.round(((i + 1) / totalImages) * 100);
            updateProgressBar(progress);
        }

        return imageUrls;
    }

    // Event listener for form submission (to handle both add and edit)
    eventForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        // Get form input values
        const title = document.getElementById("title").value;
        const category = document.getElementById("eventCategory").value;
        const discount = document.getElementById("discount").value;
        const description = document.getElementById("description").value;
        const eventDate = document.getElementById("eventDate").value;
        const images = imageInput.files;

        // Validate image selection
        if (images.length > 5) {
            alert("You can upload a maximum of 5 images.");
            return;
        }

         // Confirmation for upload or update
        const confirmationMessage = currentEditingEventId 
            ? "Are you sure you want to update this event?" 
            : "Are you sure you want to upload this event?";
        if (!confirm(confirmationMessage)) return;

        showProgressBar();

        const newImageUrls = await uploadImages(images);

        if (currentEditingEventId) {
            const eventRef = ref(db, `events/${currentEditingEventId}`);
            const snapshot = await new Promise((resolve) => onValue(eventRef, resolve, { onlyOnce: true }));
            const oldImages = snapshot.val()?.images || [];

            const imagesToDelete = oldImages.filter((url) => !newImageUrls.includes(url));
            for (const url of imagesToDelete) {
                const imageRef = storageRef(storage, url);
                await deleteObject(imageRef);
            }

            await set(eventRef, {
                title,
                eventDate,
                timestamp: new Date().toISOString(),
                event_category: category,
                discount,
                description,
                images: newImageUrls,
                isActive: true,
                
            });

            alert("Event updated successfully!");
        } else {
            const newEventRef = push(ref(db, "events"));
            await set(newEventRef, {
                title,
                eventDate,
                timestamp: new Date().toISOString(),
                event_category: category,
                discount,
                description,
                images: newImageUrls,
                isActive: true,
            });

            alert("Event uploaded successfully!");
        }

        // Reset form and clear previews
        hideProgressBar();
        eventForm.reset();
        imagePreviewContainer.innerHTML = "";
        currentEditingEventId = null;

        // Hide update and cancel buttons, show upload button again
        uploadButton.style.display = "block";
        updateButton.style.display = "none";
        cancelButton.style.display = "none";

        fetchEvents(); // Refresh the event list
    });

    function fetchEvents() {
        onValue(ref(db, "events"), (snapshot) => {
            eventList.innerHTML = ""; // Clear current list
    
            if (snapshot.exists()) {
                // If there are events, disable the Upload Event button
                uploadButton.disabled = true;
                uploadButton.textContent = "Event Already Uploaded";
                uploadButton.style.backgroundColor = "#ccc"; // Optional styling for disabled state
                uploadButton.style.cursor = "not-allowed";
            } else {
                // If no events exist, enable the Upload Event button
                uploadButton.disabled = false;
                uploadButton.textContent = "Upload Event";
                uploadButton.style.backgroundColor = ""; // Reset styling
                uploadButton.style.cursor = "pointer";
            }
    
            snapshot.forEach((childSnapshot) => {
                const event = childSnapshot.val();
                const eventId = childSnapshot.key;
    
                // Create a card for each event
                const eventCard = document.createElement("div");
                eventCard.className = "event-card";
    
                // Create images HTML
                const imagesHtml = event.images
                    .map((url) => `<img src="${url}" alt="Event Image" class="event-image">`)
                    .join("");
    
                // Set the card content
                eventCard.innerHTML = `
                    <div>
                        <strong style="font-size:20px;">${event.title}</strong><br>
                        <strong style="color:#4B5966;">Category:</strong> ${event.event_category}<br>
                        <strong style="color:#4B5966;">Discount:</strong> ${event.discount}%<br>
                        <strong style="color:#4B5966;">Description:</strong> ${event.description}<br>
                        <strong style="color:#4B5966;">Date:</strong> ${event.eventDate}<br>
                        <div class="event-images">${imagesHtml}</div>
                    </div>
                    <div class="event-icons">
                        <i class="bx bxs-edit" data-event-id="${eventId}">Edit</i>
                    <i class="bx bxs-trash" style="color:#d52424de;" data-event-id="${eventId}" data-images='${JSON.stringify(event.images)}'>Delete</i>
                    </div>
                `;
    
                // Add the card to the list
                eventList.appendChild(eventCard);
            });
    
            // Add event listeners for edit and delete buttons
            document.querySelectorAll('.bxs-edit').forEach(editButton => {
                editButton.addEventListener('click', function() {
                    const eventId = this.getAttribute('data-event-id');
                    editEvent(eventId);
                });
            });
    
            document.querySelectorAll('.bxs-trash').forEach(deleteButton => {
                deleteButton.addEventListener('click', function() {
                    const eventId = this.getAttribute('data-event-id');
                    const imageUrls = JSON.parse(this.getAttribute('data-images'));
                    deleteEvent(eventId, imageUrls);
                });
            });
        });
    }
    

    // Function to edit an event
    function editEvent(eventId) {
        currentEditingEventId = eventId;
        const eventRef = ref(db, `events/${eventId}`);

        // Fetch the event data
        onValue(eventRef, (snapshot) => {
            const event = snapshot.val();

            // Populate the form with the event data
            document.getElementById("title").value = event.title;
            document.getElementById("eventCategory").value = event.event_category;
            document.getElementById("discount").value = event.discount;
            document.getElementById("description").value = event.description;
            document.getElementById("eventDate").value = event.eventDate;

            // Populate image previews
            imagePreviewContainer.innerHTML = "";
            event.images.forEach((url) => {
                const img = document.createElement("img");
                img.src = url;
                img.className = "preview-image";
                imagePreviewContainer.appendChild(img);
            });

            // Show update and cancel buttons, hide upload button
            uploadButton.style.display = "none";
            updateButton.style.display = "block";
            cancelButton.style.display = "block";
        });
    }

    // Function to delete an event
    async function deleteEvent(eventId, imageUrls) {
        if (confirm("Are you sure you want to delete this event?")) {
            // Delete the event data from Realtime Database
            const eventRef = ref(db, `events/${eventId}`);
            await remove(eventRef);

            // Delete images from Firebase Storage
            imageUrls.forEach(async (url) => {
                const imageRef = storageRef(storage, url);
                await deleteObject(imageRef);
            });

            await deleteEventAndUpdateProducts(eventId);

            alert("Event deleted successfully!");
            fetchEvents(); // Refresh the event list
        }
    }

    // Cancel the edit operation
    cancelButton.addEventListener("click", () => {
        // Reset the form and hide the update/cancel buttons
        eventForm.reset();
        imagePreviewContainer.innerHTML = "";
        currentEditingEventId = null;

        // Hide update and cancel buttons, show upload button again
        uploadButton.style.display = "block";
        updateButton.style.display = "none";
        cancelButton.style.display = "none";
    });

    // Initialize by fetching events
    fetchEvents();


    // Function to check and delete past events
    async function deleteExpiredEvents() {
        const db = getDatabase();
        const eventsRef = ref(db, "events");
        const currentDate = new Date().toISOString(); // Get current date and time in ISO format
    
        // Fetch all events
        onValue(eventsRef, async (snapshot) => {
            if (snapshot.exists()) {
                snapshot.forEach(async (childSnapshot) => {
                    const event = childSnapshot.val();
                    const eventId = childSnapshot.key;
    
                    // Compare eventDate with current date
                    if (event.eventDate <= currentDate) {
                        // Event has passed, delete the event and associated products
                        await deleteEventAndUpdateProducts(eventId);
                    }
                });
            }
        });
    }
    
    // Function to delete event and update associated products
    async function deleteEventAndUpdateProducts(eventId) {
        const db = getDatabase();
        const eventRef = ref(db, `events/${eventId}`);
    
        // Delete the event from events table
        await remove(eventRef);
        console.log(`Event with ID ${eventId} deleted`);
    
        // Update the associated products to set 'event' to false
        const productsRef = ref(db, "upload_products");
        const productsQuery = query(productsRef, orderByChild("event"), equalTo(true));
    
        // Fetch products linked to the deleted event
        onValue(productsQuery, async (snapshot) => {
            if (snapshot.exists()) {
                snapshot.forEach(async (productSnapshot) => {
                    const productId = productSnapshot.key;
    
                    // Update the 'event' field to false
                    await update(ref(db, `upload_products/${productId}`), { event: false });
                    console.log(`Product with ID ${productId} updated to remove event link`);
                });
            }
        });
    }
    
    // Call the function to check for expired events
    deleteExpiredEvents();

</script>
