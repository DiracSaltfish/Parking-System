import http from './http'

export const adminLogin = (payload) => http.post('/auth/admin/login', payload)
export const userRegister = (payload) => http.post('/auth/user/register', payload)
export const userLogin = (payload) => http.post('/auth/user/login', payload)
export const getProfile = () => http.get('/auth/profile')
