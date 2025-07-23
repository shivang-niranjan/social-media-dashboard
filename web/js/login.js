document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(loginForm);
        const username = formData.get('username');
        const password = formData.get('password');

        // Basic client-side validation
        if (!username || !password) {
            errorMessage.textContent = 'Please enter both username and password.';
            return;
        }

        // Send login request to backend
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                // Redirect to dashboard on successful login
                window.location.href = '/dashboard.html';
            } else {
                const data = await response.json();
                errorMessage.textContent = data.message || 'Login failed. Please try again.';
            }
        } catch (error) {
            errorMessage.textContent = 'Error connecting to server.';
        }
    });
});
