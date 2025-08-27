// Bookings page JavaScript

async function populateTurfs() {
    const res = await fetch('/api/turfs');
    const turfs = await res.json();
    const select = document.getElementById('turf-select');
    select.innerHTML = '<option value="">Choose a turf</option>';
    turfs.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = `${t.name} - ${t.city}`;
        select.appendChild(opt);
    });
}

async function submitBooking(e) {
    e.preventDefault();
    const authRes = await fetch('/api/auth/me');
    const me = await authRes.json();
    if (!me.authenticated) {
        window.location.href = '/login.html';
        return;
    }
    const turfId = document.getElementById('turf-select').value;
    const bookingDate = document.getElementById('date').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const numberOfPlayers = parseInt(document.getElementById('players').value || '10', 10);
    if (!turfId || !bookingDate || !startTime || !endTime) {
        alert('Please fill all fields');
        return;
    }
    const payload = { userId: me.id, turfId: Number(turfId), bookingDate, startTime, endTime, numberOfPlayers };
    const res = await fetch('/api/bookings', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    if (res.ok) alert('Booking created!'); else alert('Booking failed');
}

document.addEventListener('DOMContentLoaded', function() {
    populateTurfs();
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) bookingForm.addEventListener('submit', submitBooking);
    const dateInput = document.getElementById('date');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.min = today;
    }
});