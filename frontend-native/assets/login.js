import {
  hideMessage,
  passwordPattern,
  request,
  saveSession,
  showMessage,
  usernamePattern
} from './common.js';

const form = document.getElementById('loginForm');
const messageBox = document.getElementById('messageBox');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);

  const username = form.username.value.trim();
  const password = form.password.value.trim();

  if (!usernamePattern.test(username)) {
    showMessage(messageBox, '账号仅支持 4-16 位字母、数字或下划线', 'error');
    return;
  }
  if (!passwordPattern.test(password)) {
    showMessage(messageBox, '密码需为 6-20 位，且至少包含字母和数字', 'error');
    return;
  }

  try {
    const path = username === 'admin' ? '/auth/admin/login' : '/auth/user/login';
    const data = await request(path, {
      method: 'POST',
      auth: false,
      body: { username, password }
    });
    saveSession(data);
    showMessage(messageBox, '登录成功，正在跳转...', 'success');
    window.location.href = data.role === 'ADMIN' ? './admin.html' : './user.html';
  } catch (error) {
    showMessage(messageBox, error.message || '登录失败', 'error');
  }
});
