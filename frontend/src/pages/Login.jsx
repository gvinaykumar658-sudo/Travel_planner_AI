import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api, { getDefaultApiUrl } from '../api'
import BackendStatusModal from '../components/BackendStatusModal.jsx'
import { OWNER_INFO } from '../data/fleetData'

export default function Login({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [isConfigOpen, setIsConfigOpen] = useState(false)
  const navigate = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const res = await api.post('/auth/login', { email, password })
      onLogin(res.data, res.data.token)
      if (res.data.role === 'VENDOR') navigate('/vendor')
      else if (res.data.role === 'ADMIN') navigate('/admin')
      else navigate('/trips')
    } catch (err) {
      let errorMsg = 'Login failed. Check your credentials.'
      if (err.response) {
        if (typeof err.response.data === 'string') {
          errorMsg = err.response.data
        } else if (err.response.data?.message) {
          errorMsg = err.response.data.message
        }
      } else if (err.isNetworkError || err.request) {
        errorMsg = `Cannot connect to backend server at ${getDefaultApiUrl()}. If using Render cloud hosting, the instance may be waking up from sleep (takes ~30-50s). Please wait a moment and retry, or configure the API URL.`
      } else {
        errorMsg = err.message || 'Login failed.'
      }
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className="hero" style={{border:0, paddingTop:40, textAlign:'center'}}>
        <h1>TravelMate Management Portal</h1>
        <p>Sign in to manage fleet vehicles, view live trip assignments, and track customer bookings.</p>
      </div>

      <div style={{display:'flex', justifyContent:'center', padding:'0 20px 40px'}}>
        <form className="formcard" style={{width:'100%', maxWidth:440}} onSubmit={submit}>
          <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:10}}>
            <h3 style={{margin:0}}>Sign in</h3>
            <button 
              type="button" 
              className="btn ghost small" 
              style={{fontSize:11}}
              onClick={() => setIsConfigOpen(true)}
            >
              ⚙️ Server Config
            </button>
          </div>

          <p style={{color:'var(--muted)', fontSize:13, marginTop:4, marginBottom:18}}>
            New here? <Link to="/register" style={{color:'var(--amber)'}}>Create an account</Link> or <Link to="/" style={{color:'var(--amber)'}}>Return to Showroom</Link>
          </p>

          {error && (
            <div className="msg err">
              <div>{String(error)}</div>
              <div style={{marginTop:8}}>
                <button 
                  type="button" 
                  className="btn ghost small" 
                  style={{fontSize:11, padding:'4px 8px'}}
                  onClick={() => setIsConfigOpen(true)}
                >
                  ⚙️ Change API URL
                </button>
              </div>
            </div>
          )}

          <div className="field">
            <label>Email Address</label>
            <input 
              type="email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              placeholder="e.g. vendor@travelmate.com" 
              required 
            />
          </div>

          <div className="field">
            <label>Password</label>
            <input 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              placeholder="Enter your password" 
              required 
            />
          </div>

          <button className="btn" style={{width:'100%', marginTop:6}} disabled={loading}>
            {loading ? 'Connecting to backend...' : 'Sign in'}
          </button>

          <div style={{marginTop:20, paddingTop:16, borderTop:'1px solid var(--line)', textAlign:'center', fontSize:12, color:'var(--muted)'}}>
            For immediate offline bookings, call owner <b>{OWNER_INFO.name}</b> at <a href={`tel:+91${OWNER_INFO.phone}`} style={{color:'var(--amber)'}}>{OWNER_INFO.phone}</a> or <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer" style={{color:'var(--ok)'}}>WhatsApp</a>.
          </div>
        </form>
      </div>

      <BackendStatusModal isOpen={isConfigOpen} onClose={() => setIsConfigOpen(false)} />
    </>
  )
}
