function showAuthenticatedUI() {
  document.getElementById('site-header').removeAttribute('hidden');
  document.getElementById('account-chip').removeAttribute('hidden');
  document.getElementById('main-nav').innerHTML = `
    <a href="#market">Marché</a>
    <a href="#inventory">Inventaire</a>
    <a href="#collection">Collection</a>
    <a href="#leaderboard">Hall of Infamy</a>`;
  initAccountChip();
  renderHUD();
}

function showLoginUI() {
  document.getElementById('site-header').setAttribute('hidden', '');
  document.getElementById('hud').setAttribute('hidden', '');
  document.getElementById('account-chip').setAttribute('hidden', '');
  document.getElementById('account-popover').setAttribute('hidden', '');
  const container = document.getElementById('page-container');
  authPage.render(container);
}

async function _loadGameState() {
  const [player, status] = await Promise.all([
    apiFetch(ENDPOINT_ACCOUNT_INFO),
    apiFetch(ENDPOINT_GAME_STATUS),
  ]);
  state.player = player;
  state.level  = player.level ?? 1;
  state.reput  = player.reput ?? 0;
  state.currentRound = status.currentRound;
  startRoundTimer(status.secondsUntilNextRound);
}

async function init() {
  initState();
  if (state.token) {
    try {
      await _loadGameState();
      showAuthenticatedUI();
      openSSE();
      initRouter();
    } catch (e) {
      if (e.message !== 'AUTH_EXPIRED') {
        setToken(null);
        showLoginUI();
      }
    }
  } else {
    showLoginUI();
  }
}

document.addEventListener('DOMContentLoaded', init);
