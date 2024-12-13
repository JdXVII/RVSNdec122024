<script type="module">
    // Import Firebase SDKs
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-app.js";
    import { getDatabase, ref, get, child, set, update, remove, onValue } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-database.js";
    import { getStorage, ref as storageRef, deleteObject } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-storage.js";

    // Your web app's Firebase configuration
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
    const storage = getStorage(app);

    let currentVendorId;

    // Function to fetch vendor data
    async function fetchVendorData() {
        try {
            const dbRef = ref(database);
            const permitsSnapshot = await get(child(dbRef, 'permits_pending'));
            const vendorsSnapshot = await get(child(dbRef, 'vendors'));

            if (permitsSnapshot.exists() && vendorsSnapshot.exists()) {
                const permits = permitsSnapshot.val();
                const vendors = vendorsSnapshot.val();
                renderVendorTable(permits, vendors);
            } else {
                console.error("No data available");
            }
        } catch (error) {
            console.error("Error fetching data: ", error);
        }
    }

    // Function to render vendor table
    function renderVendorTable(permits, vendors) {
        const tbody = document.querySelector('.pending-vendor-table tbody');
        tbody.innerHTML = ''; // Clear existing rows

        let rowsHTML = ''; // Build rows in a batch
        // Loop through permits to get vendor data
        for (const permitId in permits) {
            const permit = permits[permitId];
            const vendorId = permit.userId;
            if (vendors[vendorId]) {
                const vendor = vendors[vendorId];
                // Create a new row for each vendor
                rowsHTML += `
                    <tr data-vendor-id="${vendorId}">
                        <td>
                            <img src="${vendor.profileImageUrl}" alt="Vendor's Profile" class="pic">
                        </td>
                        <td>
                            <img src="${permit.imageUrl}" alt="Permit Image" class="pic1" onclick="zoomPermitImage(this)">
                        </td>
                        <td>${vendor.storeName}</td>
                        <td>${permit.status}</td>
                        <td>
                            <button class="btn-btn7" onclick="openDatePicker('${vendorId}')" title="Accept">
                                <i class="fas fa-check-circle"></i> <!-- Accept Icon -->
                            </button>
                            <button class="btn-btn8" onclick="declineVendorPermit('${vendorId}')" title="Decline">
                                <i class="fas fa-times-circle"></i> <!-- Decline Icon -->
                            </button>
                        </td>
                    </tr>
                `;
            }
        }

        // Append all rows at once
        tbody.innerHTML = rowsHTML;
    }

    // Function to open the date picker
    window.openDatePicker = function(vendorId) {
        currentVendorId = vendorId; // Set the current vendor ID
        document.getElementById('dateModal').style.display = 'block'; // Show the modal
    };

    // Function to save the selected expiration date
    document.getElementById('saveDateButton').onclick = async function() {
        const expirationDate = document.getElementById('expirationDate').value;

        if (!expirationDate) {
            alert("Please select a date.");
            return;
        }

        try {
            const dbRef = ref(database);

            // Fetch the current permit and vendor data
            const [permitSnapshot, vendorSnapshot] = await Promise.all([
                get(child(dbRef, `permits_pending/${currentVendorId}`)),
                get(child(dbRef, `vendors/${currentVendorId}`))
            ]);

            if (permitSnapshot.exists() && vendorSnapshot.exists()) {
                const permit = permitSnapshot.val();
                const vendor = vendorSnapshot.val();

                // Archive old permitImageUrl and expirationDate
                const archiveId = Date.now().toString(); // Using timestamp for uniqueness
                const archiveData = {
                    userId: currentVendorId,
                    oldPermitImageUrl: vendor.permitImageUrl,
                    oldExpirationDate: vendor.expirationDate,
                };
                const archiveRef = ref(database, `archive/${currentVendorId}/${archiveId}`);
                await set(archiveRef, archiveData);

                // Update vendor permitImageUrl and expirationDate
                const vendorUpdate = {
                    permitImageUrl: permit.imageUrl,
                    status: 'approved',
                    expirationDate: expirationDate // Update with the new expiration date
                };
                const vendorRef = ref(database, `vendors/${currentVendorId}`);
                await update(vendorRef, vendorUpdate);

                // Remove the permit from the permits_pending table
                const permitRef = ref(database, `permits_pending/${currentVendorId}`);
                await remove(permitRef);

                // Optimistically update the UI
                updateVendorTableAfterAction(currentVendorId, true);

                console.log(`Vendor ${currentVendorId} approved, expiration date set, and permit data archived.`);
            } else {
                console.error("Permit or Vendor data not found.");
            }

            // Close the date picker modal
            document.getElementById('dateModal').style.display = 'none';
        } catch (error) {
            console.error("Error accepting vendor permit: ", error);
        }
    };

    // Function to cancel the date picker
    document.getElementById('cancelDateButton').onclick = function() {
        document.getElementById('dateModal').style.display = 'none'; // Close the modal
    };

    // Function to handle declining the vendor permit with reason input
