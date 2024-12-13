<script type="module">
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-app.js";
    import { getDatabase, ref, push, set, get, update, remove } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-database.js";
    import { getStorage, ref as storageRef, uploadBytes, getDownloadURL, deleteObject } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-storage.js";

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

    let currentProductId = null;

    // Fetch units and categories from Firebase
    async function loadUnitsAndCategories() {
        try {
            // Fetch units from 'units' table
            const unitsSnapshot = await get(ref(database, 'units/'));
            const units = unitsSnapshot.val();
            const unitSelect = document.getElementById('productQuantity');
            unitSelect.innerHTML = '<option value="">Select Unit</option>';
            
            // Populate unit options dynamically
            for (const key in units) {
                const option = document.createElement('option');
                option.value = units[key];
                option.textContent = units[key];
                unitSelect.appendChild(option);
            }
    
            // Fetch categories from 'product_category' table
            const categoriesSnapshot = await get(ref(database, 'product_category/'));
            const categories = categoriesSnapshot.val();
            const categorySelect = document.getElementById('productCategory');
            categorySelect.innerHTML = '<option value="">Select Category</option>';
            
            // Populate category options dynamically
            for (const key in categories) {
                const option = document.createElement('option');
                option.value = categories[key];
                option.textContent = categories[key];
                categorySelect.appendChild(option);
            }
        } catch (error) {
            console.error("Error loading units and categories: ", error);
        }
    }

    // Function to add a product
    async function addProduct(name, minPrice, maxPrice, quantity, imageFile, category) {
        try {
            const newProductRef = push(ref(database, 'products/'));
    
            // Upload image
            const imageRef = storageRef(storage, 'product_images/' + newProductRef.key + '_' + imageFile.name);
            await uploadBytes(imageRef, imageFile);
            const imageUrl = await getDownloadURL(imageRef);
    
            // Set current date for history entry
            const currentDate = new Date().toLocaleDateString();
            const initialHistoryEntry = { 
                date: currentDate, 
                price: { min: String(minPrice), max: String(maxPrice) } 
            };
    
            // Save product details with timestamp and initial history
            await set(newProductRef, {
                name: name,
                price: { min: String(minPrice), max: String(maxPrice) },
                quantity: quantity,
                image: imageUrl,
                category: category,
                history: [initialHistoryEntry], 
                createdAt: new Date().toISOString() 
            });
    
            showPopupMessage('Product added successfully!');
            loadProducts();
        } catch (error) {
            console.error("Error adding product: ", error);
            showPopupMessage('Error adding product: ' + error.message, true);
        }
    }

    // Handle form submission
    document.getElementById('productForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const name = document.getElementById('productName').value;
        const minPrice = parseFloat(document.getElementById('productMinPrice').value);
        const maxPrice = parseFloat(document.getElementById('productMaxPrice').value);
        const quantity = document.getElementById('productQuantity').value;
        const imageFile = document.getElementById('productImage').files[0];
        const category = document.getElementById('productCategory').value;
    
        // Validation for correct price format and range
        if (minPrice > maxPrice) {
            showPopupMessage('Invalid price range! The minimum price cannot be greater than the maximum price.', true);
            return;
        }
    
        // Add product
        addProduct(name, minPrice, maxPrice, quantity, imageFile, category);
        document.getElementById('productForm').reset();
    });

    async function populateDropdowns() {
        try {
            const unitsRef = ref(database, 'units');
            const categoriesRef = ref(database, 'product_category');
    
            // Fetch units
            const unitsSnapshot = await get(unitsRef);
            const units = unitsSnapshot.val();
    
            const unitDropdown = document.getElementById('updateProductUnit');
            unitDropdown.innerHTML = '<option value="" disabled selected>Select a unit</option>'; // Reset options
            for (const key in units) {
                const option = document.createElement('option');
                option.value = units[key];
                option.textContent = units[key];
                unitDropdown.appendChild(option);
            }
    
            // Fetch categories
            const categoriesSnapshot = await get(categoriesRef);
            const categories = categoriesSnapshot.val();
    
            const categoryDropdown = document.getElementById('updateProductCategory');
            categoryDropdown.innerHTML = '<option value="" disabled selected>Select a category</option>'; // Reset options
            for (const key in categories) {
                const option = document.createElement('option');
                option.value = categories[key];
                option.textContent = categories[key];
                categoryDropdown.appendChild(option);
            }
        } catch (error) {
            console.error('Error fetching dropdown data:', error);
        }
    }

    // Attach a one-time event listener for the update form submission
    document.getElementById('updateForm').addEventListener('submit', function(e) {
        e.preventDefault();
    
        const productName = document.getElementById('updateProductName').value;
        const minPrice = parseFloat(document.getElementById('updateProductMinPrice').value);
        const maxPrice = parseFloat(document.getElementById('updateProductMaxPrice').value);
        const productDescription = document.getElementById('updateProductUnit').value;
        const productCategory = document.getElementById('updateProductCategory').value;
        const productUnit = document.getElementById('updateProductUnit').value;
    
        if (isNaN(minPrice) || isNaN(maxPrice)) {
            showPopupMessage('Invalid format! Please enter valid numbers for both min and max prices.', true);
            return;
        }
    
        if (minPrice > maxPrice) {
            showPopupMessage('Invalid price range! The minimum price cannot be greater than the maximum price.', true);
            return;
        }

        // Confirmation dialog before proceeding with the update
        const confirmUpdate = confirm('Are you sure you want to update this product?');
        if (!confirmUpdate) {
            return;  // Stop the update process if the user cancels
        }

        updateProduct(currentProductId, {
            name: productName,
            price: { min: minPrice, max: maxPrice },
            quantity: productDescription,
            category: productCategory
        });
    
        document.getElementById('updatePopup').style.display = 'none';
    });
    
    // Open update popup and set the current product ID and fields
    function openUpdatePopup(productId, product) {
        currentProductId = productId;
    
        // Populate input fields
        document.getElementById('updateProductName').value = product.name;
        document.getElementById('updateProductMinPrice').value = product.price.min;
        document.getElementById('updateProductMaxPrice').value = product.price.max;
    
        // Populate dropdowns and select current values
        populateDropdowns().then(() => {
            document.getElementById('updateProductUnit').value = product.quantity || '';
            document.getElementById('updateProductCategory').value = product.category || '';
        });
    
        document.getElementById('updatePopup').style.display = 'flex';
    }

    
    // Update product in Firebase with all fields
    async function updateProduct(productId, product) {
        try {
            const currentDate = new Date().toLocaleDateString();
            const priceToSave = {
                min: String(product.price.min),
                max: String(product.price.max)
            };
    
            const historyEntry = {
                date: currentDate,
                price: priceToSave
            };
    
            // Include the unit in the updated data
            await update(ref(database, 'products/' + productId), {
                name: product.name,
                price: priceToSave,
                quantity: product.quantity,
                category: product.category,
                history: [...(await getHistory(productId)), historyEntry]
            });
    
            showPopupMessage('Product updated successfully!');
            await loadProducts();
        } catch (error) {
            console.error("Error updating product: ", error);
            showPopupMessage('Error updating product: ' + error.message, true);
        }
    }

    // Show and hide the add unit/category forms
    document.getElementById('addUnitBtn').addEventListener('click', function() {
        document.getElementById('addCategoryForm').classList.add('hidden');
        document.getElementById('addUnitForm').classList.toggle('hidden');
    });
    
    document.getElementById('addCategoryBtn').addEventListener('click', function() {
        document.getElementById('addUnitForm').classList.add('hidden');
        document.getElementById('addCategoryForm').classList.toggle('hidden');
    });
    
    // Handle adding a new unit with confirmation
    document.getElementById('submitUnitBtn').addEventListener('click', async function() {
        const newUnitName = document.getElementById('newUnitName').value;
        if (!newUnitName) {
            showPopupMessage('Please enter a unit name.', true);
            return;
        }
    
        // Ask for confirmation before adding the unit
        const confirmAdd = window.confirm('Are you sure you want to add this unit?');
        if (!confirmAdd) {
            return; // If not confirmed, stop the operation
        }
    
        try {
            // Add new unit to Firebase
            const unitRef = ref(database, 'units');
            const newUnitKey = push(unitRef).key;
            const newUnit = { [`${newUnitKey}`]: newUnitName };
    
            await update(unitRef, newUnit);
            showPopupMessage('Unit added successfully!');
            document.getElementById('newUnitName').value = '';
            loadUnits();
        } catch (error) {
            console.error('Error adding unit:', error);
            showPopupMessage('Error adding unit: ' + error.message, true);
        }
    });
    
    // Handle adding a new category with confirmation
    document.getElementById('submitCategoryBtn').addEventListener('click', async function() {
        const newCategoryName = document.getElementById('newCategoryName').value;
        if (!newCategoryName) {
            showPopupMessage('Please enter a category name.', true);
            return;
        }
    
        // Ask for confirmation before adding the category
        const confirmAdd = window.confirm('Are you sure you want to add this category?');
        if (!confirmAdd) {
            return; // If not confirmed, stop the operation
        }
    
        try {
            // Add new category to Firebase
            const categoryRef = ref(database, 'product_category');
            const newCategoryKey = push(categoryRef).key;
            const newCategory = { [`${newCategoryKey}`]: newCategoryName };
    
            await update(categoryRef, newCategory);
            showPopupMessage('Category added successfully!');
            document.getElementById('newCategoryName').value = '';
            loadCategories();
        } catch (error) {
            console.error('Error adding category:', error);
            showPopupMessage('Error adding category: ' + error.message, true);
        }
    });
    
    const unitsRef = ref(database, 'units');
    const categoriesRef = ref(database, 'product_category');
    
    // Load and display units with delete options
    async function loadUnits() {
        const snapshot = await get(unitsRef);
        const units = snapshot.val();
        const unitList = document.getElementById('unitList');
        unitList.innerHTML = ''; // Clear current list
    
        for (const key in units) {
            const unitName = units[key];
            const unitItem = document.createElement('div');
            unitItem.classList.add('unit-item');
            unitItem.innerHTML = `
                <span class="unit-name">${unitName}</span>
                <button class="delete" style="background-color:#d52424de;" data-id="${key}">Delete</button>
            `;
            unitList.appendChild(unitItem);
        }
    
        // Attach event listeners for delete buttons
        const deleteButtons = document.querySelectorAll('.unit-item .delete');
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                const unitKey = button.getAttribute('data-id');
                deleteUnit(unitKey);
            });
        });
    }
    
    // Load and display categories with delete options
    async function loadCategories() {
        const snapshot = await get(categoriesRef);
        const categories = snapshot.val();
        const categoryList = document.getElementById('categoryList');
        categoryList.innerHTML = ''; // Clear current list
    
        for (const key in categories) {
            const categoryName = categories[key];
            const categoryItem = document.createElement('div');
            categoryItem.classList.add('category-item');
            categoryItem.innerHTML = `
                <span class="category-name">${categoryName}</span>
                <button class="delete" style="background-color:#d52424de;" data-id="${key}">Delete</button>
            `;
            categoryList.appendChild(categoryItem);
        }
    
        // Attach event listeners for delete buttons
        const deleteButtons = document.querySelectorAll('.category-item .delete');
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                const categoryKey = button.getAttribute('data-id');
                deleteCategory(categoryKey);
            });
        });
    }
    
    // Delete unit with confirmation
    async function deleteUnit(unitKey) {
        const confirmDelete = window.confirm('Are you sure you want to delete this unit?');
        if (confirmDelete) {
            try {
                await remove(ref(database, `units/${unitKey}`));
                showPopupMessage('Unit deleted successfully!');
                loadUnits(); // Refresh unit list after deletion
            } catch (error) {
                console.error('Error deleting unit:', error);
                showPopupMessage('Error deleting unit: ' + error.message, true);
            }
        }
    }
    
    // Delete category with confirmation
    async function deleteCategory(categoryKey) {
        const confirmDelete = window.confirm('Are you sure you want to delete this category?');
        if (confirmDelete) {
            try {
                await remove(ref(database, `product_category/${categoryKey}`));
                showPopupMessage('Category deleted successfully!');
                loadCategories(); // Refresh category list after deletion
            } catch (error) {
                console.error('Error deleting category:', error);
                showPopupMessage('Error deleting category: ' + error.message, true);
            }
        }
    }

    // Function to fetch the current history for a product
    async function getHistory(productId) {
        const productSnapshot = await get(ref(database, 'products/' + productId));
        return productSnapshot.val().history || [];
    }


    // Close update popup
    document.querySelector('.close-btn').addEventListener('click', function() {
        document.getElementById('updatePopup').style.display = 'none';
    });

    // Load products from Firebase
    async function loadProducts() {
        try {
            const productTable = document.getElementById('productTable');
            productTable.innerHTML = '';
    
            const productsSnapshot = await get(ref(database, 'products/'));
            const products = productsSnapshot.val();
    
            if (products) {
                // Convert products object to an array and sort by createdAt
                const productArray = Object.entries(products).map(([key, value]) => ({ id: key, ...value }));
                productArray.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Sort by createdAt in descending order
    
                productArray.forEach(product => {
                    const row = document.createElement('tr');
    
                    const imageCell = document.createElement('td');
                    const imageElement = document.createElement('img');
                    imageElement.src = product.image;
                    imageElement.classList.add('pic');
                    imageCell.appendChild(imageElement);
                    row.appendChild(imageCell);
    
                    const nameCell = document.createElement('td');
                    nameCell.textContent = product.name;
                    row.appendChild(nameCell);
    
                    const priceCell = document.createElement('td');
                    priceCell.textContent = product.price.min + ' - ' + product.price.max;
                    row.appendChild(priceCell);
    
                    const quantityCell = document.createElement('td');
                    quantityCell.textContent = product.quantity;
                    row.appendChild(quantityCell);
    
                    const categoryCell = document.createElement('td');
                    categoryCell.textContent = product.category;
                    row.appendChild(categoryCell);
    
                    const actionCell = document.createElement('td');
    
                    const updateButton = document.createElement('button');
                    updateButton.classList.add('update-btn');
                    updateButton.textContent = 'Update';
                    updateButton.addEventListener('click', function() {
                        openUpdatePopup(product.id, product);
                    });
                    actionCell.appendChild(updateButton);
    
                    const viewButton = document.createElement('button');
                    viewButton.classList.add('view-btn');
                    viewButton.textContent = 'View History';
                    viewButton.addEventListener('click', function() {
                        viewPriceHistory(product.history);
                    });
                    actionCell.appendChild(viewButton);
    
                    const deleteButton = document.createElement('button');
                    deleteButton.classList.add('delete-btn');
                    deleteButton.textContent = 'Delete';
                    deleteButton.addEventListener('click', function() {
                        deleteProduct(product.id);
                    });
                    actionCell.appendChild(deleteButton);
    
                    row.appendChild(actionCell);
                    productTable.appendChild(row);
                });
            }
        } catch (error) {
            console.error("Error loading products: ", error);
        }
    }


    // Function to delete a product from Firebase with confirmation
    async function deleteProduct(productId) {
        try {
            const confirmDelete = confirm("Are you sure you want to delete this product?");
            if (confirmDelete) {
                const productSnapshot = await get(ref(database, 'products/' + productId));
                const product = productSnapshot.val();
                const imageUrl = product.image;
                const imageRef = storageRef(storage, imageUrl);

                await deleteObject(imageRef);
                await remove(ref(database, 'products/' + productId));
                showPopupMessage('Product deleted successfully!');
                loadProducts();
            }
        } catch (error) {
            console.error("Error deleting product: ", error);
            showPopupMessage('Error deleting product: ' + error.message, true);
        }
    }

    // Populate category dropdown dynamically
    async function populateCategoryDropdown() {
        try {
            const categoriesRef = ref(database, 'product_category');
            const categoriesSnapshot = await get(categoriesRef);
            const categories = categoriesSnapshot.val();
    
            const categoryDropdown = document.getElementById('categoryDropdown');
            categoryDropdown.innerHTML = '<option value="">All Categories</option>'; // Reset options
    
            for (const key in categories) {
                const option = document.createElement('option');
                option.value = categories[key];
                option.textContent = categories[key];
                categoryDropdown.appendChild(option);
            }
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    }
    
    // Filter products based on search input and category
    document.getElementById('categoryDropdown').addEventListener('change', filterProducts);
    document.getElementById('searchInput').addEventListener('input', filterProducts);
    
    function filterProducts() {
        const searchText = document.getElementById('searchInput').value.toLowerCase().trim();
        const selectedCategory = document.getElementById('categoryDropdown').value.toLowerCase();
        const productRows = document.querySelectorAll('#productTable tr');
    
        productRows.forEach(row => {
            const productName = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
            const productCategory = row.querySelector('td:nth-child(5)').textContent.toLowerCase();
    
            const matchesCategory = selectedCategory === '' || productCategory.includes(selectedCategory);
            const matchesSearch = productName.includes(searchText);
    
            row.style.display = matchesCategory && matchesSearch ? '' : 'none';
        });
    }
    
    // Load categories into the dropdown on page load
    document.addEventListener('DOMContentLoaded', async () => {
        await populateCategoryDropdown();
    });

    // Function to show popup message
    function showPopupMessage(message, isError = false) {
        const popupMessage = document.getElementById('popupMessage');
        popupMessage.textContent = message;
        popupMessage.classList.toggle('error', isError);
        popupMessage.style.display = 'block';

        setTimeout(() => {
            popupMessage.style.display = 'none';
        }, 3000);
    }

    // View price history
    function viewPriceHistory(history) {
        const historyContainer = document.getElementById('historyContainer');
        historyContainer.innerHTML = ''; // Clear previous entries
    
        // Sort history in reverse order (latest first)
        const sortedHistory = history.sort((a, b) => new Date(b.date) - new Date(a.date));
    
        sortedHistory.forEach(entry => {
            const historyEntry = document.createElement('p');
            historyEntry.classList.add('history-entry_history'); // Updated class name
            historyEntry.textContent = `${entry.date}: Price (min - max) ${entry.price.min} - ${entry.price.max}`;
            historyContainer.appendChild(historyEntry);
        });
    
        // Display the popup
        const historyPopup = document.getElementById('historyPopup');
        historyPopup.style.display = 'block';
    }
    
    // Close history popup
    function closeHistoryPopup() {
        const historyPopup = document.getElementById('historyPopup');
        historyPopup.style.display = 'none';
    }

    loadUnitsAndCategories();
    // Initial load of products
    loadProducts();

    loadUnits();
    loadCategories();
</script>
