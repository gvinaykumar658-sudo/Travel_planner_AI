import React, { useState } from 'react'
import { OWNER_INFO, FLEET_VEHICLES, getWhatsAppLink, getMailtoLink } from '../data/fleetData'
import api from '../api'

export default function BookingForm({ initialVehicle = '', onClose }) {
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    pickupLocation: 'Hyderabad',
    destination: '',
    pickupDate: '',
    pickupTime: '08:00',
    vehicle: initialVehicle || 'Force Urbania 10-Seater (Power Recliner)',
    tripType: 'Outstation Pilgrimage / Tour',
    passengers: '4',
    notes: ''
  })
  const [submitting, setSubmitting] = useState(false)
  const [success, setSuccess] = useState(false)
  const [channelUsed, setChannelUsed] = useState('')

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  // Handle direct WhatsApp Booking
  const handleWhatsAppBooking = (e) => {
    if (e) e.preventDefault()
    if (!formData.phone || formData.phone.trim().length < 10) {
      alert('Please enter a valid 10-digit mobile number so Mr. Surender Reddy can contact you.')
      return
    }

    syncBackendSilently()
    const url = getWhatsAppLink(formData)
    window.open(url, '_blank')
    setChannelUsed('WhatsApp')
    setSuccess(true)
  }

  // Handle direct Call
  const handleCallBooking = () => {
    syncBackendSilently()
    window.location.href = `tel:+91${OWNER_INFO.phone}`
    setChannelUsed('Phone Call')
    setSuccess(true)
  }

  // Handle direct Email Booking
  const handleEmailBooking = () => {
    if (!formData.phone) {
      alert('Please enter your phone number.')
      return
    }
    syncBackendSilently()
    const mailto = getMailtoLink(formData)
    window.location.href = mailto
    setChannelUsed('Email')
    setSuccess(true)
  }

  // Silently sync to backend if reachable
  const syncBackendSilently = async () => {
    try {
      await api.post('/bookings/public', {
        customerName: formData.name,
        customerPhone: formData.phone,
        customerEmail: formData.email,
        vehicleName: formData.vehicle,
        pickupLocation: formData.pickupLocation,
        destination: formData.destination,
        pickupDateTime: `${formData.pickupDate}T${formData.pickupTime}:00`,
        tripType: formData.tripType,
        notes: formData.notes
      })
    } catch (err) {
      // Backend may be waking up or offline on free tier, WhatsApp & Call work 100% reliably
      console.log('Backend sync status:', err.message)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    handleWhatsAppBooking()
  }

  return (
    <div className="booking-card">
      <div className="booking-header">
        <div className="booking-badge">FAST CONFIRMATION IN 15 MINS</div>
        <h3>Hire a Car / Force Urbania in Hyderabad</h3>
        <p>Book with Car Owner <b>{OWNER_INFO.name}</b> directly via WhatsApp, Phone or Email.</p>
        <div className="owner-strip">
          <span>📞 Call/WhatsApp: <a href={`tel:${OWNER_INFO.phone}`}>{OWNER_INFO.phoneFormatted}</a></span>
          <span>✉️ <a href={`mailto:${OWNER_INFO.email}`}>{OWNER_INFO.email}</a></span>
        </div>
      </div>

      {success ? (
        <div className="booking-success-box">
          <div className="success-icon">✓</div>
          <h4>Booking Request Sent via {channelUsed}!</h4>
          <p>
            Thank you <b>{formData.name || 'Valued Customer'}</b>. Your booking details for <b>{formData.vehicle}</b> have been dispatched directly to <b>{OWNER_INFO.name}</b>.
          </p>
          <div className="success-contact-card">
            <div><b>Owner:</b> {OWNER_INFO.name}</div>
            <div><b>Direct Phone / WhatsApp:</b> {OWNER_INFO.phoneFormatted}</div>
            <div><b>Email:</b> {OWNER_INFO.email}</div>
          </div>
          <div style={{display:'flex', gap:10, justifyContent:'center', marginTop:16, flexWrap:'wrap'}}>
            <a 
              href={getWhatsAppLink(formData)} 
              target="_blank" 
              rel="noreferrer" 
              className="btn-whatsapp"
            >
              💬 Open WhatsApp Chat
            </a>
            <a 
              href={`tel:+91${OWNER_INFO.phone}`} 
              className="btn-call"
            >
              📞 Call Owner Now
            </a>
            {onClose && (
              <button className="btn ghost" onClick={onClose}>Close Window</button>
            )}
          </div>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="booking-form-grid">
          <div className="form-group">
            <label>Full Name *</label>
            <input 
              type="text" 
              name="name" 
              value={formData.name} 
              onChange={handleChange} 
              placeholder="e.g. Rahul Sharma" 
              required 
            />
          </div>

          <div className="form-group">
            <label>Phone / WhatsApp Number *</label>
            <input 
              type="tel" 
              name="phone" 
              value={formData.phone} 
              onChange={handleChange} 
              placeholder="e.g. 9848587868" 
              required 
            />
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <input 
              type="email" 
              name="email" 
              value={formData.email} 
              onChange={handleChange} 
              placeholder="e.g. yourname@gmail.com" 
            />
          </div>

          <div className="form-group">
            <label>Select Vehicle (All Cars & Travelers Available) *</label>
            <select name="vehicle" value={formData.vehicle} onChange={handleChange}>
              <optgroup label="Force Urbania Luxury Series">
                <option value="Force Urbania 10-Seater (Power Recliner)">Force Urbania 10-Seater (Power Recliner Luxury)</option>
                <option value="Force Urbania 12-Seater (Modified Executive)">Force Urbania 12-Seater (Executive Modified)</option>
                <option value="Force Urbania 16-Seater (Long Wheelbase)">Force Urbania 16-Seater (Long Wheelbase Group)</option>
              </optgroup>
              <optgroup label="Premium SUVs & MUVs">
                <option value="Toyota Innova Crysta (7+1 Seater)">Toyota Innova Crysta (7+1 Seater)</option>
                <option value="Toyota Fortuner 4x4 (VIP Luxury)">Toyota Fortuner 4x4 (VIP Luxury SUV)</option>
                <option value="Maruti Suzuki Ertiga / XL6 (6+1 Seater)">Maruti Suzuki Ertiga / XL6 (6+1 Seater)</option>
              </optgroup>
              <optgroup label="Executive Sedans">
                <option value="Maruti Swift Dzire (4+1 Seater)">Maruti Swift Dzire (4+1 Seater Sedan)</option>
                <option value="Honda City / Hyundai Verna (Executive)">Honda City / Hyundai Verna (Executive Sedan)</option>
              </optgroup>
              <optgroup label="Tempo Travellers & Mini Buses">
                <option value="12-Seater Luxury Tempo Traveller">12-Seater Luxury Tempo Traveller</option>
                <option value="17-Seater AC Tempo Traveller">17-Seater AC Tempo Traveller</option>
                <option value="26-Seater Force Tempo Traveller">26-Seater Force Tempo Traveller</option>
                <option value="Luxury Mini Bus (18 - 35 Seater)">Luxury Mini Bus (18 to 35 Seater)</option>
              </optgroup>
            </select>
          </div>

          <div className="form-group">
            <label>Trip Category</label>
            <select name="tripType" value={formData.tripType} onChange={handleChange}>
              <option value="Local Sightseeing (8 Hours / 80 Km)">Local Hyderabad (8 Hours / 80 Km)</option>
              <option value="Extended Local (12 Hours / 120 Km)">Extended Local (12 Hours / 120 Km)</option>
              <option value="Outstation Pilgrimage / Tour">Outstation Pilgrimage / Temple Tour</option>
              <option value="Airport Pickup / Drop (RGIA Shamshabad)">RGIA Shamshabad Airport Transfer</option>
              <option value="Wedding / Corporate Event">Wedding / Corporate Event</option>
            </select>
          </div>

          <div className="form-group">
            <label>Pickup Location in Hyderabad *</label>
            <input 
              type="text" 
              name="pickupLocation" 
              value={formData.pickupLocation} 
              onChange={handleChange} 
              placeholder="e.g. Gachibowli, Kukatpally, Secunderabad, Airport" 
              required 
            />
          </div>

          <div className="form-group">
            <label>Where do you want to go? (Destination) *</label>
            <input 
              type="text" 
              name="destination" 
              value={formData.destination} 
              onChange={handleChange} 
              placeholder="e.g. Srisailam, Tirupati, Yadagirigutta, Local Tour" 
              required 
            />
          </div>

          <div className="form-row-two">
            <div className="form-group">
              <label>Pickup Date *</label>
              <input 
                type="date" 
                name="pickupDate" 
                value={formData.pickupDate} 
                onChange={handleChange} 
                required 
              />
            </div>
            <div className="form-group">
              <label>Pickup Time</label>
              <input 
                type="time" 
                name="pickupTime" 
                value={formData.pickupTime} 
                onChange={handleChange} 
              />
            </div>
          </div>

          <div className="form-group">
            <label>Special Notes / Passengers / Flight No.</label>
            <textarea 
              name="notes" 
              value={formData.notes} 
              onChange={handleChange} 
              placeholder="e.g. 8 family members, need infant car seat, morning departure at 6 AM" 
              rows={2} 
            />
          </div>

          <div className="booking-action-buttons">
            <button 
              type="button" 
              className="btn-whatsapp-large" 
              onClick={handleWhatsAppBooking}
            >
              <span className="btn-icon">💬</span> Book Instantly via WhatsApp ({OWNER_INFO.whatsapp})
            </button>

            <div className="booking-sub-actions">
              <button 
                type="button" 
                className="btn-call-outline" 
                onClick={handleCallBooking}
              >
                📞 Direct Call ({OWNER_INFO.phone})
              </button>
              <button 
                type="button" 
                className="btn-email-outline" 
                onClick={handleEmailBooking}
              >
                ✉️ Send via Email
              </button>
            </div>
          </div>
        </form>
      )}
    </div>
  )
}
