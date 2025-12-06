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
        BigDecimal finalBalance
) {
    public TransactionDetailsDTO (Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getSourceAccount() != null
                        ? transaction.getSourceAccount().getAccountNumber()
                        : null,
                transaction.getDestinationAccount() != null
                        ? transaction.getDestinationAccount().getAccountNumber()
                        : null,
                determineFinalBalance(transaction)
        );
    }

    private static BigDecimal determineFinalBalance(Transaction transaction) {
        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            return transaction.getDestinationAccount().getBalance();

        } else if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            return transaction.getSourceAccount().getBalance();
        }

        // Para transferências, mostro o saldo da conta de origem
        return transaction.getSourceAccount() != null
                ? transaction.getSourceAccount().getBalance()
                : null;
    }
}
