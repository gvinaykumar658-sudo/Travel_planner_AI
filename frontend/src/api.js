import axios from 'axios'

// Determine the default backend API URL based on environment
export const getDefaultApiUrl = () => {
  // 1. User manual override stored in browser
  const custom = localStorage.getItem('tm_api_url')
  if (custom && custom.trim()) return custom.trim()

  // 2. Vite environment variable (e.g. from Vercel)
  if (import.meta.env.VITE_API_URL) return import.meta.env.VITE_API_URL

  // 3. If running on localhost in browser, default to local Spring Boot port 8090
  if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
    return 'http://localhost:8090/api'
  }

  // 4. Default production cloud backend on Render
  return 'https://travelmate-backend.onrender.com/api'
}

export const BASE_URL = getDefaultApiUrl()

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach JWT token to requests if available
api.interceptors.request.use((config) => {
  // Always ensure baseURL is up to date if user changed it in localStorage
  config.baseURL = getDefaultApiUrl()

  const token = localStorage.getItem('tm_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

// Catch network errors and provide clear, informative errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response && error.request) {
      error.isNetworkError = true
      error.userFriendlyMessage = `Cannot reach backend at ${getDefaultApiUrl()}. If using Render free tier, it may be waking up (takes ~30-50s). You can also book directly via WhatsApp (9848587868) or configure the backend URL in Settings.`
    }
    return Promise.reject(error)
  }
)

export default api
