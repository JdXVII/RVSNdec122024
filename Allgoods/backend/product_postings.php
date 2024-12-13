<script type="module">
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-app.js";
  import { getDatabase, ref, onValue, update, remove, set } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-database.js";
  import { getStorage, ref as storageRef, deleteObject } from "https://www.gstatic.com/firebasejs/10.12.3/firebase-storage.js";

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
  const database = getDatabase(app);
  const storage = getStorage(app);

  const productTableBody = document.getElementById('product-table-body');
  
  function fetchProducts() {
    const productsRef = ref(database, 'upload_products');
    onValue(productsRef, (snapshot) => {
      productTableBody.innerHTML = '';
      snapshot.forEach((childSnapshot) => {
        const product = childSnapshot.val();
        if (product.status !== 'approved') {
          const productRow = document.createElement('tr');
          productRow.id = `product-${product.productId}`;
  
          const vendorRef = ref(database, `vendors/${product.userId}`);
          onValue(vendorRef, (vendorSnapshot) => {
            const vendor = vendorSnapshot.val();
            const storeName = vendor.firstName + ' ' + vendor.lastName;
  
            productRow.innerHTML = `
              <td>
                <img class="pic" src="${product.imageUrls[0]}" width="90" alt="Product Image">
              </td>
              <td>${product.name}</td>
              <td>${storeName}</td>
              <td>${formatPrice(product.price)}</td>
              <td class="action-btns">
                <button class="btn3 btn-preview" onclick="viewProduct('${product.productId}')">Preview</button>
                <button class="btn1 btn-approve" onclick="approveProduct('${product.productId}')">Approve</button>
                <button class="btn2 btn-delete" onclick="showDeclineModal('${product.productId}', '${product.userId}', '${product.imageUrls[0]}')">Decline</button>
              </td>
            `;
            productTableBody.appendChild(productRow);
          });
        }
      });
    });
  }

  function formatPrice(price) {
    return '₱' + parseFloat(price).toFixed(2);
  }

  window.viewProduct = function(productId) {
    const productRef = ref(database, `upload_products/${productId}`);
    onValue(productRef, (snapshot) => {
      const product = snapshot.val();
      if (product) {
        // Product exists, proceed with displaying the data
        document.getElementById('preview-product-name').innerText = product.name || 'N/A';
        document.getElementById('preview-product-description').innerText = product.description || 'No description available.';
        document.getElementById('preview-product-category').innerText = product.category || 'N/A';
        document.getElementById('preview-product-price').innerText = formatPrice(product.price);
        document.getElementById('preview-product-unit').innerText = product.unit || 'N/A';
        document.getElementById('preview-producttype').innerText = product.productType || 'N/A';
        document.getElementById('preview-product-saletype').innerText = product.type || 'N/A';
        document.getElementById('preview-product-delivery').innerText = product.deliveryOption || 'N/A';
        document.getElementById('preview-product-payment').innerText = product.paymentOption || 'N/A';
        document.getElementById('preview-product-stock').innerText = product.stock || 0;
        
        const modalImages = document.getElementById('preview-product-images');
        modalImages.innerHTML = '';
        if (product.imageUrls && product.imageUrls.length > 0) {
          product.imageUrls.forEach(imageUrl => {
            const img = document.createElement('img');
            img.src = imageUrl;
            img.style.width = '100%';
            img.style.marginBottom = '10px';
            modalImages.appendChild(img);
          });
        } else {
          modalImages.innerHTML = '<p>No images available</p>';
        }
  
        document.getElementById('preview-modal').style.display = 'block';
      } else {
        alert('Product not found.');
      }
    });
  };

  window.closePreviewModal = function() {
    document.getElementById('preview-modal').style.display = 'none';
  };

  window.showDeclineModal = function(productId, userId, imageUrl) {
    window.productIdToDecline = productId;
    window.userIdToDecline = userId;
    window.imageUrlToDelete = imageUrl;
    document.getElementById('decline-modal').style.display = 'block';
  };

  window.closeDeclineModal = function() {
    document.getElementById('decline-modal').style.display = 'none';
  };

  window.toggleCustomReason = function() {
    const reasonSelect = document.getElementById('decline-reason');
    const customReasonContainer = document.getElementById('custom-reason-container');
    customReasonContainer.style.display = reasonSelect.value === 'Others' ? 'block' : 'none';
  };

  window.confirmDecline = function() {
    if (confirm("Are you sure you want to decline this product?")) {
      const reason = document.getElementById('decline-reason').value === 'Others' 
        ? document.getElementById('custom-reason').value 
        : document.getElementById('decline-reason').value;
        
      if (!reason) {
        alert('Please provide a reason for declining the product.');
        return;
      }
    
      const productRef = ref(database, `upload_products/${window.productIdToDecline}`);
      const imageRef = storageRef(storage, window.imageUrlToDelete);
      const declinedProductRef = ref(database, `declined_products/${window.productIdToDecline}`);
    
      onValue(productRef, (snapshot) => {
        const product = snapshot.val();
        if (product) {
          const productName = product.name || 'Unknown Product';
    
          set(declinedProductRef, { 
            reason, 
            userId: window.userIdToDecline, 
            name: productName,
            isRead: false
          }).then(() => {
            remove(productRef).then(() => {
              deleteObject(imageRef).then(() => {
                alert('Product declined successfully');
                closeDeclineModal();
              }).catch((error) => {
                console.error('Error deleting image:', error);
              });
            }).catch((error) => {
              console.error('Error deleting product:', error);
            });
          }).catch((error) => {
            console.error('Error storing decline reason:', error);
          });
        }
      });
    }
  };

  window.approveProduct = function(productId) {
    if (confirm("Are you sure you want to approve this product?")) {
      const productRef = ref(database, `upload_products/${productId}`);
      update(productRef, { status: 'approved' }).then(() => {
        const productRow = document.getElementById(`product-${productId}`);
        if (productRow) {
          productRow.remove();
        }
        alert('Product approved successfully');
      }).catch((error) => {
        console.error('Error approving product:', error);
      });
    }
  };

  fetchProducts();
</script>