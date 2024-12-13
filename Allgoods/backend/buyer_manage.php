<script type="module">
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";
    import { getDatabase, ref, onValue, update, remove } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js";
    import { getAuth, signInWithEmailAndPassword, deleteUser } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
    import { getStorage, ref as storageRef, deleteObject } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-storage.js";

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
    const auth = getAuth(app);
    const storage = getStorage(app);

    emailjs.init('U7K8sg-mpV38W9Vtm'); // EmailJS user ID

    const buyersRef = ref(database, 'buyers');

    // Function to approve buyer
    window.approveBuyer = function(buyerId, login_email) {
        if (!confirm("Are you sure you want to approve this vendor?")) {
            return;
        }
        const buyerRef = ref(database, 'buyers/' + buyerId);
        const currentTime = new Date().toISOString(); // Get current timestamp
        update(buyerRef, { status: 'approved', approvedAt: currentTime }) // Add approvedAt timestamp
            .then(() => {
                alert('Buyer approved');
                sendApprovalEmail(login_email);
            })
            .catch((error) => {
                console.error('Error approving buyer: ', error);
            });
    }


    window.declineBuyer = function(buyerId, login_email, profileImageUrl, valididImageUrl) {
        if (!confirm("Are you sure you want to decline this buyer?")) {
            return; 
        }
    
        // Create modal for reason selection
        const modal = document.createElement('div');
        modal.style.position = 'fixed';
        modal.style.top = '0';
        modal.style.left = '0';
        modal.style.width = '100%';
        modal.style.height = '100%';
        modal.style.backgroundColor = 'rgba(0,0,0,0.5)';
        modal.style.display = 'flex';
        modal.style.alignItems = 'center';
        modal.style.justifyContent = 'center';
        modal.style.zIndex = '9999';
    
        const modalContent = document.createElement('div');
        modalContent.style.backgroundColor = 'white';
        modalContent.style.padding = '20px';
        modalContent.style.borderRadius = '10px';
        modalContent.style.boxShadow = '0px 4px 6px rgba(0,0,0,0.1)';
        modalContent.style.width = '400px';
        modalContent.style.textAlign = 'center';
    
        const title = document.createElement('h2');
        title.textContent = 'Select Decline Reason';
        title.style.marginBottom = '15px';
    
        const dropdown = document.createElement('select');
        dropdown.style.marginBottom = '15px';
        dropdown.style.padding = '10px';
        dropdown.style.width = '100%';
        dropdown.style.fontSize = '1rem';
    
        const reasons = ['Invalid or Incorrect ID Submission', 'Inappropriate Behavior in Past Accounts', 'Mismatched Information', 'Duplicate Account', 'Inappropriate Profile Picture', 'Others'];
        reasons.forEach((reason) => {
            const option = document.createElement('option');
            option.value = reason;
            option.textContent = reason;
            dropdown.appendChild(option);
        });
    
        const otherReasonInput = document.createElement('input');
        otherReasonInput.type = 'text';
        otherReasonInput.placeholder = 'Specify reason';
        otherReasonInput.style.display = 'none'; 
        otherReasonInput.style.marginTop = '10px';
        otherReasonInput.style.padding = '10px';
        otherReasonInput.style.width = '100%';
        otherReasonInput.style.border = '1px solid #ccc';
        otherReasonInput.style.borderRadius = '5px';
    
        dropdown.addEventListener('change', () => {
            if (dropdown.value === 'Others') {
                otherReasonInput.style.display = 'block';
            } else {
                otherReasonInput.style.display = 'none';
            }
        });
    
        const buttonContainer = document.createElement('div');
        buttonContainer.style.display = 'flex';
        buttonContainer.style.justifyContent = 'space-between';
        buttonContainer.style.marginTop = '20px';
    
        const submitBtn = document.createElement('button');
        submitBtn.textContent = 'Decline';
        submitBtn.style.backgroundColor = '#f44336';
        submitBtn.style.color = 'white';
        submitBtn.style.border = 'none';
        submitBtn.style.borderRadius = '5px';
        submitBtn.style.padding = '10px 20px';
        submitBtn.style.cursor = 'pointer';
    
        const cancelBtn = document.createElement('button');
        cancelBtn.textContent = 'Cancel';
        cancelBtn.style.backgroundColor = '#ccc';
        cancelBtn.style.color = '#333';
        cancelBtn.style.border = 'none';
        cancelBtn.style.borderRadius = '5px';
        cancelBtn.style.padding = '10px 20px';
        cancelBtn.style.cursor = 'pointer';
    
        cancelBtn.onclick = () => document.body.removeChild(modal);
    
        submitBtn.onclick = function() {
            const selectedReason = dropdown.value === 'Others' ? otherReasonInput.value : dropdown.value;
    
            if (!selectedReason || (dropdown.value === 'Others' && !otherReasonInput.value.trim())) {
                alert('Please provide a valid reason.');
                return;
            }
    
            sendEmail(selectedReason, login_email);
    
            const buyerRef = ref(database, 'buyers/' + buyerId);
    
            // Continue with the rest of the decline logic
            onValue(buyerRef, (snapshot) => {
                const buyer = snapshot.val();
                if (buyer) {
                    const password = buyer.password;
    
                    signInWithEmailAndPassword(auth, login_email, password)
                        .then(() => deleteUser(auth.currentUser))
                        .then(() => remove(buyerRef))
                        .then(() => {
                            if (profileImageUrl) {
                                const profileImageRef = storageRef(storage, profileImageUrl);
                                return deleteObject(profileImageRef);
                            }
                        })
                        .then(() => {
                            if (valididImageUrl) {
                                const valididImageRef = storageRef(storage, valididImageUrl);
                                return deleteObject(valididImageRef);
                            }
                        })
                        .then(() => {
                            alert('Buyer declined and data successfully deleted.');
                            document.body.removeChild(modal);
                        })
                        .catch((error) => {
                            console.error('Error during the decline process:', error);
                            alert('Error declining buyer: ' + error.message);
                        });
                } else {
                    alert('Buyer data found.');
                }
            });
        };
    
        modalContent.appendChild(title);
        modalContent.appendChild(dropdown);
        modalContent.appendChild(otherReasonInput);
        buttonContainer.appendChild(submitBtn);
        buttonContainer.appendChild(cancelBtn);
        modalContent.appendChild(buttonContainer);
        modal.appendChild(modalContent);
        document.body.appendChild(modal);
    };

    // Function to zoom permit image
    window.zoomPermitImage = function(img) {
        // Implement zoom functionality here, e.g., opening the image in a modal
        const modal = document.createElement('div');
        modal.style.position = 'fixed';
        modal.style.top = '0';
        modal.style.left = '0';
        modal.style.width = '100%';
        modal.style.height = '100%';
        modal.style.backgroundColor = 'rgba(0,0,0,0.8)';
        modal.style.display = 'flex';
        modal.style.alignItems = 'center';
        modal.style.justifyContent = 'center';
        modal.onclick = () => document.body.removeChild(modal);

        const modalImg = document.createElement('img');
        modalImg.src = img.src;
        modalImg.style.maxWidth = '90%';
        modalImg.style.maxHeight = '90%';

        modal.appendChild(modalImg);
        document.body.appendChild(modal);
    }

    // Function to send approval email to buyer
    function sendApprovalEmail(login_email) {
        const templateParams = {
            email: login_email,
            name: "Buyer"
        };

        emailjs.send('service_u6rit42', 'template_3lo6l2f', templateParams)
            .then((response) => {
                console.log('Success:', response);
                alert('Approval email sent successfully.');
            }, (error) => {
                console.error('Error:', error);
                alert('Failed to send approval email.');
            });
    }

    // Function to send decline email to buyer
    function sendEmail(reason, login_email) {
        const templateParams = {
            decline_reason: reason,
            email: login_email,
            name: "Buyer"
        };

        emailjs.send('service_u6rit42', 'template_svvgufp', templateParams)
            .then((response) => {
                console.log('Success:', response);
                alert('Decline email sent successfully.');
            }, (error) => {
                console.error('Error:', error);
                alert('Failed to send decline email.');
            });
    }

    // Load buyer data from Firebase and populate the table
    onValue(buyersRef, (snapshot) => {
        const buyerTableBody = document.getElementById('buyer-table-body');
        buyerTableBody.innerHTML = '';
    
        const now = new Date().getTime(); // Current timestamp
        const threeDaysInMilliseconds = 3 * 24 * 60 * 60 * 1000; // 3 days in milliseconds
    
        snapshot.forEach((childSnapshot) => {
            const buyer = childSnapshot.val();
            
            // Check if the buyer is approved and validate the approvedAt timestamp
            if (buyer.status === 'approved' && buyer.approvedAt) {
                const approvedAtTimestamp = new Date(buyer.approvedAt).getTime();
                
                // If approved date is older than 3 days, delete valididImageUrl
                if (now - approvedAtTimestamp > threeDaysInMilliseconds) {
                    // Delete from storage
                    if (buyer.valididImageUrl) {
                        const valididImageRef = storageRef(storage, buyer.valididImageUrl);
                        deleteObject(valididImageRef)
                            .then(() => {
                                console.log(`Deleted valididImageUrl: ${buyer.valididImageUrl}`);
                            })
                            .catch((error) => {
                                console.error('Error deleting image from storage:', error);
                            });
                    }
    
                    // Remove valididImageUrl from the database
                    const buyerRef = ref(database, 'buyers/' + childSnapshot.key);
                    update(buyerRef, { valididImageUrl: null })
                        .then(() => {
                            console.log(`Removed valididImageUrl from buyer: ${buyer.userId}`);
                        })
                        .catch((error) => {
                            console.error('Error updating buyer record:', error);
                        });
                }
            }
    
            // Only display pending buyers in the table
            if (buyer.status === 'pending') {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>
                        <div class="profile-container">
                            <img class="pic1" src="${buyer.profileImageUrl}" alt="Profile Image">
                        </div>
                    </td>
                    <td><img class="pic" src="${buyer.valididImageUrl}" alt="Permit Image" onclick="zoomPermitImage(this)"></td>
                    <td>${buyer.firstName} ${buyer.lastName}</td>
                    <td>
                        <button class="btn-btn3" onclick="viewBuyerInfo('${buyer.userId}')" title="View">
                            <i class="fas fa-eye"></i> <!-- View Icon -->
                        </button>
                        <button class="btn-btn1" onclick="approveBuyer('${buyer.userId}', '${buyer.login_email}')" title="Approve">
                            <i class="fas fa-check-circle"></i> <!-- Accept Icon -->
                        </button>
                        <button class="btn-btn2" onclick="declineBuyer('${buyer.userId}', '${buyer.login_email}', '${buyer.profileImageUrl}', '${buyer.valididImageUrl}')" title="Decline">
                            <i class="fas fa-times-circle"></i> <!-- Decline Icon -->
                        </button>
                    </td>
                `;
                buyerTableBody.appendChild(row);
            }
        });
    });


    // Function to open the vendor info modal when "View" is clicked
    window.viewBuyerInfo = function(buyerId) {
        const buyerRef = ref(database, 'buyers/' + buyerId);
        onValue(buyerRef, (snapshot) => {
            const buyer = snapshot.val();
            if (buyer) {
                const buyerInfo = `
                    <strong>Owner:</strong> ${buyer.firstName} ${buyer.lastName}<br>
                    <strong>Email:</strong> ${buyer.login_email}<br>
                    <strong>Phone:</strong> ${buyer.phone}<br>
                    <strong>City:</strong> ${buyer.cities}, ${buyer.barangay}, ${buyer.provinces}<br>
                `;
                document.getElementById('buyerInfo').innerHTML = buyerInfo;
    
                // Display the modal
                document.getElementById('buyerInfoModal').style.display = 'flex';
            }
        });
    };
    
    // Function to close the vendor info modal
    document.getElementById('closeBuyerInfoBtn').onclick = function() {
        document.getElementById('buyerInfoModal').style.display = 'none';
    };
    
    // Ensure the modal closes if the user clicks outside the modal content
    window.onclick = function(event) {
        const modal = document.getElementById('buyerInfoModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };

    // Show notification modal when notification bell is clicked
    const notificationBell = document.getElementById('notificationBell');
    const notificationModal = document.getElementById('notificationModal');
    notificationBell.addEventListener('click', () => {
        notificationModal.style.display = 'block';
    });

    // Close notification modal when close button is clicked
    const closeNotificationModalBtn = document.getElementById('closeNotificationModalBtn');
    closeNotificationModalBtn.addEventListener('click', () => {
        notificationModal.style.display = 'none';
    });

    // Close notification modal when clicking outside the modal
    window.addEventListener('click', (event) => {
        if (event.target === notificationModal) {
            notificationModal.style.display = 'none';
        }
    });
</script>
