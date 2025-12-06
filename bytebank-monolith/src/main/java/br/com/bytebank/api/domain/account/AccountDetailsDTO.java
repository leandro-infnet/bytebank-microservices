package br.com.bytebank.api.domain.account;

import java.math.BigDecimal;

public record AccountDetailsDTO(
        Long id,
        Long userId,
        String agencyNumber,
        String accountNumber,
        BigDecimal balance,
        AccountType accountType,
        boolean isActive
) {
    public AccountDetailsDTO (Account account) {
        this(
                account.getId(),
                account.getUser().getId(),  // Extrair só ID do objeto User
                account.getAgencyNumber(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType(),
                account.isActive()
        );
    }
}
