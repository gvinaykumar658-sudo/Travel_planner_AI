package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleRegistrationNumber;

    private String serviceType;
    private String description;
    private LocalDate serviceDate;
    private Integer kilometerReading;
    private BigDecimal serviceCost;
    private LocalDate nextServiceDate;
    private Integer nextServiceKilometerReading;
    private MaintenanceStatus status;

    private boolean isOverdue;
    private boolean isDueSoon;
    private String alert;

    private LocalDateTime createdAt;
}
