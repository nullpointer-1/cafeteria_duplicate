/**
 * Admin Dashboard JavaScript
 */
const BASE_URL = 'http://localhost:8080';
document.addEventListener('DOMContentLoaded', function() {
  // Initialize dashboard components
  initializeModals();
  initializeShopCards();
  initializeCredentialGeneration();

  // Load shop data from backend
  loadShopData();
});

/**
 * Initialize modal functionality
 */
function initializeModals() {
  // Get all modals
  const modals = document.querySelectorAll('.modal');
  const modalTriggers = {
    'add-shop-modal': document.getElementById('add-shop-btn'),
    'credentials-modal': document.querySelectorAll('.generate-credentials-btn')
  };
  
  // Setup modal open buttons
  if (modalTriggers['add-shop-modal']) {
    modalTriggers['add-shop-modal'].addEventListener('click', function() {
      openModal('add-shop-modal');
    });
  }
  
  // Setup close buttons for all modals
  document.querySelectorAll('.close-modal').forEach(button => {
    button.addEventListener('click', function() {
      const modal = this.closest('.modal');
      closeModal(modal.id);
    });
  });
  
  // Close modals when clicking outside
  modals.forEach(modal => {
    modal.addEventListener('click', function(e) {
      if (e.target === this) {
        closeModal(this.id);
      }
    });
  });
  
  // Setup cancel buttons
  document.getElementById('cancel-add-shop')?.addEventListener('click', function() {
    closeModal('add-shop-modal');
  });
  
  document.getElementById('cancel-credentials')?.addEventListener('click', function() {
    closeModal('credentials-modal');
  });
  
  // Setup save buttons
  document.getElementById('save-shop')?.addEventListener('click', function() {
    saveShop();
  });
  
  document.getElementById('save-credentials')?.addEventListener('click', function() {
    saveCredentials();
  });
}

function initializeCredentialGeneration() {
  // Setup regenerate username button
  document.getElementById('regenerate-username')?.addEventListener('click', function() {
    const vendorEmail = document.getElementById('vendor-email').value;
    let username;
    
    if (vendorEmail) {
      // Extract username from email
      username = vendorEmail.split('@')[0];
    } else {
      username = 'vendor' + Math.floor(1000 + Math.random() * 9000);
    }
    
    document.getElementById('generated-username').value = username;
  });

  // Setup regenerate password button
  document.getElementById('regenerate-password')?.addEventListener('click', function() {
    const password = generatePassword(8);
    document.getElementById('generated-password').value = password;
  });
}

/**
 * Open a modal by ID
 */
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }
}

/**
 * Close a modal by ID
 */
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
  }
}

/**
 * Initialize shop card functionality
 */
function initializeShopCards() {
  document.addEventListener('click', function(e) {
    if (e.target.classList.contains('shop-details-btn')) {
      const shopId = e.target.dataset.shopId;
      viewShopDetails(shopId);
    }

    if (e.target.classList.contains('generate-credentials-btn')) {
      const shopId = e.target.dataset.shopId;
      const vendorName = e.target.dataset.vendorName;
      const vendorId = e.target.dataset.vendorId; 
            e.target.disabled = true;
 // Get vendorId from data attribute
      openCredentialsModal(shopId, vendorName, vendorId);  // Pass vendorId here
    }
  });
}


/**
 * Open credentials modal and initialize fields
 */
function openCredentialsModal(shopId, vendorName, vendorId) {
  const form = document.getElementById('credentials-form');
  
  // Set shopId and vendorId as data attributes on the form
  form.dataset.shopId = shopId;
  form.dataset.vendorId = vendorId;

  console.log('Vendor ID in openCredentialsModal:', vendorId);  // Check if vendorId is passed correctly

  const username = generateUsername(vendorName);
  const password = generatePassword(8);

  // Set generated username and password
  document.getElementById('generated-username').value = username;
  document.getElementById('generated-password').value = password;

  // Open modal
  openModal('credentials-modal');
}



/**
 * Save credentials
 */
/**
 * Save credentials
 */
async function saveCredentials() {
  const form = document.getElementById('credentials-form');
  const vendorId = form.dataset.vendorId;
  const email = document.getElementById('vendor-email').value;
  const username = document.getElementById('generated-username').value;
  const password = document.getElementById('generated-password').value;
  const generateButton = document.querySelector('.generate-credentials-btn[data-vendor-id="' + vendorId + '"]');

  if (!email || !username || !password) {
    showNotification('Please fill all required fields', 'error');
    return;
  }

  try {
    const response = await fetch(`${BASE_URL}/api/vendors/credentials`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        vendorId: vendorId,
        email,
        username,
        password,
      })
    });

    if (!response.ok) throw new Error('Failed to save credentials');

    closeModal('credentials-modal');
    form.reset();
    showNotification('Credentials saved successfully!', 'success');
    
    // Re-enable the button after credentials are saved
    if (generateButton) {
      generateButton.disabled = false;
    }

  } catch (error) {
    showNotification('Error saving credentials', 'error');
    
    // Re-enable the button in case of error too
    if (generateButton) {
      generateButton.disabled = false;
    }
  }
}

/**
 * View shop details
 */
