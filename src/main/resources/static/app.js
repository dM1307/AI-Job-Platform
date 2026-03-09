const state = { matches: 0 };

const resumeForm = document.getElementById('resumeForm');
const jobForm = document.getElementById('jobForm');
const refreshBtn = document.getElementById('refreshBtn');
const resumeList = document.getElementById('resumeList');
const logEl = document.getElementById('activityLog');

const kpiResumes = document.getElementById('kpiResumes');
const kpiJobs = document.getElementById('kpiJobs');
const kpiMatches = document.getElementById('kpiMatches');

function log(message) {
    const li = document.createElement('li');
    li.textContent = `${new Date().toLocaleTimeString()} — ${message}`;
    logEl.prepend(li);
}

async function request(path, options = {}) {
    const response = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });

    if (!response.ok) {
        let msg = `Request failed (${response.status})`;
        try {
            const err = await response.json();
            msg = err.error || msg;
        } catch (e) {
            // keep default message
        }
        throw new Error(msg);
    }

    const contentType = response.headers.get('content-type') || '';
    return contentType.includes('application/json') ? response.json() : response.text();
}

async function refresh() {
    try {
        const [resumes, jobs] = await Promise.all([
            request('/api/resumes'),
            fetch('/api/jobs', { method: 'GET' }).then(async r => r.ok ? r.json() : [])
        ]);

        kpiResumes.textContent = resumes.length;
        kpiJobs.textContent = Array.isArray(jobs) ? jobs.length : 0;
        kpiMatches.textContent = state.matches;

        resumeList.innerHTML = '';

        resumes.forEach((resume) => {
            const card = document.createElement('div');
            card.className = 'item';
            card.innerHTML = `
                <strong>${resume.candidateName}</strong><br/>
                <small>${resume.email}</small><br/>
                <small>Skills: ${(resume.skills || []).join(', ') || '—'}</small>
                <div class="item-actions">
                  <button data-id="${resume.id}" class="match-btn">Match Jobs</button>
                </div>
            `;
            resumeList.appendChild(card);
        });

        document.querySelectorAll('.match-btn').forEach((btn) => {
            btn.addEventListener('click', async () => {
                const id = btn.getAttribute('data-id');
                try {
                    const jobs = await request(`/api/resumes/${id}/match-jobs`);
                    state.matches += 1;
                    kpiMatches.textContent = state.matches;
                    log(`Matched resume #${id} with ${jobs.length} jobs`);
                } catch (error) {
                    log(`Match failed: ${error.message}`);
                }
            });
        });
    } catch (error) {
        log(`Refresh failed: ${error.message}`);
    }
}

resumeForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = {
        candidateName: document.getElementById('candidateName').value,
        email: document.getElementById('candidateEmail').value,
        rawText: document.getElementById('candidateRawText').value
    };

    try {
        await request('/api/resumes', { method: 'POST', body: JSON.stringify(payload) });
        log(`Resume created for ${payload.candidateName}`);
        resumeForm.reset();
        await refresh();
    } catch (error) {
        log(`Resume create failed: ${error.message}`);
    }
});

jobForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = {
        title: document.getElementById('jobTitle').value,
        description: document.getElementById('jobDescription').value
    };

    try {
        await request('/api/jobs', { method: 'POST', body: JSON.stringify(payload) });
        log(`Job created: ${payload.title}`);
        jobForm.reset();
        await refresh();
    } catch (error) {
        log(`Job create failed: ${error.message}`);
    }
});

refreshBtn.addEventListener('click', refresh);
refresh();
