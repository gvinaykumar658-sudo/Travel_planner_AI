package app.config;

import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.entity.Vehicle;
import app.entity.VehicleStatus;
import app.entity.VehicleType;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Ensure existing accounts have BCrypt-hashed passwords
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getPassword() != null && !u.getPassword().startsWith("$2a$") && !u.getPassword().startsWith("$2b$")) {
                log.info("Upgrading plain text password to BCrypt hash for user: {}", u.getEmail());
                u.setPassword(passwordEncoder.encode(u.getPassword()));
                userRepository.save(u);
            }
        }

        // 2. Ensure system ADMIN exists
        if (userRepository.findByEmail("admin@travelmate.com").isEmpty()) {
            log.info("Creating default system administrator account: admin@travelmate.com");
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@travelmate.com")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .role(Role.ADMIN)
                    .businessName("TravelMate HQ")
                    .phone("9876543210")
                    .address("Admin HQ, Hyderabad")
                    .build();
            userRepository.save(admin);
        }

        // 3. Ensure a default demo vendor exists
        User vendor = userRepository.findByEmail("vendor@travelmate.com").orElse(null);
        if (vendor == null) {
            log.info("Creating demo vendor account: vendor@travelmate.com");
            vendor = User.builder()
                    .name("Apex Rentals")
                    .email("vendor@travelmate.com")
                    .password(passwordEncoder.encode("Vendor@12345"))
                    .role(Role.VENDOR)
                    .businessName("Apex Luxury Fleet")
                    .phone("9876543211")
                    .address("Madhapur, Hyderabad")
                    .build();
            vendor = userRepository.save(vendor);
        }

        // 4. Seed sample vehicles if table is empty
        if (vehicleRepository.count() == 0) {
            log.info("Seeding initial fleet of vehicles for TravelMate...");

            Vehicle v1 = Vehicle.builder()
                    .registrationNumber("TS09-EX-1001")
                    .vehicleType(VehicleType.SUV)
                    .brand("Toyota")
                    .model("Innova Crysta")
                    .name("Toyota Innova Crysta")
                    .seatingCapacity(7)
                    .fuelType("Diesel")
                    .currentKm(25000)
                    .rentalPricePerDay(new BigDecimal("4200.00"))
                    .description("Spacious 7-seater luxury MPV for family and group trips.")
                    .status(VehicleStatus.AVAILABLE)
                    .vendor(vendor)
                    .build();

            Vehicle v2 = Vehicle.builder()
                    .registrationNumber("TS09-EX-1002")
                    .vehicleType(VehicleType.SUV)
                    .brand("Hyundai")
                    .model("Creta SX")
                    .name("Hyundai Creta SX")
                    .seatingCapacity(5)
                    .fuelType("Petrol")
                    .currentKm(18000)
                    .rentalPricePerDay(new BigDecimal("3200.00"))
                    .description("Premium compact SUV with panoramic sunroof and high comfort.")
                    .status(VehicleStatus.AVAILABLE)
                    .vendor(vendor)
                    .build();

            Vehicle v3 = Vehicle.builder()
                    .registrationNumber("TS09-EX-1003")
                    .vehicleType(VehicleType.HATCHBACK)
                    .brand("Maruti Suzuki")
                    .model("Swift ZXi")
                    .name("Maruti Suzuki Swift")
                    .seatingCapacity(5)
                    .fuelType("Petrol")
                    .currentKm(12000)
                    .rentalPricePerDay(new BigDecimal("1800.00"))
                    .description("Economical, agile city hatchback with great fuel efficiency.")
                    .status(VehicleStatus.AVAILABLE)
                    .vendor(vendor)
                    .build();

            Vehicle v4 = Vehicle.builder()
                    .registrationNumber("TS09-EX-1004")
                    .vehicleType(VehicleType.SEDAN)
                    .brand("Honda")
                    .model("City VX")
                    .name("Honda City VX")
                    .seatingCapacity(5)
                    .fuelType("Petrol")
                    .currentKm(21000)
                    .rentalPricePerDay(new BigDecimal("2800.00"))
                    .description("Executive luxury sedan for smooth highway cruising and business travel.")
                    .status(VehicleStatus.AVAILABLE)
                    .vendor(vendor)
                    .build();

            vehicleRepository.saveAll(List.of(v1, v2, v3, v4));
            log.info("Seeded 4 initial fleet vehicles successfully.");
        }
    }
}
