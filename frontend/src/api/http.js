import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

http.interceptors.request.use((config) => {
  const session = JSON.parse(localStorage.getItem('parking-session') || 'null')
  if (session?.token) {
    config.headers.Authorization = `Bearer ${session.token}`
  }
  return config
})

export default http
