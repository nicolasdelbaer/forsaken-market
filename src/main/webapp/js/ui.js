let _timerInterval = null;
let _roundSeconds  = 0;

const _RUMORS = [
  'Un crâne de roi vient d\'arriver — les assassins se battent déjà pour le posséder.',
  'On dit que les reliques FORSAKEN changent de mains avant minuit.',
  'Le sceau d\'un ordre disparu a été repéré sur trois items cette semaine.',
  'Méfiez-vous des phiales troubles — leur contenu n\'est jamais ce qu\'il prétend.',
  'Un collectionneur de Varek paierait le double pour une vieille clé.',
  'Les objets corrompus ont été vendus deux fois plus cher ce cycle.',
  'Rumeur : le miroir à retardement montre l\'avenir. Personne ne veut le regarder.',
  'Une alliance de marchands propose un pacte. Méfiance.',
  'Trois receleurs ont disparu après avoir acheté le même item. Coïncidence.',
];

function showToast(message, type = 'info', duration = 3500) {
  const container = document.getElementById('toast-container');
  const el = document.createElement('div');
  el.className = `toast toast-${type}`;
  el.innerHTML = `<div class="toast-title">${type.toUpperCase()}</div>${message}`;
  container.appendChild(el);
  setTimeout(() => {
    el.classList.add('removing');
    el.addEventListener('animationend', () => el.remove(), { once: true });
  }, duration);
}

function showOverlay(html) {
  closeOverlay();
  const backdrop = document.createElement('div');
  backdrop.className = 'overlay-backdrop';
  backdrop.id = 'overlay-backdrop';
  backdrop.innerHTML = `<div class="overlay-card">${html}</div>`;
  backdrop.addEventListener('click', (e) => { if (e.target === backdrop) closeOverlay(); });
  document.body.appendChild(backdrop);
}

function closeOverlay() {
  document.getElementById('overlay-backdrop')?.remove();
}

function _segmentedRing(round, cycleSize, size) {
  const r = (size - 6) / 2;
  const cx = size / 2, cy = size / 2;
  const circ = 2 * Math.PI * r;
  const gapDeg = 6;
  const segLen = circ * ((360 - gapDeg * cycleSize) / 360) / cycleSize;
  const gapLen = circ * gapDeg / 360;
  const cyclePos = round != null ? ((round - 1) % cycleSize) : -1;

  let arcs = '';
  for (let i = 0; i < cycleSize; i++) {
    const filled = i <= cyclePos;
    const offset = -(i * (segLen + gapLen));
    arcs += `<circle cx="${cx}" cy="${cy}" r="${r}" fill="none"
      stroke="${filled ? 'var(--gold)' : 'rgba(232,220,192,0.1)'}"
      stroke-width="3"
      stroke-dasharray="${segLen.toFixed(2)} ${(circ - segLen).toFixed(2)}"
      stroke-dashoffset="${offset.toFixed(2)}"
      ${filled ? 'style="filter:drop-shadow(0 0 3px rgba(201,162,74,0.35))"' : ''}/>`;
  }
  return `<svg width="${size}" height="${size}" style="transform:rotate(-90deg)">${arcs}</svg>
          <div class="center-txt">${round ?? '—'}</div>`;
}

function renderHUD() {
  const hud = document.getElementById('hud');
  if (!hud || !state.player) return;
  hud.removeAttribute('hidden');

  const round = state.currentRound;
  const size = 56;
  const rumorHtml = [..._RUMORS, ..._RUMORS]
    .map(t => `<span><span class="bullet">✦</span>${t}</span>`)
    .join('');

  const lv = state.level || (state.player.level ?? 1);
  const rp = state.reput ?? (state.player.reput ?? 0);
  const maxRp = lv < LEVEL_THRESHOLDS.length ? LEVEL_THRESHOLDS[lv] : LEVEL_THRESHOLDS[LEVEL_THRESHOLDS.length - 1];
  const rpPct = maxRp > 0 ? Math.min(100, (rp / maxRp) * 100).toFixed(1) : 100;
  const repCell = lv < LEVEL_THRESHOLDS.length
    ? `<div class="rep-bar"><span style="width:${rpPct}%"></span></div>
       <div class="sub">${rp} / ${maxRp} pts</div>`
    : `<div class="sub">Niveau maximum ✦</div>`;

  hud.innerHTML = `
    <div class="hud-cell">
      <div class="label">Coffre</div>
      <div class="value">${(state.player.wallet || 0).toLocaleString()}<span style="font-size:13px;color:var(--gold-dim);margin-left:3px">g</span></div>
    </div>
    <div class="hud-cell">
      <div class="label">Niveau ${lv} · ${_esc(state.player.name || state.player.email || '—')}</div>
      <div class="value small">${_esc(state.player.name || state.player.email || '—')}</div>
      ${repCell}
    </div>
    <div class="rumor">
      <div class="rumor-label">Rumeurs</div>
      <div class="rumor-track"><div class="rumor-inner">${rumorHtml}</div></div>
    </div>
    <div class="hud-cell" style="display:grid;grid-template-columns:auto 1fr;gap:14px;align-items:center">
      <div class="cycle-ring">
        ${_segmentedRing(round, ROUNDS_BY_CYCLE, size)}
      </div>
      <div>
        <div class="label">Prochain round</div>
        <div class="value" id="timer-val" style="font-size:22px">—</div>
      </div>
    </div>
  `;

  const playerName = state.player.name || state.player.email || '—';
  const initials = playerName.slice(0, 2).toUpperCase();
  _setText('ac-avatar',    initials);
  _setText('ac-name',      playerName);
  _setText('acp-avatar',   initials);
  _setText('acp-name',     playerName);
  _setText('session-chip', `Round ${round ?? '—'}`);

  updateTimerUI();
}

