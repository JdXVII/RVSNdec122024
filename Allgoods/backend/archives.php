<script type="module">
    // Import the necessary Firebase functions
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-app.js";
    import { getDatabase, ref, onValue } from "https://www.gstatic.com/firebasejs/10.13.2/firebase-database.js";
    
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
    
    const searchInput = document.getElementById("searchInput");
    const profileCards = document.getElementById("profileCards");
    let allVendors = []; // Store all vendors here for search functionality
    
    // Function to fetch vendors and populate profile cards
    function fetchVendors() {
        const vendorsRef = ref(database, 'vendors/');
        onValue(vendorsRef, (snapshot) => {
            const vendors = snapshot.val();
            allVendors = []; // Reset allVendors array
            
            profileCards.innerHTML = ''; // Clear previous cards
            
            if (vendors) {
                Object.keys(vendors).forEach(vendorId => {
                    const vendor = vendors[vendorId];
                    
                    // Only store vendors with status "approved"
                    if (vendor.status === 'approved') {
                        allVendors.push(vendor); // Add vendor to the array
                    }
                });
                displayVendors(allVendors); // Initially display all approved vendors
            }
        });
    }
    
    // Function to create a profile card element
    function createProfileCard(vendor) {
        const card = document.createElement('div');
        card.className = 'profile-card';
    
        // Profile Image
        const img = document.createElement('img');
        img.src = vendor.profileImageUrl;
        img.alt = 'Profile Image';
        card.appendChild(img);
    
        // Name
        const nameDiv = document.createElement('div');
        nameDiv.className = 'name';
        nameDiv.textContent = `${vendor.firstName} ${vendor.lastName}`;
        card.appendChild(nameDiv);
    
        // Store Name
        const infoDiv = document.createElement('div');
        infoDiv.className = 'info';
        infoDiv.innerHTML = `<span><b>Store Name</b></span><span class="store">${vendor.storeName}</span>`;
        card.appendChild(infoDiv);
    
        // Add click event to show popup
        card.addEventListener('click', () => {
            showPopup(vendor.userId, vendor.storeName); // Pass store name here
        });
    
        return card;
    }
    
    // Function to display vendors (either filtered or all)
    function displayVendors(vendorsToDisplay) {
        profileCards.innerHTML = ''; // Clear current cards
        
        vendorsToDisplay.forEach(vendor => {
            const card = createProfileCard(vendor);
            profileCards.appendChild(card);
        });
    }
    
    // Function to filter vendors based on search input
    function filterVendors() {
        const searchTerm = searchInput.value.toLowerCase();
        
        const filteredVendors = allVendors.filter(vendor => {
            const fullName = `${vendor.firstName} ${vendor.lastName}`.toLowerCase();
            return fullName.includes(searchTerm); // Check if full name contains search term
        });
        
        displayVendors(filteredVendors); // Display filtered vendors
    }
    
    // Add event listener for the search input
    searchInput.addEventListener('input', filterVendors);
    
    // Function to show popup and fetch archive data
    function showPopup(vendorUserId, storeName) {
        const archiveRef = ref(database, `archive/${vendorUserId}/`);
        onValue(archiveRef, (snapshot) => {
            const archives = snapshot.val();
            const imageContainer = document.querySelector('.popup-images');
            imageContainer.innerHTML = ''; // Clear previous images
            let found = false;

            if (archives) {
                // Get keys of each archive entry, then reverse them to fetch the latest entries first
                const archiveKeys = Object.keys(archives).reverse();
                
                archiveKeys.forEach(entryId => {
                    const entry = archives[entryId];
                    found = true;

                    // Create new image element for each archive entry
                    const img = document.createElement('img');
                    img.src = entry.oldPermitImageUrl;
                    img.alt = 'Permit Image';
                    imageContainer.appendChild(img);

                    img.addEventListener("click", () => {
                      openFullScreen(img.src);
                    });

                    // Create new paragraph element for the expiration date
                    const dateParagraph = document.createElement('p');
                    dateParagraph.className = 'image-date';
                    dateParagraph.textContent = entry.oldExpirationDate;
                    imageContainer.appendChild(dateParagraph);
                });
            }

            // Show the popup only if archive data is found
            if (found) {
                document.getElementById("popupTitle").textContent = `Archive for Store: ${storeName}`; // Display store name
                document.getElementById("popupOverlay").style.display = "flex";
            } else {
                alert("No archive found for this vendor.");
            }
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
    
    // Close popup function
    function closePopup() {
        document.getElementById("popupOverlay").style.display = "none";
    }
    
    // Call the function to fetch vendors on page load
    document.addEventListener("DOMContentLoaded", function () {
        fetchVendors();
    });
</script>