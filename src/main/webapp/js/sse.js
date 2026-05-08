let _eventSource = null;
let _retryCount  = 0;
const MAX_RETRIES = 3;

function openSSE() {
  if (_eventSource) _eventSource.close();
  _eventSource = new EventSource(ENDPOINT_SSE);

  _eventSource.addEventListener('NewRound',         onNewRound);
  _eventSource.addEventListener('Payday',           onPayday);
  _eventSource.addEventListener('EndOfDay',         onEndOfDay);
  _eventSource.addEventListener('ItemBought',       onItemBought);
  _eventSource.addEventListener('ItemSold',         onItemSold);
  _eventSource.addEventListener('ItemExpiration',   onItemExpiration);
  _eventSource.addEventListener('LevelUp',          onLevelUp);
  _eventSource.addEventListener('NewCollectedItem', onNewCollectedItem);
  _eventSource.onerror = handleSSEError;

  _eventSource.onopen = () => { _retryCount = 0; };
}

function closeSSE() {
  if (_eventSource) { _eventSource.close(); _eventSource = null; }
}

function handleSSEError() {
  closeSSE();
  if (!state.token) return;
  if (_retryCount < MAX_RETRIES) {
    _retryCount++;
    setTimeout(openSSE, 5000);
  } else {
    showToast('Connexion temps réel perdue. Rechargez la page.', 'error', 8000);
  }
}

function onNewRound(e) {
  const data = JSON.parse(e.data);
  state.currentRound = data.roundCounter;
  state.rerollsLeft = MAX_REROLLS_PER_ROUND;
  startRoundTimer();
  renderHUD();
  refreshCurrentView();
}

function onPayday(e) {
  const data = JSON.parse(e.data);
  showToast(`⚔ Payday +${data.value}g`, 'success');
  apiFetch(ENDPOINT_ACCOUNT_INFO).then(p => { state.player = p; renderHUD(); }).catch(() => {});
}

function onEndOfDay() {
  showToast('☽ La Nuit tombe sur l\'Emporium...', 'warning', 5000);
}

function onItemBought(e) {
  const data = JSON.parse(e.data);
  const diff = data.priceDifference;
  const sign = diff > 0 ? '+' : '';
  showToast(`Acquisition confirmée — ${data.currentPrice}g (${sign}${diff}g)`, 'success');
}

function onItemSold(e) {
  const data = JSON.parse(e.data);
  const diff = data.priceDifference;
  const sign = diff >= 0 ? '+' : '';
  const type = diff >= 0 ? 'success' : 'warning';
  showToast(`Vente — ${data.currentPrice}g (${sign}${diff}g)`, type);
  apiFetch(ENDPOINT_ACCOUNT_INFO).then(p => { state.player = p; renderHUD(); }).catch(() => {});
}

function onItemExpiration(e) {
  const data = JSON.parse(e.data);
  const count = data.soldInventoryItemForPrice ? data.soldInventoryItemForPrice.length : 0;
  showToast(`☠ ${count} item(s) corrompu(s) — vendu(s) automatiquement`, 'warning', 5000);
  apiFetch(ENDPOINT_ACCOUNT_INFO).then(p => { state.player = p; renderHUD(); }).catch(() => {});
  refreshCurrentView();
}

function onLevelUp(e) {
  const data = JSON.parse(e.data);
  state.level = data.newLevel;
  state.reput = data.toReput;
  renderHUD();
  if (data.newLevel > data.initialLevel) {
    showOverlay(`
      <h2>⚜ Niveau ${data.newLevel}</h2>
      <p>Votre réputation grandit dans les ombres de l'Emporium.</p>
      <p style="font-family:var(--f-mono);color:var(--gold)">${data.fromReput} → ${data.toReput} pts</p>
      <button class="btn primary" onclick="closeOverlay()">Continuer</button>
    `);
  }
}

function onNewCollectedItem(e) {
  const data = JSON.parse(e.data);
  showToast(`✦ Découverte : ${data.title}`, 'success', 5000);
}
