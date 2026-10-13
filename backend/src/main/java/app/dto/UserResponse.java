package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import com.travelmate.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String businessName;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
