import React from 'react'
import { Link, useLocation } from 'react-router-dom'

export default function Navbar({ user, onLogout }) {
  return (
    <header className="top">
      <div className="wrap topbar">
        <Link to="/" className="brand">Travel<span>Mate</span></Link>
        {user ? (
          <div className="userbox">
            {user.role === 'CUSTOMER' && (
              <nav className="tabs" style={{margin:0}}>
                <Link to="/">Browse</Link>
                <Link to="/trips">My trips</Link>
              </nav>
            )}
            <b>{user.name}</b> · {user.role.toLowerCase()}
            <button className="logout" onClick={onLogout}>Log out</button>
          </div>
        ) : (
          <Link to="/register" className="btn small">Create account</Link>
        )}
      </div>
    </header>
  )
}
