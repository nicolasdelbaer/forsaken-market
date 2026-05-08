const collectionPage = {
  _items: [],
  _filter: 'ALL',

  async render(container) {
    showSpinner(container);
    await this.refresh(container);
  },

  async refresh(container) {
    const c = container || document.getElementById('page-container');
    if (!c) return;
    try {
      this._items = await apiFetch(ENDPOINT_COLLECTION);
      this._renderPage(c);
    } catch (e) {
      if (e.message !== 'AUTH_EXPIRED') showToast('Erreur collection : ' + e.message, 'error');
    }
  },

  _renderPage(c) {
    const discovered = this._items.filter(i => i.isCollected).length;
    const rarities = ['ALL', 'MUNDANE', 'TAINTED', 'CURSED', 'FORSAKEN'];
    c.innerHTML = `
      <div class="panel">
        <div class="panel-head">
          <span class="title">Collection</span>
          <span class="meta"><span id="coll-count">${discovered}</span> / ${this._items.length} découverts</span>
        </div>
        <div class="filter-bar">
          <span class="filter-label">Rareté</span>
          ${rarities.map(r => `<button class="filter-btn ${this._filter === r ? 'active' : ''}" data-filter="${r}">${r === 'ALL' ? 'Tous' : r}</button>`).join('')}
        </div>
        <div class="collection-grid" id="coll-grid"></div>
      </div>`;
    this._renderGrid();
    document.querySelectorAll('.filter-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        this._filter = btn.dataset.filter;
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.toggle('active', b.dataset.filter === this._filter));
        this._renderGrid();
      });
    });
  },

  _renderGrid() {
    const grid = document.getElementById('coll-grid');
    if (!grid) return;
    const filtered = this._filter === 'ALL'
      ? this._items
      : this._items.filter(i => i.rarity === this._filter);

    grid.innerHTML = filtered.map(item => {
      const hidden = !item.isCollected;
      return `
        <div class="coll-item ${hidden ? 'hidden' : ''}" data-rarity="${item.rarity}">
          <div class="frame sm">
            <img src="${hidden ? '/img/items/default.png' : resolveIcon(item.icon)}" alt="${hidden ? '???' : item.title}" onerror="this.src='/img/items/default.png'">
          </div>
          <span class="chip" data-rarity="${item.rarity}">${item.rarity}</span>
          <div class="coll-name">${hidden ? '???' : item.title}</div>
          ${!hidden ? `<div class="coll-desc">${item.description}</div>` : ''}
        </div>`;
    }).join('');
  },
};

registerPage('#collection', collectionPage);
