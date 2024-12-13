<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/archivess.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    
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

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="archive.php">
                            <i class='bx bxs-archive-in icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Archive</span>
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
                <i class='bx bxs-archive-in' ></i>
                    <span class="text">Archive</span>
                </div>

                <div class="archive-container">
                    <div class="search-bar">
                        <input type="text" id="searchInput" placeholder="Search...">
                        <i class='bx bx-search i'></i>
                    </div>

                    <div class="profile-cards" id="profileCards">
                        <?php
                            @include 'backend/archives.php';
                        ?>
                    </div>
                </div>

            </div>
        </div>
    </section>

    <div class="popup-overlay" id="popupOverlay">
        <div class="popup" id="popupContent">
            <h2 id="popupTitle"></h2>
            <div class="popup-images">
                <div class="image-container">
                    <img src="" alt="Profile Image">
                    <p class="image-date"></p>
                </div>
            </div>
            <i onclick="closePopup()" class='bx bxs-x-circle close-btn'></i>
        </div>
    </div>


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

        function closePopup() {
            document.getElementById("popupOverlay").style.display = "none";
        }
    </script>

</body>
</html>