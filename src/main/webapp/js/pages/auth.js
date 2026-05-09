const authPage = {
  render(container) {
    container.innerHTML = `
      <div id="auth-page">
        <div class="login-card">
          <div class="eyebrow">Dungeon Registry</div>
          <h2>The Emporium</h2>
          <p class="sub">All items are cursed. No refunds.</p>
          <div class="login-tabs">
            <button id="tab-login" class="active" onclick="authPage._showTab('login')">Connexion</button>
            <button id="tab-register" onclick="authPage._showTab('register')">Inscription</button>
          </div>
          <div id="auth-form-container"></div>
          <div class="login-error" id="auth-error" style="display:none"></div>
        </div>
      </div>`;
    this._showTab('login');
  },

  _showTab(tab) {
    document.getElementById('tab-login')   .classList.toggle('active', tab === 'login');
    document.getElementById('tab-register').classList.toggle('active', tab === 'register');
    const fc = document.getElementById('auth-form-container');
    if (tab === 'login') {
      fc.innerHTML = `
        <form class="login-form" id="login-form">
          <label>Email <input type="email" name="email" required autocomplete="email" /></label>
          <label>Mot de passe <input type="password" name="password" required autocomplete="current-password" /></label>
          <button type="submit" class="btn primary primary-action">Entrer</button>
        </form>`;
      document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData(e.target);
        await authPage._attempt(() => login(fd.get('email'), fd.get('password')));
      });
    } else {
      fc.innerHTML = `
        <form class="login-form" id="register-form">
          <label>Nom du marchand <input type="text" name="userName" required /></label>
          <label>Email <input type="email" name="email" required autocomplete="email" /></label>
          <label>Mot de passe <input type="password" name="password" required autocomplete="new-password" /></label>
          <button type="submit" class="btn primary primary-action">Créer un compte</button>
        </form>`;
      document.getElementById('register-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData(e.target);
        await authPage._attempt(() => register(fd.get('email'), fd.get('password'), fd.get('userName')));
      });
    }
  },

  async _attempt(fn) {
    const errEl = document.getElementById('auth-error');
    errEl.style.display = 'none';
    try {
      await fn();
      await _loadGameState();
      showAuthenticatedUI();
      openSSE();
      initRouter();
      navigate('#market');
    } catch (e) {
      if (e.message === 'AUTH_EXPIRED') return;
      errEl.textContent = e.message || 'Erreur de connexion';
      errEl.style.display = 'block';
    }
  },
};
