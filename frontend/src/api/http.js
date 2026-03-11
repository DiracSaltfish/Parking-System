import axios from 'axios'

const apiProtocol = window.location.protocol === 'https:' ? 'https:' : 'http:'
const apiHost = window.location.hostname || 'localhost'

const http = axios.create({
  baseURL: `${apiProtocol}//${apiHost}:8080/api`,
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
