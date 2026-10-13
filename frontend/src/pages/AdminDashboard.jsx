import React, { useEffect, useState } from 'react'
import api from '../api'

export default function AdminDashboard() {
  const [tab, setTab] = useState('vehicles')
  const [vehicles, setVehicles] = useState([])
  const [pending, setPending] = useState([])
  const [bookings, setBookings] = useState([])

  const load = async () => {
    try {
      const [all, pend, book] = await Promise.all([
        api.get('/vehicles'), api.get('/vehicles/pending'), api.get('/bookings')
      ])
      setVehicles(Array.isArray(all.data) ? all.data : (all.data?.content || []))
      setPending(Array.isArray(pend.data) ? pend.data : (pend.data?.content || []))
      setBookings(Array.isArray(book.data) ? book.data : (book.data?.content || []))
    } catch (e) {
      console.error('Failed to load admin data', e)
    }
  }
  useEffect(() => { load() }, [])

  const approve = async (id) => {
    try {
      await api.put(`/vehicles/${id}/approve`)
      load()
    } catch (e) {
      alert(e.response?.data?.message || e.response?.data || 'Failed to approve vehicle.')
    }
  }

  return (
    <>
      <div className="tabs">
        <a className={tab==='vehicles'?'active':''} onClick={()=>setTab('vehicles')}>Vehicles & vendors</a>
        <a className={tab==='bookings'?'active':''} onClick={()=>setTab('bookings')}>All bookings</a>
      </div>

      {tab === 'vehicles' ? (
        <>
          <div className="stats">
            <div className="stat"><div className="n">{vehicles.length}</div><div className="l">Total vehicles</div></div>
            <div className="stat"><div className="n">{pending.length}</div><div className="l">Pending approval</div></div>
            <div className="stat"><div className="n">{bookings.length}</div><div className="l">Total bookings</div></div>
          </div>
          <div className="section-head"><h2>Pending vehicle approvals</h2></div>
          <div className="grid" style={{marginBottom:28}}>
            {pending.length ? pending.map(v => (
              <div className="card" key={v.id}>
                <div className="imgblock">🚘</div>
                <h3>{v.name}</h3>
                <div className="meta"><span>{v.type}</span><span>📍 {v.location}</span><span>{v.vendor?.businessName || v.vendor?.name}</span></div>
                <div className="row">
                  <div className="price">₹{v.pricePerDay}<small> /day</small></div>
                  <button className="btn small" onClick={() => approve(v.id)}>Approve</button>
                </div>
              </div>
            )) : <div className="empty" style={{gridColumn:'1/-1'}}>Nothing pending review.</div>}
          </div>
          <div className="section-head"><h2>All vehicles</h2></div>
          <div className="tablewrap"><table>
            <thead><tr><th>Vehicle</th><th>Vendor</th><th>Location</th><th>Price/day</th><th>Status</th></tr></thead>
            <tbody>{vehicles.map(v => (
              <tr key={v.id}><td>{v.name}</td><td>{v.vendor?.businessName || v.vendor?.name}</td><td>{v.location}</td><td>₹{v.pricePerDay}</td>
              <td><span className={`tag ${v.status==='APPROVED'?'avail':'pending'}`}>{v.status}</span></td></tr>
            ))}</tbody>
          </table></div>
        </>
      ) : (
        <>
          <div className="section-head"><h2>All bookings</h2></div>
          {!bookings.length ? <div className="empty">No bookings yet.</div> : (
            <div className="tablewrap"><table>
              <thead><tr><th>Vehicle</th><th>Customer</th><th>From</th><th>To</th><th>Status</th></tr></thead>
              <tbody>{bookings.map(b => (
                <tr key={b.id}><td>{b.vehicle?.name}</td><td>{b.customer?.name}</td><td>{b.fromDate}</td><td>{b.toDate}</td>
                <td><span className={`tag ${b.status==='CONFIRMED'?'avail':'pending'}`}>{b.status}</span></td></tr>
              ))}</tbody>
            </table></div>
          )}
        </>
      )}
    </>
  )
}
