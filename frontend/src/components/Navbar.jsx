import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { OWNER_INFO } from '../data/fleetData'
import BackendStatusModal from './BackendStatusModal.jsx'

export default function Navbar({ user, onLogout }) {
  const [isApiModalOpen, setIsApiModalOpen] = useState(false)
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const location = useLocation()

  return (
    <>
      <header className="top">
        <div className="wrap topbar">
          <div className="brand-group">
            <Link to="/" className="brand">
              Travel<span>Mate</span>
              <span className="brand-sub">HYDERABAD FLEET</span>
            </Link>
          </div>

          {/* Desktop Navigation Links */}
          <nav className="desktop-nav">
            <Link to="/" className={location.pathname === '/' ? 'nav-link active' : 'nav-link'}>Home</Link>
            <a href="/#all-vehicles" className="nav-link">All Cars & Fleet</a>
            <a href="/#tour-packages" className="nav-link">Tour Packages</a>
            <a href="/#contact" className="nav-link">Contact</a>
            {user?.role === 'CUSTOMER' && (
              <Link to="/trips" className="nav-link">My Trips</Link>
            )}
            {user?.role === 'VENDOR' && (
              <Link to="/vendor" className="nav-link">Vendor Fleet</Link>
            )}
            {user?.role === 'ADMIN' && (
              <Link to="/admin" className="nav-link">Admin Console</Link>
            )}
          </nav>

          {/* Quick Actions & Auth */}
          <div className="topbar-actions">
            <a href={`tel:+91${OWNER_INFO.phone}`} className="nav-phone-pill" title="Direct Call to Owner">
              📞 <b>{OWNER_INFO.phone}</b>
            </a>

            <a 
              href={`https://wa.me/91${OWNER_INFO.whatsapp}?text=Hello%20Mr.%20Surender%20Reddy,%20I%20want%20to%20enquire%20about%20vehicle%20booking.`} 
              target="_blank" 
              rel="noreferrer" 
              className="nav-whatsapp-pill"
              title="Chat on WhatsApp"
            >
              💬 WhatsApp
            </a>

            <button 
              className="btn-api-status" 
              onClick={() => setIsApiModalOpen(true)}
              title="Configure Backend Connection"
            >
              ⚙️ API
            </button>

            {user ? (
              <div className="userbox">
                <span className="user-badge"><b>{user.name}</b> ({user.role.toLowerCase()})</span>
                <button className="logout" onClick={onLogout}>Log out</button>
              </div>
            ) : (
              <div className="auth-links">
                <Link to="/login" className="btn ghost small">Sign in</Link>
                <Link to="/register" className="btn small">Register</Link>
              </div>
            )}

            {/* Mobile Hamburger toggle */}
            <button 
              className="mobile-hamburger" 
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              aria-label="Toggle Navigation"
            >
              ☰
            </button>
          </div>
        </div>

        {/* Mobile Dropdown Menu */}
        {mobileMenuOpen && (
          <div className="mobile-nav-drawer">
            <Link to="/" onClick={() => setMobileMenuOpen(false)}>🏠 Home</Link>
            <a href="/#all-vehicles" onClick={() => setMobileMenuOpen(false)}>🚗 All Cars & Fleet</a>
            <a href="/#tour-packages" onClick={() => setMobileMenuOpen(false)}>🛕 Tour Packages</a>
            <a href="/#contact" onClick={() => setMobileMenuOpen(false)}>📞 Contact & Location</a>
            {user && user.role === 'CUSTOMER' && (
              <Link to="/trips" onClick={() => setMobileMenuOpen(false)}>📋 My Trips</Link>
            )}
            {user && user.role === 'VENDOR' && (
              <Link to="/vendor" onClick={() => setMobileMenuOpen(false)}>🏢 Vendor Fleet</Link>
            )}
            {user && user.role === 'ADMIN' && (
              <Link to="/admin" onClick={() => setMobileMenuOpen(false)}>🛡️ Admin Console</Link>
            )}
            {!user ? (
              <div className="mobile-auth-row">
                <Link to="/login" className="btn ghost small" onClick={() => setMobileMenuOpen(false)}>Sign in</Link>
                <Link to="/register" className="btn small" onClick={() => setMobileMenuOpen(false)}>Create Account</Link>
              </div>
            ) : (
              <button className="logout" onClick={() => { onLogout(); setMobileMenuOpen(false) }}>Log out</button>
            )}
          </div>
        )}
      </header>

      {/* Backend Status & Config Modal */}
      <BackendStatusModal isOpen={isApiModalOpen} onClose={() => setIsApiModalOpen(false)} />
    </>
  )
}
