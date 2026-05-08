async function apiFetch(url, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(state.token ? { 'Authorization': `Bearer ${state.token}` } : {}),
    ...options.headers,
  };
  const res = await fetch(url, { ...options, headers });
  if (res.status === 401 || res.status === 403) {
    handleTokenExpired();
    throw new Error('AUTH_EXPIRED');
  }
  if (!res.ok) {
    const text = await res.text().catch(() => res.statusText);
    throw new Error(text || res.statusText);
  }
  if (res.status === 204 || res.headers.get('content-length') === '0') return null;
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) return res.json();
  return null;
}

function handleTokenExpired() {
  setToken(null);
  closeSSE();
  state.player = null;
  showLoginUI();
  showToast('Session expirée. Reconnectez-vous.', 'error');
}
