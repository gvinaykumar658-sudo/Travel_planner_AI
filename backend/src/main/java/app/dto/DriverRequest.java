package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequest {

    @NotBlank(message = "Driver name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Driving licence number is required")
    @Size(max = 50, message = "Licence number must not exceed 50 characters")
    private String licenceNumber;

    @NotNull(message = "Licence expiry date is required")
    private LocalDate licenceExpiryDate;

    private String address;

    @Builder.Default
    private DriverStatus status = DriverStatus.AVAILABLE;
}
