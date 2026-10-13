package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
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
public class ExpenseRequest {

    private Long vehicleId; // Optional: can be a vehicle-specific expense or a general company expense

    @NotNull(message = "Expense category is required (FUEL, DRIVER_SALARY, REPAIRS, INSURANCE, PERMIT, CLEANING, OTHER)")
    private ExpenseCategory category;

    @NotBlank(message = "Expense description is required")
    private String description;

    @NotNull(message = "Expense amount is required")
    @DecimalMin(value = "0.01", message = "Expense amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String receiptReference;
}
