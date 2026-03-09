/**
 * Frontend Unit Tests — NeuralHire AI Job Platform
 * Run: npm test
 * Framework: Jest + jsdom
 */

// ── DOM Helpers mock ─────────────────────────────────────────────
function setupDOM(extra = '') {
  document.body.innerHTML = `
    <canvas id="bg-canvas"></canvas>
    <div id="toast-container"></div>
    <div id="activityLog"></div>
    <div id="kpiResumes">—</div>
    <div id="kpiJobs">—</div>
    <div id="kpiMatches">—</div>
    <div id="kpiAvgScore">—</div>
    <div id="dashResumeTable"></div>
    <div id="resumeTable"></div>
    <div id="jobTable"></div>
    <div id="matchHistory"></div>
    <div id="matchPageResult"></div>
    <div id="matchResult"></div>
    <div id="skillResults"></div>
    <div id="popularSkills"></div>
    <input id="rName" />
    <input id="rEmail" />
    <input id="rPhone" />
    <textarea id="rSummary"></textarea>
    <input id="rSkills" />
    <input id="jTitle" />
    <input id="jCompany" />
    <input id="jLocation" />
    <textarea id="jDesc"></textarea>
    <input id="jSkills" />
    <input id="jSalary" />
    <input id="matchResumeId" />
    <input id="matchJobId" />
    <input id="matchR" />
    <input id="matchJ" />
    <input id="bulkJobId" />
    <input id="skillQuery" />
    <input id="apiBaseUrl" value="http://localhost:8080" />
    <input id="resumeSearch" />
    <input id="jobSearch" />
    <select id="envSelect"><option value="local">Local</option></select>
    <div id="pageTitle"></div>
    <span id="themeIcon">☀</span>
    <span id="themeToggleLabel">Switch to Light Mode</span>
    <div id="matchModal" class="modal-overlay"></div>
    <div class="modal-overlay" id="matchModal2"></div>
    <button id="createResumeBtn"></button>
    <span id="createResumeSpinner" style="display:none"></span>
    <button id="createJobBtn"></button>
    <span id="createJobSpinner" style="display:none"></span>
    <button id="matchBtn"></button>
    <span id="matchSpinner" style="display:none"></span>
    <button id="matchBtn2"></button>
    <span id="matchSpinner2" style="display:none"></span>
    <span id="skillSpinner" style="display:none"></span>
    ${extra}
  `;
}

