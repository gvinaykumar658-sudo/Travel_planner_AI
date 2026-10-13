import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api'

export default function Login({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await api.post('/auth/login', { email, password })
      onLogin(res.data, res.data.token)
      navigate('/')
    } catch (err) {
      let errorMsg = 'Login failed. Check your credentials.'
      if (err.response) {
        if (typeof err.response.data === 'string') {
          errorMsg = err.response.data
        } else if (err.response.data?.message) {
          errorMsg = err.response.data.message
        }
      } else if (err.request) {
        errorMsg = 'Cannot connect to backend server on port 8090. Please ensure the backend is running.'
      } else {
        errorMsg = err.message || 'Login failed.'
      }
      setError(errorMsg)
    }
  }

  return (
    <>
      <div className="hero" style={{border:0, paddingTop:64}}>
        <h1>Rent vehicles from trusted local businesses.</h1>
        <p>TravelMate connects car rental vendors with travelers — search, book, and manage rentals in one place.</p>
      </div>
      <form className="formcard" onSubmit={submit}>
        <h3>Sign in</h3>
        <p style={{color:'var(--muted)', fontSize:13, marginTop:4, marginBottom:18}}>New here? <Link to="/register" style={{color:'var(--amber)'}}>Create an account</Link></p>
        {error && <div className="msg err">{String(error)}</div>}
        <div className="field"><label>Email</label><input type="email" value={email} onChange={e=>setEmail(e.target.value)} required /></div>
        <div className="field"><label>Password</label><input type="password" value={password} onChange={e=>setPassword(e.target.value)} required /></div>
        <button className="btn" style={{width:'100%'}}>Sign in</button>
      </form>
    </>
  )
}
