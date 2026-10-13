package app.dto;

import com.travelmate.model.User;
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
public class ApiResponse {

    private boolean success;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ApiResponse success(String message) {
        return ApiResponse.builder().success(true).message(message).timestamp(LocalDateTime.now()).build();
    }

    public static ApiResponse error(String message) {
        return ApiResponse.builder().success(false).message(message).timestamp(LocalDateTime.now()).build();
    }
}