// ── Utility functions (inlined from index.html) ──────────────────
function escHtml(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

function renderSkillBadges(skills) {
  if (!skills || !skills.length) return '<span style="color:var(--muted)">—</span>';
  return (
    skills
      .slice(0, 3)
      .map((s) => `<span class="badge badge-purple">${escHtml(s)}</span>`)
      .join(' ') +
    (skills.length > 3
      ? ` <span class="badge badge-blue">+${skills.length - 3}</span>`
      : '')
  );
}

function setLoading(btnId, spinnerId, loading) {
  const btn = document.getElementById(btnId);
  const spin = document.getElementById(spinnerId);
  if (btn) btn.disabled = loading;
  if (spin) spin.style.display = loading ? 'inline-block' : 'none';
}

const logs = [];
function addLog(msg) {
  const now = new Date().toLocaleTimeString();
  logs.unshift({ msg, time: now });
  if (logs.length > 50) logs.pop();
}
function clearLog() {
  logs.length = 0;
}

function toast(msg, type = 'info') {
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = msg;
  el.dataset.testType = type;
  const container = document.getElementById('toast-container');
  if (container) container.appendChild(el);
}

const POPULAR_SKILLS = [
  'Java', 'Spring Boot', 'Python', 'Machine Learning', 'React',
  'Node.js', 'Docker', 'Kubernetes', 'PostgreSQL', 'Redis',
  'AWS', 'TypeScript', 'GraphQL', 'Kafka',
];

function renderPopularSkills() {
  const el = document.getElementById('popularSkills');
  if (!el) return;
  el.innerHTML = POPULAR_SKILLS.map(
    (s) =>
      `<span class="skill-tag" data-skill="${s}">${s}</span>`
  ).join('');
}

function renderMatchHistory(matchHistory) {
  const el = document.getElementById('matchHistory');
  if (!el) return;
  if (!matchHistory.length) {
    el.innerHTML = '<div class="empty-state">No matches yet</div>';
    return;
  }
  el.innerHTML = matchHistory
    .slice()
    .reverse()
    .map(
      (m) =>
        `<div class="match-card" data-rid="${m.resumeId}" data-jid="${m.jobId}">
          <span class="match-score">${(m.score * 100).toFixed(1)}%</span>
        </div>`
    )
    .join('');
}

function renderDashResumeTable(allResumes) {
  const tb = document.getElementById('dashResumeTable');
  if (!tb) return;
  if (!allResumes.length) {
    tb.innerHTML = `<tr><td colspan="5">No resumes yet</td></tr>`;
    return;
  }
  tb.innerHTML = allResumes
    .slice(0, 5)
    .map(
      (r) =>
        `<tr data-id="${r.id}">
          <td>#${r.id}</td>
          <td>${escHtml(r.name || '—')}</td>
          <td>${escHtml(r.email || '—')}</td>
          <td>${renderSkillBadges(r.skills)}</td>
          <td><button class="match-btn">Match</button></td>
        </tr>`
    )
    .join('');
}

function filterResumes(allResumes, query) {
  const q = query.toLowerCase();
  return allResumes.filter(
    (r) =>
      (r.name || '').toLowerCase().includes(q) ||
      (r.email || '').toLowerCase().includes(q)
  );
}

function filterJobs(allJobs, query) {
  const q = query.toLowerCase();
  return allJobs.filter(
    (j) =>
      (j.title || '').toLowerCase().includes(q) ||
      (j.company || '').toLowerCase().includes(q)
  );
}

// ── TESTS ─────────────────────────────────────────────────────────

describe('escHtml()', () => {
  test('escapes ampersand', () => {
    expect(escHtml('a & b')).toBe('a &amp; b');
  });
  test('escapes less-than', () => {
    expect(escHtml('<script>')).toBe('&lt;script&gt;');
  });
  test('handles numbers', () => {
    expect(escHtml(42)).toBe('42');
  });
  test('handles empty string', () => {
    expect(escHtml('')).toBe('');
  });
});

describe('renderSkillBadges()', () => {
  test('returns dash for null', () => {
    expect(renderSkillBadges(null)).toContain('—');
  });
  test('returns dash for empty array', () => {
    expect(renderSkillBadges([])).toContain('—');
  });
  test('renders up to 3 badges', () => {
    const html = renderSkillBadges(['Java', 'Python', 'Go']);
    expect(html).toContain('Java');
    expect(html).toContain('Python');
    expect(html).toContain('Go');
    expect(html).not.toContain('+');
  });
  test('renders overflow badge for >3 skills', () => {
    const html = renderSkillBadges(['Java', 'Python', 'Go', 'Rust', 'C++']);
    expect(html).toContain('+2');
  });
  test('escapes skill HTML', () => {
    const html = renderSkillBadges(['<b>bold</b>']);
    expect(html).toContain('&lt;b&gt;bold&lt;/b&gt;');
  });
});

describe('setLoading()', () => {
  beforeEach(() => setupDOM());

  test('disables button and shows spinner when loading=true', () => {
    setLoading('createResumeBtn', 'createResumeSpinner', true);
    expect(document.getElementById('createResumeBtn').disabled).toBe(true);
    expect(document.getElementById('createResumeSpinner').style.display).toBe('inline-block');
  });
  test('enables button and hides spinner when loading=false', () => {
    setLoading('createResumeBtn', 'createResumeSpinner', true);
    setLoading('createResumeBtn', 'createResumeSpinner', false);
    expect(document.getElementById('createResumeBtn').disabled).toBe(false);
    expect(document.getElementById('createResumeSpinner').style.display).toBe('none');
  });
  test('handles missing element gracefully', () => {
    expect(() => setLoading('nonExistent', 'alsoGone', true)).not.toThrow();
  });
});

describe('toast()', () => {
  beforeEach(() => setupDOM());

  test('appends a toast element', () => {
    toast('Hello World', 'success');
    const toasts = document.querySelectorAll('.toast');
    expect(toasts.length).toBeGreaterThan(0);
  });
  test('sets correct type class', () => {
    toast('Error msg', 'error');
    const el = document.querySelector('.toast.error');
    expect(el).toBeTruthy();
  });
  test('contains message text', () => {
    toast('Test message', 'info');
    const el = document.querySelector('.toast.info');
    expect(el.textContent).toContain('Test message');
  });
});

describe('addLog()', () => {
  beforeEach(() => clearLog());

  test('adds entry to log', () => {
    addLog('Resume created');
    expect(logs.length).toBe(1);
    expect(logs[0].msg).toBe('Resume created');
  });
  test('prepends (newest first)', () => {
    addLog('First');
    addLog('Second');
    expect(logs[0].msg).toBe('Second');
  });
  test('caps at 50 entries', () => {
    for (let i = 0; i < 60; i++) addLog(`Item ${i}`);
    expect(logs.length).toBe(50);
  });
  test('includes timestamp', () => {
    addLog('test');
    expect(logs[0].time).toBeTruthy();
  });
});

describe('filterResumes()', () => {
  const resumes = [
    { id: 1, name: 'Alice Chen',    email: 'alice@dev.io' },
    { id: 2, name: 'Bob Kumar',     email: 'bob@ml.ai' },
    { id: 3, name: 'Charlie Ndiaye',email: 'charlie@corp.com' },
  ];

  test('filters by name (case-insensitive)', () => {
    const result = filterResumes(resumes, 'alice');
    expect(result.length).toBe(1);
    expect(result[0].name).toBe('Alice Chen');
  });
  test('filters by email', () => {
    const result = filterResumes(resumes, 'ml.ai');
    expect(result.length).toBe(1);
    expect(result[0].name).toBe('Bob Kumar');
  });
  test('returns all on empty query', () => {
    expect(filterResumes(resumes, '').length).toBe(3);
  });
  test('returns empty on no match', () => {
    expect(filterResumes(resumes, 'zzz').length).toBe(0);
  });
});

describe('filterJobs()', () => {
  const jobs = [
    { id: 1, title: 'Backend Engineer', company: 'NeuralCorp' },
    { id: 2, title: 'ML Researcher',    company: 'DeepHire' },
    { id: 3, title: 'Frontend Dev',     company: 'PixelLab' },
  ];

  test('filters by title', () => {
    const result = filterJobs(jobs, 'ML');
    expect(result.length).toBe(1);
    expect(result[0].title).toBe('ML Researcher');
  });
  test('filters by company', () => {
    const result = filterJobs(jobs, 'pixel');
    expect(result.length).toBe(1);
    expect(result[0].company).toBe('PixelLab');
  });
  test('returns all on empty', () => {
    expect(filterJobs(jobs, '').length).toBe(3);
  });
});

describe('renderDashResumeTable()', () => {
  beforeEach(() => setupDOM());

  test('renders empty state when no resumes', () => {
    renderDashResumeTable([]);
    expect(document.getElementById('dashResumeTable').innerHTML).toContain('No resumes');
  });

  test('renders resume rows', () => {
    renderDashResumeTable([
      { id: 1, name: 'Alice', email: 'a@b.com', skills: ['Java'] },
    ]);
    const rows = document.querySelectorAll('#dashResumeTable tr');
    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('Alice');
  });

  test('limits to 5 rows', () => {
    const data = Array.from({ length: 10 }, (_, i) => ({
      id: i + 1, name: `Person ${i + 1}`, email: `p${i}@x.com`, skills: [],
    }));
    renderDashResumeTable(data);
    expect(document.querySelectorAll('#dashResumeTable tr').length).toBe(5);
  });

  test('escapes XSS in name', () => {
    renderDashResumeTable([{ id: 1, name: '<script>alert(1)</script>', email: '', skills: [] }]);
    expect(document.getElementById('dashResumeTable').innerHTML).not.toContain('<script>');
  });
});

describe('renderMatchHistory()', () => {
  beforeEach(() => setupDOM());

  test('shows empty state for empty history', () => {
    renderMatchHistory([]);
    expect(document.getElementById('matchHistory').innerHTML).toContain('No matches');
  });

  test('renders match cards in reverse order', () => {
    const history = [
      { resumeId: 1, jobId: 2, score: 0.85 },
      { resumeId: 3, jobId: 4, score: 0.72 },
    ];
    renderMatchHistory(history);
    const cards = document.querySelectorAll('.match-card');
    expect(cards.length).toBe(2);
    // most recent last pushed appears first (reversed)
    expect(cards[0].dataset.rid).toBe('3');
  });

  test('formats score as percentage', () => {
    renderMatchHistory([{ resumeId: 1, jobId: 2, score: 0.9345 }]);
    expect(document.querySelector('.match-score').textContent).toBe('93.5%');
  });
});

describe('renderPopularSkills()', () => {
  beforeEach(() => setupDOM());

  test('renders all popular skill tags', () => {
    renderPopularSkills();
    const tags = document.querySelectorAll('.skill-tag');
    expect(tags.length).toBe(POPULAR_SKILLS.length);
  });

  test('each tag has data-skill attribute', () => {
    renderPopularSkills();
    const tags = document.querySelectorAll('.skill-tag');
    tags.forEach((t) => expect(t.dataset.skill).toBeTruthy());
  });

  test('includes Java skill', () => {
    renderPopularSkills();
    const javaTag = Array.from(document.querySelectorAll('.skill-tag'))
      .find((t) => t.textContent === 'Java');
    expect(javaTag).toBeTruthy();
  });
});

describe('POPULAR_SKILLS constant', () => {
  test('has at least 10 skills', () => {
    expect(POPULAR_SKILLS.length).toBeGreaterThanOrEqual(10);
  });
  test('all entries are non-empty strings', () => {
    POPULAR_SKILLS.forEach((s) => {
      expect(typeof s).toBe('string');
      expect(s.length).toBeGreaterThan(0);
    });
  });
  test('no duplicate skills', () => {
    const unique = new Set(POPULAR_SKILLS);
    expect(unique.size).toBe(POPULAR_SKILLS.length);
  });
});
