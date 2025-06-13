package com.foodie.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateSecurityRequest {
	
	@NotBlank(message = "Current password is required")
    private String currentPassword;

    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    private boolean enable2FA;


}
