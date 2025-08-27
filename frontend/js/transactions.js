async function loadTransactions() {
    const authRes = await fetch('/api/auth/me');
    const me = await authRes.json();
    if (!me.authenticated) {
        window.location.href = '/login.html';
        return;
    }
    const res = await fetch(`/api/transactions/user/${me.id}`);
    const list = await res.json();
    const container = document.getElementById('transactionsList');
    container.innerHTML = '';
    list.forEach(tx => {
        const col = document.createElement('div');
        col.style.gridColumn = 'span 6';
        col.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <div class="flex justify-between items-center">
                        <div><strong>${tx.transactionId || 'TXN'}</strong></div>
                        <div>${tx.paymentStatus}</div>
                    </div>
                    <div class="mt-2">Amount: ₹${tx.amount}</div>
                    <div class="mt-2">Method: ${tx.paymentMethod}</div>
                    <div class="mt-2">Date: ${tx.transactionDate?.replace('T',' ') || ''}</div>
                </div>
            </div>`;
        container.appendChild(col);
    });
}

document.addEventListener('DOMContentLoaded', loadTransactions);


