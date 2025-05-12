/**
 * Main JavaScript file for the Shop Management System
 */

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
  // Initialize animations for elements with data-animate attribute
  initAnimations();
  
  // Add event listeners to buttons with animation effects
  initButtonEffects();
  
  // Initialize form validation if forms exist
  initFormValidation();
});

/**
 * Initialize animations for elements
 */
function initAnimations() {
  // Add subtle entrance animations to cards and other elements
  const animatedElements = document.querySelectorAll('.card, .btn, .feature-card');
  
  animatedElements.forEach(element => {
    // Add hover listeners for subtle scale effect
    element.addEventListener('mouseenter', function() {
      this.style.transition = 'all 0.3s ease';
    });
  });
}

/**
 * Initialize button click effects
 */
function initButtonEffects() {
  const buttons = document.querySelectorAll('.btn');
  
  buttons.forEach(button => {
    button.addEventListener('click', function(e) {
      // Create ripple effect
      const ripple = document.createElement('span');
      ripple.classList.add('ripple');
      
      // Position the ripple
      const rect = this.getBoundingClientRect();
      const size = Math.max(rect.width, rect.height);
      
      ripple.style.width = ripple.style.height = `${size}px`;
      ripple.style.left = `${e.clientX - rect.left - size / 2}px`;
      ripple.style.top = `${e.clientY - rect.top - size / 2}px`;
      
      this.appendChild(ripple);
      
      // Remove the ripple after animation completes
      setTimeout(() => {
        ripple.remove();
      }, 600);
    });
  });
}

/**
 * Form validation for login and registration forms
 */
function initFormValidation() {
  const forms = document.querySelectorAll('form');
  
  forms.forEach(form => {
    form.addEventListener('submit', function(e) {
      const requiredFields = form.querySelectorAll('[required]');
      let isValid = true;
      
      // Check all required fields
      requiredFields.forEach(field => {
        if (!field.value.trim()) {
          isValid = false;
          showError(field, 'This field is required');
        } else {
          clearError(field);
          
          // Email validation
          if (field.type === 'email' && !validateEmail(field.value)) {
            isValid = false;
            showError(field, 'Please enter a valid email address');
          }
          
          // Password validation for password fields
          if (field.type === 'password' && field.value.length < 6) {
            isValid = false;
            showError(field, 'Password must be at least 6 characters');
          }
        }
      });
      
      if (!isValid) {
        e.preventDefault();
      }
    });
  });
}

/**
 * Display error message for a form field
 */
function showError(field, message) {
  // Clear any existing error
  clearError(field);
  
  // Add error class to the field
  field.classList.add('error');
  
  // Create and insert error message
  const errorDiv = document.createElement('div');
  errorDiv.className = 'error-message';
  errorDiv.innerText = message;
  
  // Insert error message after the field
  field.parentNode.insertBefore(errorDiv, field.nextSibling);
}

/**
 * Clear error message for a form field
 */
function clearError(field) {
  // Remove error class
  field.classList.remove('error');
  
  // Remove any existing error message
  const errorDiv = field.parentNode.querySelector('.error-message');
  if (errorDiv) {
    errorDiv.remove();
  }
}

/**
 * Validate email format
 */
function validateEmail(email) {
  const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}

/**
 * Show a notification message to the user
 */
function showNotification(message, type = 'info') {
  const notification = document.createElement('div');
  notification.className = `notification notification-${type}`;
  notification.innerText = message;
  
  document.body.appendChild(notification);
  
  // Animate in
  setTimeout(() => {
    notification.classList.add('show');
  }, 10);
  
  // Automatically remove after 5 seconds
  setTimeout(() => {
    notification.classList.remove('show');
    setTimeout(() => {
      notification.remove();
    }, 300);
  }, 5000);
}