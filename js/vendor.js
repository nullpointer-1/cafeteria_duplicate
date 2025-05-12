const BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
  const vendorLoginForm = document.getElementById('vendor-login-form');

  if (vendorLoginForm) {
    vendorLoginForm.addEventListener('submit', function(e) {
      e.preventDefault();
      
      const username = document.getElementById('vendor-username').value;
      const password = document.getElementById('vendor-password').value;

      // Simple validation 
      if (!username || !password) {
        showNotification('Please enter both username and password', 'error');
        return;
      }

      // Create a credentials object to send to the backend
      const credentials = {
        username: username,
        password: password
      };

      // Send credentials to backend API
      fetch(`${BASE_URL}/vendors/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials)
      })
      .then(response => response.json())
      .then(data => {
        if (data.token) {
          // If token is received, store it in localStorage/sessionStorage
          localStorage.setItem('vendorToken', data.token);
          console.log('Received Token:', data.token);
          
          // Show success notification
          showNotification('Login successful! Redirecting to vendor dashboard...', 'success');
          
          // Redirect to the dashboard after a short delay
         // Assuming token is set and everything else is correct
setTimeout(() => {
    window.location.href = '/food/pages/vendor/dashboard.html'; // Redirecting to vendor dashboard
}, 500);  // Short delay before redirect

        } else {
          showNotification('Invalid username or password', 'error');
        }
      })
      .catch(error => {
        showNotification('Error logging in. Please try again.', 'error');
      });
    });
  }

  // Show notification message
  function showNotification(message, type = 'info') {
    let notificationContainer = document.querySelector('.notification-container');

    if (!notificationContainer) {
      notificationContainer = document.createElement('div');
      notificationContainer.className = 'notification-container';
      document.body.appendChild(notificationContainer);
      
      Object.assign(notificationContainer.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        zIndex: '1000'
      });
    }

    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;

    notification.style.animationName = 'slideIn';
    notification.style.animationDuration = '0.3s';
    
    notificationContainer.appendChild(notification);

    setTimeout(() => {
      notification.style.animationName = 'slideOut';
      setTimeout(() => {
        notification.remove();

        if (notificationContainer.children.length === 0) {
          notificationContainer.remove();
        }
      }, 300);
    }, 5000);
  }
});
