package app;

import app.dto.BookingRequest;
import app.dto.BookingResponse;
import app.entity.*;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.exception.ConflictException;
import app.repository.BookingRepository;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import app.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.travelmate.TravelmateApplication.class)
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User testCustomer;
    private User testVendor;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        long ts = System.currentTimeMillis();
        testVendor = userRepository.save(User.builder()
                .name("Apex Rentals")
                .email("vendor" + ts + "@apex.com")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.VENDOR)
                .businessName("Apex Rentals")
                .build());

        testCustomer = userRepository.save(User.builder()
                .name("Kavitha Sharma")
                .email("customer" + ts + "@mail.com")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.CUSTOMER)
                .build());

        testVehicle = vehicleRepository.save(Vehicle.builder()
                .registrationNumber("TS07XY" + (ts % 10000))
                .vehicleType(VehicleType.CAR)
                .brand("Hyundai")
                .model("Creta")
                .name("Hyundai Creta SX")
                .seatingCapacity(5)
                .fuelType("Diesel")
                .currentKm(25000)
                .rentalPricePerDay(new BigDecimal("3000.00"))
                .status(VehicleStatus.AVAILABLE)
                .vendor(testVendor)
                .build());
    }

    @Test
    void testCreateBookingAndPriceCalculation() {
        LocalDateTime pickup = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0);
        LocalDateTime returnTime = pickup.plusDays(3).withHour(18).withMinute(0); // 4 calendar days span

        BookingRequest req = BookingRequest.builder()
                .vehicleId(testVehicle.getId())
                .pickupLocation("Gachibowli, Hyderabad")
                .destination("Srisailam")
                .pickupDateTime(pickup)
                .returnDateTime(returnTime)
                .numberOfPassengers(4)
                .additionalCharges(new BigDecimal("500.00"))
                .build();

        BookingResponse resp = bookingService.createBooking(req, testCustomer.getId());

        assertNotNull(resp);
        assertNotNull(resp.getBookingReference());
        assertEquals("CONFIRMED", resp.getBookingStatus().name());
        assertEquals("PENDING", resp.getPaymentStatus().name());
        assertTrue(resp.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("Srisailam", resp.getDestination());
    }

    @Test
    void testOverlappingBookingPrevention() {
        LocalDateTime pickup = LocalDateTime.now().plusDays(5).withHour(9).withMinute(0);
        LocalDateTime returnTime = pickup.plusDays(2).withHour(18).withMinute(0);

        BookingRequest req1 = BookingRequest.builder()
                .vehicleId(testVehicle.getId())
                .pickupLocation("Banjara Hills, Hyderabad")
                .destination("Warangal")
                .pickupDateTime(pickup)
                .returnDateTime(returnTime)
                .numberOfPassengers(3)
                .build();

        bookingService.createBooking(req1, testCustomer.getId());

        // Attempt overlapping reservation on same vehicle
        BookingRequest req2 = BookingRequest.builder()
                .vehicleId(testVehicle.getId())
                .pickupLocation("Secunderabad")
                .destination("Nagarjuna Sagar")
                .pickupDateTime(pickup.plusHours(12)) // Overlaps
                .returnDateTime(returnTime.plusDays(1))
                .numberOfPassengers(2)
                .build();

        assertThrows(ConflictException.class, () -> bookingService.createBooking(req2, testCustomer.getId()));
    }
}
