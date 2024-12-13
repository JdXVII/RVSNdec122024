<script type="module">
  // Import necessary Firebase functions
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-app.js";
  import { getDatabase, ref, onValue, update } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-database.js";

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

  // Declare selectedBuyerId globally
  let selectedBuyerId = null;
  let firstViolationReason = '';

  // Fetch buyers data
  const buyersRef = ref(database, 'buyers');
  onValue(buyersRef, (snapshot) => {
    const buyerTableBody = document.getElementById('buyerTableBody');
    buyerTableBody.innerHTML = ''; // Clear the table before adding new data

    snapshot.forEach((childSnapshot) => {
      const buyer = childSnapshot.val();
      const buyerKey = childSnapshot.key;

      if (buyer.status === 'approved') {
        // Create table row
        const row = document.createElement('tr');

        // Profile Image
        const profileCell = document.createElement('td');
        const profileContainer = document.createElement('div');
        profileContainer.classList.add('profile-container');
        const profileImage = document.createElement('img');
        profileImage.classList.add('pic1');
        profileImage.src = buyer.profileImageUrl || '';
        profileImage.alt = 'Profile Image';
        profileImage.style.cursor = 'pointer';
        profileImage.onclick = () => openImageInModal(buyer.profileImageUrl); 
        profileContainer.appendChild(profileImage);
        profileCell.appendChild(profileContainer);
        row.appendChild(profileCell);

        // Buyer's Name
        const nameCell = document.createElement('td');
        nameCell.textContent = `${buyer.firstName} ${buyer.lastName}`;
        row.appendChild(nameCell);

        // Status
        const statusCell = document.createElement('td');
        statusCell.textContent = `${buyer.status}`;
        row.appendChild(statusCell);

        // Action Buttons
        const actionCell = document.createElement('td');

        // View Button
        const viewButton = document.createElement('button');
        viewButton.innerHTML = '<i class="fas fa-eye"></i>';
        viewButton.title = 'View';
        viewButton.classList.add('btn-btn4');
        viewButton.onclick = () => viewBuyerInfo(buyer);
        actionCell.appendChild(viewButton);

        // Warning Button
        const warningButton = document.createElement('button');
        warningButton.innerHTML = '<i class="fas fa-exclamation-triangle"></i>';
        warningButton.title = 'Send Warning';
        warningButton.classList.add('btn-btn');
        
        // Disable Warning button if status is deactivated
        if (buyer.status === 'deactivated') {
          warningButton.disabled = true;
          warningButton.classList.add('disabled'); 
        } else {
          warningButton.addEventListener('click', () => openWarningModal_buyer(buyerKey));
        }

        actionCell.appendChild(warningButton);

        row.appendChild(actionCell);

        // Append row to table
        buyerTableBody.appendChild(row);
      }
    });
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
        closeBtn.onclick = () => document.body.removeChild(modal);
        closeBtn.style.position = 'absolute';
        closeBtn.style.top = '20px';
        closeBtn.style.right = '20px';
        closeBtn.style.backgroundColor = 'white';
        closeBtn.style.border = 'none';
        closeBtn.style.display = 'none';
        closeBtn.style.borderRadius = '5px';
        closeBtn.style.padding = '10px';
        closeBtn.style.cursor = 'pointer';

        modal.appendChild(modalImg);
        modal.appendChild(closeBtn);
        document.body.appendChild(modal);
    }

  // Function to open buyer info modal
  function viewBuyerInfo(buyer) {
    const buyerInfoModal = document.getElementById('buyerInfoM');
    buyerInfoModal.style.display = 'block';

    const buyerInfo = `
      <strong>First Name:</strong> ${buyer.firstName}<br>
      <strong>Last Name:</strong> ${buyer.lastName}<br>
      <strong>Email:</strong> ${buyer.login_email}<br>
      <strong>Phone:</strong> ${buyer.phone}<br>
      <strong>Province:</strong> ${buyer.provinces}<br>
      <strong>City:</strong> ${buyer.cities}<br>
      <strong>Barangay:</strong> ${buyer.barangay}<br>
      <strong>Zip Code:</strong> ${buyer.zipCode}<br>
      <strong>Zone:</strong> ${buyer.zone}<br>
      <strong>Status:</strong> ${buyer.status}<br>
    `;
    document.getElementById('buyerInfoContent').innerHTML = buyerInfo;

    // Get the warning info from the database
    const warningRef = ref(database, `warnings/${buyer.userId}`);
        onValue(warningRef, (snapshot) => {
            if (snapshot.exists()) {
                const warningData = snapshot.val();
                const warningInfo = `
                    <br><br><strong>Violation:</strong> ${warningData.violation}<br>
                    <strong>Reason:</strong> ${warningData.reason}<br>
                `;
                document.getElementById('buyerInfoContent').innerHTML += warningInfo;
            }
        });
  }


  document.getElementById('closeBuyerInfo').onclick = () => {
    document.getElementById('buyerInfoM').style.display = 'none';
  };

  window.onclick = (event) => {
    const buyerInfoModal = document.getElementById('buyerInfoM');
    if (event.target === buyerInfoModal) {
      buyerInfoModal.style.display = 'none';
    }
  };

  // Function to open the Warning Modal
  function openWarningModal_buyer(buyerId) {
    selectedBuyerId = buyerId;  // Assign the selected buyer ID to the global variable
    document.getElementById('warningModal_buyer').style.display = 'block';
    resetWarningModal();
  
    // Check if the buyer already has a 1st Violation
    const warningRef = ref(database, `warnings/${selectedBuyerId}`);
    onValue(warningRef, (snapshot) => {
      if (snapshot.exists()) {
        const warningData = snapshot.val();
        if (warningData.violation === '1st Violation') {
          // Hide 1st Violation option and show only 2nd Violation
          document.getElementById('firstViolationContainer').style.display = 'none'; 
          document.getElementById('secondViolationContainer').style.display = 'block';
          document.querySelector('input[name="warningType_buyer"][value="2nd Violation"]').checked = true;
  
          // Hide the warning options for the 1st Violation
          document.getElementById('warningOptions1st_buyer').style.display = 'none';
  
          // Change button text to Deactivate
          document.getElementById('sendWarningBtn_buyer').innerText = 'Deactivate';
  
          // Set the reason from 1st Violation
          firstViolationReason = warningData.reason; 
        }
      } else {
        // Show both violation options for the first warning
        document.getElementById('firstViolationContainer').style.display = 'block'; 
        document.getElementById('secondViolationContainer').style.display = 'none'; 
        document.querySelector('input[name="warningType_buyer"][value="1st Violation"]').checked = true;
  
        // Automatically show warning options for the 1st Violation
        document.getElementById('warningOptions1st_buyer').style.display = 'block';
        document.getElementById('sendWarningBtn_buyer').innerText = 'Send'; 
      }
      document.getElementById('sendWarningBtn_buyer').disabled = false; 
    });
  }
  
  // Show warning options based on selection
  document.querySelectorAll('input[name="warningType_buyer"]').forEach((input) => {
    input.addEventListener('change', function() {
      if (this.value === '1st Violation') {
        document.getElementById('warningOptions1st_buyer').style.display = 'block'; 
        document.getElementById('sendWarningBtn_buyer').innerText = 'Send'; 
      } else {
        document.getElementById('warningOptions1st_buyer').style.display = 'none';
        document.getElementById('sendWarningBtn_buyer').innerText = 'Deactivate';
      }
      // Enable Send button
      document.getElementById('sendWarningBtn_buyer').disabled = false;
    });
  });
  
  // Close Warning Modal and reset form
  document.getElementById('closeWarningModal_buyer').onclick = () => {
    document.getElementById('warningModal_buyer').style.display = 'none';
    resetWarningModal();
  };
  
  // Function to reset the warning modal
  function resetWarningModal() {
    document.querySelectorAll('input[name="warningType_buyer"]').forEach((input) => {
      input.checked = false;
    });
    
    document.querySelectorAll('input[name="warning1st_buyer"]').forEach((input) => input.checked = false);
    document.getElementById('customReasonInput_buyer').value = '';
    document.getElementById('warningOptions1st_buyer').style.display = 'none';
    document.getElementById('sendWarningBtn_buyer').disabled = true; 
    document.getElementById('sendWarningBtn_buyer').innerText = 'Send'; 
  
    // Reset display for violation options
    document.getElementById('firstViolationContainer').style.display = 'block'; 
    document.getElementById('secondViolationContainer').style.display = 'none'; 
  }
  
  // Send warning
  document.getElementById('sendWarningBtn_buyer').onclick = () => {
    const warningType = document.querySelector('input[name="warningType_buyer"]:checked')?.value;
    let selectedReason = document.querySelector('input[name="warning1st_buyer"]:checked')?.value;
  
    // If 'Others' is selected, get the custom input
    if (selectedReason === 'Others') {
      selectedReason = document.getElementById('customReasonInput_buyer').value;
    }
  
    if (warningType === '2nd Violation') {
      selectedReason = firstViolationReason;
    }
  
    if (!warningType || (!selectedReason && warningType === '1st Violation')) {
      alert('Please select both a warning type and a reason.');
      return;
    }
  
    if (selectedReason && selectedBuyerId) {
      if (!confirm("Are you sure you want to update?")) {
            return;
        }
      const warningData = {
        userId: selectedBuyerId,
        violation: warningType,
        name: 'Warning: Violation Notice',
        reason: selectedReason,
        isRead: false
      };
  
      // Check if it's a 2nd Violation
      if (warningType === '2nd Violation') {
        // Deactivate the buyer
        const buyerRef = ref(database, `buyers/${selectedBuyerId}`);
        update(buyerRef, { status: 'deactivated' })
          .then(() => {
            console.log('Buyer status updated to deactivated.');
          })
          .catch((error) => {
            console.error('Error updating buyer status: ', error);
          });
      }
  
      // Update warning data in the 'warnings' node
      const warningRef = ref(database, `warnings/${selectedBuyerId}`);
      update(warningRef, warningData)
        .then(() => {
          console.log('Warning saved/updated successfully.');
          document.getElementById('warningModal_buyer').style.display = 'none';
          resetWarningModal(); 
        })
        .catch((error) => {
          console.error('Error saving warning: ', error);
        });
    }
  };

</script>
