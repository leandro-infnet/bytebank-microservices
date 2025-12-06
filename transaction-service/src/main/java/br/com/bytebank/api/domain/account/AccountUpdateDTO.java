package br.com.bytebank.api.domain.account;

import jakarta.validation.constraints.NotNull;

public record AccountUpdateDTO(
        @NotNull
        Boolean isActive
) {
}
