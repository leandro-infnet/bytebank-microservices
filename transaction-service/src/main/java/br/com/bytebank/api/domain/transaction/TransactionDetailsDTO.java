package br.com.bytebank.api.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDetailsDTO(
        Long transactionId,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDateTime date,
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal finalBalance) {
}
