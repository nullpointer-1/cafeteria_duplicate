/**
 * Vendor Dashboard JavaScript
 */

const BASE_URL = 'http://localhost:8080/api';
let vendorData = null;
// Assuming you're using Fetch API to get product details

document.addEventListener('DOMContentLoaded', function() {
  // Check if vendor is logged in
  checkVendorAuth();
  
  // Initialize components
  initializeModals();
  initializeEventListeners();
  
  // Load initial data
  loadDashboardData();
});

/**
 * Check vendor authentication
 */
async function checkVendorAuth() {
  const token = localStorage.getItem('vendorToken');
  console.log('Token:', token); // Checking token
  
  if (!token) {
    window.location.href = 'login.html'; // Redirect if no token
    return;
  }

  try {
    const response = await fetch(`${BASE_URL}/vendors/me`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
      
    });
    
    if (!response.ok) throw new Error('Authentication failed');
    
    vendorData = await response.json();
    console.log(vendorData);
    updateVendorInfo(vendorData);
  } catch (error) {
    // localStorage.removeItem('vendorToken');
    // window.location.href = 'login.html'; // Redirect if authentication fails
  }
}


/**
 * Update vendor information in the UI
 */
function updateVendorInfo(vendor) {
  document.getElementById('vendor-name').textContent = vendor.name;
  document.getElementById('vendor-avatar').textContent = vendor.name.charAt(0);
}

/**
 * Initialize modal functionality
 */
function initializeModals() {
  // Product modal
  const productModal = document.getElementById('product-modal');
  const closeProductModal = productModal.querySelector('.close-modal');
  const cancelProduct = document.getElementById('cancel-product');
  
  closeProductModal.addEventListener('click', () => closeModal('product-modal'));
  cancelProduct.addEventListener('click', () => closeModal('product-modal'));
}

/**
 * Initialize event listeners
 */
function initializeEventListeners() {
  // Add product button
  document.getElementById('add-product-btn').addEventListener('click', () => {
    document.getElementById('product-modal-title').textContent = 'Add New Product';
    document.getElementById('product-form').reset();
    document.getElementById('product-id').value = '';
    openModal('product-modal');
  });
  
  // Save product button
  document.getElementById('save-product').addEventListener('click', saveProduct);
  
  // Logout button
  document.getElementById('logout-btn').addEventListener('click', logout);
}

/**
 * Load dashboard data
 */
async function loadDashboardData() {
  try {
    const [products, orders, stats] = await Promise.all([
      fetch(`${BASE_URL}/vendors/products`).then(res => res.json()),
      fetch(`${BASE_URL}/vendors/orders`).then(res => res.json()),
      fetch(`${BASE_URL}/vendors/stats`).then(res => res.json())
    ]);
    
    updateDashboardStats(stats);
    renderProducts(products);
    renderOrders(orders);
  } catch (error) {
    showNotification('Error loading dashboard data', 'error');
  }
}

/**
 * Update dashboard statistics
 */
function updateDashboardStats(stats) {
  document.getElementById('total-products').textContent = stats.totalProducts;
  document.getElementById('pending-orders').textContent = stats.pendingOrders;
  document.getElementById('completed-orders').textContent = stats.completedToday;
  document.getElementById('today-revenue').textContent = `₹${stats.todayRevenue}`;
}

/**
 * Render products table
 */
function renderProducts(products) {
  const tableBody = document.getElementById('products-table-body');
  
  tableBody.innerHTML = products.map(product => `
    <tr>
      <td>
        <div class="product-name">${product.name}</div>
        <div class="product-description" style="color: var(--neutral-600); font-size: 0.875rem;">
          ${product.description}
        </div>
      </td>
      <td>${product.category}</td>
      <td>₹${product.price}</td>
      <td>
        <label class="availability-toggle">
          <input type="checkbox" ${product.available ? 'checked' : ''} 
                 onchange="toggleAvailability(${product.id}, this.checked)">
          <span class="toggle-slider"></span>
        </label>
      </td>
      <td>
        <div class="product-actions">
          <button class="btn btn-secondary btn-sm" onclick="editProduct(${product.id})">Edit</button>
          <button class="btn btn-error btn-sm" onclick="deleteProduct(${product.id})">Delete</button>
        </div>
      </td>
    </tr>
  `).join('');
}

/**
 * Toggle product availability
 */
