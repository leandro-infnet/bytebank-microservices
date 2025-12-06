package br.com.bytebank.api.domain.user;

public record UserDetailsDTO(
        Long id,
        String name,
        String email,
        String documentNumber
) {
    public UserDetailsDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getDocumentNumber());
    }
}
