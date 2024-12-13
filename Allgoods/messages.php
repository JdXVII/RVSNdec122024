<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/mssgss.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    <title>Messages</title> 
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

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="messages.php">
                            <i class='bx bxs-chat icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Messages</span>
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
                <!-- Vendor Messages Section -->
                <div class="title">
                    <i class='bx bxs-chat'></i>
                    <span class="text">Vendors</span>
                </div>
                <div class="search-bar">
                    <input type="text" id="search-input" placeholder="Search...">
                    <i class='bx bx-search search-icon'></i>
                </div>
                <ul class="name-list">
                    <?php @include 'backend/message_vendor.php'; ?>
                </ul>
            </div>
            
            <!-- Buyer Section -->
            <div class="buyer-overview">
                <div class="title">
                    <i class='bx bxs-user'></i>
                    <span class="text">Buyers</span>
                </div>
                <div class="search-bar">
                    <input type="text" id="buyer-search-input" placeholder="Search Buyers...">
                    <i class='bx bx-search search-icon'></i>
                </div>
                <ul class="buyer-name-list">
                    <?php @include 'backend/message_buyer.php'; ?>
                </ul>
            </div>
        </div>
    </section>

    <div id="chatbox" class="chatbox">
        <div class="chatbox-header">
            <span class="chatbox-title">
                <img id="chatbox-img" class="chatbox-img" src="" alt="Chat with">
                <span id="chatbox-user"></span>
            </span>
            <button id="chatbox-close" class="chatbox-close"><i class='bx bxs-x-circle' style="font-size: 30px;"></i></button>
        </div>
        <div class="chatbox-content">

        </div>
        <div class="chatbox-input">
             <input type="file" id="attach-image" style="display: none;" accept="image/*">
             <img src="img/new.png" alt="Attach Image" id="attach-image-icon" class="profile-img">
             <input type="text" id="chatbox-message" placeholder="Type a message">
             <button id="chatbox-send" >Send</button>
         </div>
    </div>

    <div id="buyer-chatbox" class="buyer-chatbox">
        <div class="buyer-chatbox-header">
            <span class="buyer-chatbox-title">
                <img id="buyer-chatbox-img" class="buyer-chatbox-img" src="" alt="Chat with">
                <span id="buyer-chatbox-user"></span>
            </span>
            <button id="buyer-chatbox-close" class="buyer-chatbox-close"><i class='bx bxs-x-circle' style="font-size: 30px;"></i></button>
        </div>
        <div class="buyer-chatbox-content">
    
        </div>
        <div class="buyer-chatbox-input">
            <input type="file" id="buyer-attach-image" style="display: none;" accept="image/*">
            <img src="img/new.png" alt="Attach Image" id="buyer-attach-image-icon" class="buyer-profile-img">
            <input type="text" id="buyer-chatbox-message" placeholder="Type a message">
            <button id="buyer-chatbox-send">Send</button>
        </div>
    </div>


    <script src="js/scripts.js"></script>

    <script>
        // Check if the user is logged in
        if (!localStorage.getItem('isLoggedIn')) {
          // If not logged in, redirect to the login page
          window.location.href = 'login.php';
        }

        document.getElementById('logoutButton').addEventListener('click', () => {
            // Remove login state from local Storage
            localStorage.removeItem('isLoggedIn');
    
            // Redirect back to login page
            window.location.href = 'login.php';
        });
    </script>

</body>
</html>