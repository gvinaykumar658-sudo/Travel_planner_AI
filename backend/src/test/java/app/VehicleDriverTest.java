package app;

import app.dto.DriverRequest;
import app.dto.DriverResponse;
import app.dto.VehicleRequest;
import app.dto.VehicleResponse;
import app.entity.*;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.exception.ConflictException;
import app.repository.DriverRepository;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import app.service.DriverService;
import app.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.travelmate.TravelmateApplication.class)
@Transactional
public class VehicleDriverTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    private User testVendor;

    @BeforeEach
    void setUp() {
        testVendor = userRepository.save(User.builder()
                .name("Speedy Travels")
                .email("vendor" + System.currentTimeMillis() + "@travels.com")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.VENDOR)
                .businessName("Speedy Travels")
                .build());
    }

    @Test
    void testCreateVehicleSuccess() {
        String regNo = "TS09AB" + (System.currentTimeMillis() % 10000);
        VehicleRequest req = VehicleRequest.builder()
                .registrationNumber(regNo)
                .vehicleType(VehicleType.CAR)
                .brand("Toyota")
                .model("Innova Crysta")
                .name("Toyota Innova Crysta Luxury")
                .seatingCapacity(7)
                .fuelType("Diesel")
                .currentKm(15000)
                .insuranceExpiryDate(LocalDate.now().plusMonths(6))
                .rcExpiryDate(LocalDate.now().plusYears(2))
                .permitExpiryDate(LocalDate.now().plusMonths(12))
                .rentalPricePerDay(new BigDecimal("3500.00"))
                .description("Comfortable 7-seater")
                .status(VehicleStatus.AVAILABLE)
                .build();

        VehicleResponse resp = vehicleService.createVehicle(req, testVendor.getId());

        assertNotNull(resp.getId());
        assertEquals(regNo, resp.getRegistrationNumber());
        assertEquals(7, resp.getSeatingCapacity());
        assertFalse(resp.isHasExpiredDocuments());
    }

    @Test
    void testDuplicateRegistrationRejected() {
        String regNo = "TS09DUP" + (System.currentTimeMillis() % 10000);
        VehicleRequest req1 = VehicleRequest.builder()
                .registrationNumber(regNo)
                .vehicleType(VehicleType.CAR)
                .brand("Honda")
                .model("City")
                .name("Honda City Sedan")
                .seatingCapacity(5)
                .fuelType("Petrol")
                .currentKm(20000)
                .rentalPricePerDay(new BigDecimal("2200.00"))
                .build();
        vehicleService.createVehicle(req1, testVendor.getId());

        VehicleRequest req2 = VehicleRequest.builder()
                .registrationNumber(regNo)
                .vehicleType(VehicleType.CAR)
                .brand("Hyundai")
                .model("Verna")
                .name("Hyundai Verna")
                .seatingCapacity(5)
                .fuelType("Petrol")
                .currentKm(10000)
                .rentalPricePerDay(new BigDecimal("2100.00"))
                .build();

        assertThrows(ConflictException.class, () -> vehicleService.createVehicle(req2, testVendor.getId()));
    }

    @Test
    void testDriverCreationAndExpiryWarning() {
        String licenceNo = "DL" + System.currentTimeMillis();
        DriverRequest req = DriverRequest.builder()
                .name("Rajesh Kumar")
                .phone("9876543210")
                .licenceNumber(licenceNo)
                .licenceExpiryDate(LocalDate.now().plusDays(10)) // Expiring in 10 days
                .status(DriverStatus.AVAILABLE)
                .build();

        DriverResponse resp = driverService.createDriver(req, testVendor.getId());

        assertNotNull(resp.getId());
        assertEquals("Rajesh Kumar", resp.getName());
        assertTrue(resp.isLicenceExpiringSoon());
        assertNotNull(resp.getWarning());
    }
}
