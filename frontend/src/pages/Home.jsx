import React, { useState, useEffect } from 'react'
import { OWNER_INFO, FLEET_VEHICLES, TOUR_PACKAGES, SAFETY_FEATURES, TESTIMONIALS, getWhatsAppLink } from '../data/fleetData'
import BookingForm from '../components/BookingForm.jsx'
import api from '../api'

export default function Home({ onOpenAuthModal }) {
  const [selectedCategory, setSelectedCategory] = useState('All')
  const [modalVehicle, setModalVehicle] = useState(null)
  const [isBookingModalOpen, setIsBookingModalOpen] = useState(false)
  const [customVehicles, setCustomVehicles] = useState([])

  // Attempt to load any additional dynamic vehicles from backend
  useEffect(() => {
    const fetchBackendVehicles = async () => {
      try {
        const res = await api.get('/vehicles/public')
        if (Array.isArray(res.data) && res.data.length > 0) {
          setCustomVehicles(res.data)
        }
      } catch (err) {
        // Backend might be sleeping on free tier; default rich fleet is always available!
      }
    }
    fetchBackendVehicles()
  }, [])

  // Filter vehicles
  const filteredVehicles = selectedCategory === 'All' 
    ? FLEET_VEHICLES 
    : FLEET_VEHICLES.filter(v => v.category === selectedCategory || v.categoryTag.includes(selectedCategory))

  const handleBookVehicle = (vehicle) => {
    setModalVehicle(vehicle)
    setIsBookingModalOpen(true)
  }

  const handleBookPackage = (pkg) => {
    const url = getWhatsAppLink({
      vehicle: pkg.recommendedVehicle,
      pickupLocation: 'Hyderabad',
      destination: pkg.title,
      tripType: 'Outstation Tour Package',
      notes: `Interested in ${pkg.title} (${pkg.duration}). Please provide package cost.`
    })
    window.open(url, '_blank')
  }

  return (
    <div className="home-container">
      {/* 1. TOP ANNOUNCEMENT & OWNER STRIP */}
      <section className="top-owner-banner">
        <div className="wrap banner-content">
          <div className="banner-left">
            <span className="live-dot"></span>
            <span>24/7 Car & Force Urbania Rentals in Hyderabad</span>
            <span className="banner-divider">|</span>
            <span>Owner: <b>{OWNER_INFO.name}</b></span>
          </div>
          <div className="banner-right">
            <a href={`tel:+91${OWNER_INFO.phone}`} className="banner-link">
              📞 <b>{OWNER_INFO.phoneFormatted}</b>
            </a>
            <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer" className="banner-whatsapp-badge">
              💬 WhatsApp Us
            </a>
            <a href={`mailto:${OWNER_INFO.email}`} className="banner-link email-hide-mobile">
              ✉️ {OWNER_INFO.email}
            </a>
          </div>
        </div>
      </section>

      {/* 2. HERO SHOWCASE SECTION */}
      <section className="hero-section">
        <div className="wrap hero-grid">
          <div className="hero-content">
            <div className="hero-pill">HYDERABAD'S PREMIER FLEET & FORCE URBANIA SERVICE</div>
            <h1>Travel Together in Luxury, Comfort and Safety</h1>
            <p>
              Looking for a comfortable ride? Whether you need a <b>Force Urbania with Power Recliners (10, 12, 16 Seater)</b>, 
              <b> Toyota Innova Crysta</b>, <b>Fortuner</b>, <b>Swift Dzire</b>, <b>Tempo Traveller</b>, or <b>Luxury Mini Bus</b> — 
              we have all cars ready for outstation pilgrimages, local sightseeing, weddings, and airport transfers.
            </p>

            <div className="hero-cta-group">
              <a href={`tel:+91${OWNER_INFO.phone}`} className="btn-hero-call">
                <span className="btn-icon">📞</span> Call Owner: {OWNER_INFO.phone}
              </a>
              <a 
                href={getWhatsAppLink({ vehicle: 'Force Urbania / Luxury Car', destination: 'Hyderabad / Outstation' })} 
                target="_blank" 
                rel="noreferrer" 
                className="btn-hero-whatsapp"
              >
                <span className="btn-icon">💬</span> WhatsApp Booking
              </a>
              <a href="#booking-form-section" className="btn-hero-secondary">
                ⚡ Instant Quote Form
              </a>
            </div>

            <div className="hero-trust-badges">
              <div className="trust-item">
                <span className="trust-icon">✓</span>
                <span><b>45+</b> Luxury Fleet</span>
              </div>
              <div className="trust-item">
                <span className="trust-icon">✓</span>
                <span><b>Power Recliner</b> Urbania</span>
              </div>
              <div className="trust-item">
                <span className="trust-icon">✓</span>
                <span><b>Verified</b> Chauffeurs</span>
              </div>
              <div className="trust-item">
                <span className="trust-icon">✓</span>
                <span><b>24/7</b> Doorstep Pickup</span>
              </div>
            </div>
          </div>

          <div className="hero-form-wrapper" id="booking-form-section">
            <BookingForm initialVehicle={modalVehicle?.name} />
          </div>
        </div>
      </section>

      {/* 3. ABOUT OWNER & BUSINESS SECTION (Inspired by reference site) */}
      <section className="about-section">
        <div className="wrap about-grid">
          <div className="about-text">
            <div className="section-label">ABOUT TRAVELMATE HYDERABAD</div>
            <h2>Premium Force Urbania & All Cars Rental</h2>
            <div className="styled-divider"></div>
            <p>
              Under the leadership of <b>{OWNER_INFO.name}</b>, TravelMate is a trusted travel service provider in Hyderabad, 
              offering premium Force Urbania rentals, luxury sedans, family SUVs, and tempo travellers for family trips, corporate 
              travel, destination weddings, pilgrimages, tours, and outstation journeys.
            </p>
            <p>
              With spacious vehicles, ultra-comfortable seating, modern onboard entertainment, advanced monocoque safety features, 
              and experienced highway drivers, we ensure every journey from Hyderabad is safe, smooth, and memorable.
            </p>

            <div className="owner-bio-card">
              <div className="owner-avatar">SR</div>
              <div className="owner-details">
                <h4>{OWNER_INFO.name}</h4>
                <div className="owner-title">Fleet Owner & Managing Director</div>
                <div className="owner-contact-row">
                  <span>📱 Phone & WhatsApp: <b>{OWNER_INFO.phoneFormatted}</b></span>
                  <span>✉️ Email: <b>{OWNER_INFO.email}</b></span>
                  <span>📍 Hyderabad, Telangana</span>
                </div>
              </div>
            </div>
          </div>

          <div className="about-features-card">
            <h3>Why Travel With Us?</h3>
            <ul className="about-points">
              <li>
                <b>✨ All Cars & Models Available:</b> From economical Swift Dzire and 7-seater Innova Crysta to 10/12/16 seater Force Urbania and 35-seater Mini Buses.
              </li>
              <li>
                <b>💺 Ultimate Luxury Interiors:</b> Power recliner captain seats, individual AC vents, ambient mood lights, and USB mobile charging on every seat.
              </li>
              <li>
                <b>🛡️ Uncompromised Safety:</b> Monocoque crash-tested body, ABS, EBD, ESP, VDC, airbags, and hill-hold assist on ghat roads.
              </li>
              <li>
                <b>👨‍✈️ Uniformed Experienced Drivers:</b> Police-verified, polite, punctual chauffeurs who know every pilgrimage and tourist route thoroughly.
              </li>
              <li>
                <b>💯 Transparent Billing:</b> No hidden surcharges, no surprise fees. Toll and state permit estimates provided up front.
              </li>
            </ul>
          </div>
        </div>
      </section>

      {/* 4. FORCE URBANIA WHEELBASE VARIANTS (Direct from reference site) */}
      <section className="urbania-variants-section">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">FORCE MOTORS SIGNATURE VAN</div>
            <h2>Force Urbania Variants Available</h2>
            <p className="section-desc">Choose from 3 specialized wheelbase configurations tailored for different group sizes.</p>
          </div>

          <div className="variants-grid">
            <div className="variant-card">
              <div className="variant-badge">10 + 1 Seats</div>
              <div className="variant-img-wrap">
                <img src="https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80" alt="Short Wheelbase Urbania" />
              </div>
              <h3>Short Wheelbase (SWB)</h3>
              <p className="variant-sub">10 Passenger Seats + 1 Driver</p>
              <p className="variant-info">
                Exclusive power recliner captain chairs, high headroom, walk-through aisle, individual dual chillers. Ideal for VIPs and nuclear families.
              </p>
              <button className="btn-variant" onClick={() => handleBookVehicle(FLEET_VEHICLES[0])}>
                Book 10-Seater Urbania
              </button>
            </div>

            <div className="variant-card featured">
              <div className="variant-badge popular">13 + 1 Seats · Most Popular</div>
              <div className="variant-img-wrap">
                <img src="https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=600&q=80" alt="Medium Wheelbase Urbania" />
              </div>
              <h3>Medium Wheelbase (MWB)</h3>
              <p className="variant-sub">13 Passenger Seats + 1 Driver</p>
              <p className="variant-info">
                Modified executive interiors, pushback seats, smart LED screen & entertainment system, generous luggage boot. Top choice for temple yatras.
              </p>
              <button className="btn-variant featured" onClick={() => handleBookVehicle(FLEET_VEHICLES[1])}>
                Book 12/13-Seater Urbania
              </button>
            </div>

            <div className="variant-card">
              <div className="variant-badge">16 + 1 Seats</div>
              <div className="variant-img-wrap">
                <img src="https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80" alt="Long Wheelbase Urbania" />
              </div>
              <h3>Long Wheelbase (LWB)</h3>
              <p className="variant-sub">16 Passenger Seats + 1 Driver</p>
              <p className="variant-info">
                Maximum capacity group luxury. 16 pushback seats with dedicated reading lamps, massive rear luggage hold, and monocoque stability.
              </p>
              <button className="btn-variant" onClick={() => handleBookVehicle(FLEET_VEHICLES[2])}>
                Book 16-Seater Urbania
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 5. ALL CARS & VEHICLES SHOWCASE (Addressing customer's exact requirement: ALL CARS) */}
      <section className="fleet-section" id="all-vehicles">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">OUR COMPLETE FLEET</div>
            <h2>All Cars & Vehicles Available For Rent</h2>
            <p className="section-desc">
              Not only Force Urbania — we have every car you desire! From economical sedans to luxury SUVs, tempo travellers, and buses.
            </p>

            {/* Category Filter Pills */}
            <div className="category-tabs">
              {['All', 'Force Urbania', 'SUVs & MUVs', 'Sedans', 'Tempo Travellers', 'Mini Buses'].map(cat => (
                <button 
                  key={cat} 
                  className={`category-tab ${selectedCategory === cat ? 'active' : ''}`}
                  onClick={() => setSelectedCategory(cat)}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <div className="fleet-grid">
            {filteredVehicles.map((vehicle) => (
              <div key={vehicle.id} className="fleet-card">
                <div className="fleet-card-media">
                  <img src={vehicle.image} alt={vehicle.name} onError={(e) => { e.target.src = vehicle.fallbackImg }} />
                  <span className="fleet-category-tag">{vehicle.categoryTag}</span>
                  <span className="fleet-capacity-badge">👥 {vehicle.capacity}</span>
                </div>

                <div className="fleet-card-body">
                  <div className="fleet-title-row">
                    <h3>{vehicle.name}</h3>
                    <div className="fleet-price">
                      <span className="price-amount">{vehicle.ratePerKm}</span>
                      <span className="price-sub">or ₹{vehicle.pricePerDay.toLocaleString()}/day</span>
                    </div>
                  </div>
                  <p className="fleet-subtitle">{vehicle.subtitle}</p>

                  <div className="fleet-features-tags">
                    {vehicle.features.slice(0, 4).map((f, i) => (
                      <span key={i} className="f-tag">✓ {f}</span>
                    ))}
                  </div>

                  <p className="fleet-desc">{vehicle.description}</p>

                  <div className="fleet-card-actions">
                    <button 
                      className="btn-card-whatsapp" 
                      onClick={() => {
                        const url = getWhatsAppLink({
                          vehicle: vehicle.name,
                          pickupLocation: 'Hyderabad',
                          notes: `I want to book ${vehicle.name}. Please confirm availability.`
                        })
                        window.open(url, '_blank')
                      }}
                    >
                      💬 WhatsApp Book
                    </button>
                    <button 
                      className="btn-card-call" 
                      onClick={() => window.location.href = `tel:+91${OWNER_INFO.phone}`}
                    >
                      📞 Call Owner
                    </button>
                    <button 
                      className="btn-card-enquire" 
                      onClick={() => handleBookVehicle(vehicle)}
                    >
                      ⚡ Quick Book
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 6. POPULAR TOUR PACKAGES FROM HYDERABAD (Direct from reference site) */}
      <section className="packages-section" id="tour-packages">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">POPULAR PILGRIMAGES & TOURS</div>
            <h2>Popular Tour Packages from Hyderabad</h2>
            <p className="section-desc">
              Doorstep pickup across Hyderabad, comfortable travel in Force Urbania or car of your choice, and hassle-free return.
            </p>
          </div>

          <div className="packages-grid">
            {TOUR_PACKAGES.map((pkg) => (
              <div key={pkg.id} className="package-card">
                <div className="package-img-wrap">
                  <img src={pkg.image} alt={pkg.title} />
                  <span className="package-badge">{pkg.badge}</span>
                </div>

                <div className="package-body">
                  <h3>{pkg.title}</h3>
                  <div className="package-temple">📍 {pkg.temple}</div>
                  
                  <div className="package-meta">
                    <div><b>Distance:</b> {pkg.distance}</div>
                    <div><b>Duration:</b> {pkg.duration}</div>
                  </div>

                  <p className="package-desc">{pkg.description}</p>

                  <div className="package-footer">
                    <div className="pkg-price">
                      <span className="starts">Starts from</span>
                      <span className="price-tag">{pkg.startingPrice}</span>
                    </div>
                    <button className="btn-package-book" onClick={() => handleBookPackage(pkg)}>
                      Book on WhatsApp 💬
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 7. HOURLY PACKAGES SECTION */}
      <section className="hourly-section">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">FLEXIBLE RENTALS</div>
            <h2>Hyderabad Local & Hourly Packages</h2>
            <p className="section-desc">
              Punctual pickups for business meetings, city shopping, airport drops, and extended sightseeing.
            </p>
          </div>

          <div className="hourly-grid">
            <div className="hourly-card">
              <div className="hourly-hours">8 Hours</div>
              <div className="hourly-km">80 Kilometers</div>
              <h4>City Sightseeing & Business Package</h4>
              <p>Ideal for visiting Charminar, Golconda, Birla Mandir, or corporate client meetings in Cyberabad.</p>
              <ul className="hourly-list">
                <li>✓ Doorstep pickup & drop anywhere in Hyderabad</li>
                <li>✓ AC running at all times</li>
                <li>✓ Choice of Sedan, Innova, or Force Urbania</li>
                <li>✓ Extra hours & kms at standard transparent rates</li>
              </ul>
              <button 
                className="btn-hourly" 
                onClick={() => {
                  const url = getWhatsAppLink({
                    vehicle: '8 Hours Local Package (Urbania / Car)',
                    tripType: 'Local Sightseeing (8 Hours / 80 Km)',
                    pickupLocation: 'Hyderabad'
                  })
                  window.open(url, '_blank')
                }}
              >
                Enquire 8-Hour Package
              </button>
            </div>

            <div className="hourly-card featured">
              <div className="hourly-badge">RECOMMENDED FOR FULL DAY</div>
              <div className="hourly-hours">12 Hours</div>
              <div className="hourly-km">120 Kilometers</div>
              <h4>Full Day City & Outskirts Package</h4>
              <p>Perfect for Ramoji Film City, Yadagirigutta day tours, or wedding guest transfers.</p>
              <ul className="hourly-list">
                <li>✓ 12 full hours of vehicle and chauffeur access</li>
                <li>✓ Multiple stops across Greater Hyderabad</li>
                <li>✓ Free waiting at temples / tourist spots</li>
                <li>✓ Experienced driver assistance</li>
              </ul>
              <button 
                className="btn-hourly featured" 
                onClick={() => {
                  const url = getWhatsAppLink({
                    vehicle: '12 Hours Full Day Package (Urbania / Car)',
                    tripType: 'Extended Local (12 Hours / 120 Km)',
                    pickupLocation: 'Hyderabad'
                  })
                  window.open(url, '_blank')
                }}
              >
                Enquire 12-Hour Package
              </button>
            </div>

            <div className="hourly-card">
              <div className="hourly-hours">Airport Transfer</div>
              <div className="hourly-km">RGIA Shamshabad</div>
              <h4>24/7 Airport Pick & Drop</h4>
              <p>Guaranteed on-time arrival for midnight and early morning flights at Hyderabad International Airport.</p>
              <ul className="hourly-list">
                <li>✓ Flight tracking for timely terminal pickup</li>
                <li>✓ Clean, sanitized boot for heavy luggage</li>
                <li>✓ Chauffeur holds name placard on request</li>
                <li>✓ Flat rate with all parking and toll guidance</li>
              </ul>
              <button 
                className="btn-hourly" 
                onClick={() => {
                  const url = getWhatsAppLink({
                    vehicle: 'Airport Transfer (Innova Crysta / Urbania / Dzire)',
                    tripType: 'Airport Pickup / Drop (RGIA Shamshabad)',
                    pickupLocation: 'Hyderabad / RGIA'
                  })
                  window.open(url, '_blank')
                }}
              >
                Book Airport Transfer
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 8. HIGH-END SAFETY FEATURES (From reference site) */}
      <section className="safety-section">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">SAFETY & COMFORT FIRST</div>
            <h2>High-End Safety Standards</h2>
            <p className="section-desc">
              Travel with complete peace of mind with advanced automotive safety systems.
            </p>
          </div>

          <div className="safety-grid">
            {SAFETY_FEATURES.map((item) => (
              <div key={item.id} className="safety-card">
                <div className="safety-icon-bubble">🛡️</div>
                <h3>{item.title}</h3>
                <p>{item.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 9. COUNTER / ACHIEVEMENTS */}
      <section className="stats-counter-section">
        <div className="wrap counter-grid">
          <div className="counter-box">
            <div className="counter-num">45+</div>
            <div className="counter-label">Commercial Fleet & Urbania</div>
          </div>
          <div className="counter-box">
            <div className="counter-num">8,500+</div>
            <div className="counter-label">Average Kms Driven Daily</div>
          </div>
          <div className="counter-box">
            <div className="counter-num">36+</div>
            <div className="counter-label">Professional Chauffeurs</div>
          </div>
          <div className="counter-box">
            <div className="counter-num">5,000+</div>
            <div className="counter-label">Happy Customers & Groups</div>
          </div>
        </div>
      </section>

      {/* 10. TESTIMONIALS */}
      <section className="testimonials-section">
        <div className="wrap">
          <div className="section-header text-center">
            <div className="section-label">CUSTOMER EXPERIENCES</div>
            <h2>What Our Customers Say</h2>
            <p className="section-desc">Rated 4.9/5 stars for vehicle cleanliness, driver conduct, and on-time service.</p>
          </div>

          <div className="testimonials-grid">
            {TESTIMONIALS.map((t, i) => (
              <div key={i} className="testimonial-card">
                <div className="stars">★★★★★</div>
                <p className="testimonial-text">"{t.comment}"</p>
                <div className="testimonial-meta">
                  <b>{t.name}</b>
                  <span>{t.location} · {t.trip}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 11. CONTACT US & DIRECT BOOKING INFO (All details for Mr. Mudhunuri Surender Reddy) */}
      <section className="contact-section" id="contact">
        <div className="wrap contact-grid">
          <div className="contact-info-card">
            <div className="section-label">DIRECT CONTACT WITH CAR OWNER</div>
            <h2>Get In Touch With Mr. Surender Reddy</h2>
            <div className="styled-divider"></div>
            <p>
              Call or WhatsApp anytime to get instant rate quotes, verify vehicle availability, or customize an interstate tour itinerary.
            </p>

            <div className="contact-methods">
              <a href={`tel:+91${OWNER_INFO.phone}`} className="contact-row-item">
                <span className="contact-icon">📞</span>
                <div>
                  <div className="contact-type">Phone Number (Call 24/7)</div>
                  <div className="contact-val"><b>{OWNER_INFO.phoneFormatted}</b></div>
                </div>
              </a>

              <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer" className="contact-row-item">
                <span className="contact-icon">💬</span>
                <div>
                  <div className="contact-type">WhatsApp Number (Same as Phone)</div>
                  <div className="contact-val"><b>{OWNER_INFO.phoneFormatted}</b></div>
                </div>
              </a>

              <a href={`mailto:${OWNER_INFO.email}`} className="contact-row-item">
                <span className="contact-icon">✉️</span>
                <div>
                  <div className="contact-type">Email Address</div>
                  <div className="contact-val"><b>{OWNER_INFO.email}</b></div>
                </div>
              </a>

              <div className="contact-row-item">
                <span className="contact-icon">📍</span>
                <div>
                  <div className="contact-type">Operating Hub</div>
                  <div className="contact-val"><b>{OWNER_INFO.address}</b></div>
                </div>
              </div>
            </div>

            <div className="contact-quick-buttons">
              <a href={`tel:+91${OWNER_INFO.phone}`} className="btn-call-solid">
                📞 Call {OWNER_INFO.name}
              </a>
              <a href={`https://wa.me/91${OWNER_INFO.whatsapp}`} target="_blank" rel="noreferrer" className="btn-whatsapp-solid">
                💬 Chat on WhatsApp
              </a>
            </div>
          </div>

          <div className="contact-map-card">
            <h3>Find Us in Hyderabad</h3>
            <div className="map-embed-wrapper">
              <iframe 
                title="Hyderabad Location"
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d121789.70477759885!2d78.36195574999999!3d17.448293!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3bcb90c8a6eb5c33%3A0x90c9d72830c71b16!2sHyderabad%2C%20Telangana!5e0!3m2!1sen!2sin!4v1789048275546!5m2!1sen!2sin" 
                width="100%" 
                height="320" 
                style={{border:0, borderRadius:12}} 
                allowFullScreen="" 
                loading="lazy" 
                referrerPolicy="no-referrer-when-downgrade"
              ></iframe>
            </div>
            <p style={{fontSize:12, color:'var(--muted)', marginTop:8}}>
              Serving all areas in Hyderabad: Hitech City, Gachibowli, Madhapur, Kukatpally, Secunderabad, Banjara Hills, Jubilee Hills, and Shamshabad Airport.
            </p>
          </div>
        </div>
      </section>

      {/* MODAL FOR FAST VEHICLE BOOKING */}
      {isBookingModalOpen && (
        <div className="modal-bg" onClick={() => setIsBookingModalOpen(false)}>
          <div className="modal booking-modal" onClick={e => e.stopPropagation()}>
            <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:12}}>
              <h3 style={{margin:0}}>Book {modalVehicle?.name || 'Vehicle'}</h3>
              <button className="btn ghost small" onClick={() => setIsBookingModalOpen(false)}>✕</button>
            </div>
            <BookingForm 
              initialVehicle={modalVehicle ? `${modalVehicle.name} (${modalVehicle.capacity})` : ''} 
              onClose={() => setIsBookingModalOpen(false)} 
            />
          </div>
        </div>
      )}
    </div>
  )
}
