async function loadTournaments() {
    try {
        const res = await fetch('/api/tournaments/upcoming');
        const tournaments = await res.json();
        const container = document.getElementById('tournamentsList');
        container.innerHTML = '';
        
        if (tournaments.length === 0) {
            container.innerHTML = '<p class="text-center">No upcoming tournaments at the moment.</p>';
            return;
        }
        
        tournaments.forEach(t => {
            const col = document.createElement('div');
            col.style.gridColumn = 'span 4';
            col.innerHTML = `
                <article class="card slide-up">
                    <img src="${t.imageUrl || 'images/placeholder.jpg'}" alt="${t.name}" onerror="this.src='images/placeholder.jpg'">
                    <div class="card-body">
                        <h3 class="mb-2">${t.name}</h3>
                        <div class="mb-2">${t.location}</div>
                        <div class="tournament-date mb-2">${new Date(t.tournamentDate).toLocaleDateString()}</div>
                        <div class="tournament-info mb-2">
                            <span class="badge">${t.turfType}</span>
                            <span class="badge">Entry: ₹${t.entryFee}</span>
                            ${t.prizeMoney ? `<span class="badge">Prize: ₹${t.prizeMoney}</span>` : ''}
                        </div>
                        <div class="tournament-actions">
                            <a class="btn ghost" href="turf-details.html?id=${t.id}">View Details</a>
                            <button class="btn" onclick="registerForTournament(${t.id})">Register</button>
                        </div>
                    </div>
                </article>`;
            container.appendChild(col);
        });
    } catch (error) {
        console.error('Error loading tournaments:', error);
        document.getElementById('tournamentsList').innerHTML = 
            '<p class="text-center">Unable to load tournaments at the moment.</p>';
    }
}

// Tournament registration function
async function registerForTournament(tournamentId) {
    try {
        // Check if user is authenticated
        const authResponse = await fetch('/api/auth/me');
        const authData = await authResponse.json();
        
        if (!authData.authenticated) {
            alert('Please login to register for tournaments');
            window.location.href = 'login.html';
            return;
        }
        
        // Get team information from user
        const teamName = prompt('Enter your team name:');
        if (!teamName) return;
        
        const teamMembers = prompt('Enter team members (comma separated):');
        if (!teamMembers) return;
        
        const contactPhone = prompt('Enter contact phone number:');
        if (!contactPhone) return;
        
        // Register for tournament
        const response = await fetch(`/api/tournaments/${tournamentId}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: authData.id,
                teamName: teamName,
                teamMembers: teamMembers,
                contactPhone: contactPhone
            })
        });
        
        if (response.ok) {
            alert('Successfully registered for tournament!');
            loadTournaments(); // Refresh the list
        } else {
            const errorData = await response.json();
            alert('Registration failed: ' + (errorData.message || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error registering for tournament:', error);
        alert('Failed to register for tournament. Please try again.');
    }
}

document.addEventListener('DOMContentLoaded', loadTournaments);


