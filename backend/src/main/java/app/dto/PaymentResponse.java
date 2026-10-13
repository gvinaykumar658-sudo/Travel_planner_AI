package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.PaymentMethod;
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
public class PaymentResponse {

    private Long id;
    private Long bookingId;
    private String bookingReference;

    private Long customerId;
    private String customerName;
    private String customerEmail;

    private Long vendorId;
    private String vendorBusinessName;

    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String notes;

    // Booking balance snapshot
    private BigDecimal bookingTotalAmount;
    private BigDecimal totalPaidToDate;
    private BigDecimal outstandingBalance;

    private LocalDateTime createdAt;
}