window.declineVendorPermit = async function (vendorId) {
    if (!confirm("Are you sure you want to decline this permit?")) {
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

    const reasons = ['Invalid permit details', 'Expired permit', 'Duplicate permit submission', 'Others'];
    reasons.forEach((reason) => {
        const option = document.createElement('option');
        option.value = reason;
        option.textContent = reason;
        dropdown.appendChild(option);
    });

    const otherReasonInput = document.createElement('input');
    otherReasonInput.type = 'text';
    otherReasonInput.placeholder = 'Specify reason';
    otherReasonInput.style.display = 'none'; // Hidden initially
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

    submitBtn.onclick = async function () {
        const selectedReason = dropdown.value === 'Others' ? otherReasonInput.value : dropdown.value;

        if (!selectedReason || (dropdown.value === 'Others' && !otherReasonInput.value.trim())) {
            alert('Please provide a valid reason.');
            return;
        }

        try {
            const permitRef = ref(database, `permits_pending/${vendorId}`);
            const permitSnapshot = await get(permitRef);

            if (!permitSnapshot.exists()) {
                alert('Permit data not found.');
                console.error('Permit data not found.');
                return;
            }

            const permitData = permitSnapshot.val();
            const permitImageUrl = permitData.imageUrl;

            // Generate a unique ID for the declined permit entry
            const uniqueId = Date.now().toString(); // Using timestamp for uniqueness

            // Store the decline reason in `declined_permits/{vendorId}/{uniqueId}`
            const declinedRef = ref(database, `declined_permits/${uniqueId}`);
            const declinedData = {
                userId: vendorId,
                reason: selectedReason,
                isRead: false,
                name: 'New Permit Decline',
            };
            await set(declinedRef, declinedData);

            // Delete the image from Firebase Storage
            if (permitImageUrl) {
                const storagePath = decodeURIComponent(permitImageUrl.split('/o/')[1].split('?alt=media')[0]);
                const imageRef = storageRef(storage, storagePath);
                await deleteObject(imageRef);
                console.log(`Image ${permitImageUrl} deleted from Firebase Storage.`);
            }

            // Remove the permit from `permits_pending`
            await remove(permitRef);

            // Optimistically update the UI
            updateVendorTableAfterAction(vendorId, false);

            alert(`Permit for vendor ${vendorId} declined successfully. Reason: ${selectedReason}`);
            document.body.removeChild(modal);
        } catch (error) {
            console.error('Error declining vendor permit:', error);
        }
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


    // Function to update vendor table after an action
    function updateVendorTableAfterAction(vendorId, accepted) {
        const tbody = document.querySelector('.pending-vendor-table tbody');
        const row = tbody.querySelector(`tr[data-vendor-id="${vendorId}"]`);
        if (row) {
            row.remove(); // Remove the row if accepted or declined
        }
    }

    // Real-time listener for permits and vendors
    function setupRealTimeListeners() {
        const permitsRef = ref(database, 'permits_pending');
        const vendorsRef = ref(database, 'vendors');

        // Listen for changes in permits
        onValue(permitsRef, (snapshot) => {
            if (snapshot.exists()) {
                fetchVendorData(); // Re-fetch vendor data
            }
        });

        // Listen for changes in vendors
        onValue(vendorsRef, (snapshot) => {
            if (snapshot.exists()) {
                fetchVendorData(); // Re-fetch vendor data
            }
        });
    }

    // Function to filter table rows based on search input
    document.getElementById('searchInput').addEventListener('input', function () {
        const searchQuery = this.value.toLowerCase();
        const rows = document.querySelectorAll('.pending-vendor-table tbody tr');

        rows.forEach(row => {
            const storeName = row.querySelector('td:nth-child(3)').textContent.toLowerCase();
            if (storeName.includes(searchQuery)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    // Call the function on page load
    window.onload = () => {
        fetchVendorData();
        setupRealTimeListeners();
    };
</script>