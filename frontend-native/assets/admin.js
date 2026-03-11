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
  spaceCodePattern,
  spaceStatusLabel,
  statusPill,
  typeLabel,
  payStatusLabel,
  recordStatusLabel
} from './common.js?v=20260311-3';

const session = requireRole('ADMIN');
if (!session) {
  throw new Error('未登录');
}

const messageBox = document.getElementById('messageBox');
const pageTitle = document.getElementById('pageTitle');
const userInfo = document.getElementById('userInfo');
const summaryCards = document.getElementById('summaryCards');
const currentTableBody = document.getElementById('currentTableBody');
const recordsTableBody = document.getElementById('recordsTableBody');
const spacesTableBody = document.getElementById('spacesTableBody');

const sections = {
  dashboard: document.getElementById('dashboardSection'),
  current: document.getElementById('currentSection'),
  records: document.getElementById('recordsSection'),
  spaces: document.getElementById('spacesSection')
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

document.getElementById('refreshCurrentBtn').addEventListener('click', loadCurrentParking);
document.getElementById('refreshRecordsBtn').addEventListener('click', loadRecords);
document.getElementById('refreshSpacesBtn').addEventListener('click', loadSpaces);

document.getElementById('entryForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);
  const plateNumber = normalizePlateNumber(document.getElementById('entryPlateNumber').value);
  const spaceType = document.getElementById('entrySpaceType').value;

  if (!plateNumberPattern.test(plateNumber)) {
    showMessage(messageBox, '请输入正确的车牌号', 'error');
    return;
  }

  try {
    await request('/admin/parking/entry', {
      method: 'POST',
      body: { plateNumber, spaceType }
    });
    showMessage(messageBox, '车辆入场成功，车位状态已同步更新', 'success');
    event.target.reset();
    document.getElementById('entrySpaceType').value = 'NORMAL';
    await Promise.all([loadCurrentParking(), loadRecords(), loadSpaces()]);
  } catch (error) {
    showMessage(messageBox, error.message || '入场失败', 'error');
  }
});

document.getElementById('spaceForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  hideMessage(messageBox);
  const spaceCode = document.getElementById('spaceCode').value.trim().toUpperCase();
  const type = document.getElementById('spaceType').value;
  const floor = document.getElementById('spaceFloor').value.trim().toUpperCase();
  const remark = document.getElementById('spaceRemark').value.trim();

  if (!spaceCodePattern.test(spaceCode)) {
    showMessage(messageBox, '车位编号格式应为 A-021 这类形式', 'error');
    return;
  }

  try {
    await request('/admin/spaces', {
      method: 'POST',
      body: { spaceCode, type, floor, remark }
    });
    showMessage(messageBox, '车位创建成功', 'success');
    event.target.reset();
    document.getElementById('spaceType').value = 'NORMAL';
    document.getElementById('spaceFloor').value = 'B1';
    await loadSpaces();
  } catch (error) {
    showMessage(messageBox, error.message || '车位创建失败', 'error');
  }
});

currentTableBody.addEventListener('click', async (event) => {
  const button = event.target.closest('button[data-record-id]');
  if (!button) {
    return;
  }
  hideMessage(messageBox);
  try {
    await request('/admin/parking/exit', {
      method: 'POST',
      body: { recordId: button.dataset.recordId }
    });
    showMessage(messageBox, '车辆出场成功，车位已释放', 'success');
    await Promise.all([loadCurrentParking(), loadRecords(), loadSpaces()]);
  } catch (error) {
    showMessage(messageBox, error.message || '办理出场失败', 'error');
  }
});

spacesTableBody.addEventListener('click', async (event) => {
  const button = event.target.closest('button[data-space-id]');
  if (!button) {
    return;
  }
  hideMessage(messageBox);
  try {
    await request(`/admin/spaces/${button.dataset.spaceId}`, {
      method: 'DELETE'
    });
    showMessage(messageBox, '车位删除成功', 'success');
    await loadSpaces();
  } catch (error) {
    showMessage(messageBox, error.message || '车位删除失败', 'error');
  }
});

async function loadDashboard() {
  try {
    const data = await request('/admin/dashboard/summary');
    const cards = [
      ['总车位数', data.totalSpaces ?? '-'],
      ['空闲车位', data.freeSpaces ?? '-'],
      ['当前占用', data.occupiedSpaces ?? '-'],
      ['今日收入', data.todayIncome ? `${data.todayIncome} 元` : '-']
    ];
    summaryCards.innerHTML = cards
      .map(([title, value]) => `
        <article class="summary-card">
          <p>${title}</p>
          <strong>${value}</strong>
        </article>
      `)
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '仪表盘加载失败', 'error');
  }
}

async function loadCurrentParking() {
  try {
    const data = await request('/admin/parking/current?pageNum=1&pageSize=50');
    const rows = data.records || [];
    if (!rows.length) {
      currentTableBody.innerHTML = renderEmptyRow(9);
      return;
    }
    currentTableBody.innerHTML = rows
      .map((row) => {
        const [payText, payTone] = payStatusLabel(row.payStatus);
        return `
          <tr>
            <td>${row.recordId}</td>
            <td>${row.plateNumber}</td>
            <td>${row.ownerName || '-'}</td>
            <td>${row.spaceCode || '-'}</td>
            <td>${row.entryTime || '-'}</td>
            <td>${row.durationMinutes ?? 0} 分钟</td>
            <td>${formatAmount(row.finalAmount)}</td>
            <td>${statusPill(payText, payTone)}</td>
            <td><button class="action-link" data-record-id="${row.recordId}">办理出场</button></td>
          </tr>
        `;
      })
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '当前车辆加载失败', 'error');
  }
}

async function loadRecords() {
  try {
    const data = await request('/admin/parking/records?pageNum=1&pageSize=100');
    const rows = data.records || [];
    if (!rows.length) {
      recordsTableBody.innerHTML = renderEmptyRow(9);
      return;
    }
    recordsTableBody.innerHTML = rows
      .map((row) => {
        const [payText, payTone] = payStatusLabel(row.payStatus);
        const [recordText, recordTone] = recordStatusLabel(row.recordStatus);
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
            <td>${statusPill(recordText, recordTone)}</td>
          </tr>
        `;
      })
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '历史记录加载失败', 'error');
  }
}

async function loadSpaces() {
  try {
    const data = await request('/admin/spaces?pageNum=1&pageSize=100');
    const rows = data.records || [];
    if (!rows.length) {
      spacesTableBody.innerHTML = renderEmptyRow(6);
      return;
    }
    spacesTableBody.innerHTML = rows
      .map((row) => {
        const [statusText, statusTone] = spaceStatusLabel(row.status);
        return `
          <tr>
            <td>${row.spaceCode}</td>
            <td>${typeLabel(row.type)}</td>
            <td>${statusPill(statusText, statusTone)}</td>
            <td>${row.floor || '-'}</td>
            <td>${row.remark || '-'}</td>
            <td><button class="action-link danger" data-space-id="${row.spaceId}">删除</button></td>
          </tr>
        `;
      })
      .join('');
  } catch (error) {
    showMessage(messageBox, error.message || '车位列表加载失败', 'error');
  }
}

Promise.all([loadDashboard(), loadCurrentParking(), loadRecords(), loadSpaces()]);
