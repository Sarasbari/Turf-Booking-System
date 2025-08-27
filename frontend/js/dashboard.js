async function loadBookings() {
    const authRes = await fetch('/api/auth/me');
    const me = await authRes.json();
    if (!me.authenticated) { window.location.href = '/login.html'; return; }
    const res = await fetch(`/api/bookings/user/${me.id}`);
    const bookings = await res.json();
    const list = document.getElementById('bookingsList');
    const empty = document.getElementById('noBookings');
    list.innerHTML = '';
    if (!bookings.length) { empty.style.display = 'block'; return; }
    empty.style.display = 'none';
    bookings.forEach(b => {
        const col = document.createElement('div');
        col.style.gridColumn = 'span 6';
        const canPay = b.status === 'PENDING';
        col.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <div class="flex justify-between items-center">
                        <div><strong>${b.turf?.name || 'Turf'}</strong></div>
                        <div>${b.status}</div>
                    </div>
                    <div class="mt-2">Date: ${b.bookingDate}</div>
                    <div class="mt-2">Time: ${b.startTime} - ${b.endTime}</div>
                    <div class="mt-2">Amount: ₹${b.totalAmount || 0}</div>
                    ${canPay ? '<button class="btn mt-2" data-booking="'+b.id+'">Pay Now</button>' : ''}
                </div>
            </div>`;
        list.appendChild(col);
    });

    list.querySelectorAll('button[data-booking]').forEach(btn => {
        btn.addEventListener('click', async () => {
            const bookingId = btn.getAttribute('data-booking');
            const payload = { bookingId, method: 'UPI', details: '{}' };
            const res = await fetch('/api/transactions/pay', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            if (res.ok) { alert('Payment successful'); location.reload(); } else { alert('Payment failed'); }
        });
    });
}

document.addEventListener('DOMContentLoaded', loadBookings);


