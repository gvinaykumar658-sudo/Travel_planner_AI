import React, { useState } from 'react'
import { getDefaultApiUrl } from '../api'

export default function BackendStatusModal({ isOpen, onClose }) {
  const currentUrl = getDefaultApiUrl()
  const [customUrl, setCustomUrl] = useState(localStorage.getItem('tm_api_url') || currentUrl)
  const [savedMsg, setSavedMsg] = useState('')

  if (!isOpen) return null

  const handleSave = () => {
    if (customUrl && customUrl.trim()) {
      localStorage.setItem('tm_api_url', customUrl.trim())
      setSavedMsg('API URL saved! Reloading...')
      setTimeout(() => {
        window.location.reload()
      }, 800)
    } else {
      localStorage.removeItem('tm_api_url')
      setSavedMsg('Reset to default URL. Reloading...')
      setTimeout(() => {
        window.location.reload()
      }, 800)
    }
  }

  const handleResetToRender = () => {
    localStorage.setItem('tm_api_url', 'https://travelmate-backend.onrender.com/api')
    setCustomUrl('https://travelmate-backend.onrender.com/api')
    setSavedMsg('Set to Render cloud backend! Reloading...')
    setTimeout(() => {
      window.location.reload()
    }, 800)
  }

  const handleResetToLocal = () => {
    localStorage.setItem('tm_api_url', 'http://localhost:8090/api')
    setCustomUrl('http://localhost:8090/api')
    setSavedMsg('Set to Localhost 8090! Reloading...')
    setTimeout(() => {
      window.location.reload()
    }, 800)
  }

  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal" style={{maxWidth: 520}} onClick={e => e.stopPropagation()}>
        <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:14}}>
          <h3 style={{margin:0}}>⚙️ Backend API Connection</h3>
          <button className="btn ghost small" onClick={onClose}>✕</button>
        </div>

        <p style={{fontSize:13, color:'var(--muted)', marginBottom:16}}>
          Configure the Spring Boot backend server URL. TravelMate connects to this API for user logins, vendor management, and driver dispatching.
        </p>

        {savedMsg && <div className="msg ok">{savedMsg}</div>}

        <div className="field">
          <label>Active API Base URL:</label>
          <input 
            type="text" 
            value={customUrl} 
            onChange={e => setCustomUrl(e.target.value)} 
            placeholder="e.g. https://travelmate-backend.onrender.com/api" 
          />
        </div>

        <div style={{display:'flex', gap:8, marginBottom:16, flexWrap:'wrap'}}>
          <button type="button" className="btn ghost small" onClick={handleResetToRender}>
            ☁️ Use Cloud (Render)
          </button>
          <button type="button" className="btn ghost small" onClick={handleResetToLocal}>
            💻 Use Localhost (8090)
          </button>
        </div>

        <div style={{background:'var(--ink)', padding:12, borderRadius:8, fontSize:12, color:'var(--muted)', marginBottom:16}}>
          💡 <b>Notice on Render Free Tier:</b> Render cloud instances automatically sleep when inactive. Waking them up takes about 30–50 seconds on the first request. Car bookings made via WhatsApp, Phone, and Email to <b>Mr. Mudhunuri Surender Reddy (9848587868)</b> always succeed immediately regardless of server state!
        </div>

        <div style={{display:'flex', gap:10, justifyContent:'flex-end'}}>
          <button className="btn" onClick={handleSave}>Save & Apply</button>
          <button className="btn ghost" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  )
}
