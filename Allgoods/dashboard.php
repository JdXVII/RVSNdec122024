<?php
@include 'backend/dashboard.php';
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/dash.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    
    <title>Dashboard</title> 
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
                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="dashboard.php">
                            <i class='bx bxs-home icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Dashboard</span>
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
    <div class="top">
            
        </div>

        <div class="dash-content">
            <div class="overview">
                <div class="title">
                    <i class='bx bxs-dashboard' ></i>
                    <span class="text">Dashboard</span>
                </div>

                <div class="boxes">
                    <div class="box box1">
                        <i class='bx bxs-user' ></i>
                        <span class="text">Total Buyers</span>
                        <span class="number"></span>
                    </div>
                    <div class="box box2">
                        <i class='bx bxs-user-circle'></i>
                        <span class="text">Total Vendors</span>
                        <span class="number"></span>
                    </div>
                    <div class="box box3">
                        <i class='bx bxs-basket' ></i>
                        <span class="text">Total Sales Transactions</span>
                        <span class="number"></span>
                    </div>
                    <div class="box box4">
                        <i class='bx bxl-product-hunt'></i>
                        <span class="text">Total Products</span>
                        <span class="number"></span>
                    </div>
                </div>
            </div>

            <div class="top-generation">
                <div class="title">
                    <i class='bx bxs-file'></i>
                    <span class="text">Top Performer Store</span>
                </div>
            
                <div class="date-filter">
                    <div>
                        <label for="start-date">Select Date:</label>
                        <input type="date" id="start-date" name="start-date">
                    </div>
                    
                    <div>
                        <label for="category">Select Category:</label>
                        <select id="category">
                            <option value="">All Categories</option>
                            
                        </select>
                    </div>
            
                    <button>Fetch</button>
                </div>
            
                <table>
                    <thead>
                        <tr>
                            <th>Ranking</th>
                            <th>Store Name</th>
                            <th>Best Selling Product</th>
                            <th>Sold Quantity</th>
                        </tr>
                    </thead>
                    <tbody id="report-table-body">
                    <?php
                    @include 'backend/top_vendors.php';
                    ?>
                    </tbody>
                </table>
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