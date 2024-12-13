<script type="module">
  // Import the functions you need from the SDKs you need
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-app.js";
  import { getDatabase, ref, onValue } from "https://www.gstatic.com/firebasejs/10.13.1/firebase-database.js";

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
  const database = getDatabase(app);

  // Reference to buyers, vendors, and purchases node in the database
  const buyersRef = ref(database, 'buyers');
  const vendorsRef = ref(database, 'vendors');
  const purchasesRef = ref(database, 'purchases');
  const productsRef = ref(database, 'upload_products');

  // Function to count the total buyers and update the DOM for box1
  onValue(buyersRef, (snapshot) => {
    const buyersData = snapshot.val();
    const totalBuyers = Object.keys(buyersData).length;

    // Update the number in box1
    document.querySelector('.box1 .number').textContent = totalBuyers;
  });

  // Function to count the total vendors and update the DOM for box2
  onValue(vendorsRef, (snapshot) => {
    const vendorsData = snapshot.val();
    const totalVendors = Object.keys(vendorsData).length;

    // Update the number in box2
    document.querySelector('.box2 .number').textContent = totalVendors;
  });

  // Function to count the total purchases and update the DOM for box3
  onValue(purchasesRef, (snapshot) => {
    const purchasesData = snapshot.val();
    const totalPurchases = Object.keys(purchasesData).length; 

    // Update the number in box3
    document.querySelector('.box3 .number').textContent = totalPurchases;
  });

  // Function to count the total purchases and update the DOM for box3
  onValue(productsRef, (snapshot) => {
    const productsData = snapshot.val();
    const totalProducts = Object.keys(productsData).length; 

    // Update the number in box3
    document.querySelector('.box4 .number').textContent = totalProducts;
  });
</script>