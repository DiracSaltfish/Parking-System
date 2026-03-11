import {
  formatAmount,
  hideMessage,
  logout,
  normalizePlateNumber,
  plateNumberPattern,
  renderEmptyRow,
  request,
  requireRole,
  showMessage,
  statusPill,
  payStatusLabel
} from './common.js';

const session = requireRole('USER');
if (!session) {
  throw new Error('未登录');
}

const messageBox = document.getElementById('messageBox');
const pageTitle = document.getElementById('pageTitle');
const userInfo = document.getElementById('userInfo');
const homeSummary = document.getElementById('homeSummary');
const vehiclesTableBody = document.getElementById('vehiclesTableBody');
const currentCard = document.getElementById('currentCard');
const recordsTableBody = document.getElementById('recordsTableBody');
const paymentsTableBody = document.getElementById('paymentsTableBody');

const sections = {
  home: document.getElementById('homeSection'),
  vehicles: document.getElementById('vehiclesSection'),
  current: document.getElementById('currentSection'),
  records: document.getElementById('recordsSection'),
  payments: document.getElementById('paymentsSection')
};

userInfo.textContent = `${session.displayName} / ${session.username}`;

document.getElementById('logoutBtn').addEventListener('click', logout);
document.querySelectorAll('.nav-btn').forEach((button) => {
  button.addEventListener('click', () => {
    document.querySelectorAll('.nav-btn').forEach((item) => item.classList.remove('is-active'));
    button.classList.add('is-active');
    const section = button.dataset.section;
    Object.entries(sections).forEach(([key, element]) => {
      element.hidden = key !== section;
    });
    pageTitle.textContent = button.textContent;
  });
});

document.getElementById('refreshVehiclesBtn').addEventListener('click', loadVehicles);
document.getElementById('refreshCurrentBtn').addEventListener('click', loadCurrentParking);
document.getElementById('refreshRecordsBtn').addEventListener('click', loadRecords);
document.getElementById('refreshPaymentsBtn').addEventListener('click', loadPayments);

document.getElementById('vehicleForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);
  const plateNumber = normalizePlateNumber(document.getElementById('vehiclePlateNumber').value);
  const isPrimary = document.getElementById('vehiclePrimary').checked;

  if (!plateNumberPattern.test(plateNumber)) {
    showMessage(messageBox, '请输入正确的车牌号', 'error');
    return;
  }

  try {
    await request('/user/vehicles', {
      method: 'POST',
      body: { plateNumber, isPrimary }
    });
    showMessage(messageBox, '车辆绑定成功', 'success');
    event.target.reset();
    document.getElementById('vehiclePrimary').checked = true;
    await Promise.all([loadVehicles(), loadHomeSummary()]);
  } catch (error) {
    showMessage(messageBox, error.message || '车辆绑定失败', 'error');
  }
});

document.getElementById('paymentForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);
  const recordId = document.getElementById('paymentRecordId').value.trim();
  const payMethod = document.getElementById('paymentMethod').value;

  if (!recordId) {
    showMessage(messageBox, '请输入停车记录编号', 'error');
    return;
  }

  try {
    await request('/user/payments/pay', {
      method: 'POST',
      body: { recordId, payMethod }
    });
    showMessage(messageBox, '模拟缴费成功', 'success');
    event.target.reset();
    await Promise.all([loadCurrentParking(), loadRecords(), loadPayments(), loadHomeSummary()]);
  } catch (error) {
    showMessage(messageBox, error.message || '缴费失败', 'error');
  }
});

vehiclesTableBody.addEventListener('click', async (event) => {
  const button = event.target.closest('button[data-vehicle-id]');
  if (!button) {
    return;
  }
  hideMessage(messageBox);
  try {
    await request(`/user/vehicles/${button.dataset.vehicleId}`, {
      method: 'DELETE'
    });
    showMessage(messageBox, '车辆删除成功', 'success');
    await Promise.all([loadVehicles(), loadHomeSummary()]);
  } catch (error) {
    showMessage(messageBox, error.message || '车辆删除失败', 'error');
  }
});

