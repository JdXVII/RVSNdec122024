<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/price.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    <title>Product Prices</title> 
</head>
<body>
    <nav class="sidebar close">
        <header>
            <div class="image-text">
                <span class="image">
                    <img src="img/logos.png" alt="">
                </span>

                <div class="text logo-text">
                    <span class="name">Allgoods</span>
                    <span class="profession">Bagsakan Villasis</span>
                </div>
            </div>

            <i class='bx bx-chevron-right toggle'></i>
        </header>
        <br>
        <hr>

        <div class="menu-bar">
            <div class="menu">

                <ul class="menu-links">
                    <li class="nav-link">
                        <a href="dashboard.php">
                            <i class='bx bxs-home icon' ></i>
                            <span class="text nav-text">Dashboard</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="product_posting.php">
                            <i class='bx bxs-food-menu icon' ></i>
                            <span class="text nav-text">Product Posting</span>
                        </a>
                    </li>

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="product_prices.php">
                            <i class='bx bxs-dollar-circle icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Product Prices</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="user_management.php">
                            <i class='bx bxs-user-account icon'></i>
                            <span class="text nav-text">User Management</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="messages.php">
                            <i class='bx bxs-chat icon' ></i>
                            <span class="text nav-text">Messages</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="archive.php">
                            <i class='bx bxs-archive-in icon' ></i>
                            <span class="text nav-text">Archive</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="events.php">
                            <i class='bx bxs-calendar-event icon'></i>
                            <span class="text nav-text">Events</span>
                        </a>
                    </li>


                </ul>
            </div>

            <div class="bottom-content">
                <li class="search-box">
                    <a href="#">
                        <i class='bx bx-log-out icon' ></i>
                        <span class="text nav-text" id="logoutButton">Logout</span>
                    </a>
                </li>
                
            </div>
        </div>

    </nav>

    <section class="dashboard">
    <div class="dash-content">
        <div class="overview">
            <div class="title">
                <i class='bx bxs-dollar-circle'></i>
                <span class="text">Product Prices</span>
            </div>

            <div class="search-bar">
                <div class="dropdown-container">
                    <select id="categoryDropdown">
                        <option value="">All Categories</option>
                       
                    </select>
                </div>

                <div class="search-container">
                    <input type="text" id="searchInput" placeholder="Search products...">
                    <i class="bx bx-search"></i>
                </div>
                <div class="action-buttons">
                    <button class="btn_add" id="addUnitBtn">Add Unit</button>
                    <button class="btn_add" id="addCategoryBtn">Add Category</button>
                </div>
            </div>

            <div id="popupMessage" class="popup-message"></div>

            <div class="add-product-form">
                <h3>Add New Product</h3>
                <form id="productForm">
                    <div class="form-group">
                        <input type="text" id="productName" placeholder="Product Name" required>
                    </div>
                    <div class="form-group">
                        <input type="number" id="productMinPrice" placeholder="Min Price" required>
                    </div>
                    <div class="form-group">
                        <input type="number" id="productMaxPrice" placeholder="Max Price" required>
                    </div>
                    <div class="dropdown-container">
                        <select id="productQuantity" required>
                            <option value="">Select Unit</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="file" id="productImage" accept="image/*" required>
                    </div>
                    <div class="dropdown-container">
                        <select id="productCategory" required>
                            <option value="">Select Category</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="add-btn">Add Product</button>
                    </div>
                </form>
            </div>

            <!-- Form for adding new units or categories -->
            <div id="addUnitForm" class="hidden">
                <h4>Add New Unit</h4>
                <input type="text" id="newUnitName" placeholder="Unit Name">
                <button id="submitUnitBtn">Add Unit</button>
                <div id="unitList"></div>
            </div>
            
            <div id="addCategoryForm" class="hidden">
                <h4>Add New Category</h4>
                <input type="text" id="newCategoryName" placeholder="Category Name">
                <button id="submitCategoryBtn">Add Category</button>
                <div id="categoryList"></div>
            </div>
            <br>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Image</th>
                            <th>Product Name</th>
                            <th>Price(₱)</th>
                            <th>Unit</th>
                            <th>Category</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="productTable">
                        <tr>
                        <?php
                        @include 'backend/product_price.php';
                        ?>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Popup for Price History -->
    <div id="historyPopup" class="popup_history">
        <div class="popup-content_history">
            <span class="close-btn_history" onclick="closeHistoryPopup()">&times;</span>
            <h2>Price History</h2>
            <div id="historyContainer"></div>
        </div>
    </div>

    <!-- Popup Container for Updating Product -->
    <div id="updatePopup" class="popup-container">
        <div class="popup-content">
            <span class="close-btn">&times;</span>
            <h3>Update Product</h3>
            <form id="updateForm">
                <div class="form-group">
                    <input type="text" id="updateProductName" placeholder="Product Name" readonly>
                </div>
                <div class="add-product-form">
                    <div class="form-group">
                        <h5>Price Range:</h5>
                        <label for="updateProductMinPrice">Min Price</label>
                        <input type="number" id="updateProductMinPrice" placeholder="Min Price" required>
                    </div>
                    <div class="form-group">
                        <label for="updateProductMaxPrice">Max Price</label>
                        <input type="number" id="updateProductMaxPrice" placeholder="Max Price" required>
                    </div>
                </div>
                <div class="form-group dropdown-container">
                    <h5>Unit:</h5>
                    <select id="updateProductUnit" required>
                        <option value="" disabled selected>Select a unit</option>
                    </select>
                </div>
                <div class="form-group dropdown-container">
                    <h5>Category:</h5>
                    <select id="updateProductCategory" required>
                        <option value="" disabled selected>Select a category</option>
                    </select>
                </div>
                <div class="form-group">
                    <button type="submit" class="update-btn">Update Product</button>
                </div>
            </form>
        </div>
    </div>

</section>

    <script src="js/scripts.js"></script>
    <script>
        // Check if the user is already logged in
        if (!localStorage.getItem('isLoggedIn')) {
          // If not logged in, redirect to the login page
          window.location.href = 'login.php';
        }

        document.getElementById('logoutButton').addEventListener('click', () => {
            // Remove login state from localStorage
            localStorage.removeItem('isLoggedIn');
    
            // Redirect back to login page
            window.location.href = 'login.php';
        });

        // Close history popup
        function closeHistoryPopup() {
            const historyPopup = document.getElementById('historyPopup');
            historyPopup.style.display = 'none';
        }

    </script>

</body>
</html>
