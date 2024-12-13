<script type="module">
    // Import the functions you need from the SDKs
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-app.js";
    import { getDatabase, ref, update, onValue } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-database.js";

    // Firebase configuration
    const firebaseConfig = {
      apiKey: "AIzaSyCSx9Dx9f6fbwBvf8b02Wo8W6py5mFkpzI",
      authDomain: "allgoods2024-5163a.firebaseapp.com",
      databaseURL: "https://allgoods2024-5163a-default-rtdb.firebaseio.com",
      projectId: "allgoods2024-5163a",
      storageBucket: "allgoods2024-5163a.appspot.com",
      messagingSenderId: "980525971714",
      appId: "1:980525971714:web:78849efcc5ac2325a7ea1b"
    };

    // Initialize Firebase
    const app = initializeApp(firebaseConfig);
    const database = getDatabase(app);

    let selectedVendorId = null;
    let firstViolationReasonVendor = '';

    // Reference to the 'vendors' node
    const vendorsRef = ref(database, 'vendors');

    onValue(vendorsRef, (snapshot) => {
        const vendors = snapshot.val();
        const tableBody = document.getElementById('vendorTableBody');
        tableBody.innerHTML = ''; 
    
        // Loop through the vendors
        for (let vendorId in vendors) {
            const vendor = vendors[vendorId];
            if (vendor.status === "approved" || vendor.status === "deactivated") {
                // Create table rows for both approved and deactivated vendors
                const row = document.createElement('tr');
    
                // Profile Image
                const profileCell = document.createElement('td');
                const profileContainer = document.createElement('div');
                profileContainer.className = 'profile-container';
                const profileImage = document.createElement('img');
                profileImage.className = 'pic';
                profileImage.src = vendor.profileImageUrl || '';
                profileImage.alt = "Profile Image";
                profileContainer.appendChild(profileImage);
                profileImage.style.cursor = 'pointer';
                profileImage.onclick = () => openImageInModal(vendor.profileImageUrl); 
                profileCell.appendChild(profileContainer);
                row.appendChild(profileCell);
    
                // Permit Image (New column)
                const permitCell = document.createElement('td');
                const permitImage = document.createElement('img');
                permitImage.className = 'pic1';
                permitImage.src = vendor.permitImageUrl || '';
                permitImage.alt = "Permit Image";
                permitImage.style.cursor = 'pointer';
                permitImage.onclick = () => openImageInModal(vendor.permitImageUrl); 
                permitCell.appendChild(permitImage);
                row.appendChild(permitCell);
    
                // Store Name
                const storeCell = document.createElement('td');
                storeCell.textContent = vendor.storeName;
                row.appendChild(storeCell);
    
                // Status
                const statusCell = document.createElement('td');
                statusCell.textContent = vendor.status;
                row.appendChild(statusCell);
    
                // Action Buttons
                const actionCell = document.createElement('td');
    
                // View button (Font Awesome Icon)
                const viewButton = document.createElement('button');
                viewButton.className = 'btn-btn3';
                viewButton.innerHTML = '<i class="fas fa-eye"></i>';
                viewButton.title = 'View';
                viewButton.onclick = () => viewVendorInfo(vendor);
                actionCell.appendChild(viewButton);
    
               // Activate Button (Font Awesome Icon, only shown if status is 'deactivated')
               if (vendor.status === 'deactivated') {
                    const activateButton = document.createElement('button');
                    activateButton.className = 'btn-btn1';
                    activateButton.innerHTML = '<i class="fas fa-check-circle"></i>'; // Check-circle icon
                    activateButton.title = 'Activate Vendor';
                    activateButton.onclick = () => updateVendorStatus(vendorId, 'approved');
                    actionCell.appendChild(activateButton);
                }
    
                // Deactivate Button (Font Awesome Icon, only shown if status is 'approved')
                if (vendor.status === 'approved') {
                    const deactivateButton = document.createElement('button');
                    deactivateButton.className = 'btn-btn2';
                    deactivateButton.innerHTML = '<i class="fas fa-times-circle"></i>'; // Times-circle icon
                    deactivateButton.title = 'Deactivate Vendor';
                    deactivateButton.onclick = () => updateVendorStatus(vendorId, 'deactivated');
                    actionCell.appendChild(deactivateButton);
                }
    
                // Warning Button (Font Awesome Icon)
                const warningButton = document.createElement('button');
                warningButton.className = 'btn-btn-warning';
                warningButton.innerHTML = '<i class="fas fa-exclamation-triangle"></i>'; // Warning icon
                warningButton.title = 'Send Warning';
                warningButton.onclick = () => openWarningModal(vendorId);
                actionCell.appendChild(warningButton);
    
                // Update Expiration Date Button
                const updateButton = document.createElement('button');
                updateButton.className = 'btn-btn1';
                updateButton.innerHTML = '<i class="fas fa-edit"></i>';
                updateButton.title = 'Update Expiration Date';
                updateButton.onclick = () => openDatePickerModal(vendorId);
                actionCell.appendChild(updateButton);
    
                // Append action buttons to row
                row.appendChild(actionCell);
    
                // Append row to table body
                tableBody.appendChild(row);
            }
        }
    });

    // Function to open the image in a modal
    function openImageInModal(imageUrl) {
        const modal = document.createElement('div');
        modal.style.position = 'fixed';
        modal.style.top = '0';
        modal.style.left = '0';
        modal.style.width = '100%';
        modal.style.height = '100%';
        modal.style.backgroundColor = 'rgba(0, 0, 0, 0.8)';
        modal.style.display = 'flex';
        modal.style.alignItems = 'center';
        modal.style.justifyContent = 'center';
        modal.onclick = () => document.body.removeChild(modal);

        const modalImg = document.createElement('img');
        modalImg.src = imageUrl;
        modalImg.style.maxWidth = '90%';
        modalImg.style.maxHeight = '90%';

        const closeBtn = document.createElement('button');
        closeBtn.textContent = 'Close';
        closeBtn.onclick = () => document.body.removeChild(modal);
        closeBtn.style.position = 'absolute';
        closeBtn.style.top = '20px';
        closeBtn.style.display = 'none';
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

    // Function to open vendor info modal
    function viewVendorInfo(vendor) {
        const vendorInfoModal = document.getElementById('vendorModal');
        vendorInfoModal.style.display = 'block';
    
        const vendorInfo = `
            <strong>Store Name:</strong> ${vendor.storeName}<br>
            <strong>Owner:</strong> ${vendor.firstName} ${vendor.lastName}<br>
            <strong>Email:</strong> ${vendor.email}<br>
            <strong>Phone:</strong> ${vendor.phone}<br>
            <strong>Province:</strong> ${vendor.province}<br>
            <strong>City:</strong> ${vendor.city}<br>
            <strong>Barangay:</strong> ${vendor.barangay}<br>
            <strong>Status:</strong> ${vendor.status}<br>
            <strong>Expiration Date:</strong> ${vendor.expirationDate}<br>
        `;
    
        document.getElementById('vendorInfoContent').innerHTML = vendorInfo;
    
        // Get the warning info from the database
        const warningRef = ref(database, `warnings/${vendor.userId}`);
        onValue(warningRef, (snapshot) => {
            if (snapshot.exists()) {
                const warningData = snapshot.val();
                const warningInfo = `
                    <br><br><strong>Violation:</strong> ${warningData.violation}<br>
                    <strong>Reason:</strong> ${warningData.reason}<br>
                `;
                document.getElementById('vendorInfoContent').innerHTML += warningInfo;
            }
        });
    }

    // Function to close the vendor info modal
    document.getElementById('closeVendorInfo').onclick = () => {
        document.getElementById('vendorModal').style.display = 'none';
    };

    // Ensure the modal closes if the user clicks outside the modal content
    window.onclick = (event) => {
        const vendorInfoModal = document.getElementById('vendorModal');
        if (event.target === vendorInfoModal) {
            vendorInfoModal.style.display = 'none';
        }
    };

    // Function to update vendor status
    function updateVendorStatus(vendorId, newStatus) {
        if (!confirm("Are you sure you want to update?")) {
            return;
        }
        const vendorRef = ref(database, 'vendors/' + vendorId);
        update(vendorRef, {
            status: newStatus
        })
        .then(() => {
            console.log('Vendor status updated to: ' + newStatus);
        })
        .catch((error) => {
            console.error('Error updating status: ', error);
        });
    }

    // Open Date Picker Modal
    function openDatePickerModal(vendorId) {
        selectedVendorId = vendorId;
        document.getElementById('datePickerModal').style.display = 'block';
    }

    // Save New Expiration Date
    document.getElementById('saveDateBtn').onclick = () => {
        const newDate = document.getElementById('expirationDateInput').value;

        if (newDate && selectedVendorId) {
            const vendorRef = ref(database, 'vendors/' + selectedVendorId);
            update(vendorRef, {
                expirationDate: newDate
            })
            .then(() => {
                console.log('Expiration date updated to: ' + newDate);
                document.getElementById('datePickerModal').style.display = 'none';
            })
            .catch((error) => {
                    console.error('Error updating expiration date: ', error);
                });
        }
    };

    // Cancel the date picker modal
    document.getElementById('cancelBtn').onclick = () => {
        document.getElementById('datePickerModal').style.display = 'none';
    };

    // Function to open the Warning Modal for the vendor
    function openWarningModal(vendorId) {
      selectedVendorId = vendorId;
      document.getElementById('warningModal').style.display = 'block';
      resetWarningModal(); // Reset modal fields on open
    
      // Check if the vendor already has a 1st Violation
      const warningRef = ref(database, `warnings/${selectedVendorId}`);
      onValue(warningRef, (snapshot) => {
        if (snapshot.exists()) {
          const warningData = snapshot.val();
          if (warningData.violation === '1st Violation') {
            // Hide 1st Violation option and show only 2nd Violation
            document.getElementById('firstViolationContainerVendor').style.display = 'none';
            document.getElementById('secondViolationContainerVendor').style.display = 'block';
            document.querySelector('input[name="warningType"][value="2nd Violation"]').checked = true;
    
            // Hide the warning options for the 1st Violation
            document.getElementById('warningOptions1st').style.display = 'none';
    
            // Change button text to Deactivate
            document.getElementById('sendWarningBtn').innerText = 'Deactivate';
    
            // Set the reason from 1st Violation
            firstViolationReasonVendor = warningData.reason;
          }
        } else {
          // Show both violation options for the first warning
          document.getElementById('firstViolationContainerVendor').style.display = 'block';
          document.getElementById('secondViolationContainerVendor').style.display = 'none';
          document.querySelector('input[name="warningType"][value="1st Violation"]').checked = true;
    
          // Automatically show warning options for the 1st Violation
          document.getElementById('warningOptions1st').style.display = 'block';
          document.getElementById('sendWarningBtn').innerText = 'Send';
        }
        document.getElementById('sendWarningBtn').disabled = false; // Enable Send button
      });
    }
    
    // Show warning options based on selection
    document.querySelectorAll('input[name="warningType"]').forEach((input) => {
      input.addEventListener('change', function() {
        if (this.value === '1st Violation') {
          document.getElementById('warningOptions1st').style.display = 'block'; // Show options for 1st Violation
          document.getElementById('sendWarningBtn').innerText = 'Send'; // Change button text
        } else {
          document.getElementById('warningOptions1st').style.display = 'none'; // Hide options for 2nd Violation
          document.getElementById('sendWarningBtn').innerText = 'Deactivate'; // Change button text
        }
        document.getElementById('sendWarningBtn').disabled = false;
      });
    });
    
    // Close Warning Modal and reset form
    document.getElementById('closeWarningModal').onclick = () => {
      document.getElementById('warningModal').style.display = 'none';
      resetWarningModal();
    };
    
    // Function to reset the warning modal
    function resetWarningModal() {
      document.querySelectorAll('input[name="warningType"]').forEach((input) => input.checked = false);
      document.querySelectorAll('input[name="warning1st"]').forEach((input) => input.checked = false);
      document.getElementById('customReasonInput').value = '';
      document.getElementById('warningOptions1st').style.display = 'none';
      document.getElementById('sendWarningBtn').disabled = true; // Reset Send button state
      document.getElementById('sendWarningBtn').innerText = 'Send'; // Reset button text
    
      // Reset display for violation options
      document.getElementById('firstViolationContainerVendor').style.display = 'block';
      document.getElementById('secondViolationContainerVendor').style.display = 'none';
    }
    
    // Send warning
    document.getElementById('sendWarningBtn').onclick = () => {
      const warningType = document.querySelector('input[name="warningType"]:checked')?.value;
      let selectedReason = document.querySelector('input[name="warning1st"]:checked')?.value;
    
      // If 'Others' is selected, get the custom input
      if (selectedReason === 'Others') {
        selectedReason = document.getElementById('customReasonInput').value;
      }
    
      // Automatically set reason for 2nd Violation based on 1st Violation
      if (warningType === '2nd Violation') {
        selectedReason = firstViolationReasonVendor;
      }
    
      if (!warningType || (!selectedReason && warningType === '1st Violation')) {
        alert('Please select both a warning type and a reason.');
        return;
      }
    
      if (selectedReason && selectedVendorId) {
        if (!confirm("Are you sure you want to update?")) {
            return;
        }
        const warningData = {
          userId: selectedVendorId,
          violation: warningType,
          name: 'Warning: Violation Notice',
          reason: selectedReason,
          isRead: false
        };
    
        // Deactivate the vendor if it's a 2nd Violation
        if (warningType === '2nd Violation') {
          const vendorRef = ref(database, `vendors/${selectedVendorId}`);
          update(vendorRef, { status: 'deactivated' })
            .then(() => {
              console.log('Vendor status updated to deactivated.');
            })
            .catch((error) => {
              console.error('Error updating vendor status: ', error);
            });
        }
    
        // Update warning data in the 'warnings' node
        const warningRef = ref(database, `warnings/${selectedVendorId}`);
        update(warningRef, warningData)
          .then(() => {
            console.log('Warning saved/updated successfully.');
            document.getElementById('warningModal').style.display = 'none';
            resetWarningModal(); // Reset modal fields after successful warning submission
          })
          .catch((error) => {
            console.error('Error saving warning: ', error);
          });
      }
    };
</script>