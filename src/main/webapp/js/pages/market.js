const marketPage = {
  _items: [],

  async render(container) {
    showSpinner(container);
    await this.refresh(container);
  },

  async refresh(container) {
    const c = container || document.getElementById('page-container');
    if (!c) return;
    try {
      this._items = await apiFetch(ENDPOINT_MARKET_ITEMS);
      c.innerHTML = `
        <div class="panel">
          <div class="panel-head">
            <span class="title">Marché</span>
            <span class="meta">${this._items.length} items — Rerolls: <span id="rerolls-count">${state.rerollsLeft}</span>/${MAX_REROLLS_PER_ROUND}</span>
          </div>
          <div id="market-list"></div>
        </div>`;
      this._renderList();
    } catch (e) {
      if (e.message !== 'AUTH_EXPIRED') showToast('Erreur marché : ' + e.message, 'error');
    }
  },

  _renderList() {
    const list = document.getElementById('market-list');
    if (!list) return;
    if (!this._items.length) {
      list.innerHTML = `<div class="empty-state">Le marché est vide</div>`;
      return;
    }
    list.innerHTML = this._items.map(item => this._rowHTML(item)).join('');
    this._bindEvents();
  },

  _rowHTML(item) {
    const canAfford = state.player && state.player.wallet >= item.currentPrice;
    const canReroll = state.rerollsLeft > 0;
    return `
      <div class="market-row" data-rarity="${item.rarity}" data-market-id="${item.marketId}">
        <div class="frame clickable" title="Voir l'historique des prix"
             onclick="showOHLC(${item.blueprintId}, '${item.title.replace(/'/g,'&#39;')}')">
          <img src="${resolveIcon(item.icon)}" alt="${item.title}" onerror="this.src='/img/items/default.png'">
        </div>
        <div><span class="chip" data-rarity="${item.rarity}">${item.rarity}</span></div>
        <div class="name-block">
          <div class="name">${item.title}</div>
          <div class="flavor">${item.description}</div>
        </div>
        <div class="prices">
          <div class="price-cell">
            <div class="k">Prix</div>
            <div class="v">${item.currentPrice}<span class="unit">g</span></div>
          </div>
        </div>
        <div class="row-actions">
          <button class="btn small primary btn-buy" data-id="${item.marketId}" ${canAfford ? '' : 'disabled'}>Acheter</button>
          <button class="btn small ghost btn-reroll" data-id="${item.marketId}" ${canReroll ? '' : 'disabled'}>Reroll (${REROLL_COST}g)</button>
        </div>
      </div>`;
  },

  _bindEvents() {
    document.querySelectorAll('.btn-buy').forEach(btn => {
      btn.addEventListener('click', async () => {
        btn.disabled = true;
        const row = btn.closest('.market-row');
        try {
          await apiFetch(ENDPOINT_TRADE_BUY(btn.dataset.id), { method: 'POST' });
          if (row) { row.style.transition = 'opacity 200ms'; row.style.opacity = '0'; }
          state.player = await apiFetch(ENDPOINT_ACCOUNT_INFO);
          state.level = state.player.level ?? state.level;
          state.reput = state.player.reput ?? state.reput;
          renderHUD();
          await this.refresh();
        } catch (e) {
          if (row) row.style.opacity = '';
          btn.disabled = false;
          if (e.message !== 'AUTH_EXPIRED') showToast(e.message || 'Erreur achat', 'error');
        }
      });
    });

    document.querySelectorAll('.btn-reroll').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (state.rerollsLeft <= 0) return;
        btn.disabled = true;
        const row = btn.closest('.market-row');
        try {
          await apiFetch(ENDPOINT_MARKET_REROLL(btn.dataset.id), { method: 'POST' });
          if (row) { row.style.transition = 'opacity 200ms'; row.style.opacity = '0'; }
          state.rerollsLeft = Math.max(0, state.rerollsLeft - 1);
          await this.refresh();
        } catch (e) {
          if (row) row.style.opacity = '';
          btn.disabled = false;
          if (e.message !== 'AUTH_EXPIRED') showToast(e.message || 'Erreur reroll', 'error');
        }
      });
    });
  },
};

registerPage('#market', marketPage);
