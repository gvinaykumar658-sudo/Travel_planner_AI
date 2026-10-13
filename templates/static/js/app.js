// 🔥 MAIN GENERATE FUNCTION
function generate() {
    let city = document.getElementById("city").value.trim();
    let budget = document.getElementById("budget").value.trim();
    let people = document.getElementById("people").value.trim();
    let days = document.getElementById("days").value.trim();

    if (!city || !budget || !people || !days) {
        if (window.Swal) Swal.fire({icon:'warning', title:'Missing fields', text:'Fill all fields'});
        else alert("Fill all fields");
        return;
    }

    document.getElementById("result").innerHTML = `
        <div class="loader"></div>
        <p>AI Agents are working...</p>
    `;

    fetch(`/generate/?city=${city}&budget=${budget}&people=${people}&days=${days}`)
    .then(res => res.json())
    .then(data => {
        // Show success alert
        if (window.Swal) {
            Swal.fire({
                icon:'success',
                title:'✨ Plan Generated!',
                text:'AI agents have created your perfect trip plan',
                timer:2000,
                timerProgressBar:true,
                didClose: () => {}
            });
        }

        document.getElementById("result").innerHTML = `
        
        <!-- SUMMARY -->
        <div class="card">
            <h2>📍 ${data.city}</h2>
            <p><b>👥 People:</b> ${data.people}</p>
            <p><b>📅 Days:</b> ${data.days}</p>
            <p><b>💰 Budget:</b> ${data.budget}</p>
        </div>

        <!-- AGENTS -->
        <h2>🤖 Agent Outputs</h2>

        <div class="agent-grid">

            <div class="agent-card">
                <h3>🏙 City</h3>
                <div class="agent-content">
                    <div class="city-agent-map">
                        <iframe 
                            width="100%" 
                            height="200"
                            style="border-radius:10px;border:none;margin-bottom:12px;"
                            src="https://www.google.com/maps?q=${data.city}&output=embed">
                        </iframe>
                    </div>
                    <div class="city-agent-text">
                        ${data.agents.city_agent}
                    </div>
                </div>
            </div>

            <div class="agent-card">
                <h3>💰 Budget</h3>
                <div class="agent-content">
                    ${data.agents.budget_agent.replace(/\n/g, "<br>")}
                </div>
            </div>

            <div class="agent-card">
                <h3>🚆 Transport</h3>
                <div class="agent-content">
                    ${data.agents.transport_agent.replace(/\n/g, "<br>")}
                </div>
            </div>

            <div class="agent-card">
                <h3>🍽 Food</h3>
                <div class="agent-content">
                    ${data.agents.food_agent.replace(/\n/g, "<br>")}
                </div>
            </div>

            <div class="agent-card">
                <h3>📅 Itinerary</h3>
                <div class="agent-content itinerary-box">
                    ${data.agents.itinerary_agent.replace(/\n/g, "<br>")}
                </div>
            </div>

        </div>

        <!-- ACTION BUTTONS -->
        <div style="text-align:center;margin-top:20px;">
            <button onclick="saveTrip(event)" data-city="${data.city}" data-budget="${data.budget}" data-itinerary="${data.itinerary}">
                💾 Save Trip
            </button>

            <button onclick="downloadPDF(event)" data-city="${data.city}" data-itinerary="${data.itinerary}">
                📄 Download PDF
            </button>
        </div>
        `;
    })
    .catch(err => {
        console.error(err);
        alert("Error generating plan");
    });
}


