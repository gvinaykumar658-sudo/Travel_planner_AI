package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.00", message = "Payment amount must be at least 1.00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required (CASH, UPI, CARD, BANK_TRANSFER)")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Transaction reference or receipt number is required")
    private String transactionReference;

    private String notes;
}
