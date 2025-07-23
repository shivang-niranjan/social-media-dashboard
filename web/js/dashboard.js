document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logoutBtn');
    const socialMediaForm = document.getElementById('socialMediaForm');
    const statsContainer = document.getElementById('statsContainer');

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

    async function fetchSocialMediaData(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return await response.json();
        } catch (error) {
            console.error('Fetch error:', error);
            return null;
        }
    }

    function createStatElement(platform, data) {
        const container = document.createElement('div');
        container.classList.add('platform-stats');

        const title = document.createElement('h3');
        title.textContent = platform;
        container.appendChild(title);

        const list = document.createElement('ul');
        for (const key in data) {
            const item = document.createElement('li');
            item.textContent = `${key}: ${data[key]}`;
            list.appendChild(item);
        }
        container.appendChild(list);

        return container;
    }

    async function loadStats(twitterId, facebookId, instagramId) {
        statsContainer.innerHTML = '<p>Loading social media stats...</p>';

        const twitterData = twitterId ? await fetchSocialMediaData(`/api/social/twitter?id=${encodeURIComponent(twitterId)}`) : null;
        const facebookData = facebookId ? await fetchSocialMediaData(`/api/social/facebook?id=${encodeURIComponent(facebookId)}`) : null;
        const instagramData = instagramId ? await fetchSocialMediaData(`/api/social/instagram?id=${encodeURIComponent(instagramId)}`) : null;

        statsContainer.innerHTML = '';

        if (twitterData) {
            statsContainer.appendChild(createStatElement('Twitter', twitterData));
        }
        if (facebookData) {
            statsContainer.appendChild(createStatElement('Facebook', facebookData));
        }
        if (instagramData) {
            statsContainer.appendChild(createStatElement('Instagram', instagramData));
        }

        if (!twitterData && !facebookData && !instagramData) {
            statsContainer.innerHTML = '<p>No social media stats to display.</p>';
        }
    }

    socialMediaForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const twitterId = document.getElementById('twitterId').value.trim();
        const facebookId = document.getElementById('facebookId').value.trim();
        const instagramId = document.getElementById('instagramId').value.trim();
        loadStats(twitterId, facebookId, instagramId);
    });

    // Initial load with no IDs
    statsContainer.innerHTML = '<p>Please enter social media IDs and click "Fetch Stats".</p>';
});
