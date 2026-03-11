import {
  hideMessage,
  passwordPattern,
  phonePattern,
  request,
  showMessage,
  usernamePattern
} from './common.js?v=20260311-3';

const form = document.getElementById('registerForm');
const messageBox = document.getElementById('messageBox');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);

  const username = form.username.value.trim();
  const password = form.password.value.trim();
  const confirmPassword = form.confirmPassword.value.trim();
  const phone = form.phone.value.trim();
  const realName = form.realName.value.trim();

  if (!usernamePattern.test(username)) {
    showMessage(messageBox, '账号仅支持 4-16 位字母、数字或下划线', 'error');
    return;
  }
  if (!passwordPattern.test(password)) {
    showMessage(messageBox, '密码需为 6-20 位，且至少包含字母和数字', 'error');
    return;
  }
  if (password !== confirmPassword) {
    showMessage(messageBox, '两次输入的密码不一致', 'error');
    return;
  }
  if (!phonePattern.test(phone)) {
    showMessage(messageBox, '请输入正确的 11 位手机号', 'error');
    return;
  }
  if (!realName) {
    showMessage(messageBox, '请输入真实姓名', 'error');
    return;
  }

  try {
    await request('/auth/user/register', {
      method: 'POST',
      auth: false,
      body: { username, password, phone, realName }
    });
    showMessage(messageBox, '注册成功，请返回登录', 'success');
    form.reset();
  } catch (error) {
    showMessage(messageBox, error.message || '注册失败', 'error');
  }
});
