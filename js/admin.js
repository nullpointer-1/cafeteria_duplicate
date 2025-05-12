/**
 * Admin Login JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
  const adminLoginForm = document.getElementById('admin-login-form');

  if (adminLoginForm) {
    adminLoginForm.addEventListener('submit', function(e) {
      e.preventDefault();
      
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
      
      // Simple validation 
      if (!username || !password) {
        showNotification('Please enter both username and password', 'error');
        return;
      }
      
      // For demo purposes, hardcode the admin credentials
      // In a real application, this would be handled by the backend
      if (username === 'admin' && password === 'admin123') {
        // Show success message
        showNotification('Login successful! Redirecting to dashboard...', 'success');
        
        // Redirect to admin dashboard
        setTimeout(() => {
          window.location.href = 'dashboard.html';
        }, 1500);
      } else {
        showNotification('Invalid username or password', 'error');
      }
    });
  }
  
  /**
   * Show notification message
   */
  function showNotification(message, type = 'info') {
    // Check if a notification container already exists
    let notificationContainer = document.querySelector('.notification-container');
    
    // If not, create one
    if (!notificationContainer) {
      notificationContainer = document.createElement('div');
      notificationContainer.className = 'notification-container';
      document.body.appendChild(notificationContainer);
      
      // Style the notification container
      Object.assign(notificationContainer.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        zIndex: '1000'
      });
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    
    // Add animation classes
    notification.style.animationName = 'slideIn';
    notification.style.animationDuration = '0.3s';
    
    // Add to container
    notificationContainer.appendChild(notification);
    
    // Remove after 5 seconds
    setTimeout(() => {
      notification.style.animationName = 'slideOut';
      setTimeout(() => {
        notification.remove();
        
        // If container is empty, remove it too
        if (notificationContainer.children.length === 0) {
          notificationContainer.remove();
        }
      }, 300);
    }, 5000);
  }
});