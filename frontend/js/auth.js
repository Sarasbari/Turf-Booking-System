// Simple auth state helper using /api/auth/me
async function fetchAuth() {
    try {
        const res = await fetch('/api/auth/me', { credentials: 'include' });
        if (!res.ok) return { authenticated: false };
        return await res.json();
    } catch {
        return { authenticated: false };
    }
}

async function ensureLoginLink() {
    const auth = await fetchAuth();
    const link = document.getElementById('loginLink');
    if (!link) return;
    if (auth.authenticated) {
        link.textContent = 'Dashboard';
        link.href = '/dashboard';
    } else {
        link.textContent = 'Login';
        link.href = '/login.html';
    }
}

document.addEventListener('DOMContentLoaded', ensureLoginLink);