async function toggleAvailability(productId, available) {
  try {
    const response = await fetch(`${BASE_URL}/vendors/products/${productId}/availability`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('vendorToken')}`
      },
      body: JSON.stringify({ available })
    });
    
    if (!response.ok) throw new Error('Failed to update availability');
    
    showNotification(
      `Product ${available ? 'is now available' : 'is now unavailable'}`,
      'success'
    );
  } catch (error) {
    showNotification('Error updating availability', 'error');
    // Revert the toggle
    const checkbox = document.querySelector(`input[data-product-id="${productId}"]`);
    if (checkbox) checkbox.checked = !available;
  }
}

/**
 * Render orders list
 */
function renderOrders(orders) {
  const container = document.getElementById('orders-container');
  
  container.innerHTML = orders.map(order => `
    <div class="order-card">
      <div class="order-info">
        <div class="order-id">#${order.id}</div>
        <div class="order-items">${order.items.length} items</div>
        <div class="order-total">₹${order.total}</div>
      </div>
      <div class="order-status ${order.status === 'PENDING' ? 'status-pending' : 'status-completed'}">
        ${order.status}
      </div>
      <div class="order-actions">
        ${order.status === 'PENDING' ? `
          <button class="btn btn-primary" onclick="completeOrder(${order.id})">
            Complete Order
          </button>
        ` : ''}
      </div>
    </div>
  `).join('');
}

/**
 * Save product
 */
async function saveProduct() {
  const productId = document.getElementById('product-id').value;
  const formData = {
    name: document.getElementById('product-name').value,
    description: document.getElementById('product-description').value,
    price: parseFloat(document.getElementById('product-price').value),
    category: document.getElementById('product-category').value,
    available: document.getElementById('product-availability').value === 'true'
  };
  
  try {
    const url = productId ? 
      `${BASE_URL}/vendors/products/${productId}` : 
      `${BASE_URL}/vendors/products`;
    
    const response = await fetch(url, {
      method: productId ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('vendorToken')}`
      },
      body: JSON.stringify(formData)
    });
    
    if (!response.ok) throw new Error('Failed to save product');
    
    closeModal('product-modal');
    loadDashboardData();
    showNotification('Product saved successfully', 'success');
  } catch (error) {
    showNotification('Error saving product', 'error');
  }
}

/**
 * Edit product
 */
async function editProduct(productId) {
  try {
    const response = await fetch(`${BASE_URL}/vendors/products/${productId}`);
    if (!response.ok) throw new Error('Failed to fetch product');
    
    const product = await response.json();
    
    document.getElementById('product-modal-title').textContent = 'Edit Product';
    document.getElementById('product-id').value = product.id;
    document.getElementById('product-name').value = product.name;
    document.getElementById('product-description').value = product.description;
    document.getElementById('product-price').value = product.price;
    document.getElementById('product-category').value = product.category;
    document.getElementById('product-availability').value = product.available.toString();
    
    openModal('product-modal');
  } catch (error) {
    showNotification('Error fetching product details', 'error');
  }
}

/**
 * Delete product
 */
async function deleteProduct(productId) {
  const confirmed = await Swal.fire({
    title: 'Are you sure?',
    text: "You won't be able to revert this!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Yes, delete it!'
  });
  
  if (confirmed.isConfirmed) {
    try {
      const response = await fetch(`${BASE_URL}/vendors/products/${productId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('vendorToken')}`
        }
      });
      
      if (!response.ok) throw new Error('Failed to delete product');
      
      loadDashboardData();
      showNotification('Product deleted successfully', 'success');
    } catch (error) {
      showNotification('Error deleting product', 'error');
    }
  }
}

/**
 * Complete order
 */
async function completeOrder(orderId) {
  try {
    const response = await fetch(`${BASE_URL}/vendors/orders/${orderId}/complete`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('vendorToken')}`
      }
    });
    
    if (!response.ok) throw new Error('Failed to complete order');
    
    loadDashboardData();
    showNotification('Order completed successfully', 'success');
  } catch (error) {
    showNotification('Error completing order', 'error');
  }
}

/**
 * Logout vendor
 */
function logout() {
  localStorage.removeItem('vendorToken');
  window.location.href = 'login.html';
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
  Swal.fire({
    title: type.charAt(0).toUpperCase() + type.slice(1),
    text: message,
    icon: type,
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true
  });
}

/**
 * Open modal
 */
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }
}

/**
 * Close modal
 */
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
  }
}