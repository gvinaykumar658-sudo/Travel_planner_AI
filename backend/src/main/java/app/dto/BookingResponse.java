package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.BookingStatus;
import app.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private String bookingReference;

    // Customer
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Vendor
    private Long vendorId;
    private String vendorName;
    private String vendorBusinessName;

    // Vehicle
    private Long vehicleId;
    private String vehicleName;
    private String vehicleRegistrationNumber;
    private String vehicleType;
    private BigDecimal rentalPricePerDay;
    private String vehicleImageUrl;

    // Driver
    private Long driverId;
    private String driverName;
    private String driverPhone;

    // Trip details
    private String pickupLocation;
    private String destination;
    private LocalDateTime pickupDateTime;
    private LocalDateTime returnDateTime;
    private Integer numberOfPassengers;
    private long totalDays;

    // Financials
    private BigDecimal rentalPrice;
    private BigDecimal additionalCharges;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal balanceDue;

    // Statuses
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getStatus() {
        return bookingStatus != null ? bookingStatus.name() : "CONFIRMED";
    }

    public String getFromDate() {
        return pickupDateTime != null ? pickupDateTime.toLocalDate().toString() : "";
    }

    public String getToDate() {
        return returnDateTime != null ? returnDateTime.toLocalDate().toString() : "";
    }

    public java.util.Map<String, Object> getVehicle() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", vehicleId);
        map.put("name", vehicleName != null ? vehicleName : "Vehicle");
        map.put("pricePerDay", rentalPricePerDay != null ? rentalPricePerDay : java.math.BigDecimal.ZERO);
        return map;
    }

    public java.util.Map<String, Object> getCustomer() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", customerId);
        map.put("name", customerName != null ? customerName : "Customer");
        map.put("email", customerEmail != null ? customerEmail : "");
        return map;
    }
}
