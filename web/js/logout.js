document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logoutBtn');

    logoutBtn.addEventListener('click', async () => {
        try {
            const response = await fetch('/api/logout', {
                method: 'POST',
            });
            if (response.ok) {
                window.location.href = '/login.html';
            } else {
                alert('Logout failed. Please try again.');
            }
        } catch (error) {
            alert('Error connecting to server.');
        }
    });

    // Placeholder for loading social media stats dynamically
    const statsContainer = document.getElementById('statsContainer');
    statsContainer.innerHTML = '<p>Welcome to your social media dashboard! Stats will be displayed here.</p>';
});
