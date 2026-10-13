package app;

import app.dto.BookingRequest;
import app.dto.BookingResponse;
import app.dto.ExpenseRequest;
import app.dto.ExpenseResponse;
import app.dto.PaymentRequest;
import app.dto.PaymentResponse;
import app.entity.*;
import com.travelmate.model.Role;
import com.travelmate.model.User;
import app.exception.ConflictException;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import app.service.BookingService;
import app.service.ExpenseService;
import app.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.travelmate.TravelmateApplication.class)
@Transactional
public class PaymentExpenseTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User testCustomer;
    private User testVendor;
    private Vehicle testVehicle;
    private BookingResponse testBooking;

    @BeforeEach
    void setUp() {
        long ts = System.currentTimeMillis();
        testVendor = userRepository.save(User.builder()
                .name("Prime Rides")
                .email("vendor" + ts + "@prime.com")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.VENDOR)
                .businessName("Prime Rides")
                .build());

        testCustomer = userRepository.save(User.builder()
                .name("Anand Reddy")
                .email("customer" + ts + "@prime.com")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .role(Role.CUSTOMER)
                .build());

        testVehicle = vehicleRepository.save(Vehicle.builder()
                .registrationNumber("AP28TR" + (ts % 10000))
                .vehicleType(VehicleType.CAR)
                .brand("Maruti")
                .model("Ertiga")
                .name("Maruti Ertiga ZXi")
                .seatingCapacity(7)
                .fuelType("Petrol")
                .currentKm(18000)
                .rentalPricePerDay(new BigDecimal("2500.00"))
                .status(VehicleStatus.AVAILABLE)
                .vendor(testVendor)
                .build());

        BookingRequest bReq = BookingRequest.builder()
                .vehicleId(testVehicle.getId())
                .pickupLocation("Secunderabad")
                .destination("Tirupati")
                .pickupDateTime(LocalDateTime.now().plusDays(3))
                .returnDateTime(LocalDateTime.now().plusDays(4))
                .numberOfPassengers(5)
                .build();

        testBooking = bookingService.createBooking(bReq, testCustomer.getId());
    }

    @Test
    void testRecordPaymentAndStatusUpdate() {
        String txnRef = "TXN" + System.currentTimeMillis();
        PaymentRequest req = PaymentRequest.builder()
                .bookingId(testBooking.getId())
                .amount(testBooking.getTotalAmount()) // Full payment
                .paymentMethod(PaymentMethod.UPI)
                .transactionReference(txnRef)
                .notes("Advance UPI Payment")
                .build();

        PaymentResponse resp = paymentService.recordPayment(req, testCustomer.getId(), Role.CUSTOMER);

        assertNotNull(resp);
        assertEquals(txnRef, resp.getTransactionReference());
        assertEquals("COMPLETED", resp.getPaymentStatus().name());
        assertEquals(0, resp.getOutstandingBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testDuplicateTransactionReferenceRejected() {
        String txnRef = "TXN-DUP" + System.currentTimeMillis();
        PaymentRequest req1 = PaymentRequest.builder()
                .bookingId(testBooking.getId())
                .amount(new BigDecimal("1000.00"))
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference(txnRef)
                .build();
        paymentService.recordPayment(req1, testCustomer.getId(), Role.CUSTOMER);

        PaymentRequest req2 = PaymentRequest.builder()
                .bookingId(testBooking.getId())
                .amount(new BigDecimal("500.00"))
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference(txnRef)
                .build();

        assertThrows(ConflictException.class, () -> paymentService.recordPayment(req2, testCustomer.getId(), Role.CUSTOMER));
    }

    @Test
    void testExpenseCreation() {
        ExpenseRequest req = ExpenseRequest.builder()
                .vehicleId(testVehicle.getId())
                .category(ExpenseCategory.FUEL)
                .description("Full tank petrol refill")
                .amount(new BigDecimal("3500.00"))
                .expenseDate(LocalDate.now())
                .receiptReference("RCPT-98765")
                .build();

        ExpenseResponse resp = expenseService.createExpense(req, testVendor.getId());

        assertNotNull(resp);
        assertEquals("FUEL", resp.getCategory().name());
        assertEquals(testVehicle.getId(), resp.getVehicleId());
        assertEquals(0, resp.getAmount().compareTo(new BigDecimal("3500.00")));
    }
}
