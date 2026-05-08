const pages = {};

function registerPage(hash, page) {
  pages[hash] = page;
}

function refreshCurrentView() {
  const hash = location.hash || '#market';
  const page = pages[hash];
  if (page && typeof page.refresh === 'function') page.refresh();
}

function navigate(hash) {
  location.hash = hash;
}

function initRouter() {
  if (window._routerReady) { refreshCurrentView(); return; }
  window._routerReady = true;
  function route() {
    const hash = location.hash || '#market';
    const page = pages[hash];
    const container = document.getElementById('page-container');
    if (!container) return;
    setNavActive(hash);
    if (page && typeof page.render === 'function') {
      page.render(container);
    } else {
      container.innerHTML = `<div class="empty-state">Page introuvable</div>`;
    }
  }
  window.addEventListener('hashchange', route);
  route();
}
