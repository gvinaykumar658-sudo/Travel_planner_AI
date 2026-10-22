import React from 'react'
import { OWNER_INFO, getWhatsAppLink } from '../data/fleetData'

export default function FloatingContact({ onOpenBookingModal }) {
  const defaultWhatsAppUrl = getWhatsAppLink({
    vehicle: 'Force Urbania / Car Rental',
    pickupLocation: 'Hyderabad',
    destination: 'Outstation / Local'
  })

  return (
    <>
      {/* Floating Action Buttons for Desktop & Tablet */}
      <div className="floating-contact-container">
        <a 
          href={defaultWhatsAppUrl} 
          target="_blank" 
          rel="noreferrer" 
          className="floating-btn whatsapp"
          title={`WhatsApp Mr. ${OWNER_INFO.name}`}
          aria-label="WhatsApp Us"
        >
          <span className="floating-btn-icon">💬</span>
          <span className="floating-btn-text">WhatsApp <b>{OWNER_INFO.whatsapp}</b></span>
        </a>

        <a 
          href={`tel:+91${OWNER_INFO.phone}`} 
          className="floating-btn call"
          title={`Call ${OWNER_INFO.name}`}
          aria-label="Call Us"
        >
          <span className="floating-btn-icon">📞</span>
          <span className="floating-btn-text">Call <b>{OWNER_INFO.phone}</b></span>
        </a>
      </div>

      {/* Sticky Bottom Bar for Mobile Devices */}
      <div className="mobile-sticky-bottom-bar">
        <a href={`tel:+91${OWNER_INFO.phone}`} className="mobile-bar-btn call">
          <span className="bar-icon">📞</span> Call Now
        </a>
        <a href={defaultWhatsAppUrl} target="_blank" rel="noreferrer" className="mobile-bar-btn whatsapp">
          <span className="bar-icon">💬</span> WhatsApp
        </a>
        {onOpenBookingModal && (
          <button onClick={onOpenBookingModal} className="mobile-bar-btn book">
            <span className="bar-icon">⚡</span> Hire Car
          </button>
        )}
      </div>
    </>
  )
}
