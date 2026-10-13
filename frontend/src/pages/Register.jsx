import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'

export default function Register({ onLogin }) {
  const [form, setForm] = useState({ name:'', email:'', password:'', confirmPassword:'', role:'CUSTOMER', businessName:'' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const update = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')

    // Client-side validations
    if (!form.name.trim() || !form.email.trim() || !form.password) {
      setError('Please fill in all required fields.')
      return
    }

    if (form.password.length < 6) {
      setError('Password must be at least 6 characters long.')
      return
    }

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match. Please re-enter.')
      return
    }

    if (form.role === 'ADMIN') {
      setError('Administrator accounts cannot be created through public registration. Please sign in or register as Customer/Vendor.')
      return
    }

    if (form.role === 'VENDOR' && !form.businessName.trim()) {
      setError('Business name is required for vendor accounts.')
      return
    }

    setLoading(true)
    try {
      const payload = {
        name: form.name.trim(),
        email: form.email.trim(),
        password: form.password,
        role: form.role,
        businessName: form.role === 'VENDOR' ? form.businessName.trim() : null
      }
      const res = await api.post('/auth/register', payload)
      onLogin(res.data, res.data.token)
      navigate('/')
    } catch (err) {
      let errorMsg = 'Registration failed.'
      if (err.response) {
        if (typeof err.response.data === 'string') {
          errorMsg = err.response.data
        } else if (err.response.data?.message) {
          errorMsg = err.response.data.message
        } else if (err.response.data?.error) {
          errorMsg = err.response.data.error
        }
      } else if (err.request) {
        errorMsg = 'Cannot connect to backend server on port 8090. Please ensure the backend is running.'
      } else {
        errorMsg = err.message || 'Registration failed.'
      }
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="formcard" style={{marginTop:40}} onSubmit={submit}>
      <h3>Create your account</h3>
      <p style={{color:'var(--muted)', fontSize:13, marginBottom:18}}>Sign up as a customer or vendor to get started.</p>
      {error && <div className="msg err">{String(error)}</div>}
      <div className="field">
        <label>Full name *</label>
        <input value={form.name} onChange={update('name')} placeholder="e.g. Rahul Sharma" required />
      </div>
      <div className="field">
        <label>Email address *</label>
        <input type="email" value={form.email} onChange={update('email')} placeholder="e.g. rahul@example.com" required />
      </div>
      <div className="field">
        <label>Password *</label>
        <input type="password" value={form.password} onChange={update('password')} placeholder="Min 6 characters" required />
      </div>
      <div className="field">
        <label>Confirm Password *</label>
        <input type="password" value={form.confirmPassword} onChange={update('confirmPassword')} placeholder="Re-type password" required />
      </div>
      <div className="field">
        <label>Account type</label>
        <select value={form.role} onChange={update('role')}>
          <option value="CUSTOMER">Customer — book vehicles</option>
          <option value="VENDOR">Vendor — list & manage vehicles</option>
        </select>
      </div>
      {form.role === 'VENDOR' && (
        <div className="field">
          <label>Business name *</label>
          <input value={form.businessName} onChange={update('businessName')} placeholder="e.g. Metro Rentals" required />
        </div>
      )}
      <button className="btn" style={{width:'100%'}} disabled={loading}>
        {loading ? 'Creating account...' : 'Create account'}
      </button>
    </form>
  )
}
