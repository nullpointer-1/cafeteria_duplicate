/**
 * User Login with OTP JavaScript
 */

const BASE_URL = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', function () {
  const userLoginForm = document.getElementById('user-login-form');
  const otpVerificationForm = document.getElementById('otp-verification-form');
  const requestOtpBtn = document.getElementById('request-otp-btn');
  const resendOtpBtn = document.getElementById('resend-otp');
  const otpInputs = document.querySelectorAll('.otp-input');

  // Clear previous OTP notifications and OTP values on page load
  clearPreviousOtpData();

  // OTP handling functionality
  if (requestOtpBtn) {
    requestOtpBtn.addEventListener('click', function () {
      const phoneNumber = document.getElementById('phone').value;

      // Simple validation
      if (!phoneNumber || phoneNumber.length < 10) {
        showNotification('Please enter a valid phone number', 'error');
        return;
      }

      // Make an API call to the backend to request OTP
      fetch(`${BASE_URL}/otp/send?phone=${phoneNumber}`, { method: 'GET' })
      .then((response) => response.json())
      .then((data) => {
        console.log(data); // Check the structure of the response
    
        if (data.success) {
          userLoginForm.style.display = 'none';
          otpVerificationForm.style.display = 'block';
          showNotification('OTP sent to ' + phoneNumber, 'success');
    
          if (otpInputs.length > 0) {
            otpInputs[0].focus();
          }
        } else {
          showNotification(data.message, 'error');
        }
      })
      .catch((error) => {
        showNotification('Error sending OTP: ' + error.message, 'error');
      });
    });
  }

  // Handle resend OTP button
  if (resendOtpBtn) {
    resendOtpBtn.addEventListener('click', function (e) {
      e.preventDefault();

      const phoneNumber = document.getElementById('phone').value;

      // Make an API call to the backend to resend OTP
      fetch(`${BASE_URL}/otp/send?phone=${phoneNumber}`, { method: 'GET' })
        .then((response) => response.json())
        .then((data) => {
          if (data.success) {
            showNotification('OTP resent successfully', 'success');
          } else {
            showNotification(data.message, 'error');
          }
        })
        .catch((error) => {
          showNotification('Error resending OTP: ' + error.message, 'error');
        });
    });
  }

  // Handle OTP input navigation and auto-submit
  if (otpInputs.length > 0) {
    otpInputs.forEach((input, index) => {
      input.addEventListener('keyup', function (e) {
        // If a digit is entered, move to next input
        if (this.value.length === this.maxLength) {
          if (index < otpInputs.length - 1) {
            otpInputs[index + 1].focus();
          }
        }

        // If backspace is pressed on an empty input, move to previous input
        if (e.key === 'Backspace' && this.value.length === 0) {
          if (index > 0) {
            otpInputs[index - 1].focus();
          }
        }

        // Check if all inputs are filled
        const allFilled = Array.from(otpInputs).every((input) => input.value.length === 1);

        // Auto-submit if all inputs are filled
        if (allFilled) {
          verifyOtp();
        }
      });
    });
  }

  // Handle OTP form submission
  if (otpVerificationForm) {
    otpVerificationForm.addEventListener('submit', function (e) {
      e.preventDefault();
      verifyOtp();
    });
  }

  /**
   * Verify the OTP
   */
  function verifyOtp() {
    // Get OTP value from inputs
    const otpValue = Array.from(otpInputs).map((input) => input.value).join('');

    console.log(otpValue);
    if (otpValue.length !== otpInputs.length) {
      showNotification('Please enter the complete OTP', 'error');
      return;
    }

    // Make an API call to verify the OTP
    const phoneNumber = document.getElementById('phone').value;

    fetch(`${BASE_URL}/otp/verify?phone=${phoneNumber}&otp=${otpValue}`, { method: 'POST' })
      .then((response) => response.json())
      .then((data) => {
        if (data.success) {
          showNotification('OTP verified successfully! Redirecting...', 'success');

          setTimeout(() => {
            window.location.href = '../../dashboard.html'; 
          }, 2000);
        } else {
          showNotification(data.message, 'error');
        }
      })
      .catch((error) => {
        showNotification('Error verifying OTP: ' + error.message, 'error');
      });
  }

  /**
   * Clear previous OTP notifications and input values
   */
  function clearPreviousOtpData() {
    // Clear OTP inputs
    otpInputs.forEach(input => input.value = '');
    
    // Clear any existing notifications
    let notificationContainer = document.querySelector('.notification-container');
    if (notificationContainer) {
      notificationContainer.innerHTML = ''; // Empty the notification container
    }
  }

  /**
   * Show notification message
   */
  function showNotification(message, type = 'info') {
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
        zIndex: '1000',
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
