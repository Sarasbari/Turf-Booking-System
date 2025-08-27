function qs(name) {
    const url = new URL(window.location.href);
    return url.searchParams.get(name);
}

async function fetchTurf(id) {
    const res = await fetch(`/api/turfs/${id}/details`);
    if (!res.ok) return null;
    return res.json();
}

async function fetchReviews(turfId) {
    const res = await fetch(`/api/reviews/turf/${turfId}`);
    if (!res.ok) return [];
    return res.json();
}

async function renderTurf() {
    const id = qs('id');
    if (!id) return;
    const turf = await fetchTurf(id);
    if (!turf) return;

    const header = document.getElementById('turfHeader');
    header.innerHTML = `
        <div style="grid-column: span 7" class="card fade-in">
            <img src="${turf.imageUrl || 'images/placeholder.jpg'}" alt="${turf.name}">
        </div>
        <div style="grid-column: span 5" class="fade-in-delayed">
            <h2>${turf.name}</h2>
            <div class="mt-2 mb-2"><span class="badge">${turf.turfType}</span> ⭐ ${turf.rating || 0} (${turf.totalReviews || 0})</div>
            <p class="mb-2">${turf.description || ''}</p>
            <div class="mb-2">Location: ${turf.location || ''}</div>
            <div class="mb-2">Price: ₹${turf.pricePerHour || 0} / hr</div>
        </div>`;

    const booking = document.getElementById('bookingSection');
    booking.innerHTML = `
        <div style="grid-column: span 12" class="card">
            <div class="card-body">
                <h3 class="mb-2">Book a Slot</h3>
                <form id="bookForm">
                    <div class="input-group mb-2">
                        <input required type="date" id="date" />
                        <input required type="time" id="startTime" />
                        <input required type="time" id="endTime" />
                        <input type="number" id="players" min="1" value="10" />
                        <button class="btn" type="submit">Book Now</button>
                    </div>
                </form>
                <div id="bookingMsg" class="mt-2"></div>
            </div>
        </div>`;

    document.getElementById('bookForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const auth = await fetch('/api/auth/me');
        const user = await auth.json();
        if (!user.authenticated) {
            window.location.href = '/login.html';
            return;
        }
        const payload = {
            userId: user.id,
            turfId: turf.id,
            bookingDate: document.getElementById('date').value,
            startTime: document.getElementById('startTime').value,
            endTime: document.getElementById('endTime').value,
            numberOfPlayers: parseInt(document.getElementById('players').value || '10', 10)
        };
        const res = await fetch('/api/bookings', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        document.getElementById('bookingMsg').textContent = res.ok ? 'Booking created!' : 'Failed to create booking';
    });

    const reviews = await fetchReviews(id);
    const reviewsSection = document.getElementById('reviewsSection');
    reviewsSection.innerHTML = `<h3 class=\"mb-2\">Reviews</h3>` + reviews.map(r => `
        <div class="card mb-2"><div class="card-body">
            <div>⭐ ${r.rating}</div>
            <div class="mt-2">${r.reviewText || ''}</div>
        </div></div>`).join('');

    // Google Maps embed (no API key needed for place embed placeholder)
    if (turf.latitude && turf.longitude) {
        const map = document.createElement('div');
        map.className = 'mt-4';
        map.innerHTML = `
            <iframe width="100%" height="320" style="border:0" loading="lazy" allowfullscreen
              src="https://www.google.com/maps?q=${turf.latitude},${turf.longitude}&z=15&output=embed"></iframe>`;
        reviewsSection.appendChild(map);
    }

    // Review form
    const authRes = await fetch('/api/auth/me');
    const me = await authRes.json();
    if (me.authenticated) {
        const formWrap = document.createElement('div');
        formWrap.className = 'card mt-4';
        formWrap.innerHTML = `
            <div class=\"card-body\">
                <h3 class=\"mb-2\">Write a Review</h3>
                <form id=\"reviewForm\">
                    <div class=\"input-group\">
                        <select id=\"rating\" required>
                            <option value=\"\">Rating</option>
                            <option>5</option>
                            <option>4</option>
                            <option>3</option>
                            <option>2</option>
                            <option>1</option>
                        </select>
                        <input id=\"reviewText\" type=\"text\" placeholder=\"Your feedback\" />
                        <button class=\"btn\" type=\"submit\">Submit</button>
                    </div>
                </form>
            </div>`;
        reviewsSection.appendChild(formWrap);

        document.getElementById('reviewForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const payload = {
                userId: me.id,
                turfId: turf.id,
                rating: Number(document.getElementById('rating').value),
                reviewText: document.getElementById('reviewText').value
            };
            const res = await fetch('/api/reviews', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            if (res.ok) location.reload();
            else alert('Failed to submit review');
        });
    }
}

document.addEventListener('DOMContentLoaded', renderTurf);


