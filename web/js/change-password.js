document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('changePasswordForm');
    const messageDiv = document.getElementById('message');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const currentPassword = form.currentPassword.value;
        const newPassword = form.newPassword.value;
        const confirmPassword = form.confirmPassword.value;

        if (!currentPassword || !newPassword || !confirmPassword) {
            messageDiv.textContent = 'Please fill in all fields.';
            messageDiv.style.color = 'red';
            return;
        }

        if (newPassword !== confirmPassword) {
            messageDiv.textContent = 'New password and confirmation do not match.';
            messageDiv.style.color = 'red';
            return;
        }

        try {
            const response = await fetch('/api/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ currentPassword, newPassword, confirmPassword }),
            });

            const data = await response.json();

            if (response.ok) {
                messageDiv.textContent = data.message;
                messageDiv.style.color = 'green';
                form.reset();
            } else {
                messageDiv.textContent = data.message || 'Failed to change password.';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'Error connecting to server.';
            messageDiv.style.color = 'red';
        }
    });
});
