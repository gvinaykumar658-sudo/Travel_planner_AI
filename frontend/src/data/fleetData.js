// Owner and Contact Information for TravelMate
export const OWNER_INFO = {
  name: 'Mr. Mudhunuri Surender Reddy',
  phone: '9848587868',
  phoneFormatted: '+91 9848587868',
  whatsapp: '9848587868',
  email: 'surenderreddym49@gmail.com',
  address: 'Kukatpally / Hitech City Road, Hyderabad, Telangana 500072',
  operatingCities: 'Hyderabad, Secunderabad, Cyberabad & All India Outstation',
  workingHours: '24/7 Available for Bookings & Enquiries'
}

// Generate pre-filled WhatsApp link
export const getWhatsAppLink = (bookingDetails = {}) => {
  const {
    name = '',
    phone = '',
    vehicle = 'Force Urbania / Luxury Car',
    pickupLocation = 'Hyderabad',
    destination = '',
    pickupDate = '',
    pickupTime = '',
    tripType = 'Outstation / Local',
    notes = ''
  } = bookingDetails

  let message = `*Hello Mr. Mudhunuri Surender Reddy,*\n`
  message += `I want to book a vehicle with *TravelMate*:\n`
  message += `━━━━━━━━━━━━━━━━━━━━\n`
  if (name) message += `👤 *Name:* ${name}\n`
  if (phone) message += `📞 *Phone:* ${phone}\n`
  message += `🚗 *Vehicle:* ${vehicle}\n`
  if (tripType) message += `📍 *Trip Type:* ${tripType}\n`
  if (pickupLocation) message += `🛫 *Pickup:* ${pickupLocation}\n`
  if (destination) message += `🎯 *Destination:* ${destination}\n`
  if (pickupDate) message += `📅 *Date:* ${pickupDate} ${pickupTime ? `at ${pickupTime}` : ''}\n`
  if (notes) message += `📝 *Notes:* ${notes}\n`
  message += `━━━━━━━━━━━━━━━━━━━━\n`
  message += `Please provide vehicle availability and rate quotation. Thank you!`

  return `https://wa.me/91${OWNER_INFO.whatsapp}?text=${encodeURIComponent(message)}`
}

