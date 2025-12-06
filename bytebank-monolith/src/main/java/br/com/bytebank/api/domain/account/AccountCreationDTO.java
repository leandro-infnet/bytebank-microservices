package br.com.bytebank.api.domain.account;

import jakarta.validation.constraints.NotNull;

public record AccountCreationDTO(
        @NotNull  // TODO: Adicionar validação
        Long userId,

        @NotNull
        AccountType accountType
) {
}