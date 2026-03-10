import http from './http'

export const getMyVehicles = () => http.get('/user/vehicles')
export const getCurrentParking = () => http.get('/user/parking/current')
export const getMyParkingRecords = (params) => http.get('/user/parking/records', { params })
export const getMyPayments = (params) => http.get('/user/payments', { params })
export const submitPayment = (payload) => http.post('/user/payments/pay', payload)
