package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String licenceNumber;
    private LocalDate licenceExpiryDate;
    private String address;
    private DriverStatus status;

    private Long vendorId;
    private String vendorName;
    private String vendorBusinessName;

    private boolean isLicenceExpired;
    private boolean isLicenceExpiringSoon;
    private String warning;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