async function loadHomeSummary() {
  try {
    const [vehicles, current] = await Promise.all([
      request('/user/vehicles'),
      request('/user/parking/current')
    ]);
    const cards = [
      ['绑定车辆数', vehicles.length],
      ['当前车位', current.active ? current.spaceCode : '无'],
      ['当前费用', current.active ? formatAmount(current.finalAmount) : '0.00 元'],
      ['停车状态', current.active ? '在场' : '未停车']
    ];
    homeSummary.innerHTML = cards
      .map(([title, value]) => `
        <article class="summary-card">
          <p>${title}</p>
          <strong>${value}</strong>
        </article>
      `)
      .join('');
    if (current.active && current.recordId) {
      document.getElementById('paymentRecordId').value = current.recordId;
    }
  } catch (error) {
    showMessage(messageBox, error.message || '首页信息加载失败', 'error');
  }
}

async function loadVehicles() {
  try {
    const rows = await request('/user/vehicles');
    if (!rows.length) {
      vehiclesTableBody.innerHTML = renderEmptyRow(5);
      return;
    }
    vehiclesTableBody.innerHTML = rows
      .map((row) => `
        <tr>
          <td>${row.vehicleId}</td>
          <td>${row.plateNumber}</td>
          <td>${row.isPrimary ? '是' : '否'}</td>
          <td>${row.bindTime || '-'}</td>
          <td><button class="action-link danger" data-vehicle-id="${row.vehicleId}">删除</button></td>
        </tr>
      `)
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '车辆列表加载失败', 'error');
  }
}

async function loadCurrentParking() {
  try {
    const current = await request('/user/parking/current');
    if (!current.active) {
      currentCard.innerHTML = '<p class="muted">当前没有在场车辆。</p>';
      return;
    }
    const [payText, payTone] = payStatusLabel(current.payStatus);
    currentCard.innerHTML = `
      <div><strong>记录编号：</strong>${current.recordId}</div>
      <div><strong>车牌号：</strong>${current.plateNumber}</div>
      <div><strong>当前车位：</strong>${current.spaceCode}</div>
      <div><strong>入场时间：</strong>${current.entryTime}</div>
      <div><strong>停车时长：</strong>${current.durationMinutes} 分钟</div>
      <div><strong>当前费用：</strong>${formatAmount(current.finalAmount)}</div>
      <div><strong>支付状态：</strong>${statusPill(payText, payTone)}</div>
    `;
    document.getElementById('paymentRecordId').value = current.recordId || '';
  } catch (error) {
    showMessage(messageBox, error.message || '当前停车信息加载失败', 'error');
  }
}

async function loadRecords() {
  try {
    const data = await request('/user/parking/records?pageNum=1&pageSize=100');
    const rows = data.records || [];
    if (!rows.length) {
      recordsTableBody.innerHTML = renderEmptyRow(8);
      return;
    }
    recordsTableBody.innerHTML = rows
      .map((row) => {
        const [payText, payTone] = payStatusLabel(row.payStatus);
        return `
          <tr>
            <td>${row.recordId}</td>
            <td>${row.plateNumber}</td>
            <td>${row.spaceCode || '-'}</td>
            <td>${row.entryTime || '-'}</td>
            <td>${row.exitTime || '-'}</td>
            <td>${row.durationMinutes ?? 0} 分钟</td>
            <td>${formatAmount(row.finalAmount)}</td>
            <td>${statusPill(payText, payTone)}</td>
          </tr>
        `;
      })
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '停车记录加载失败', 'error');
  }
}

async function loadPayments() {
  try {
    const data = await request('/user/payments?pageNum=1&pageSize=100');
    const rows = data.records || [];
    if (!rows.length) {
      paymentsTableBody.innerHTML = renderEmptyRow(6);
      return;
    }
    paymentsTableBody.innerHTML = rows
      .map((row) => {
        const [payText, payTone] = payStatusLabel(row.payStatus);
        return `
          <tr>
            <td>${row.paymentId}</td>
            <td>${row.recordId}</td>
            <td>${row.payMethod}</td>
            <td>${formatAmount(row.payAmount)}</td>
            <td>${statusPill(payText, payTone)}</td>
            <td>${row.payTime || '-'}</td>
          </tr>
        `;
      })
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '支付记录加载失败', 'error');
  }
}

Promise.all([loadHomeSummary(), loadVehicles(), loadCurrentParking(), loadRecords(), loadPayments()]);
