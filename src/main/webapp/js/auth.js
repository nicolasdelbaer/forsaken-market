async function login(email, password) {
  const data = await apiFetch(ENDPOINT_AUTH_LOGIN, {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  setToken(data.token);
}

async function register(email, password, userName) {
  const data = await apiFetch(ENDPOINT_AUTH_REGISTER, {
    method: 'POST',
    body: JSON.stringify({ email, password, userName }),
  });
  setToken(data.token);
}

function logout() {
  setToken(null);
  location.reload();
}
