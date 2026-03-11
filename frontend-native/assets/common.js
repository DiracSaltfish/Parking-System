export const API_BASE_URL = 'http://localhost:8080/api';
const STORAGE_KEY = 'parking-native-session';

export const usernamePattern = /^[a-zA-Z0-9_]{4,16}$/;
export const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*._-]{6,20}$/;
export const phonePattern = /^1\d{10}$/;
export const plateNumberPattern = /^[\u4e00-\u9fa5][A-Z][A-Z0-9]{5,6}$/;
export const spaceCodePattern = /^[A-Z]-\d{3}$/;

export function normalizePlateNumber(value) {
  return String(value || '').trim().toUpperCase();
}

export function getSession() {
  const raw = localStorage.getItem(STORAGE_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function saveSession(session) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY);
}

export function requireRole(role) {
  const session = getSession();
  if (!session) {
    window.location.href = './index.html';
    return null;
  }
  if (role && session.role !== role) {
    window.location.href = session.role === 'ADMIN' ? './admin.html' : './user.html';
    return null;
  }
  return session;
}

export async function request(path, options = {}) {
  const { method = 'GET', body, auth = true } = options;
  const headers = {};
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (auth) {
    const session = getSession();
    if (session?.token) {
      headers.Authorization = `Bearer ${session.token}`;
    }
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined
  });

  const payload = await response.json().catch(() => ({
    code: response.status,
    message: '响应解析失败',
    data: null
  }));

  if (!response.ok || payload.code >= 400) {
    throw new Error(payload.message || `请求失败：${response.status}`);
  }

  return payload.data;
}

export function showMessage(container, message, type = 'success') {
  container.hidden = false;
  container.textContent = message;
  container.className = `message-box message-${type}`;
}

export function hideMessage(container) {
  container.hidden = true;
  container.textContent = '';
  container.className = 'message-box';
}

export function logout() {
  clearSession();
  window.location.href = './index.html';
}

export function formatAmount(value) {
  const amount = Number(value || 0);
  return Number.isFinite(amount) ? `${amount.toFixed(2)} 元` : '0.00 元';
}

export function statusPill(text, tone = 'warning') {
  return `<span class="status-pill status-${tone}">${text}</span>`;
}

export function typeLabel(type) {
  const mapping = {
    NORMAL: '普通车位',
    NEW_ENERGY: '新能源车位',
    VIP: 'VIP 车位'
  };
  return mapping[type] || type || '-';
}

export function spaceStatusLabel(status) {
  const mapping = {
    FREE: ['空闲', 'success'],
    OCCUPIED: ['占用', 'danger']
  };
  return mapping[status] || [status || '-', 'warning'];
}

export function payStatusLabel(status) {
  const mapping = {
    PAID: ['已支付', 'success'],
    UNPAID: ['未支付', 'warning'],
    EXEMPTED: ['已豁免', 'danger']
  };
  return mapping[status] || [status || '-', 'warning'];
}

export function recordStatusLabel(status) {
  const mapping = {
    PARKING: ['停车中', 'warning'],
    COMPLETED: ['已完成', 'success']
  };
  return mapping[status] || [status || '-', 'warning'];
}

export function renderEmptyRow(colspan, text = '暂无数据') {
  return `<tr><td class="table-empty" colspan="${colspan}">${text}</td></tr>`;
}