// 💾 SAVE TRIP FUNCTION
function saveTrip(event) {
    event.preventDefault();
    const btn = event.target;
    const city = btn.dataset.city;
    const budget = btn.dataset.budget;
    const itinerary = btn.dataset.itinerary;
    
    // Use POST with CSRF token and proper error handling
    function getCookie(name) {
        let cookieValue = null;
        if (document.cookie && document.cookie !== '') {
            const cookies = document.cookie.split(';');
            for (let i = 0; i < cookies.length; i++) {
                const cookie = cookies[i].trim();
                if (cookie.substring(0, name.length + 1) === (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }

    const csrftoken = getCookie('csrftoken');

    fetch(`/save/`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRFToken': csrftoken,
            'Accept': 'application/json'
        },
        credentials: 'same-origin',
        body: JSON.stringify({ city, budget, itinerary })
    })
    .then(res => {
        // If server redirected to login page it will return HTML; detect that
        const ctype = res.headers.get('content-type') || '';
        if (res.status === 401 || res.status === 403) {
            if (window.Swal) Swal.fire({icon:'warning', title:'Login required', text:'Please log in to save trips'});
            else alert('Please log in to save trips');
            return Promise.reject('auth');
        }
        if (!res.ok) return res.text().then(t => Promise.reject(t));
        if (!ctype.includes('application/json')) {
            // likely redirected to login HTML
            if (window.Swal) Swal.fire({icon:'warning', title:'Login required', text:'Please log in to save trips'});
            else alert('Please log in to save trips');
            return Promise.reject('auth');
        }
        return res.json();
    })
    .then(() => {
        // update UI: show success toast and refresh saved list
        if (window.Swal) {
            Swal.fire({
                icon:'success',
                title:'💾 Trip Saved!',
                text:'Your trip has been saved successfully',
                timer:1800,
                timerProgressBar:true,
                didClose: () => location.reload()
            });
        } else {
            alert("✅ Trip Saved Successfully");
            setTimeout(()=> location.reload(), 700);
        }
    })
    .catch((err) => {
        if (err === 'auth') return;
        console.error('saveTrip error', err);
        if (window.Swal) Swal.fire({icon:'error', title:'Save failed', text:'Error saving trip'});
        else alert("❌ Error saving trip");
    });
}

// ===== Sidebar toggle behavior =====
document.addEventListener('DOMContentLoaded', function(){
    const toggle = document.getElementById('sidebarToggle');
    const overlay = document.createElement('div');
    overlay.className = 'sidenav-overlay';
    document.body.appendChild(overlay);

    function closeSidebar() {
        document.body.classList.remove('sidenav-open');
        document.body.classList.add('sidenav-collapsed');
    }

    function openSidebar() {
        document.body.classList.add('sidenav-open');
        document.body.classList.remove('sidenav-collapsed');
    }

    // default collapsed on small screens
    if (window.innerWidth <= 900) document.body.classList.add('sidenav-collapsed');

    if (toggle) toggle.addEventListener('click', function(e){
        e.preventDefault();
        if (document.body.classList.contains('sidenav-open')) closeSidebar(); else openSidebar();
    });

    overlay.addEventListener('click', closeSidebar);
    window.addEventListener('resize', function(){
        if (window.innerWidth > 900) { document.body.classList.remove('sidenav-collapsed'); document.body.classList.remove('sidenav-open'); }
        else document.body.classList.add('sidenav-collapsed');
    });

    // logout confirmation / notice
    const logoutLink = document.querySelector('.logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e){
            e.preventDefault();
            const href = this.href;
            if (window.Swal) {
                Swal.fire({
                    title: 'Logout',
                    text: 'Are you sure you want to logout?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, logout',
                }).then(res => {
                    if (res.isConfirmed) {
                        Swal.fire({
                            icon:'success',
                            title:'👋 See you!',
                            text:'You have been logged out',
                            timer:1200,
                            timerProgressBar:true,
                            didClose: () => window.location = href
                        });
                    }
                });
            } else {
                if (confirm('Logout?')) window.location = href;
            }
        });
    }

    // saved-item click: open modal with saved trip details
    const modal = document.getElementById('tripModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMeta = document.getElementById('modalMeta');
    const modalContent = document.getElementById('modalContent');
    const modalClose = document.getElementById('modalClose');
    const modalBackdrop = document.getElementById('modalBackdrop');

    function openModal() { if (modal) { modal.classList.add('show'); modal.setAttribute('aria-hidden','false'); document.body.style.overflow = 'hidden'; } }
    function closeModal() { if (modal) { modal.classList.remove('show'); modal.setAttribute('aria-hidden','true'); document.body.style.overflow = ''; } }

    document.querySelectorAll('.saved-item').forEach(function(el){
        el.addEventListener('click', function(e){
            e.preventDefault();
            const city = this.dataset.city || '';
            const budget = this.dataset.budget || '';
            const itineraryRaw = this.dataset.itinerary || '';

            if (modalTitle) modalTitle.textContent = city || 'Saved Trip';
            if (modalMeta) modalMeta.textContent = budget ? `Budget: ${budget}` : '';
            if (modalContent) {
                let text = itineraryRaw || '';

                // Try to decode escaped unicode sequences (e.g. \u000A, \u0027) by using JSON parsing trick
                try {
                    // wrap in quotes and parse to convert escape sequences
                    text = JSON.parse('"' + text.replace(/"/g, '\\"') + '"');
                } catch (e) {
                    // fallback replacements for common escapes
                    text = text.replace(/\\u000A/g, '\n').replace(/\\n/g, '\n');
                    text = text.replace(/\\u00A0/g, ' ');
                    text = text.replace(/\\u0027/g, "'");
                    // decode any remaining \uXXXX sequences
                    text = text.replace(/\\u([0-9A-Fa-f]{4})/g, function(_, hex){
                        return String.fromCharCode(parseInt(hex,16));
                    });
                }

                // helper to fix common mojibake (UTF-8 bytes interpreted as latin1) and remove stray artifacts
                function fixMojibake(s){
                    if (!s || typeof s !== 'string') return s || '';
                    try {
                        const decoded = decodeURIComponent(escape(s));
                        if (decoded && decoded.indexOf('Ã') === -1) s = decoded;
                    } catch(e) {}
                    s = s.replace(/\uFFFD/g, '');
                    s = s.replace(/[\x00-\x08\x0B\x0C\x0E-\x1F]+/g, ' ');
                    s = s.replace(/[ÂÃâ]/g, '');
                    return s.trim();
                }

                // use data-budget first for modal meta if available and decode escapes
                const budgetAttrRaw = el.dataset.budget || '';
                let budgetAttr = budgetAttrRaw;
                try {
                    budgetAttr = JSON.parse('"' + budgetAttrRaw.replace(/"/g, '\\"') + '"');
                } catch (e) {
                    budgetAttr = budgetAttrRaw.replace(/\\u0027/g, "'").replace(/\\u00A0/g, ' ');
                }
                // clean budget and text from mojibake artifacts
                budgetAttr = fixMojibake(budgetAttr);
                if (modalMeta) {
                    modalMeta.textContent = budgetAttr ? budgetAttr : '';
                }

                // Remove any leading budget line from text to avoid duplication
                text = text.replace(/^\s*Budget:.*(?:\n|$)/i, '');
                text = fixMojibake(text);

                // split into lines and create bullet points
                const lines = text.split(/\n+/).map(l => l.trim()).filter(l => l.length > 0);
                if (lines.length === 0) {
                    modalContent.innerHTML = '<em>No details available</em>';
                } else {
                    const ul = document.createElement('ul');
                    ul.style.margin = '0';
                    ul.style.paddingLeft = '18px';
                    lines.forEach(function(ln){
                        const li = document.createElement('li');
                        // remove markdown-like bold markers and stray Unicode artifacts
                        li.innerHTML = ln.replace(/\*\*/g, '').replace(/\* /g, '').replace(/\u00[A-Fa-f0-9]{2}/g, '');
                        ul.appendChild(li);
                    });
                    modalContent.innerHTML = '';
                    modalContent.appendChild(ul);
                }
            }

            // set current trip id, city, and itinerary on modal for delete and download actions
            if (modal) {
                modal.dataset.tripId = this.dataset.id || '';
                modal.dataset.tripCity = this.dataset.city || '';
                modal.dataset.tripItinerary = this.dataset.itinerary || '';
            }
            // show modal
            openModal();
        });
    });

    if (modalClose) modalClose.addEventListener('click', closeModal);
    const modalCloseFooter = document.getElementById('modalCloseFooter');
    if (modalCloseFooter) modalCloseFooter.addEventListener('click', closeModal);

    // Delete saved trip from modal
    const deleteBtn = document.getElementById('deleteTripBtn');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', function(e){
            e.preventDefault();
            const tripId = modal?.dataset?.tripId;
            if (!tripId) { if (window.Swal) Swal.fire({icon:'error', text:'No trip selected'}); return; }
            if (window.Swal) {
                Swal.fire({ title:'Delete trip?', text:'This will permanently delete the saved trip.', icon:'warning', showCancelButton:true, confirmButtonText:'Delete' })
                .then(res => {
                    if (!res.isConfirmed) return;
                    // perform delete
                    const csrftoken = (function(name){
                        let cookieValue = null;
                        if (document.cookie && document.cookie !== '') {
                            const cookies = document.cookie.split(';');
                            for (let i = 0; i < cookies.length; i++) {
                                const cookie = cookies[i].trim();
                                if (cookie.substring(0, name.length + 1) === (name + '=')) {
                                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                                    break;
                                }
                            }
                        }
                        return cookieValue;
                    })('csrftoken');

                    fetch(`/delete/${tripId}/`, { method:'POST', headers: {'X-CSRFToken': csrftoken, 'Accept':'application/json'}, credentials:'same-origin' })
                    .then(res => {
                        if (!res.ok) return res.text().then(t => Promise.reject(t));
                        return res.json();
                    })
                    .then(() => {
                        if (window.Swal) {
                            Swal.fire({
                                icon:'success',
                                title:'🗑️ Trip Deleted',
                                text:'Trip has been permanently deleted',
                                timer:1500,
                                timerProgressBar:true,
                                didClose: () => location.reload()
                            });
                        } else {
                            location.reload();
                        }
                    })
                    .catch(err => {
                        console.error('delete error', err);
                        if (window.Swal) Swal.fire({icon:'error', title:'Error', text:'Could not delete trip'});
                    });
                });
            } else {
                if (confirm('Delete this saved trip?')) {
                    fetch(`/delete/${tripId}/`, { method:'POST', credentials:'same-origin' })
                    .then(()=> location.reload());
                }
            }
        });
    }
    
    // Download PDF from modal
    const downloadPDFBtn = document.getElementById('downloadPDFBtn');
    if (downloadPDFBtn) {
        downloadPDFBtn.addEventListener('click', function(e){
            e.preventDefault();
            const city = modal?.dataset?.tripCity;
            const itinerary = modal?.dataset?.tripItinerary;
            if (!city || !itinerary) { 
                if (window.Swal) Swal.fire({icon:'error', text:'No trip data available for download'}); 
                return; 
            }
            // Generate PDF download
            window.open(`/download/?city=${encodeURIComponent(city)}&itinerary=${encodeURIComponent(itinerary)}`);
        });
    }
    
    if (modalBackdrop) modalBackdrop.addEventListener('click', closeModal);
    document.addEventListener('keydown', function(e){ if (e.key === 'Escape') closeModal(); });
});

function downloadPDF(event) {
    event.preventDefault();
    const btn = event.target;
    const city = btn.dataset.city;
    const itinerary = btn.dataset.itinerary;
    if (window.Swal) {
        Swal.fire({
            icon:'success',
            title:'📄 Download Started',
            text:'Your PDF is being downloaded...',
            timer:1500,
            timerProgressBar:true
        });
    }
    window.open(`/download/?city=${encodeURIComponent(city)}&itinerary=${encodeURIComponent(itinerary)}`);
}