// Generate pre-filled Mailto link
export const getMailtoLink = (bookingDetails = {}) => {
  const {
    name = 'Customer',
    phone = '',
    vehicle = 'Vehicle Booking',
    pickupLocation = 'Hyderabad',
    destination = '',
    pickupDate = '',
    tripType = 'Rental',
    notes = ''
  } = bookingDetails

  const subject = `TravelMate Booking Enquiry - ${vehicle} - ${name}`
  let body = `Dear Mr. Mudhunuri Surender Reddy,\n\n`
  body += `I would like to enquire about renting a vehicle with TravelMate.\n\n`
  body += `Booking Details:\n`
  body += `- Customer Name: ${name}\n`
  body += `- Phone Number: ${phone}\n`
  body += `- Preferred Vehicle: ${vehicle}\n`
  body += `- Trip Type: ${tripType}\n`
  body += `- Pickup Location: ${pickupLocation}\n`
  body += `- Destination: ${destination}\n`
  body += `- Pickup Date: ${pickupDate}\n`
  if (notes) body += `- Special Requests / Notes: ${notes}\n`
  body += `\nPlease let me know availability and pricing at your earliest convenience.\n\n`
  body += `Regards,\n${name}\n${phone}`

  return `mailto:${OWNER_INFO.email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
}

// Complete Fleet Categories (All cars and group travelers available)
export const FLEET_VEHICLES = [
  // FORCE URBANIA
  {
    id: 'urbania-10',
    name: 'Force Urbania 10-Seater',
    subtitle: 'Ultra Luxury Power Recliner Captain Seats',
    category: 'Force Urbania',
    categoryTag: 'Luxury Van',
    capacity: '10 + 1 Seats',
    seats: 10,
    pricePerDay: 5500,
    ratePerKm: '₹32/km',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    features: ['Power Recliner Captain Seats', 'Dual Chiller AC', 'Ambient LED Mood Lights', 'Individual USB Chargers', 'High Roof & Walk-in Cabin', 'Huge Luggage Boot'],
    description: 'Short wheelbase luxury Force Urbania with exclusive power recliners. Perfect for VIP movements, executive corporate delegations, and intimate family luxury travel.'
  },
  {
    id: 'urbania-12',
    name: 'Force Urbania 12-Seater',
    subtitle: 'Executive Modified Cabin with Smart TV & Audio',
    category: 'Force Urbania',
    categoryTag: 'Luxury Van',
    capacity: '12 + 1 Seats',
    seats: 12,
    pricePerDay: 6500,
    ratePerKm: '₹34/km',
    image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    features: ['Pushback Recliner Seats', 'Smart LED TV & Sound System', 'Panoramic Tinted Windows', 'Individual AC Vents', 'Noise-Insulated Cabin', 'Spacious Legroom'],
    description: 'Medium wheelbase modified Force Urbania. Equipped with custom luxury pushback seats, entertainment screen, and supreme comfort for long-distance pilgrimage trips.'
  },
  {
    id: 'urbania-16',
    name: 'Force Urbania 16-Seater',
    subtitle: 'Long Wheelbase Group Luxury Cruiser',
    category: 'Force Urbania',
    categoryTag: 'Luxury Van',
    capacity: '16 + 1 Seats',
    seats: 16,
    pricePerDay: 7500,
    ratePerKm: '₹38/km',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    features: ['16 Individual Luxury Seats', 'Monocoque Safety Chassis', 'Dual AC with Micro-filtration', 'Massive Rear Boot', 'ABS + EBD + ESP', 'Hill Hold Assist'],
    description: 'Long wheelbase maximum capacity Urbania. Offers business-class comfort for up to 16 passengers, making it the finest group vehicle for weddings and tours.'
  },

  // SUVS & CARS
  {
    id: 'innova-crysta',
    name: 'Toyota Innova Crysta',
    subtitle: 'King of Indian Highways & Executive Luxury',
    category: 'SUVs & MUVs',
    categoryTag: 'Premium SUV',
    capacity: '7 + 1 Seats',
    seats: 7,
    pricePerDay: 3800,
    ratePerKm: '₹22/km',
    image: 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80',
    features: ['Plush Captain Seats', 'Automatic Climate Control', 'Ultra-Quiet Ride', 'Ample Luggage Capacity', 'Superior Safety Airbags', 'GPS Navigation'],
    description: 'The indisputable gold standard for outstation travel, family holidays, and airport transfers. Smooth ride and exceptional reliability.'
  },
  {
    id: 'toyota-fortuner',
    name: 'Toyota Fortuner 4x4',
    subtitle: 'VIP Luxury SUV with Dominant Road Presence',
    category: 'SUVs & MUVs',
    categoryTag: 'VIP Luxury SUV',
    capacity: '6 + 1 Seats',
    seats: 6,
    pricePerDay: 8000,
    ratePerKm: '₹45/km',
    image: 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&w=800&q=80',
    features: ['4x4 Off-Road Capability', 'Premium Leather Upholstery', 'VIP Escort Status', 'Cooled Glove Box & Chiller', 'Touchscreen Infotainment', '7 Airbags'],
    description: 'High-profile luxury SUV suited for VIP arrivals, celebrity convoys, destination weddings, and rugged outstation adventures.'
  },
  {
    id: 'maruti-ertiga',
    name: 'Maruti Suzuki Ertiga / XL6',
    subtitle: 'Economical 7-Seater Family Comfort',
    category: 'SUVs & MUVs',
    categoryTag: 'Economy MUV',
    capacity: '6 + 1 Seats',
    seats: 6,
    pricePerDay: 2600,
    ratePerKm: '₹16/km',
    image: 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80',
    features: ['Flexible 3-Row Seating', 'Rear Roof-Mounted AC', 'Fuel-Efficient Hybrid', 'ISOFIX Child Mounts', 'Smooth City Drive', 'Bluetooth Audio'],
    description: 'Affordable, spacious, and extremely comfortable for family temple visits, Hyderabad airport drop-offs, and weekend leisure trips.'
  },

  // SEDANS
  {
    id: 'swift-dzire',
    name: 'Maruti Swift Dzire',
    subtitle: 'Reliable, Chilled AC Compact Sedan',
    category: 'Sedans',
    categoryTag: 'Sedan',
    capacity: '4 + 1 Seats',
    seats: 4,
    pricePerDay: 2200,
    ratePerKm: '₹14/km',
    image: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=800&q=80',
    features: ['Chilled Air Conditioning', 'Clean Pristine Cabin', 'Spacious 378L Boot', 'Power Windows', 'Smooth Highway Comfort', 'Punctual Pickup'],
    description: 'Best-in-value sedan for couples, business travelers, daily city runs, and quick outstation point-to-point journeys.'
  },
  {
    id: 'honda-city',
    name: 'Honda City / Hyundai Verna',
    subtitle: 'Premium Executive Class Sedan',
    category: 'Sedans',
    categoryTag: 'Executive Sedan',
    capacity: '4 + 1 Seats',
    seats: 4,
    pricePerDay: 3200,
    ratePerKm: '₹18/km',
    image: 'https://images.unsplash.com/photo-1550355291-bbee04a92027?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80',
    features: ['Leather Seating', 'Sunroof & Ambient Lights', 'Superior Legroom', 'Silent Cabin Insulation', 'Apple CarPlay / Android Auto', 'Cruise Control'],
    description: 'Elegant executive sedan ideal for corporate delegates, VIP airport transfers, business summit travel, and premium couples tours.'
  },

  // TEMPO TRAVELLERS
  {
    id: 'tempo-12',
    name: '12-Seater Luxury Tempo Traveller',
    subtitle: '1x1 Maharaja Recliner Seats & LCD Entertainment',
    category: 'Tempo Travellers',
    categoryTag: 'Group Traveller',
    capacity: '12 + 1 Seats',
    seats: 12,
    pricePerDay: 4500,
    ratePerKm: '₹26/km',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    features: ['1x1 Maharaja Recliners', 'LCD TV & Music System', 'Chilled Front & Rear AC', 'Dedicated Luggage Boot', 'Curtains & Ambient Lights', 'Smooth Air Brakes'],
    description: 'Luxury Maharaja style tempo traveller designed for spiritual Yatras to Srisailam, Tirupati, Shirdi, and outstation holiday groups.'
  },
  {
    id: 'tempo-17',
    name: '17-Seater AC Tempo Traveller',
    subtitle: 'Spacious Family & Pilgrimage Group Specialist',
    category: 'Tempo Travellers',
    categoryTag: 'Group Traveller',
    capacity: '17 + 1 Seats',
    seats: 17,
    pricePerDay: 5200,
    ratePerKm: '₹28/km',
    image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    features: ['2x1 Pushback Seats', 'Individual AC Vents', 'Overhead Luggage Storage', 'Reading Lights & Chargers', 'High Headroom', 'Clean Carpet Flooring'],
    description: 'Popular choice for large extended families, corporate picnics, wedding party transit, and weekend outstation trips.'
  },
  {
    id: 'tempo-26',
    name: '26-Seater Force Tempo Traveller',
    subtitle: 'Heavy-Duty Big Group Passenger Carrier',
    category: 'Tempo Travellers',
    categoryTag: 'Large Traveller',
    capacity: '26 + 1 Seats',
    seats: 26,
    pricePerDay: 7000,
    ratePerKm: '₹35/km',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    features: ['High Capacity 26 Seats', 'Air Suspension on Rear', 'Powerful Roof Blower AC', 'Massive Boot Space', 'Emergency Safety Exits', 'Experienced Highway Driver'],
    description: 'Ideal bridge between a tempo traveller and a bus. Perfect for wedding baraats, corporate events, and religious pilgrimage tours.'
  },

  // MINI BUSES
  {
    id: 'minibus-35',
    name: 'Luxury Mini Bus (18 - 35 Seater)',
    subtitle: 'Air Conditioned Coach for Weddings & Events',
    category: 'Mini Buses',
    categoryTag: 'Luxury Coach',
    capacity: '18 to 35 Seats',
    seats: 35,
    pricePerDay: 9000,
    ratePerKm: '₹42/km',
    image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80',
    fallbackImg: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    features: ['Comfort Pushback Seats', 'Full Central AC', 'Microphone & PA System', 'Large Under-Floor Luggage', 'Curtains & Reading Lamps', 'All India Tourist Permit'],
    description: 'Comprehensive transport solution for wedding guests, school & college industrial tours, corporate conferences, and multi-day interstate yatras.'
  }
]

// Popular Tour Packages from Hyderabad (Inspired by reference site)
export const TOUR_PACKAGES = [
  {
    id: 'srisailam',
    title: 'Hyderabad to Srisailam Tour',
    badge: 'Popular Pilgrimage',
    temple: 'Mallikarjuna Swamy Jyotirlinga & Bhramaramba Devi',
    distance: '215 km (4.5 hrs each way)',
    duration: '1 Day / 2 Days',
    recommendedVehicle: 'Force Urbania / Innova Crysta / Tempo Traveller',
    startingPrice: '₹6,500',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    description: 'Travel from Hyderabad through picturesque Nallamala forest ghats to holy Srisailam. Enjoy spacious seating, ample legroom, and smooth ghat navigation.'
  },
  {
    id: 'tirupati',
    title: 'Hyderabad to Tirupati Balaji Tour',
    badge: 'Spiritual Yatra',
    temple: 'Sri Venkateswara Swamy Temple (Tirumala)',
    distance: '560 km (9 hrs)',
    duration: '2 Nights / 3 Days',
    recommendedVehicle: 'Force Urbania 10/12 Seater / Innova Crysta',
    startingPrice: '₹18,000',
    image: 'https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80',
    description: 'Experience a hassle-free, spiritually uplifting pilgrimage to Tirumala Balaji with convenient doorstep pickup in Hyderabad and experienced highway chauffeurs.'
  },
  {
    id: 'yadagirigutta',
    title: 'Hyderabad to Yadagirigutta Tour',
    badge: 'Day Trip',
    temple: 'Sri Lakshmi Narasimha Swamy Temple',
    distance: '65 km (1.5 hrs)',
    duration: 'Same Day Return',
    recommendedVehicle: 'Any Sedan, SUV or Force Urbania',
    startingPrice: '₹2,800',
    image: 'https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=800&q=80',
    description: 'Quick, relaxing family trip to the newly renovated architectural marvel of Yadagirigutta temple with same-day return to Hyderabad.'
  },
  {
    id: 'swarnagiri',
    title: 'Hyderabad to Swarnagiri Temple',
    badge: 'Trending Devotional',
    temple: 'Manepally Hills Sri Venkateswara Swamy',
    distance: '55 km (1 hr)',
    duration: 'Half Day / Full Day',
    recommendedVehicle: 'Force Urbania / Ertiga / Dzire',
    startingPrice: '₹2,500',
    image: 'https://images.unsplash.com/photo-1590076215667-875d4ef2d7ee?auto=format&fit=crop&w=800&q=80',
    description: 'Visit the stunning golden Swarnagiri temple on Bhuvangiri hills. A serene getaway for families with elderly parents and children.'
  },
  {
    id: 'bhadrachalam',
    title: 'Hyderabad to Bhadrachalam',
    badge: 'Sacred Godavari',
    temple: 'Sri Sita Ramachandra Swamy Temple',
    distance: '310 km (6 hrs)',
    duration: '2 Days / 1 Night',
    recommendedVehicle: 'Force Urbania 12/16 Seater / Innova Crysta',
    startingPrice: '₹9,500',
    image: 'https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=800&q=80',
    description: 'Devotional journey to Lord Rama temple on the banks of holy Godavari river with stops at Parnasala.'
  },
  {
    id: 'vemulawada',
    title: 'Hyderabad to Vemulawada Tour',
    badge: 'Dakshina Kashi',
    temple: 'Sri Raja Rajeshwara Swamy Temple',
    distance: '150 km (3.5 hrs)',
    duration: '1 Day / 2 Days',
    recommendedVehicle: 'Innova Crysta / Force Urbania / Tempo Traveller',
    startingPrice: '₹4,800',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    description: 'Visit the revered temple of Vemulawada, famous for Kode Mokku ritual, combined with Kondagattu Anjaneya Swamy.'
  },
  {
    id: 'alampur',
    title: 'Hyderabad to Alampur Jogulamba',
    badge: '5th Shakti Peetham',
    temple: 'Brahmeswara & Jogulamba Devi Temple',
    distance: '220 km (4 hrs)',
    duration: 'Same Day Return',
    recommendedVehicle: 'Force Urbania / Innova / Sedan',
    startingPrice: '₹6,000',
    image: 'https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80',
    description: 'Explore the 7th-century Chalukyan heritage temples and seek blessings at the 5th Maha Shakti Peetham at Alampur.'
  },
  {
    id: 'basara',
    title: 'Hyderabad to Basara Tour',
    badge: 'Aksharabhyasam Special',
    temple: 'Gnana Saraswati Devi Temple',
    distance: '210 km (4.5 hrs)',
    duration: '1 Day / 2 Days',
    recommendedVehicle: 'Force Urbania / Innova Crysta / Ertiga',
    startingPrice: '₹6,200',
    image: 'https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=800&q=80',
    description: 'Famous temple for children Aksharabhyasam ceremony on the banks of River Godavari. Safe and comfortable for families with kids.'
  },
  {
    id: 'chirala',
    title: 'Hyderabad to Chirala Beach',
    badge: 'Coastal Holiday',
    temple: 'Vodarevu & Suryalanka Beaches',
    distance: '300 km (6 hrs)',
    duration: '2 Days / 1 Night',
    recommendedVehicle: 'Force Urbania 12 Seater / Innova Crysta',
    startingPrice: '₹9,800',
    image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',
    description: 'Refreshing beach escape to Andhra pristine coastline. Plenty of boot space for your luggage, beachwear, and shopping.'
  },
  {
    id: 'araku',
    title: 'Hyderabad to Araku Valley & Vizag',
    badge: 'Scenic Hill Station',
    temple: 'Borra Caves, Coffee Gardens & Vizag Beach',
    distance: '650 km',
    duration: '4 Days / 3 Nights',
    recommendedVehicle: 'Force Urbania 10/16 Seater / Innova',
    startingPrice: '₹22,000',
    image: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80',
    description: 'Mesmerizing Eastern Ghats road trip with lush green coffee plantations, tribal culture, Borra Caves, and waterfalls.'
  },
  {
    id: 'arunachalam',
    title: 'Hyderabad to Arunachalam',
    badge: 'Giri Pradakshina',
    temple: 'Sri Annamalaiyar Temple (Tiruvannamalai)',
    distance: '680 km (11 hrs)',
    duration: '3 Days / 2 Nights',
    recommendedVehicle: 'Force Urbania (Power Recliner) / Tempo Traveller',
    startingPrice: '₹24,000',
    image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    description: 'Peaceful long-distance group pilgrimage to holy Arunachala hill. Relax on power recliner seats throughout the journey.'
  },
  {
    id: 'shirdi',
    title: 'Hyderabad to Shirdi Sai Baba',
    badge: 'Spiritual Yatra',
    temple: 'Shirdi Sai Sansthan & Shani Shingnapur',
    distance: '600 km (10 hrs)',
    duration: '3 Days / 2 Nights',
    recommendedVehicle: 'Force Urbania 12 Seater / Innova Crysta',
    startingPrice: '₹21,000',
    image: 'https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80',
    description: 'Sacred journey to Shirdi with comfortable night and day driving, visiting Shani Shingnapur and Trimbakeshwar Jyotirlinga.'
  },
  {
    id: 'hyderabad-sightseeing',
    title: 'Hyderabad City Sightseeing (8h / 12h)',
    badge: 'Heritage & Culture',
    temple: 'Charminar, Golconda, Hussain Sagar & Birla Mandir',
    distance: 'Local City Package',
    duration: '8 Hours (80 km) / 12 Hours (120 km)',
    recommendedVehicle: 'All Cars & Force Urbania Available',
    startingPrice: '₹2,200',
    image: 'https://images.unsplash.com/photo-1604999333679-b86d54738315?auto=format&fit=crop&w=800&q=80',
    description: 'Explore the City of Pearls: Charminar, Golconda Fort, Salar Jung Museum, Qutb Shahi Tombs, Birla Mandir, and Chowmahalla Palace.'
  },
  {
    id: 'ramoji',
    title: 'Ramoji Film City Day Tour',
    badge: 'Fun & Entertainment',
    temple: 'World Largest Film Studio Complex',
    distance: '45 km from city',
    duration: 'Full Day (Morning 8 AM to 8 PM)',
    recommendedVehicle: 'Force Urbania / Innova / Tempo Traveller / Sedan',
    startingPrice: '₹3,200',
    image: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80',
    description: 'Full day family outing to Ramoji Film City with guaranteed punctual morning pickup and night drop at your doorstep.'
  }
]

// High-End Safety Features (From reference site)
export const SAFETY_FEATURES = [
  {
    id: 'monocoque',
    title: 'Monocoque High-Strength Body',
    desc: 'Automotive grade monocoque chassis structure enhances vehicle stability, eliminates vibrations, and absorbs impact energy to shield all passengers.'
  },
  {
    id: 'brakes',
    title: 'ABS + EBD + ESP + VDC Brakes',
    desc: 'Advanced electronic braking systems prevent skidding, maintain steering control during emergency panic braking, and stabilize the vehicle on wet roads.'
  },
  {
    id: 'airbags',
    title: 'Airbags & Collapsible Steering',
    desc: 'Equipped with dual frontal safety airbags and energy-absorbing collapsible steering column to protect the driver and passengers in frontal impacts.'
  },
  {
    id: 'belts',
    title: '3-Point Seat Belts on Every Seat',
    desc: 'Ergonomic 3-point lap-and-shoulder seatbelts securely hold every passenger in place during sharp corners or abrupt halts.'
  },
  {
    id: 'hillhold',
    title: 'Hill Hold Assist & Rollover Protection',
    desc: 'Prevents backward rollback on steep hill ghats (Srisailam, Tirumala, Araku) and active rollover mitigation maintains supreme center of gravity.'
  },
  {
    id: 'entertainment',
    title: 'Smart LED TV & Hi-Fi Audio',
    desc: 'Large overhead entertainment screen with Bluetooth audio, YouTube/OTT streaming support, and powerful speakers make every hour of travel fun.'
  }
]

// Customer Testimonials
export const TESTIMONIALS = [
  {
    name: 'K. Venkat Rao',
    location: 'Gachibowli, Hyderabad',
    trip: 'Hyderabad to Srisailam (Force Urbania 10-Seater)',
    comment: 'Booked the 10-seater Force Urbania with power recliners from Mr. Surender Reddy for our temple trip to Srisailam with my elderly parents. The vehicle was pristine, ultra-comfortable, and the driver drove very safely on the ghat roads. 10/10 service!'
  },
  {
    name: 'Srinivas Murthy',
    location: 'Kukatpally, Hyderabad',
    trip: 'Hyderabad to Tirupati (Innova Crysta & Urbania)',
    comment: 'We needed two vehicles for 18 family members to Tirupati. Mr. Surender Reddy arranged both on short notice via WhatsApp. Punctual, transparent billing with no hidden costs. Truly best travels in Hyderabad.'
  },
  {
    name: 'Ananya Deshmukh',
    location: 'Hitech City, Hyderabad',
    trip: 'Wedding Guest Transit (Tempo Traveller & Dzires)',
    comment: 'Booked 2 Tempo Travellers and 3 Swift Dzires for our sister destination wedding. All drivers were uniformed, courteous, and always on time at Shamshabad airport. Thank you Surender Reddy garu!'
  }
]
