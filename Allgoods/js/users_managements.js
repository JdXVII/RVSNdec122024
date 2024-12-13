document.addEventListener('DOMContentLoaded', function() {
    // Get all tables
    var tables = document.querySelectorAll('.vendor-table, .buyer-table, .approved-vendor-table, .approved-buyer-table');

    // Hide all tables
    tables.forEach(function(table) {
        table.style.display = 'none';
    });

    // Show table visibility
    function toggleTable(table) {
        tables.forEach(function(t) {
            if (t !== table) {
                t.style.display = 'none'; // Hide other tables
            }
        });

        if (table.style.display === 'none') {
            table.style.display = 'table';
        } else {
            table.style.display = 'none';
        }
    }

    var vendorManageBtn = document.querySelector('.vendor-manage-btn');
    var buyerManageBtn = document.querySelector('.buyer-manage-btn');
    var approvedVendorManageBtn = document.querySelector('.approved-vendor-manage-btn');
    var approvedBuyerManageBtn = document.querySelector('.approved-buyer-manage-btn');

    vendorManageBtn.addEventListener('click', function() {
        toggleTable(document.getElementById('vendorTable'));
    });

    buyerManageBtn.addEventListener('click', function() {
        toggleTable(document.getElementById('buyerTable'));
    });

    approvedVendorManageBtn.addEventListener('click', function() {
        toggleTable(document.getElementById('approvedVendorTable'));
    });

    approvedBuyerManageBtn.addEventListener('click', function() {
        toggleTable(document.getElementById('approvedBuyerTable'));
    });
});