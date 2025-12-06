package br.com.bytebank.api.domain.user;

public record UserUpdateDTO(
        String name,
        String email
) {
}
