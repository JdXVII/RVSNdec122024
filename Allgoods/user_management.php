<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/userss.css">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <title>User Management</title> 
    <link rel="icon" type="image/png" href="img/logos.png">
    <script src="./libs/email.min.js"></script>
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

                    <li class="nav-link">
                        <a href="product_prices.php">
                            <i class='bx bxs-dollar-circle icon' ></i>
                            <span class="text nav-text">Product Prices</span>
                        </a>
                    </li>

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="user_management.php">
                            <i class='bx bxs-user-account icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">User Management</span>
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
        <div class="top">
            <img style="background: white" src="img/user_permit.svg" alt="" id="notificationBell">
            <span id="notificationCount" class="badge"></span>
        </div>
    
        <div id="notificationModal" class="modal">
            <div class="modal-content">
                <span class="close" id="closeNotificationModalBtn">&times;</span>
                <div class="notification-columns">
                    <div class="left-column">
                        <h2>Expired Permits</h2>
                        <ul id="expiredPermitsList">
                            <!-- List items for expired permits -->
                        </ul>
                    </div>
                    <div class="right-column">
                        <h2>Soon to Expire Permits</h2>
                        <ul id="soonToExpirePermitsList">
                            <!-- List items for soon to expire permits -->
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="dash-content">
            <div class="overview">
                <div class="title">
                    <i class='bx bxs-user-account'></i>
                    <span class="text">User Management</span>
                </div>
                <div class="search-bar">
                    <div class="search-container">
                        <input type="text" id="searchInputbar" placeholder="Search..." title="Search Vendor Store name and buyer name" />
                        <i class="bx bx-search"></i>
                    </div>
                </div>
                <div class="user-options">
                    <div class="user-option">
                        <div class="option-content">
                            <i class='bx bxs-user-voice icons'></i>
                            <span class="option-text">Vendor Management</span>
                            <button class="manage-btn vendor-manage-btn">Manage</button>
                        </div>
                    </div>
                    <div class="user-option">
                        <div class="option-content">
                            <i class='bx bxs-user icons'></i>
                            <span class="option-text">Buyer Management</span>
                            <button class="manage-btn buyer-manage-btn">Manage</button>
                        </div>
                    </div>
                    <div class="user-option">
                        <div class="option-content">
                            <i class='bx bxs-user-check icons'></i>
                            <span class="option-text">Approved Vendors</span>
                            <button class="manage-btn approved-vendor-manage-btn">Manage</button>
                        </div>
                    </div>
                    <div class="user-option">
                        <div class="option-content">
                            <i class='bx bxs-user-check icons'></i>
                            <span class="option-text">Approved Buyers</span>
                            <button class="manage-btn approved-buyer-manage-btn">Manage</button>
                        </div>
                    </div>
                </div>
                <table class="vendor-table" id="vendorTable">
                    <thead>
                        <tr>
                            <th>Vendors Profile</th>
                            <th>Permit Image</th>
                            <th>Full Name</th>
                            <th>Store Name</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="vendor-table-body">
                        <?php
                        @include 'backend/vendor_manage.php'
                        ?>
                    </tbody>
                </table>
                <!-- Modal for viewing vendor info -->
                <div id="vendorInfoModal" style="display: none;">
                    <div class="modal-content">
                        <h2 style="text-align: center;">Vendor Information</h2>
                        <p id="vendorInfo"></p>
                        <button id="closeVendorInfoBtn">&times;</button>
                    </div>
                </div>

                <table class="buyer-table" id="buyerTable">
                    <thead>
                        <tr>
                            <th>Buyers Profile</th>
                            <th>Valid (ID) Image</th>
                            <th>Full Name</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="buyer-table-body">
                        <?php
                        @include 'backend/buyer_manage.php'
                        ?>
                    </tbody>
                </table>
                <!-- Modal for viewing vendor info -->
                <div id="buyerInfoModal" style="display: none;">
                    <div class="modal-content">
                        <h2 style="text-align: center;">Buyer Information</h2>
                        <p id="buyerInfo"></p>
                        <button id="closeBuyerInfoBtn">&times;</button>
                    </div>
                </div>
                <table class="approved-vendor-table" id="approvedVendorTable">
                    <thead>
                        <tr>
                            <th>Vendors Profile</th>
                            <th>Permit Image</th>
                            <th>Store Name</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="vendorTableBody">
                        <?php
                        @include 'backend/approved_vendor_manage.php'
                        ?>
                    </tbody>
                </table>
                <!-- Date Picker Modal -->
                <div id="datePickerModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background-color:white; padding:20px; border:1px solid #ccc; border-radius:10px;">
                    <h3>Select New Expiration Date</h3>
                    <input type="date" id="expirationDateInput" style="width: 100%; padding: 10px; font-size: 16px;">
                    <div style="display: flex; justify-content: space-between; margin-top: 10px;">
                        <button id="saveDateBtn" style="padding:10px; background-color:green; color:white; border:none; width:48%;">Save</button>
                        <button id="cancelBtn" style="padding:10px; background-color:red; color:white; border:none; width:48%;">Cancel</button>
                    </div>
                </div>

                <div id="vendorModal" style="display:none;">
                    <div class="modal-content">
                    <h2 style="text-align: center;">Vendor Information</h2>
                        <span id="closeVendorInfo" class="close">&times;</span>
                        <div id="vendorInfoContent"></div>
                    </div>
                </div>

                <!-- Warning Modal -->
                <div id="warningModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background-color:white; padding:20px; border:1px solid #ccc; border-radius:10px;">
                    <h3>Select Warning</h3>
                    <div id="firstViolationContainerVendor">
                      <label>
                        <input type="radio" name="warningType" value="1st Violation"> 1st Violation
                      </label>
                    </div>
                    
                    <div id="secondViolationContainerVendor" style="display:none;">
                      <label>
                        <input type="radio" name="warningType" value="2nd Violation"> 2nd Violation
                      </label>
                    </div>
                
                    <div id="warningOptions1st" style="margin-top:10px;">
                        <h4>1st Warning Options</h4>
                        <label><input type="radio" name="warning1st" value="False Product Information"> False Product Information</label><br>
                        <label><input type="radio" name="warning1st" value="Failure to Update Order Status"> Failure to Update Order Status</label><br>
                        <label><input type="radio" name="warning1st" value="Delayed Delivery"> Delayed Delivery</label><br>
                        <label><input type="radio" name="warning1st" value="Poor Communication"> Poor Communication</label><br>
                        <label><input type="radio" name="warning1st" value="Others"> Others (Please specify)</label>
                        <input type="text" id="customReasonInput" placeholder="Enter custom reason" style="width:100%; margin-top:5px;">
                    </div>
                
                    <div style="display: flex; justify-content: space-between; margin-top: 10px;">
                        <button id="sendWarningBtn" style="padding:10px; margin:5px; background-color:green; color:white; border:none; width:48%;">Send</button>
                        <button id="closeWarningModal" style="padding:10px; margin:5px; background-color:red; color:white; border:none; width:48%;">Cancel</button>
                    </div>
                </div>

                <table class="approved-buyer-table" id="approvedBuyerTable">
                    <thead>
                        <tr>
                            <th>Buyers Profile</th>
                            <th>Buyers Name</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="buyerTableBody">
                        <?php
                        @include 'backend/approved_buyer_manage.php'
                        ?>
                    </tbody>
                </table>
                <div id="buyerInfoM" style="display:none;">
                    <div class="modal-content">
                    <h2 style="text-align: center;">Buyer Information</h2>
                        <span id="closeBuyerInfo" class="close">&times;</span>
                        <div id="buyerInfoContent"></div>
                    </div>
                </div>

                <!-- Warning Modal for Buyer -->
                <div id="warningModal_buyer" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background-color:white; padding:20px; border:1px solid #ccc; border-radius:10px;">
                  <h3>Select Warning</h3>
                  <div id="firstViolationContainer">
                    <label>
                      <input type="radio" name="warningType_buyer" value="1st Violation"> 1st Violation
                    </label>
                  </div>
                  
                  <div id="secondViolationContainer" style="display:none;">
                    <label>
                      <input type="radio" name="warningType_buyer" value="2nd Violation"> 2nd Violation
                    </label>
                  </div>
                
                  <div id="warningOptions1st_buyer" style="display:none;">
                    <h4>1st Warning Options</h4>
                    <label><input type="radio" name="warning1st_buyer" value="False claims"> False claims</label><br>
                    <label><input type="radio" name="warning1st_buyer" value="Feedback manipulation"> Feedback manipulation</label><br>
                    <label><input type="radio" name="warning1st_buyer" value="Inappropriate Communication"> Inappropriate Communication</label><br>
                    <label><input type="radio" name="warning1st_buyer" value="Coercing Vendors"> Coercing Vendors</label><br>
                    <label><input type="radio" name="warning1st_buyer" value="Not Claiming Delivery"> Not Claiming Delivery</label><br>
                    <label><input type="radio" name="warning1st_buyer" value="Others"> Others (Please specify)</label>
                    <input type="text" id="customReasonInput_buyer" placeholder="Enter custom reason">
                  </div>
                
                  <div style="display: flex; justify-content: space-between; margin-top: 10px;">
                    <button id="sendWarningBtn_buyer" style="padding:10px; margin:5px; background-color:green; color:white; border:none; width:50%;">Send</button>
                    <button id="closeWarningModal_buyer" style="padding:10px; margin:5px; background-color:red; color:white; border:none; width:50%;">Cancel</button>
                  </div>
                </div>


            </div>
        </div>

        <div class="dash-content">
            <div class="overview">
                <div class="title">
                    <i class='bx bxs-user-account'></i>
                    <span class="text">Permit Renew</span>
                </div>

        <div class="search-bar">
            <div class="search-container">
                <input type="text" id="searchInput" placeholder="Search store name...">
                <i class="bx bx-search"></i>
            </div>
        </div>
        
        <table class="pending-vendor-table" >
            <thead>
                <tr>
                    <th>Vendors Profile</th>
                    <th>Permit Image</th>
                    <th>Store Name</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody >
                <?php
                @include 'backend/renew_vendor_permit.php'
                ?>
            </tbody>
        </table>
        <!-- Date Picker Modal -->
        <div id="dateModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background-color:white; padding:20px; border:1px solid #ccc; border-radius:10px;">
            <div>
                <h2>Select Expiration Date</h2>
                <input type="date" id="expirationDate" style="width: 100%; padding: 10px; font-size: 16px;" required>
                <button id="saveDateButton" style="padding:10px; background-color:green; color:white; border:none; width:48%;">Save</button>
                <button id="cancelDateButton" style="padding:10px; background-color:red; color:white; border:none; width:48%;">Cancel</button>
            </div>
        </div>
    </section>

    <script src="js/scripts.js"></script>
    <script src="js/users_managements.js"></script>
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

        // Get the search input field
        const searchInput = document.getElementById('searchInputbar');
        
        // Event listener for search input
        searchInput.addEventListener('input', function() {
          const searchText = searchInput.value.toLowerCase();
        
          // Filter Buyers Table
          filterBuyers(searchText);
        
          // Filter Vendors Table
          filterVendors(searchText);
        });
        
        // Function to filter buyers based on search text
        function filterBuyers(searchText) {
          const buyerTableBody = document.getElementById('buyerTableBody');
          const rows = buyerTableBody.getElementsByTagName('tr');
        
          for (let row of rows) {
            const nameCell = row.cells[1]; // Assuming the buyer's name is in the second column
            const fullName = nameCell ? nameCell.textContent.toLowerCase() : '';
            
            if (fullName.includes(searchText)) {
              row.style.display = '';
            } else {
              row.style.display = 'none';
            }
          }
        }
        
        // Function to filter vendors based on search text
        function filterVendors(searchText) {
          const vendorTableBody = document.getElementById('vendorTableBody');
          const rows = vendorTableBody.getElementsByTagName('tr');
        
          for (let row of rows) {
            const storeCell = row.cells[2]; // Assuming the vendor's store name is in the third column
            const storeName = storeCell ? storeCell.textContent.toLowerCase() : '';
            
            if (storeName.includes(searchText)) {
              row.style.display = '';
            } else {
              row.style.display = 'none';
            }
          }
        }
    </script>
</body>
</html>
