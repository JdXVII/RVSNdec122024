window.addEventListener('load', () => {
    const messageContainer = document.querySelector('.message-container');
    if (messageContainer) {
      messageContainer.style.visibility = 'visible';
      messageContainer.style.opacity = '1';
      setTimeout(() => {
        messageContainer.style.visibility = 'hidden';
        messageContainer.style.opacity = '0';
      }, 3000);
    }
  });