import axios from 'axios'

// TravelMate Spring Boot backend
export const BASE_URL =
  import.meta.env.VITE_API_URL || 'http://localhost:8090/api'

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('tm_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default api
