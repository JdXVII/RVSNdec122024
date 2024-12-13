<?php
@include 'backend/login.php';
?>

<!DOCTYPE html>
<html lang="en">  
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Allgoods</title>
  <link rel="stylesheet" href="css/login.css"/>
  <link rel="icon" type="image/png" href="img/logos.png">
  <script src="https://kit.fontawesome.com/b0811c54d0.js" crossorigin="anonymous"></script>
</head>
<body>
  <div class="container">
    <div class="forms-container">
      <div class="signin">
        <form id="loginForm" class="sign-in-form" method="POST">
          <h2 class="title">Sign in</h2>
          <div class="input-field"> 
            <i class="fas fa-user"></i>
            <input type="email" id="email" placeholder="Email" required>
          </div>
          <div class="input-field">
            <i class="fas fa-lock"></i>
            <input type="password" id="password" placeholder="Password" required>
            <i class="fas fa-eye toggle-password" id="togglePassword"></i>
          </div>
          <input type="submit" value="Login" class="btn solid">
        </form> 
      </div>
    </div>
    <div class="panels-container"> 
      <div class="panel left-panel"> 
        <img src="img/logo.png" class="image" alt="Login">
      </div>
    </div>
  </div>

</body>
</html>
