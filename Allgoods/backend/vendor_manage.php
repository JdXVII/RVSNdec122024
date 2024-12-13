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

    const vendorsRef = ref(database, 'vendors');

    window.approveVendor = function(vendorId, email) {
        if (!confirm("Are you sure you want to approve this vendor?")) {
            return;
        }
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
        modal.style.padding = '10px';
        modal.style.boxSizing = 'border-box';
        modal.style.zIndex = '9999';
    
        const modalContent = document.createElement('div');
        modalContent.style.backgroundColor = 'white';
        modalContent.style.padding = '20px';
        modalContent.style.borderRadius = '10px';
        modalContent.style.maxWidth = '400px';
        modalContent.style.width = '100%';
        modalContent.style.boxShadow = '0px 4px 6px rgba(0,0,0,0.1)';
        modalContent.style.textAlign = 'center';
    
        const title = document.createElement('h2');
        title.textContent = 'Set Expiration Date';
        title.style.marginBottom = '15px';
        title.style.fontFamily = 'Arial, sans-serif';
        title.style.fontSize = '1.5rem';
    
        const dateInput = document.createElement('input');
        dateInput.type = 'date';
        dateInput.required = true;
        dateInput.style.marginBottom = '20px';
        dateInput.style.padding = '10px';
        dateInput.style.fontSize = '1rem';
        dateInput.style.width = '100%';
        dateInput.style.border = '1px solid #ccc';
        dateInput.style.borderRadius = '5px';
        dateInput.style.boxSizing = 'border-box';
    
        const buttonContainer = document.createElement('div');
        buttonContainer.style.display = 'flex';
        buttonContainer.style.justifyContent = 'space-between';
        buttonContainer.style.marginTop = '20px';
    
        const submitBtn = document.createElement('button');
        submitBtn.textContent = 'Approve';
        submitBtn.style.backgroundColor = '#4CAF50';
        submitBtn.style.color = 'white';
        submitBtn.style.border = 'none';
        submitBtn.style.borderRadius = '5px';
        submitBtn.style.padding = '10px 20px';
        submitBtn.style.cursor = 'pointer';
        submitBtn.style.flex = '1';
        submitBtn.style.marginRight = '10px';
        submitBtn.style.fontSize = '1rem';
    
        const cancelBtn = document.createElement('button');
        cancelBtn.textContent = 'Cancel';
        cancelBtn.style.backgroundColor = '#f44336';
        cancelBtn.style.color = 'white';
        cancelBtn.style.border = 'none';
        cancelBtn.style.borderRadius = '5px';
        cancelBtn.style.padding = '10px 20px';
        cancelBtn.style.cursor = 'pointer';
        cancelBtn.style.flex = '1';
        cancelBtn.style.fontSize = '1rem';
        
        cancelBtn.onclick = () => document.body.removeChild(modal);
    
        modalContent.appendChild(title);
        modalContent.appendChild(dateInput);
        buttonContainer.appendChild(submitBtn);
        buttonContainer.appendChild(cancelBtn);
        modalContent.appendChild(buttonContainer);
        modal.appendChild(modalContent);
        document.body.appendChild(modal);
    
        submitBtn.onclick = function() {
            const expirationDate = dateInput.value;
    
            if (!expirationDate) {
                alert('Expiration date is required');
                return;
            }
    
            // Update the vendor's status and expiration date in the database
            const vendorRef = ref(database, 'vendors/' + vendorId);
            update(vendorRef, { status: 'approved', expirationDate: expirationDate })
                .then(() => {
                    alert('Vendor approved');
                    sendApprovalEmail(email);
                    document.body.removeChild(modal);
                })
                .catch((error) => {
                    console.error('Error approving vendor: ', error);
                });
        };
    };


    window.declineVendor = function(vendorId, email, profileImageUrl, permitImageUrl) {
        if (!confirm("Are you sure you want to decline this vendor?")) {
            return; // Exit if the user cancels
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
    
        const reasons = ['Invalid Business Permit', 'Mismatched Information', 'Duplicate Account', 'Low-Quality Document Uploads', 'Inappropriate Profile Picture', 'Others'];
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
    
            sendDeclineEmail(selectedReason, email);
    
            const vendorRef = ref(database, 'vendors/' + vendorId);
    
            // Continue with the rest of the decline logic
            onValue(vendorRef, (snapshot) => {
                const vendor = snapshot.val();
                if (vendor) {
                    const password = vendor.password; 
    
                    signInWithEmailAndPassword(auth, email, password)
                        .then(() => deleteUser(auth.currentUser))
                        .then(() => remove(vendorRef))
                        .then(() => {
                            if (profileImageUrl) {
                                const profileImageRef = storageRef(storage, profileImageUrl);
                                return deleteObject(profileImageRef);
                            }
                        })
                        .then(() => {
                            if (permitImageUrl) {
                                const permitImageRef = storageRef(storage, permitImageUrl);
                                return deleteObject(permitImageRef);
                            }
                        })
                        .then(() => {
                            alert('Vendor declined and data successfully deleted.');
                            document.body.removeChild(modal);
                        })
                        .catch((error) => {
                            console.error('Error during the decline process:', error);
                            alert('Error declining vendor: ' + error.message);
                        });
                } else {
                    alert('Vendor data found.');
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

    window.zoomPermitImage = function(img) {
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

        const closeBtn = document.createElement('button');
        closeBtn.textContent = 'Close';
        closeBtn.onclick = () => document.body.removeChild(modal);
        closeBtn.style.position = 'absolute';
        closeBtn.style.top = '20px';
        closeBtn.style.right = '20px';
        closeBtn.style.backgroundColor = 'white';
        closeBtn.style.border = 'none';
        closeBtn.style.borderRadius = '5px';
        closeBtn.style.padding = '10px';
        closeBtn.style.cursor = 'pointer';

        modal.appendChild(modalImg);
        modal.appendChild(closeBtn);
        document.body.appendChild(modal);
    }

    function sendApprovalEmail(email) {
    const templateParams = {
        email: email,
        name: "Vendor"
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
    
    function sendDeclineEmail(reason, email) {
        const templateParams = {
            decline_reason: reason,
            email: email,
            name: "Vendor"
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


    onValue(vendorsRef, (snapshot) => {
        const vendorTableBody = document.getElementById('vendor-table-body');
        vendorTableBody.innerHTML = '';
        snapshot.forEach((childSnapshot) => {
            const vendor = childSnapshot.val();
            if (vendor.status === 'pending') {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>
                        <div class="profile-container">
                            <img class="pic1" src="${vendor.profileImageUrl}" alt="Profile Image">
                        </div>
                    </td>
                    <td><img class="pic" src="${vendor.permitImageUrl}" alt="Permit Image" onclick="zoomPermitImage(this)"></td>
                    <td>${vendor.firstName} ${vendor.lastName}</td>
                    <td>${vendor.storeName}</td>
                    <td style="align-items:center;">
                        <button class="btn-btn3" onclick="viewVendorInfo('${vendor.userId}')" title="View">
                            <i class="fas fa-eye"></i> <!-- View Icon -->
                        </button>
                        <button class="btn-btn1" onclick="approveVendor('${vendor.userId}', '${vendor.email}')" title="Approve">
                            <i class="fas fa-check-circle"></i> <!-- Accept Icon -->
                        </button>
                        <button class="btn-btn2" onclick="declineVendor('${vendor.userId}', '${vendor.email}', '${vendor.profileImageUrl}', '${vendor.permitImageUrl}')" title="Decline">
                            <i class="fas fa-times-circle"></i> <!-- Decline Icon -->
                        </button>
                    </td>
                `;
                vendorTableBody.appendChild(row);
            }
        });
        updateExpiredPermitsCount(snapshot);
    });
    
    // Function to open the vendor info modal when "View" is clicked
    window.viewVendorInfo = function(vendorId) {
        const vendorRef = ref(database, 'vendors/' + vendorId);
        onValue(vendorRef, (snapshot) => {
            const vendor = snapshot.val();
            if (vendor) {
                const vendorInfo = `
                <br>
                    <strong>Store Name:</strong> ${vendor.storeName}<br>
                    <strong>Owner:</strong> ${vendor.firstName} ${vendor.lastName}<br>
                    <strong>Email:</strong> ${vendor.email}<br>
                    <strong>Phone:</strong> ${vendor.phone}<br>
                    <strong>Province:</strong> ${vendor.province}<br>
                    <strong>City:</strong> ${vendor.city}<br>
                    <strong>Barangay:</strong> ${vendor.barangay}<br>
                `;
                document.getElementById('vendorInfo').innerHTML = vendorInfo;
    
                // Display the modal
                document.getElementById('vendorInfoModal').style.display = 'flex';
            }
        });
    };
    
    // Function to close the vendor info modal
    document.getElementById('closeVendorInfoBtn').onclick = function() {
        document.getElementById('vendorInfoModal').style.display = 'none';
    };
    
    // Ensure the modal closes if the user clicks outside the modal content
    window.onclick = function(event) {
        const modal = document.getElementById('vendorInfoModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };


    function updateExpiredPermitsCount(snapshot) {
        let expiredPermitsCount = 0;
        let soonToExpirePermitsCount = 0;
        const expiredPermitsList = document.getElementById('expiredPermitsList');
        const soonToExpirePermitsList = document.getElementById('soonToExpirePermitsList');
        expiredPermitsList.innerHTML = '';
        soonToExpirePermitsList.innerHTML = '';
        
        snapshot.forEach((childSnapshot) => {
            const vendor = childSnapshot.val();
            if (vendor.status === 'approved' && vendor.expirationDate) {
                const today = new Date();
                const expirationDate = new Date(vendor.expirationDate);
                const timeDiff = expirationDate.getTime() - today.getTime();
                const daysToExpiration = Math.ceil(timeDiff / (1000 * 3600 * 24));

                if (daysToExpiration <= 0) {
                    expiredPermitsCount++;
                    const listItem = document.createElement('li');
                    listItem.textContent = `Store: ${vendor.storeName} - Permit has expired`;
                    expiredPermitsList.appendChild(listItem);
                } else if (daysToExpiration <= 30) {
                    soonToExpirePermitsCount++;
                    const listItem = document.createElement('li');
                    listItem.textContent = `Store: ${vendor.storeName} - Permit will expire soon (${vendor.expirationDate})`;
                    soonToExpirePermitsList.appendChild(listItem);
                }
            }
        });

        const notificationCount = document.getElementById('notificationCount');
        notificationCount.textContent = expiredPermitsCount + soonToExpirePermitsCount;
    }

    const notificationBell = document.getElementById('notificationBell');
    const notificationModal = document.getElementById('notificationModal');
    notificationBell.addEventListener('click', () => {
        notificationModal.style.display = 'block';
    });

    const closeNotificationModalBtn = document.getElementById('closeNotificationModalBtn');
    closeNotificationModalBtn.addEventListener('click', () => {
        notificationModal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target === notificationModal) {
            notificationModal.style.display = 'none';
        }
    });
</script>
