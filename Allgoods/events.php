<?php
@include 'backend/events.php';
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/eventss.css">
    <link rel="icon" type="image/png" href="img/logos.png">
    <link href='https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css">
    <title>Events</title> 
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
                            <i class='bx bxs-food-menu icon'></i>
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

                    <li class="nav-link" style="background: white; border-radius: 5px;">
                        <a href="events.php">
                            <i class='bx bxs-calendar-event icon' style="color: black;"></i>
                            <span class="text nav-text" style="color: black;">Events</span>
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
                    <i class='bx bxs-calendar-event icon'></i>
                    <span class="text">Events</span>
                </div>
            </div>
    
            <div class="event-creation">
                <h3>Create Event</h3>
                <form id="eventForm">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="title" style="color:#4B5966; font-weight: bold;">Title:</label>
                            <input type="text" id="title" placeholder="Enter event title" required>
                        </div>
            
                        <div class="form-group">
                            <label for="eventCategory" style="color:#4B5966; font-weight: bold;">Event Category:</label>
                            <select id="eventCategory" required>
                                <option value="Christmas">Christmas</option>
                                <option value="New Year">New Year</option>
                                <option value="Special Sales">Special Sales</option>
                            </select>
                        </div>
                    </div>
            
                    <div class="form-row">
                        <div class="form-group">
                            <label for="discount" style="color:#4B5966; font-weight: bold;">Discount (%):</label>
                            <input type="number" id="discount" min="0" max="100" placeholder="e.g., 25" required>
                        </div>
            
                        <div class="form-group">
                            <label for="description" style="color:#4B5966; font-weight: bold;">Description:</label>
                            <textarea id="description" placeholder="Describe the event" required></textarea>
                        </div>
                    </div>
            
                    <div class="form-row">
                        <div class="form-group">
                            <label for="eventDate" style="color:#4B5966; font-weight: bold;">Event Date:</label>
                            <input type="date" id="eventDate" required>
                        </div>
            
                        <div class="form-group">
                            <label for="images" style="color:#4B5966; font-weight: bold;">Upload Images (max 5):</label>
                            <input type="file" id="images" accept="image/*" multiple required>
                            <div id="imagePreviewContainer" class="image-preview-container"></div>
                        </div>
                    </div>
            
                    <button id="uploadButton" style="display: block;">Upload Event</button>
                    <!-- Update and Cancel Buttons with Icons -->
                    <div class="button-container">
                        <button id="updateButton" style="display: none;" class="update-btn">
                            <i class="bx bx-check"></i> Update
                        </button>
                        <button id="cancelButton" style="display: none;" class="cancel-btn">
                            <i class="bx bx-x"></i> Cancel
                        </button>
                    </div>

                </form>
            </div>
    
            <div class="event-view">
                <h3>Uploaded Events</h3>
                <div id="eventList" class="event-list"></div>
            </div>
            <!-- Progress Bar -->
            <div id="progressBarContainer" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 80%; max-width: 400px; height: 10px; background-color: #f3f3f3; border: 1px solid #ddd; border-radius: 5px;">
                <div id="progressBar" style="width: 0%; height: 100%; background-color: #4caf50; border-radius: 5px;"></div>
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