async function viewShopDetails(shopId) {
  try {
    const response = await fetch(`${BASE_URL}/api/shops/${shopId}`);
    if (!response.ok) throw new Error('Failed to fetch shop details');
    
    const shop = await response.json();
    
    // Populate the details modal
    document.getElementById('detail-shop-name').textContent = shop.name;
    document.getElementById('detail-vendor-name').textContent = shop.vendorName;
    document.getElementById('detail-location').textContent = shop.location;
    document.getElementById('detail-contact').textContent = shop.contactNumber;
    document.getElementById('detail-shop-type').textContent = shop.shopType;
    document.getElementById('detail-status').textContent = shop.active ? 'Active' : 'Inactive';
    document.getElementById('detail-created-at').textContent = new Date(shop.createdAt).toLocaleString();
    document.getElementById('detail-updated-at').textContent = new Date(shop.updatedAt).toLocaleString();
    
    // Open the details modal
    openModal('shop-details-modal');
  } catch (error) {
    showNotification('Error fetching shop details', 'error');
  }
}

/**
 * Load shop data from backend
 */
async function loadShopData() {
  try {
    const response = await fetch(`${BASE_URL}/api/shops`);
    if (!response.ok) throw new Error('Failed to fetch shops');
    
    const shops = await response.json();
    updateDashboardStats(shops);
    renderShopCards(shops);
  } catch (error) {
    showNotification('Error loading shop data', 'error');
  }
}

/**
 * Update dashboard statistics
 */
function updateDashboardStats(shops) {
  const totalShops = shops.length;
  const activeShops = shops.filter(shop => shop.active).length;
  
  document.querySelector('.summary-cards').innerHTML = `
    <div class="summary-card">
      <div class="summary-icon" style="background-color: var(--primary-50);">🍽️</div>
      <div class="summary-info">
        <h3>Total Outlets</h3>
        <p class="summary-value">${totalShops}</p>
      </div>
    </div>
    
    <div class="summary-card">
      <div class="summary-icon" style="background-color: var(--secondary-50);">👥</div>
      <div class="summary-info">
        <h3>Active Outlets</h3>
        <p class="summary-value">${activeShops}</p>
      </div>
    </div>
    
    <div class="summary-card">
      <div class="summary-icon" style="background-color: var(--accent-50);">📋</div>
      <div class="summary-info">
        <h3>Today's Orders</h3>
        <p class="summary-value">-</p>
      </div>
    </div>
    
    <div class="summary-card">
      <div class="summary-icon" style="background-color: #E8F5E9;">✅</div>
      <div class="summary-info">
        <h3>Active Users</h3>
        <p class="summary-value">-</p>
      </div>
    </div>
  `;
}

/**
 * Render shop cards
 */
function renderShopCards(shops) {
  const container = document.getElementById('shop-cards-container');
  container.innerHTML = shops.map(shop => `
    <div class="shop-card">
      <div class="shop-card-header">
        <h3>${shop.name}</h3>
        <span class="shop-status ${shop.active ? 'active' : 'inactive'}">
          ${shop.active ? 'Active' : 'Inactive'}
        </span>
      </div>
      <div class="shop-card-body">
        <p><strong>Vendor:</strong> ${shop.vendorName}</p>
        <p><strong>Location:</strong> ${shop.location}</p>
        <p><strong>Contact:</strong> ${shop.contactNumber}</p>
        <p><strong>Type:</strong> ${shop.shopType}</p>
      </div>
      <div class="shop-card-footer">
        <button class="btn btn-secondary shop-details-btn" data-shop-id="${shop.id}">
          View Details
        </button>
        <button class="btn btn-primary generate-credentials-btn" 
                data-shop-id="${shop.id}" 
                data-vendor-name="${shop.vendorName}" data-vendor-id="${shop.vendorId}">
                  
          Generate Credentials
        </button>
      </div>
    </div>
  `).join('');
}

/**
 * Generate a username based on vendor name
 */
function generateUsername(vendorName) {
  if (!vendorName) return 'vendor' + Math.floor(1000 + Math.random() * 9000);
  const cleanName = vendorName.toLowerCase().replace(/[^a-z0-9]/g, '');
  return cleanName + Math.floor(100 + Math.random() * 900);
}

/**
 * Generate a random password
 */
function generatePassword(length = 8) {
  const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let password = '';
  for (let i = 0; i < length; i++) {
    password += charset[Math.floor(Math.random() * charset.length)];
  }
  return password;
}

/**
 * Show notification message
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

const pno="";
async function saveShop() {
  const shopData = {
    name: document.getElementById("shop-name").value,
    location: document.getElementById("shop-location").value,
    contactNumber: document.getElementById("contact-number").value,
    shopType: document.getElementById("shop-type").value,
    active: document.getElementById("shop-status").value === "active",
    vendorName: document.getElementById("vendor-name").value,
    vendorEmail: "sample@gmail.com",
    vendorUsername: "sample",
    vendorPassword: "sample",
    vendorContactNumber: document.getElementById("contact-number").value, // Same as shop contact
  };

 
  if (!shopData.name || !shopData.vendorName || !shopData.location ||
      !shopData.contactNumber || !shopData.shopType) {
    showNotification('Please fill all required fields', 'error');
    return;
  }

  try {
    const response = await fetch(`${BASE_URL}/api/shops`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(shopData)
    });

    if (!response.ok) throw new Error("Failed to save shop");

    closeModal("add-shop-modal");
    document.getElementById("add-shop-form").reset();
    loadShopData();
    showNotification("Shop added successfully!", "success");
  } catch (error) {
    console.error("Error saving shop:", error);
    showNotification("Error saving shop data. Please try again.", "error");
  }
}
