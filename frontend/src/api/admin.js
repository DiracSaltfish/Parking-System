import http from './http'

export const getAdminSummary = () => http.get('/admin/dashboard/summary')
export const getCurrentParkingList = (params) => http.get('/admin/parking/current', { params })
export const getParkingRecords = (params) => http.get('/admin/parking/records', { params })
export const createParkingEntry = (payload) => http.post('/admin/parking/entry', payload)
export const createParkingExit = (payload) => http.post('/admin/parking/exit', payload)
export const createExemption = (payload) => http.post('/admin/parking/exempt', payload)
export const getSpaces = (params) => http.get('/admin/spaces', { params })
export const getFeeRule = () => http.get('/admin/fee-rule')
export const updateFeeRule = (payload) => http.put('/admin/fee-rule', payload)
