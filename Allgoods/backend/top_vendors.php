<script type="module">
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-app.js";
    import { getDatabase, ref, child, get } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-database.js";

    // Initialize Firebase
    const firebaseConfig = {
        apiKey: "AIzaSyCSx9Dx9f6fbwBvf8b02Wo8W6py5mFkpzI",
        authDomain: "allgoods2024-5163a.firebaseapp.com",
        databaseURL: "https://allgoods2024-5163a-default-rtdb.firebaseio.com",
        projectId: "allgoods2024-5163a",
        storageBucket: "allgoods2024-5163a.appspot.com",
        messagingSenderId: "980525971714",
        appId: "1:980525971714:web:78849efcc5ac2325a7ea1b"
    };

    const app = initializeApp(firebaseConfig);
    const db = getDatabase(app);

    // Get current date in YYYY-MM-DD format
    function getCurrentDate() {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }

    // Populate categories and auto-fetch data on page load
    document.addEventListener('DOMContentLoaded', async () => {
        const dateInput = document.getElementById('start-date');
        dateInput.value = getCurrentDate();

        await populateCategories();
        await fetchAndDisplayData(dateInput.value, document.getElementById('category').value);
    });

    // Populate categories dropdown
    async function populateCategories() {
        const dbRef = ref(db);
        const reportsSnapshot = await get(child(dbRef, `reports`));
        const reports = reportsSnapshot.val();
        const categories = new Set();
        categories.add("All Categories"); // Ensure "All Categories" is added only once

        for (const vendorId in reports) {
            const salesData = reports[vendorId].sales;
            for (const year in salesData) {
                for (const month in salesData[year]) {
                    for (const week in salesData[year][month]) {
                        const weekInfo = salesData[year][month][week];
                        for (const category in weekInfo.categorySalesMap) {
                            categories.add(category);
                        }
                    }
                }
            }
        }

        const categorySelect = document.getElementById('category');
        categorySelect.innerHTML = ''; // Clear existing options to avoid duplicates

        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categorySelect.appendChild(option);
        });
    }

    // Fetch data based on selected date and category
    document.querySelector('button').addEventListener('click', async () => {
        const selectedDate = document.getElementById('start-date').value;
        const selectedCategory = document.getElementById('category').value;
        await fetchAndDisplayData(selectedDate, selectedCategory);
    });

    async function fetchAndDisplayData(selectedDate, selectedCategory) {
        if (!selectedDate) {
            alert('Please select a date');
            return;
        }

        try {
            const weekData = await fetchDataForDate(selectedDate, selectedCategory);
            const rankedVendors = rankVendors(weekData, selectedCategory);
            await updateTable(rankedVendors, selectedCategory);
        } catch (error) {
            console.error('Error fetching or processing data:', error);
        }
    }

    async function fetchDataForDate(selectedDate, selectedCategory) {
        const dbRef = ref(db);
        const reportsSnapshot = await get(child(dbRef, `reports`));
        const reports = reportsSnapshot.val();
        const matchedWeekData = [];

        for (const vendorId in reports) {
            const salesData = reports[vendorId].sales;
            for (const year in salesData) {
                for (const month in salesData[year]) {
                    for (const week in salesData[year][month]) {
                        const weekInfo = salesData[year][month][week];
                        if (selectedDate >= weekInfo.startDate && selectedDate <= weekInfo.endDate) {
                            if (selectedCategory === "All Categories" || weekInfo.categorySalesMap[selectedCategory]) {
                                matchedWeekData.push({ vendorId, weekInfo });
                            }
                        }
                    }
                }
            }
        }
        return matchedWeekData;
    }

    function rankVendors(weekData, selectedCategory) {
        return weekData
            .map(data => ({
                vendorId: data.vendorId,
                totalQuantity: calculateTotalQuantity(data.weekInfo.categorySalesMap, selectedCategory),
                topProduct: getTopProduct(data.weekInfo.categorySalesMap, selectedCategory)
            }))
            .sort((a, b) => b.totalQuantity - a.totalQuantity)
            .slice(0, 10);
    }

    async function updateTable(rankedVendors, selectedCategory) {
        const dbRef = ref(db);
        const tbody = document.getElementById('report-table-body');
        tbody.innerHTML = ''; // Clear table

        for (let i = 0; i < rankedVendors.length; i++) {
            const { vendorId, totalQuantity, topProduct } = rankedVendors[i];
            const vendorSnapshot = await get(child(dbRef, `vendors/${vendorId}`));
            const vendorData = vendorSnapshot.val();
            const storeName = vendorData ? vendorData.storeName : 'Unknown';

            const row = `<tr>
                          <td>${i + 1}</td>
                          <td>${storeName}</td>
                          <td>${topProduct}</td>
                          <td>${totalQuantity}</td>
                        </tr>`;
            tbody.innerHTML += row;
        }
    }

    function calculateTotalQuantity(categorySalesMap, selectedCategory) {
        let totalQuantity = 0;

        for (const category in categorySalesMap) {
            if (selectedCategory === "All Categories" || category === selectedCategory) {
                const items = categorySalesMap[category].itemSalesMap;
                for (const itemId in items) {
                    totalQuantity += parseInt(items[itemId].quantity, 10);
                }
            }
        }

        return totalQuantity;
    }

    function getTopProduct(categorySalesMap, selectedCategory) {
        const productQuantities = {};

        for (const category in categorySalesMap) {
            if (selectedCategory === "All Categories" || category === selectedCategory) {
                const items = categorySalesMap[category].itemSalesMap;
                for (const itemId in items) {
                    const item = items[itemId];
                    productQuantities[item.name] = (productQuantities[item.name] || 0) + parseInt(item.quantity, 10);
                }
            }
        }

        let topProduct = "Unknown";
        let maxQuantity = 0;

        for (const product in productQuantities) {
            if (productQuantities[product] > maxQuantity) {
                maxQuantity = productQuantities[product];
                topProduct = product;
            }
        }

        return topProduct;
    }
</script>
