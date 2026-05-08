const state = {
  token:        null,
  player:       null,
  currentRound: null,
  rerollsLeft:  MAX_REROLLS_PER_ROUND,
  level:        1,
  reput:        0,
};

function initState() {
  state.token = localStorage.getItem('auth_token');
}

function setToken(token) {
  state.token = token;
  if (token) {
    localStorage.setItem('auth_token', token);
    document.cookie = `auth_token=${token}; path=/; SameSite=Lax`;
  } else {
    localStorage.removeItem('auth_token');
    document.cookie = 'auth_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
  }
}
