import React, { useState, useEffect } from 'react'
import { Routes, Route, Navigate, Link } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Home from './pages/Home.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Browse from './pages/Browse.jsx'
import MyTrips from './pages/MyTrips.jsx'
import VendorDashboard from './pages/VendorDashboard.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import FloatingContact from './components/FloatingContact.jsx'
import { OWNER_INFO } from './data/fleetData'

export default function App() {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const raw = localStorage.getItem('tm_user')
    if (raw) {
      try {
        setUser(JSON.parse(raw))
      } catch (e) {
        localStorage.removeItem('tm_user')
      }
    }
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
      
      <main className="main-content">
        <Routes>
          {/* Public Home: Feature-rich fleet showcase and booking portal */}
          <Route path="/" element={<Home />} />
          <Route path="/browse" element={<Browse />} />
          
          {/* Authentication */}
          <Route path="/login" element={user ? <Navigate to={user.role === 'VENDOR' ? '/vendor' : user.role === 'ADMIN' ? '/admin' : '/trips'} /> : <Login onLogin={login} />} />
          <Route path="/register" element={user ? <Navigate to="/" /> : <Register onLogin={login} />} />
          
          {/* Protected Customer & Vendor Portals */}
          <Route path="/trips" element={user ? <MyTrips user={user} /> : <Navigate to="/login" />} />
          <Route path="/vendor" element={user?.role === 'VENDOR' ? <VendorDashboard user={user} /> : <Navigate to="/login" />} />
          <Route path="/admin" element={user?.role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/login" />} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>

      {/* Floating Call & WhatsApp Trigger for Desktop and Mobile */}
      <FloatingContact />

      {/* Rich Footer with Owner Contact & Verification */}
      <footer className="site-footer">
        <div className="wrap footer-content">
          <div className="footer-col">
            <div className="footer-brand">Travel<span>Mate</span></div>
            <p className="footer-desc">
              Hyderabad’s premier Force Urbania (10, 12, 16 Seater) and all-car rental service. 
              Family pilgrimages, corporate luxury, weddings, and outstation tours with experienced chauffeurs.
            </p>
          </div>

          <div className="footer-col">
            <h4>Quick Links</h4>
            <ul className="footer-links">
              <li><Link to="/">Home Showroom</Link></li>
              <li><a href="/#all-vehicles">All Cars & Fleet</a></li>
              <li><a href="/#tour-packages">Popular Tour Packages</a></li>
              <li><Link to="/login">Vendor / Admin Sign In</Link></li>
              <li><Link to="/register">Create Account</Link></li>
            </ul>
          </div>

          <div className="footer-col">
            <h4>Popular Yatras</h4>
            <ul className="footer-links">
              <li><a href="/#tour-packages">Hyderabad to Srisailam</a></li>
              <li><a href="/#tour-packages">Hyderabad to Tirupati</a></li>
              <li><a href="/#tour-packages">Hyderabad to Yadagirigutta</a></li>
              <li><a href="/#tour-packages">Hyderabad to Arunachalam</a></li>
              <li><a href="/#tour-packages">Hyderabad to Bhadrachalam</a></li>
            </ul>
          </div>

          <div className="footer-col">
            <h4>Business & Fleet Owner</h4>
            <div className="footer-owner-info">
              <div className="owner-title-badge">Cars Owner / Business Holder:</div>
              <div className="footer-owner-name"><b>{OWNER_INFO.name}</b></div>
              <div>📞 Phone: <a href={`tel:+91${OWNER_INFO.phone}`}>{OWNER_INFO.phoneFormatted}</a></div>
              <div>💬 WhatsApp: <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer">{OWNER_INFO.phoneFormatted}</a></div>
              <div>✉️ Email: <a href={`mailto:${OWNER_INFO.email}`}>{OWNER_INFO.email}</a></div>
              <div>📍 Hub: {OWNER_INFO.address}</div>
            </div>
          </div>
        </div>

        <div className="footer-bottom-bar">
          <div className="wrap footer-bottom-inner">
            <span>© {new Date().getFullYear()} TravelMate — Managed by {OWNER_INFO.name}. All Rights Reserved.</span>
            <span>24/7 Booking Helpline: <a href={`tel:+91${OWNER_INFO.phone}`}>{OWNER_INFO.phoneFormatted}</a></span>
          </div>
        </div>
      </footer>
    </>
  )
}
