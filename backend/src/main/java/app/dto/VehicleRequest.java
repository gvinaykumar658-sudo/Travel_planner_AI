package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.VehicleStatus;
import app.entity.VehicleType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    private String registrationNumber;
    private VehicleType vehicleType;
    private String type;
    private String location;
    private String brand;
    private String model;

    @NotBlank(message = "Vehicle name is required")
    private String name;

    private Integer seatingCapacity;
    private String fuelType;
    private Integer currentKm;
    private LocalDate insuranceExpiryDate;
    private LocalDate rcExpiryDate;
    private LocalDate permitExpiryDate;
    private BigDecimal rentalPricePerDay;
    private BigDecimal pricePerDay;
    private String description;
    private String imageUrl;

    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;
}
