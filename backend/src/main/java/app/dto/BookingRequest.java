package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BookingRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    private Long driverId;
    private String fromDate;
    private String toDate;
    private String pickupLocation;
    private String destination;
    private LocalDateTime pickupDateTime;
    private LocalDateTime returnDateTime;
    private Integer numberOfPassengers;
    private BigDecimal additionalCharges;
}
