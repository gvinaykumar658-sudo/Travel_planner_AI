import React, { useState, useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Browse from './pages/Browse.jsx'
import MyTrips from './pages/MyTrips.jsx'
import VendorDashboard from './pages/VendorDashboard.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'

export default function App() {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const raw = localStorage.getItem('tm_user')
    if (raw) setUser(JSON.parse(raw))
  }, [])

  const login = (u, token) => {
    localStorage.setItem('tm_user', JSON.stringify(u))
    localStorage.setItem('tm_token', token)
    setUser(u)
  }
  const logout = () => {
    localStorage.removeItem('tm_user')
    localStorage.removeItem('tm_token')
    setUser(null)
  }

  return (
    <>
      <Navbar user={user} onLogout={logout} />
      <main className="wrap">
        <Routes>
          <Route path="/" element={user ? <RoleHome user={user} /> : <Login onLogin={login} />} />
          <Route path="/register" element={<Register onLogin={login} />} />
          <Route path="/trips" element={user ? <MyTrips user={user} /> : <Navigate to="/" />} />
          <Route path="/vendor" element={user?.role === 'VENDOR' ? <VendorDashboard user={user} /> : <Navigate to="/" />} />
          <Route path="/admin" element={user?.role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/" />} />
        </Routes>
      </main>
      <footer>TravelMate — Vehicle Rental Management System</footer>
    </>
  )
}

function RoleHome({ user }) {
  if (user.role === 'VENDOR') return <VendorDashboard user={user} />
  if (user.role === 'ADMIN') return <AdminDashboard />
  return <Browse />
}
