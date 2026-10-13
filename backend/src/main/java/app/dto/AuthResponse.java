package app.dto;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long id;
    private String name;
    private String email;
    private String role;
    private String businessName;
    private String phone;
}
