import React from 'react'

export default function VehicleCard({ vehicle, onBook }) {
  return (
    <div className="card">
      <div className="imgblock">🚗</div>
      <h3>{vehicle.name}</h3>
      <div className="meta">
        <span>{vehicle.type}</span>
        <span>📍 {vehicle.location}</span>
        {vehicle.vendor?.businessName && <span>{vehicle.vendor.businessName}</span>}
      </div>
      <div className="row">
        <div className="price">₹{vehicle.pricePerDay}<small> /day</small></div>
        {onBook && <button className="btn small" onClick={() => onBook(vehicle)}>Book</button>}
      </div>
    </div>
  )
}
