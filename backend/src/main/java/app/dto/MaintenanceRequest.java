package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.MaintenanceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MaintenanceRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotBlank(message = "Service type is required (e.g. Oil Change, Brake Inspection, Tire Replacement, General Service)")
    private String serviceType;

    private String description;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    @NotNull(message = "Odometer kilometer reading is required")
    @Min(value = 0, message = "Kilometer reading cannot be negative")
    private Integer kilometerReading;

    @NotNull(message = "Service cost is required")
    @DecimalMin(value = "0.00", message = "Service cost cannot be negative")
    private BigDecimal serviceCost;

    private LocalDate nextServiceDate;
    private Integer nextServiceKilometerReading;

    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;
}
