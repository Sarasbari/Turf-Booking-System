// Home page functionality
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication status
    checkAuthStatus();
    
    // Load top rated turfs
    loadTopRatedTurfs();
    
    // Load offers
    loadOffers();
    
    // Handle search form submission
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const location = document.getElementById('searchLocation').value;
        const sport = document.getElementById('searchSport').value;
        
        // Redirect to search page with parameters
        window.location.href = `search.html?location=${encodeURIComponent(location)}&sport=${encodeURIComponent(sport)}`;
    });
});

// Check if user is authenticated
async function checkAuthStatus() {
    try {
        const response = await fetch('/api/auth/me');
        const data = await response.json();
        
        if (data.authenticated) {
            // User is logged in
            document.getElementById('loginLink').style.display = 'none';
            document.getElementById('dashboardLink').style.display = 'inline';
            document.getElementById('logoutLink').style.display = 'inline';
            
            // Add logout functionality
            document.getElementById('logoutLink').addEventListener('click', function(e) {
                e.preventDefault();
                logout();
            });
        } else {
            // User is not logged in
            document.getElementById('loginLink').style.display = 'inline';
            document.getElementById('dashboardLink').style.display = 'none';
            document.getElementById('logoutLink').style.display = 'none';
        }
    } catch (error) {
        console.error('Error checking auth status:', error);
    }
}

// Logout function
async function logout() {
    try {
        await fetch('/logout', { method: 'POST' });
        window.location.reload();
    } catch (error) {
        console.error('Error during logout:', error);
        window.location.reload();
    }
}

// Load top rated turfs
async function loadTopRatedTurfs() {
    const carousel = document.getElementById('topRatedCarousel');
    
    // Show loading state
    carousel.innerHTML = '<div class="loading">Loading top rated turfs...</div>';
    
    try {
        const response = await fetch('/api/turfs/top-rated');
        const turfs = await response.json();
        
        carousel.innerHTML = '';
        
        if (turfs.length === 0) {
            carousel.innerHTML = '<p class="text-center">No top rated turfs available at the moment.</p>';
            return;
        }
        
        turfs.forEach(turf => {
            const turfCard = createTurfCard(turf);
            carousel.appendChild(turfCard);
        });
        
        // Initialize carousel if there are multiple turfs
        if (turfs.length > 1) {
            initCarousel();
        }
    } catch (error) {
        console.error('Error loading top rated turfs:', error);
        carousel.innerHTML = 
            '<p class="text-center">Unable to load top rated turfs at the moment.</p>';
    }
}

// Create turf card element
function createTurfCard(turf) {
    const card = document.createElement('div');
    card.className = 'turf-card';
    card.style.gridColumn = 'span 4';
    
    card.innerHTML = `
        <div class="card">
            <div class="card-image">
                <img src="${turf.imageUrl || 'images/default-turf.jpg'}" alt="${turf.name}" onerror="this.src='images/default-turf.jpg'">
                <div class="rating">${turf.rating ? turf.rating.toFixed(1) : 'N/A'} ⭐</div>
            </div>
            <div class="card-body">
                <h3>${turf.name}</h3>
                <p class="location">${turf.city}, ${turf.area}</p>
                <p class="sport">${turf.turfType}</p>
                <p class="price">₹${turf.pricePerHour}/hour</p>
                <a href="turf-details.html?id=${turf.id}" class="btn btn-primary">View Details</a>
            </div>
        </div>
    `;
    
    return card;
}

// Initialize carousel functionality
function initCarousel() {
    const carousel = document.getElementById('topRatedCarousel');
    let currentIndex = 0;
    const cards = carousel.querySelectorAll('.turf-card');
    
    if (cards.length <= 3) return; // No need for carousel if all cards fit
    
    // Add navigation arrows
    const prevBtn = document.createElement('button');
    prevBtn.className = 'carousel-btn prev';
    prevBtn.innerHTML = '‹';
    prevBtn.onclick = () => navigateCarousel(-1);
    
    const nextBtn = document.createElement('button');
    nextBtn.className = 'carousel-btn next';
    nextBtn.innerHTML = '›';
    nextBtn.onclick = () => navigateCarousel(1);
    
    carousel.appendChild(prevBtn);
    carousel.appendChild(nextBtn);
    
    function navigateCarousel(direction) {
        currentIndex += direction;
        if (currentIndex < 0) currentIndex = cards.length - 3;
        if (currentIndex > cards.length - 3) currentIndex = 0;
        
        updateCarouselPosition();
    }
    
    function updateCarouselPosition() {
        cards.forEach((card, index) => {
            if (index >= currentIndex && index < currentIndex + 3) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }
    
    // Auto-advance carousel
    setInterval(() => navigateCarousel(1), 5000);
}

// Load offers
async function loadOffers() {
    try {
        const response = await fetch('/api/offers');
        const offers = await response.json();
        
        const offersGrid = document.getElementById('offersGrid');
        offersGrid.innerHTML = '';
        
        offers.forEach(offer => {
            const offerCard = createOfferCard(offer);
            offersGrid.appendChild(offerCard);
        });
    } catch (error) {
        console.error('Error loading offers:', error);
        document.getElementById('offersGrid').innerHTML = 
            '<p class="text-center">Unable to load offers at the moment.</p>';
    }
}

// Create offer card element
function createOfferCard(offer) {
    const card = document.createElement('div');
    card.className = 'offer-card';
    card.style.gridColumn = 'span 3';
    
    const validFrom = new Date(offer.validFrom).toLocaleDateString();
    const validUntil = new Date(offer.validUntil).toLocaleDateString();
    
    card.innerHTML = `
        <div class="card offer">
            <div class="card-body">
                <h3>${offer.title}</h3>
                <p class="offer-code">Code: <strong>${offer.code}</strong></p>
                <p class="offer-discount">${offer.discountPercentage}% OFF</p>
                <p class="offer-validity">Valid: ${validFrom} - ${validUntil}</p>
            </div>
        </div>
    `;
    
    return card;
}


