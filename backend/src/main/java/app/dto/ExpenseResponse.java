package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.ExpenseCategory;
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
public class ExpenseResponse {

    private Long id;
    private Long vendorId;
    private String vendorBusinessName;

    private Long vehicleId;
    private String vehicleName;
    private String vehicleRegistrationNumber;

    private ExpenseCategory category;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String receiptReference;

    private LocalDateTime createdAt;
}
