import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { OWNER_INFO } from '../data/fleetData'

export default function Navbar() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const location = useLocation()

  return (
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
        </nav>

        {/* Quick Actions & Direct Contact */}
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

          <a href="/#booking-form-section" className="btn small" style={{display:'inline-flex', alignItems:'center', gap:4}}>
            ⚡ Hire Car
          </a>

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
          <Link to="/" onClick={() => setMobileMenuOpen(false)}>🏠 Home Showroom</Link>
          <a href="/#all-vehicles" onClick={() => setMobileMenuOpen(false)}>🚗 All Cars & Fleet</a>
          <a href="/#tour-packages" onClick={() => setMobileMenuOpen(false)}>🛕 Tour Packages</a>
          <a href="/#booking-form-section" onClick={() => setMobileMenuOpen(false)}>⚡ Instant Booking Form</a>
          <a href="/#contact" onClick={() => setMobileMenuOpen(false)}>📞 Contact & Location</a>
          <div className="mobile-auth-row">
            <a href={`tel:+91${OWNER_INFO.phone}`} className="mobile-bar-btn call" style={{flex:1}}>
              📞 Call {OWNER_INFO.phone}
            </a>
            <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer" className="mobile-bar-btn whatsapp" style={{flex:1}}>
              💬 WhatsApp
            </a>
          </div>
        </div>
      )}
    </header>
  )
}
