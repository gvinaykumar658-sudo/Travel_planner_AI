import React, { useEffect, useState } from 'react'
import api from '../api'

export default function MyTrips() {
  const [bookings, setBookings] = useState([])

  const load = async () => {
    try {
      const res = await api.get('/bookings/mine')
      setBookings(Array.isArray(res.data) ? res.data : (res.data?.content || []))
    } catch (e) {
      console.error('Failed to load trips', e)
    }
  }
  useEffect(() => { load() }, [])

  const cancel = async (id) => {
    try {
      await api.put(`/bookings/${id}/cancel`)
      load()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data || 'Failed to cancel trip.')
    }
  }

  return (
    <>
      <div className="section-head" style={{marginTop:8}}><h2>My trips</h2></div>
      {!bookings.length ? <div className="empty">No bookings yet — go browse some vehicles.</div> : (
        <div className="tablewrap">
          <table>
            <thead><tr><th>Vehicle</th><th>From</th><th>To</th><th>Status</th><th></th></tr></thead>
            <tbody>
              {bookings.map(b => (
                <tr key={b.id}>
                  <td>{b.vehicle?.name}</td>
                  <td>{b.fromDate}</td>
                  <td>{b.toDate}</td>
                  <td><span className={`tag ${b.status === 'CONFIRMED' ? 'avail' : 'pending'}`}>{b.status}</span></td>
                  <td>{b.status === 'CONFIRMED' && <button className="btn ghost small" onClick={() => cancel(b.id)}>Cancel</button>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
