import React, { useEffect, useState } from 'react'
import api from '../api'
import VehicleCard from '../components/VehicleCard.jsx'

export default function Browse() {
  const [vehicles, setVehicles] = useState([])
  const [location, setLocation] = useState('')
  const [type, setType] = useState('')
  const [modalVehicle, setModalVehicle] = useState(null)
  const [dates, setDates] = useState({ from:'', to:'' })
  const [msg, setMsg] = useState('')

  const load = async () => {
    try {
      const res = await api.get('/vehicles/public', { params: { location, type } })
      setVehicles(Array.isArray(res.data) ? res.data : (res.data?.content || []))
    } catch (e) {
      console.error('Failed to load vehicles', e)
    }
  }

  useEffect(() => { load() }, [])

  const book = async () => {
    setMsg('')
    if (!dates.from || !dates.to) { setMsg('Pick both dates.'); return }
    try {
      await api.post('/bookings', { vehicleId: modalVehicle.id, fromDate: dates.from, toDate: dates.to })
      setModalVehicle(null)
      setDates({ from:'', to:'' })
      alert('Booking confirmed! Check "My trips".')
    } catch (err) {
      const errMsg = err.response?.data?.message || err.response?.data?.error || err.response?.data || err.message || 'Could not book this vehicle.'
      setMsg(typeof errMsg === 'string' ? errMsg : JSON.stringify(errMsg))
    }
  }

  return (
    <>
      <div className="hero" style={{paddingTop:32}}>
        <h1>Find your next ride.</h1>
        <p>Search vehicles from rental businesses near you.</p>
        <div className="searchbar">
          <div><label>Location</label><input value={location} onChange={e=>setLocation(e.target.value)} placeholder="e.g. Hyderabad" /></div>
          <div><label>Vehicle type</label><input value={type} onChange={e=>setType(e.target.value)} placeholder="e.g. SUV" /></div>
          <button onClick={load}>Search</button>
        </div>
      </div>
      <div className="section-head" style={{marginTop:28}}>
        <h2>Available vehicles</h2><span style={{color:'var(--muted)', fontSize:13}}>{vehicles.length} found</span>
      </div>
      <div className="grid">
        {vehicles.length ? vehicles.map(v => (
          <VehicleCard key={v.id} vehicle={v} onBook={setModalVehicle} />
        )) : <div className="empty" style={{gridColumn:'1/-1'}}>No vehicles found. Try clearing filters.</div>}
      </div>

      {modalVehicle && (
        <div className="modal-bg" onClick={() => setModalVehicle(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Book {modalVehicle.name}</h3>
            <p style={{color:'var(--muted)', fontSize:13, marginBottom:14}}>₹{modalVehicle.pricePerDay}/day · {modalVehicle.location}</p>
            {msg && <div className="msg err">{String(msg)}</div>}
            <div className="two">
              <div className="field"><label>From</label><input type="date" value={dates.from} onChange={e=>setDates({...dates, from:e.target.value})} /></div>
              <div className="field"><label>To</label><input type="date" value={dates.to} onChange={e=>setDates({...dates, to:e.target.value})} /></div>
            </div>
            <div style={{display:'flex', gap:10}}>
              <button className="btn" style={{flex:1}} onClick={book}>Confirm booking</button>
              <button className="btn ghost" onClick={() => setModalVehicle(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
