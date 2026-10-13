package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

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
public class CustomerSummaryResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private long totalBookings;
    private BigDecimal totalSpent;
    private LocalDateTime lastBookingDate;
    private LocalDateTime memberSince;
}
