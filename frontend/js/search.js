let currentPage = 0;
let lastPage = 0;

async function searchTurfs(page = 0) {
    const location = document.getElementById('inputLocation').value.trim();
    const type = document.getElementById('selectType').value;
    const [sortBy, sortDirection] = document.getElementById('selectSort').value.split(',');
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;
    const minRating = document.getElementById('minRating').value;

    const params = new URLSearchParams();
    if (location) params.set('city', location);
    if (type) params.set('turfType', type);
    if (minPrice) params.set('minPrice', minPrice);
    if (maxPrice) params.set('maxPrice', maxPrice);
    if (minRating) params.set('minRating', minRating);
    params.set('sortBy', sortBy);
    params.set('sortDirection', sortDirection);
    params.set('size', '12');
    params.set('page', String(page));

    const res = await fetch(`/api/turfs/search?${params.toString()}`);
    const data = await res.json();
    const turfs = data.content || [];
    currentPage = data.number || 0;
    lastPage = (data.totalPages || 1) - 1;
    renderResults(turfs);
    renderPager();
}

function renderResults(turfs) {
    const container = document.getElementById('results');
    const noResults = document.getElementById('noResults');
    container.innerHTML = '';
    if (!turfs.length) {
        noResults.style.display = 'block';
        return;
    }
    noResults.style.display = 'none';

    turfs.forEach(t => {
        const col = document.createElement('div');
        col.style.gridColumn = 'span 4';
        col.innerHTML = `
            <article class="card fade-in">
                <img src="${t.imageUrl || 'images/placeholder.jpg'}" alt="${t.name}">
                <div class="card-body">
                    <h3 class="mb-2">${t.name}</h3>
                    <div class="mb-2"><span class="badge">${t.turfType}</span><span>${t.city} • ${t.area}</span></div>
                    <div class="mb-2">₹${t.pricePerHour || 0}/hr • ⭐ ${t.rating || 0}</div>
                    <a class="btn" href="/turf-details?id=${t.id}">Book Now</a>
                </div>
            </article>`;
        container.appendChild(col);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnSearch').addEventListener('click', searchTurfs);
    document.getElementById('prevPage').addEventListener('click', () => { if (currentPage > 0) searchTurfs(currentPage - 1); });
    document.getElementById('nextPage').addEventListener('click', () => { if (currentPage < lastPage) searchTurfs(currentPage + 1); });
    searchTurfs(0);
});

function renderPager() {
    const pageInfo = document.getElementById('pageInfo');
    pageInfo.textContent = `Page ${currentPage + 1} of ${lastPage + 1}`;
}