function initAccountChip() {
  const chip = document.getElementById('account-chip');
  const popover = document.getElementById('account-popover');
  if (!chip || chip.dataset.wired) return;
  chip.dataset.wired = '1';

  chip.addEventListener('click', (e) => {
    e.stopPropagation();
    popover.toggleAttribute('hidden');
  });

  document.getElementById('btn-logout')?.addEventListener('click', logout);

  document.addEventListener('click', (e) => {
    if (!chip.contains(e.target) && !popover?.contains(e.target)) {
      popover?.setAttribute('hidden', '');
    }
  });
}

function startRoundTimer(initialSeconds) {
  clearInterval(_timerInterval);
  _roundSeconds = initialSeconds ?? ROUND_DURATION_SECONDS;
  updateTimerUI();
  _timerInterval = setInterval(() => {
    _roundSeconds = Math.max(0, _roundSeconds - 1);
    updateTimerUI();
  }, 1000);
}

function updateTimerUI() {
  const el = document.getElementById('timer-val');
  if (el) el.textContent = `${_roundSeconds}s`;
}

function setNavActive(hash) {
  document.querySelectorAll('#main-nav a').forEach(a => {
    a.classList.toggle('active', a.getAttribute('href') === hash);
  });
}

function showSpinner(container) {
  container.innerHTML = `<div class="empty-state" style="padding:80px">Chargement...</div>`;
}

/* ===== OHLC chart modal (shared between market and inventory) ===== */
async function showOHLC(blueprintId, title) {
  if (!blueprintId) { showToast('Pas d\'historique pour cet item', 'info'); return; }
  try {
    const candles = await apiFetch(ENDPOINT_MARKET_HISTORY(blueprintId));
    if (!candles || !candles.length) { showToast('Pas d\'historique disponible', 'info'); return; }

    document.getElementById('ohlc-modal')?.remove();
    const modal = document.createElement('div');
    modal.className = 'ohlc-modal';
    modal.id = 'ohlc-modal';
    modal.innerHTML = `
      <div class="ohlc-card">
        <button class="close-btn" onclick="document.getElementById('ohlc-modal').remove()">✕</button>
        <h3>${_esc(title)} — Historique des prix</h3>
        <svg class="ohlc-chart" id="ohlc-svg"></svg>
      </div>`;
    modal.addEventListener('click', (e) => { if (e.target === modal) modal.remove(); });
    document.body.appendChild(modal);
    _drawOHLC(document.getElementById('ohlc-svg'), candles);
  } catch (e) {
    if (e.message !== 'AUTH_EXPIRED') showToast('Erreur historique', 'error');
  }
}

function _drawOHLC(svg, candles) {
  const W = svg.clientWidth || 560, H = 160;
  const pad = { t: 10, b: 20, l: 36, r: 10 };
  const all = candles.flatMap(c => [c.high, c.low]);
  const minV = Math.min(...all), maxV = Math.max(...all);
  const range = maxV - minV || 1;
  const cw = Math.max(4, Math.floor((W - pad.l - pad.r) / candles.length));

  const yScale = v => pad.t + (1 - (v - minV) / range) * (H - pad.t - pad.b);
  const xCenter = i => pad.l + i * cw + cw / 2;

  let paths = '';
  candles.forEach((c, i) => {
    const x = xCenter(i);
    const color = c.close >= c.open ? '#8aa07a' : '#b8786a';
    const yHigh = yScale(c.high), yLow = yScale(c.low);
    const yOpen = yScale(c.open), yClose = yScale(c.close);
    const top = Math.min(yOpen, yClose), bot = Math.max(yOpen, yClose);
    const bodyH = Math.max(1, bot - top);
    paths += `<line x1="${x}" y1="${yHigh}" x2="${x}" y2="${yLow}" stroke="${color}" stroke-width="1" opacity="0.7"/>`;
    paths += `<rect x="${x - cw/2 + 1}" y="${top}" width="${Math.max(1, cw - 2)}" height="${bodyH}" fill="${color}" opacity="0.85"/>`;
  });

  svg.setAttribute('viewBox', `0 0 ${W} ${H}`);
  svg.innerHTML = `
    <text x="${pad.l - 4}" y="${H - pad.b}" fill="#8a7f66" font-size="9" text-anchor="end" font-family="monospace">${minV}</text>
    <text x="${pad.l - 4}" y="${pad.t + 4}" fill="#8a7f66" font-size="9" text-anchor="end" font-family="monospace">${maxV}</text>
    ${paths}`;
}

function _esc(str) {
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function _setText(id, text) {
  const el = document.getElementById(id);
  if (el) el.textContent = text;
}
