const inventoryPage = {
  async render(container) {
    showSpinner(container);
    await this.refresh(container);
  },

  async refresh(container) {
    const c = container || document.getElementById('page-container');
    if (!c) return;
    try {
      const items = await apiFetch(ENDPOINT_INVENTORY_ITEMS);
      c.innerHTML = `
        <div class="panel">
          <div class="panel-head">
            <span class="title">Inventaire</span>
            <span class="meta">${items.length} item(s)</span>
          </div>
          <div class="inventory-grid">${items.length ? items.map(i => this._slotHTML(i)).join('') : '<div class="empty-state" style="grid-column:1/-1">Votre bourse est vide</div>'}</div>
        </div>`;
      this._bindEvents();
    } catch (e) {
      if (e.message !== 'AUTH_EXPIRED') showToast('Erreur inventaire : ' + e.message, 'error');
    }
  },

  _slotHTML(item) {
    const delta = item.currentPrice - item.boughtPrice;
    const pct = item.boughtPrice ? ((delta / item.boughtPrice) * 100).toFixed(1) : 0;
    const deltaClass = delta >= 0 ? 'positive' : 'negative';
    const sign = delta >= 0 ? '+' : '';

    if(!item.isDecayed){
      return `
      <div class="inv-slot" data-decayed="${item.isDecayed}" data-inv-id="${item.inventoryId}">
        <div class="slot-head">
          <div class="frame sm clickable" title="Voir l'historique des prix"
               onclick="showOHLC(${item.blueprintId}, '${item.title.replace(/'/g,'&#39;')}')">
            <img src="${resolveIcon(item.icon)}" alt="${item.title}" onerror="this.src='/img/items/default.png'">
          </div>
          <div class="meta">
            <div class="slot-name">
              <span>${item.title}</span>
              <span class="chip" data-rarity="${item.rarity}">${item.rarity}</span>
            </div>
            <div class="slot-sub">${item.description}</div>
          </div>
        </div>
        <div class="slot-prices">
          <div><div class="k">Acheté</div><div class="v">${item.boughtPrice}g</div></div>
          <div><div class="k">Actuel</div><div class="v ${deltaClass}">${item.currentPrice}g <span class="pct">${sign}${delta}g (${sign}${pct}%)</span></div></div>
        </div>
        <div class="slot-actions">
          <button class="btn small primary btn-sell" data-id="${item.inventoryId}">Vendre</button>
        </div>
      </div>`;
    }else{
      return `
      <div class="inv-slot" data-decayed="${item.isDecayed}" data-inv-id="${item.inventoryId}">
        <div class="slot-head">
          <div class="frame sm clickable" title="Voir l'historique des prix"
               onclick="showOHLC(${item.blueprintId}, '${item.title.replace(/'/g,'&#39;')}')">
            <img src="${resolveIcon(item.icon)}" alt="${item.title}" onerror="this.src='/img/items/default.png'">
          </div>
          <div class="meta">
            <div class="slot-name">
              <span>${item.title}</span>
              <span class="chip" data-rarity="${item.rarity}">${item.rarity}</span>
              <span class="chip" data-rarity="DECAYED">SOLD</span>
            </div>
            <div class="slot-sub">${item.description}</div>
          </div>
        </div>
        <div class="slot-prices">
          <div><div class="k">Acheté</div><div class="v">${item.boughtPrice}g</div></div>
          <div><div class="k">Vendu</div><div class="v ${deltaClass}">${item.soldPrice}g <span class="pct">${sign}${delta}g (${sign}${pct}%)</span></div></div>
        </div>
        <div class="slot-actions">
          <button class="btn small danger btn-discard" data-id="${item.inventoryId}">C'est noté</button>
        </div>
      </div>`;
    }

  },

  _bindEvents() {
    document.querySelectorAll('.btn-sell').forEach(btn => {
      btn.addEventListener('click', async () => {
        btn.disabled = true;
        const slot = btn.closest('.inv-slot');
        try {
          await apiFetch(ENDPOINT_TRADE_SELL(btn.dataset.id), { method: 'POST' });
          if (slot) { slot.style.transition = 'opacity 200ms'; slot.style.opacity = '0'; }
          state.player = await apiFetch(ENDPOINT_ACCOUNT_INFO);
          state.level = state.player.level ?? state.level;
          state.reput = state.player.reput ?? state.reput;
          renderHUD();
          await this.refresh();
        } catch (e) {
          if (slot) slot.style.opacity = '';
          btn.disabled = false;
          if (e.message !== 'AUTH_EXPIRED') showToast(e.message || 'Erreur vente', 'error');
        }
      });
    });

    document.querySelectorAll('.btn-discard').forEach(btn => {
      btn.addEventListener('click', async () => {
        btn.disabled = true;
        const slot = btn.closest('.inv-slot');
        try {
          await apiFetch(ENDPOINT_INVENTORY_DISCARD(btn.dataset.id), { method: 'POST' });
          if (slot) { slot.style.transition = 'opacity 200ms'; slot.style.opacity = '0'; }
          await this.refresh();
        } catch (e) {
          if (slot) slot.style.opacity = '';
          btn.disabled = false;
          if (e.message !== 'AUTH_EXPIRED') showToast(e.message || 'Erreur', 'error');
        }
      });
    });
  },
};

registerPage('#inventory', inventoryPage);
