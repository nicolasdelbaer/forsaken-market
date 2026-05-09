const API_BASE = '/api';

const ENDPOINT_AUTH_REGISTER    = `${API_BASE}/auth/register`;
const ENDPOINT_AUTH_LOGIN        = `${API_BASE}/auth/login`;
const ENDPOINT_ACCOUNT_INFO      = `${API_BASE}/account/info`;
const ENDPOINT_MARKET_ITEMS      = `${API_BASE}/market/items`;
const ENDPOINT_MARKET_BLUEPRINTS = `${API_BASE}/market/blueprints`;
const ENDPOINT_MARKET_HISTORY    = (id) => `${API_BASE}/market/history/${id}`;
const ENDPOINT_MARKET_REROLL     = (id) => `${API_BASE}/market/reroll/${id}`;
const ENDPOINT_TRADE_BUY         = (id) => `${API_BASE}/trade/buy/${id}`;
const ENDPOINT_TRADE_SELL        = (id) => `${API_BASE}/trade/sell/${id}`;
const ENDPOINT_INVENTORY_ITEMS   = `${API_BASE}/inventory/items`;
const ENDPOINT_INVENTORY_DISCARD = (id) => `${API_BASE}/inventory/discard/${id}`;
const ENDPOINT_COLLECTION        = `${API_BASE}/collection`;
const ENDPOINT_LEADERBOARD       = `${API_BASE}/hall-of-infamy/top25`;
const ENDPOINT_SSE               = `${API_BASE}/events`;
const ENDPOINT_GAME_STATUS       = `${API_BASE}/game/status`;

const ROUND_DURATION_SECONDS  = 42;
const ROUNDS_BY_CYCLE         = 5;
const LEVEL_THRESHOLDS        = [0, 100, 250, 500, 1000];
const MARKET_VISIBLE_LIMIT    = 8;
const REROLL_COST             = 20;
const MAX_REROLLS_PER_ROUND   = 3;
const ITEMS_IMAGES_BASE       = '/img/items/';

function resolveIcon(iconName) {
  return ITEMS_IMAGES_BASE + (iconName || 'default.png');
}
