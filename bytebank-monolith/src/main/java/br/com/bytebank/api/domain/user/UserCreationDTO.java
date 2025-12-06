package br.com.bytebank.api.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationDTO(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password,

        @NotBlank(message = "Document number is mandatory")
        String documentNumber
) {
}
