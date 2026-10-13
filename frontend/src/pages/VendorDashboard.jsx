import React, { useEffect, useState } from 'react'
import api from '../api'

export default function VendorDashboard() {
  const [tab, setTab] = useState('vehicles')
  const [vehicles, setVehicles] = useState([])
  const [bookings, setBookings] = useState([])
  const [form, setForm] = useState({ name:'', type:'Hatchback', location:'', pricePerDay:'' })
  const [msg, setMsg] = useState('')

  const load = async () => {
    try {
      const [v, b] = await Promise.all([api.get('/vehicles/mine'), api.get('/bookings/vendor')])
      setVehicles(Array.isArray(v.data) ? v.data : (v.data?.content || []))
      setBookings(Array.isArray(b.data) ? b.data : (b.data?.content || []))
    } catch (e) {
      console.error('Failed to load vendor data', e)
    }
  }
  useEffect(() => { load() }, [])

  const earnings = bookings.filter(b => b.status === 'CONFIRMED').reduce((sum, b) => {
    const days = Math.max(1, (new Date(b.toDate) - new Date(b.fromDate)) / 86400000)
    return sum + (b.vehicle?.pricePerDay || 0) * days
  }, 0)

  const addVehicle = async (e) => {
    e.preventDefault()
    setMsg('')
    try {
      await api.post('/vehicles', { ...form, pricePerDay: Number(form.pricePerDay) })
      setMsg('ok:Submitted — awaiting admin approval.')
      setForm({ name:'', type:'Hatchback', location:'', pricePerDay:'' })
      load()
    } catch (err) {
      const errMsg = err.response?.data?.message || err.response?.data?.error || err.response?.data || 'Could not add vehicle.'
      setMsg('err:' + (typeof errMsg === 'string' ? errMsg : JSON.stringify(errMsg)))
    }
  }

  return (
    <>
      <div className="tabs">
        <a className={tab==='vehicles'?'active':''} onClick={()=>setTab('vehicles')}>My vehicles</a>
        <a className={tab==='bookings'?'active':''} onClick={()=>setTab('bookings')}>Bookings</a>
      </div>

      {tab === 'vehicles' ? (
        <>
          <div className="stats">
            <div className="stat"><div className="n">{vehicles.length}</div><div className="l">Vehicles listed</div></div>
            <div className="stat"><div className="n">{bookings.length}</div><div className="l">Total bookings</div></div>
            <div className="stat"><div className="n">₹{earnings.toLocaleString()}</div><div className="l">Est. earnings</div></div>
          </div>
          <div className="section-head"><h2>My vehicles</h2></div>
          <div className="grid" style={{marginBottom:28}}>
            {vehicles.length ? vehicles.map(v => (
              <div className="card" key={v.id}>
                <div className="imgblock">🚘</div>
                <h3>{v.name}</h3>
                <div className="meta"><span>{v.type}</span><span>📍 {v.location}</span>
                  <span className={`tag ${v.status==='APPROVED'?'avail':'pending'}`}>{v.status}</span>
                </div>
                <div className="row"><div className="price">₹{v.pricePerDay}<small> /day</small></div></div>
              </div>
            )) : <div className="empty" style={{gridColumn:'1/-1'}}>You haven't listed a vehicle yet.</div>}
          </div>

          <form className="formcard" onSubmit={addVehicle}>
            <h3>List a new vehicle</h3>
            <p style={{color:'var(--muted)', fontSize:13, marginBottom:16}}>Submitted vehicles need admin approval before going live.</p>
            {msg && <div className={`msg ${msg.startsWith('ok')?'ok':'err'}`}>{msg.slice(msg.indexOf(':')+1)}</div>}
            <div className="field"><label>Vehicle name</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})} required /></div>
            <div className="two">
              <div className="field"><label>Type</label>
                <select value={form.type} onChange={e=>setForm({...form,type:e.target.value})}>
                  <option>Hatchback</option><option>Sedan</option><option>SUV</option>
                </select>
              </div>
              <div className="field"><label>Price / day (₹)</label><input type="number" value={form.pricePerDay} onChange={e=>setForm({...form,pricePerDay:e.target.value})} required /></div>
            </div>
            <div className="field"><label>Location</label><input value={form.location} onChange={e=>setForm({...form,location:e.target.value})} required /></div>
            <button className="btn" style={{width:'100%'}}>Submit for approval</button>
          </form>
        </>
      ) : (
        <>
          <div className="section-head"><h2>Bookings for my vehicles</h2></div>
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
