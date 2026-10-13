package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.VehicleStatus;
import app.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String registrationNumber;
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private String name;
    private Integer seatingCapacity;
    private String fuelType;
    private Integer currentKm;
    private LocalDate insuranceExpiryDate;
    private LocalDate rcExpiryDate;
    private LocalDate permitExpiryDate;
    private BigDecimal rentalPricePerDay;
    private String description;
    private String imageUrl;
    private VehicleStatus status;

    // Vendor info
    private Long vendorId;
    private String vendorName;
    private String vendorBusinessName;

    // Document warnings
    private boolean hasExpiredDocuments;
    private boolean hasExpiringSoonDocuments;
    private List<String> warnings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BigDecimal getPricePerDay() {
        return rentalPricePerDay;
    }

    public String getType() {
        return vehicleType != null ? vehicleType.name() : "";
    }

    public String getLocation() {
        return (vendorBusinessName != null && !vendorBusinessName.isBlank()) ? vendorBusinessName : "Hyderabad";
    }

    public java.util.Map<String, Object> getVendor() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", vendorId);
        map.put("name", vendorName);
        map.put("businessName", vendorBusinessName != null ? vendorBusinessName : vendorName);
        return map;
    }
}
