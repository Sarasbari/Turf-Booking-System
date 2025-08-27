async function loadTurfs() {
    const res = await fetch('/api/admin/turfs');
    const turfs = await res.json();
    const container = document.getElementById('adminTurfs');
    container.innerHTML = '';
    turfs.forEach(t => {
        const col = document.createElement('div');
        col.style.gridColumn = 'span 4';
        col.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h3 class="mb-2">${t.name}</h3>
                    <div class="mb-2">${t.city}</div>
                    <div class="mb-2">₹${t.pricePerHour || 0}/hr</div>
                </div>
            </div>`;
        container.appendChild(col);
    });
}

async function loadTournaments() {
    const res = await fetch('/api/admin/tournaments');
    const list = await res.json();
    const container = document.getElementById('adminTournaments');
    container.innerHTML = '';
    list.forEach(t => {
        const col = document.createElement('div');
        col.style.gridColumn = 'span 4';
        col.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h3 class="mb-2">${t.name}</h3>
                    <div class="mb-2">${t.location}</div>
                    <div class="mb-2">${t.tournamentDate}</div>
                </div>
            </div>`;
        container.appendChild(col);
    });
}

async function createOffer(e) {
    e.preventDefault();
    const payload = {
        title: document.getElementById('offerTitle').value,
        offerCode: document.getElementById('offerCode').value,
        discountPercentage: document.getElementById('offerPercent').value || null,
        validFrom: document.getElementById('offerFrom').value,
        validUntil: document.getElementById('offerUntil').value,
        isActive: true
    };
    const res = await fetch('/api/offers', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    if (res.ok) alert('Offer created'); else alert('Failed to create offer');
}

async function saveTurf(e) {
    e.preventDefault();
    const id = document.getElementById('turfId').value;
    const payload = {
        name: document.getElementById('turfName').value,
        city: document.getElementById('turfCity').value,
        area: document.getElementById('turfArea').value,
        turfType: document.getElementById('turfType').value,
        pricePerHour: document.getElementById('turfPrice').value,
        imageUrl: document.getElementById('turfImage').value,
        isActive: true
    };
    const url = id ? `/api/admin/turfs/${id}` : '/api/admin/turfs';
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    if (res.ok) { alert('Turf saved'); loadTurfs(); (document.getElementById('turfForm').reset()); } else { alert('Failed to save turf'); }
}

document.addEventListener('DOMContentLoaded', async () => {
    // Guard admin
    const authRes = await fetch('/api/auth/me');
    const me = await authRes.json();
    if (!me.authenticated || me.role !== 'ADMIN') {
        alert('Admin access required');
        window.location.href = '/login.html';
        return;
    }

    loadTurfs();
    loadTournaments();
    document.getElementById('offerForm').addEventListener('submit', createOffer);
    document.getElementById('turfForm').addEventListener('submit', saveTurf);
    document.getElementById('uploadForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const file = document.getElementById('uploadFile').files[0];
        if (!file) return;
        const fd = new FormData();
        fd.append('file', file);
        const res = await fetch('/api/uploads', { method: 'POST', body: fd });
        if (res.ok) {
            const data = await res.json();
            document.getElementById('uploadResult').innerHTML = `Uploaded: <a href="${data.url}" target="_blank">${data.url}</a>`;
        } else {
            alert('Upload failed');
        }
    });
    document.getElementById('tournamentForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('tourId').value;
        const payload = {
            name: document.getElementById('tourName').value,
            location: document.getElementById('tourLocation').value,
            turfType: document.getElementById('tourType').value,
            tournamentDate: document.getElementById('tourDate').value,
            startTime: document.getElementById('tourStart').value,
            endTime: document.getElementById('tourEnd').value,
            entryFee: document.getElementById('tourEntry').value,
            prizeMoney: document.getElementById('tourPrize').value
        };
        const url = id ? `/api/admin/tournaments/${id}` : '/api/admin/tournaments';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        if (res.ok) { alert('Tournament saved'); loadTournaments(); (document.getElementById('tournamentForm').reset()); } else { alert('Failed to save tournament'); }
    });
});


