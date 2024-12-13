<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/postings.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    <title>Product Posting</title> 
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

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="product_posting.php">
                            <i class='bx bxs-food-menu icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Product Posting</span>
                        </a>
                    </li>

                    <li class="nav-link">
                        <a href="product_prices.php">
                            <i class='bx bxs-dollar-circle icon' ></i>
                            <span class="text nav-text">Product Prices</span>
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
    <div class="top"></div>

    <div class="dash-content">
        <div class="overview">
            <div class="title">
                <i class='bx bxs-food-menu'></i>
                <span class="text">Product Posting</span>
            </div>

            <table class="product-display-table">
                <thead>
                    <tr>
                        <th>Product Image</th>
                        <th>Product Name</th>
                        <th>Store Name</th>
                        <th>Product Price</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="product-table-body">
                    <?php @include 'backend/product_postings.php'; ?>
                </tbody>
            </table>
        </div>

        <!-- Modal for Product Preview -->
        <div id="preview-modal" class="modal">
            <div class="modal-content">
                <h2 id="preview-product-name">Product Name</h2>
                <div id="preview-product-images" class="preview-images-container">
                    <!-- Dynamically added images -->
                </div>
                <div class="product-details">
                    <div class="product-row">
                        <p><strong>Description:</strong></p>
                        <p id="preview-product-description">Description goes here.</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Category:</strong></p>
                        <p id="preview-product-category">Category Name</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Price:</strong></p>
                        <p id="preview-product-price">$0.00</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Unit:</strong></p>
                        <p id="preview-product-unit">Unit</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Product Type:</strong></p>
                        <p id="preview-producttype">Type</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Sale Type:</strong></p>
                        <p id="preview-product-saletype">Retail/Wholesale</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Delivery Option:</strong></p>
                        <p id="preview-product-delivery">Available/Not Available</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Payment Option:</strong></p>
                        <p id="preview-product-payment">Online/Cash</p>
                    </div>
                    <div class="product-row">
                        <p><strong>Stock:</strong></p>
                        <p id="preview-product-stock">In Stock/Out of Stock</p>
                    </div>
                </div>
                <div class="action-buttons">
                    <button class="btn-close" onclick="closePreviewModal()">Close</button>
                </div>
            </div>
        </div>
        
        <!-- Modal for Decline Reason -->
        <div id="decline-modal" class="modal">
            <div class="modal-content">
                <h2>Decline Product</h2>
                <p>Select a reason for declining the product:</p>
                <select id="decline-reason" onchange="toggleCustomReason()">
                    <option value="Low-Quality or Blurry Images">Low-Quality or Blurry Images</option>
                    <option value="Misleading Product Description">Misleading Product Description</option>
                    <option value="Prohibited or Restricted Items">Prohibited or Restricted Items</option>
                    <option value="Non-Original Images">Non-Original Images</option>
                    <option value="Others">Others</option>
                </select>
                <div id="custom-reason-container" style="display:none;">
                    <textarea id="custom-reason" placeholder="Please enter your reason..."></textarea>
                </div>
                <div class="action-buttons">
                    <button class="btn-close" onclick="closeDeclineModal()">Close</button>
                    <button class="btn-decline" onclick="confirmDecline()">Decline</button>
                </div>
            </div>
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
    </script>

</body>
</html>