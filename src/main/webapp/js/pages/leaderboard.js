const leaderboardPage = {
  async render(container) {
    showSpinner(container);
    await this.refresh(container);
  },

  async refresh(container) {
    const c = container || document.getElementById('page-container');
    if (!c) return;
    try {
      const rows = await apiFetch(ENDPOINT_LEADERBOARD);
      c.innerHTML = `
        <div class="panel">
          <div class="panel-head">
            <span class="title">Hall of Infamy</span>
            <span class="meta">Top ${rows.length}</span>
          </div>
          <div class="lb-table">
            <div class="lb-row lb-head">
              <div></div><div></div>
              <div>Marchand</div>
              <div style="text-align:right">Score</div>
              <div style="text-align:right">Coffre</div>
            </div>
            ${rows.map((r, i) => this._rowHTML(r, i + 1)).join('')}
          </div>
        </div>`;
    } catch (e) {
      if (e.message !== 'AUTH_EXPIRED') showToast('Erreur leaderboard : ' + e.message, 'error');
    }
  },

  _rowHTML(row, rank) {
    const isSelf = state.player && state.player.id === row.playerId;
    const medals = { 1: '🥇', 2: '🥈', 3: '🥉' };
    return `
      <div class="lb-row ${isSelf ? 'self' : ''}">
        <div class="lb-rank">${rank}</div>
        <div class="lb-medal">${medals[rank] || ''}</div>
        <div class="lb-name">${row.name}</div>
        <div class="lb-score">${row.score}<span class="unit">pts</span></div>
        <div class="lb-wallet">${row.wallet}<span class="unit">g</span></div>
      </div>`;
  },
};

registerPage('#leaderboard', leaderboardPage);
