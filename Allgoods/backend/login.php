<script type="module">
    // Import the functions you need from the SDKs you need
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-app.js";
    import { getDatabase, ref, get } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-database.js";
    import { getAuth, signInWithEmailAndPassword } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-auth.js";
    import { getAnalytics } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-analytics.js";

    // Your web app's Firebase configuration
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
    const analytics = getAnalytics(app);
    const database = getDatabase(app);
    const auth = getAuth(app);

    // Check if the user is already logged in
    if (localStorage.getItem('isLoggedIn')) {
        window.location.href = 'dashboard.php'; // Redirect to dashboard if already logged in
    }

    // Handle the login form submission
    document.getElementById('loginForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        // Get the input values
        const emailInput = document.getElementById('email').value; // Updated to email
        const passwordInput = document.getElementById('password').value;

        try {
            // Reference to the admin credentials in the Firebase Realtime Database
            const adminRef = ref(database, 'admin');
            const snapshot = await get(adminRef);

            if (snapshot.exists()) {
                const adminData = snapshot.val();
                const storedEmail = adminData.email; // Updated to use email
                const storedPassword = adminData.password;

                // Validate user input with stored credentials
                if (emailInput === storedEmail && passwordInput === storedPassword) {
                    // Sign in the user with Firebase Auth
                    await signInWithEmailAndPassword(auth, emailInput, passwordInput);

                    // Save login state in localStorage
                    localStorage.setItem('isLoggedIn', 'true');

                    // Redirect to dashboard.php on successful login
                    window.location.href = "dashboard.php";
                } else {
                    alert('Invalid email or password.');
                }
            } else {
                alert('No admin data found.');
            }
        } catch (error) {
            console.error("Error fetching admin data: ", error);
            alert('Login failed due to an error.');
        }
    });

    // Toggle password visibility
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    
    togglePassword.addEventListener('click', () => {
        // Toggle the type attribute of the password input field
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        
        // Toggle the icon class to show open or closed eye
        togglePassword.classList.toggle('fa-eye-slash');
    });
</script>
