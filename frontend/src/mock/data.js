export const adminSummary = [
  { title: '总车位数', value: '120', tone: 'sun' },
  { title: '空闲车位', value: '48', tone: 'mint' },
  { title: '当前占用', value: '72', tone: 'ocean' },
  { title: '今日收入', value: '1260 元', tone: 'ember' }
]

export const currentParkingList = [
  {
    recordId: 'R1001',
    plateNumber: '粤B12345',
    ownerName: '张三',
    spaceCode: 'A-021',
    entryTime: '2026-03-10 17:30:00',
    durationMinutes: 145,
    finalAmount: 15,
    payStatus: '未支付'
  },
  {
    recordId: 'R1002',
    plateNumber: '粤A88888',
    ownerName: '李四',
    spaceCode: 'B-015',
    entryTime: '2026-03-10 18:12:00',
    durationMinutes: 103,
    finalAmount: 10,
    payStatus: '已支付'
  }
]

export const parkingRecords = [
  {
    recordId: 'R0999',
    plateNumber: '粤B12345',
    entryTime: '2026-03-09 09:30:00',
    exitTime: '2026-03-09 12:00:00',
    durationMinutes: 150,
    originalAmount: 15,
    discountAmount: 0,
    finalAmount: 15,
    payStatus: '已支付'
  },
  {
    recordId: 'R0998',
    plateNumber: '粤A88888',
    entryTime: '2026-03-08 13:10:00',
    exitTime: '2026-03-08 16:00:00',
    durationMinutes: 170,
    originalAmount: 20,
    discountAmount: 20,
    finalAmount: 0,
    payStatus: '已豁免'
  }
]

export const spaceList = [
  { spaceId: 'S1021', spaceCode: 'A-021', type: '普通车位', status: '空闲', floor: 'B1' },
  { spaceId: 'S1022', spaceCode: 'A-022', type: '新能源', status: '占用', floor: 'B1' },
  { spaceId: 'S1023', spaceCode: 'A-023', type: 'VIP', status: '维护中', floor: 'B1' }
]

export const exemptions = [
  { recordId: 'R0998', plateNumber: '粤A88888', type: '全额豁免', amount: 20, reason: '内部车辆免单' }
]

export const userVehicles = [
  { vehicleId: 'V1001', plateNumber: '粤B12345', isPrimary: true, bindTime: '2026-03-10 20:00:00' }
]

export const userCurrentParking = {
  recordId: 'R1001',
  plateNumber: '粤B12345',
  spaceCode: 'A-021',
  entryTime: '2026-03-10 17:30:00',
  durationMinutes: 145,
  originalAmount: 15,
  discountAmount: 0,
  finalAmount: 15,
  payStatus: '未支付'
}

export const userPayments = [
  { paymentId: 'P1001', recordId: 'R0999', payMethod: '微信', payAmount: 15, payStatus: '已支付', payTime: '2026-03-09 12:00:00' }
]
