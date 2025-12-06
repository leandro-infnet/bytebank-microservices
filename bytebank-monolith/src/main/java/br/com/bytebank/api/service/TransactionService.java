package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.transaction.*;
import br.com.bytebank.api.exception.BusinessRuleException;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /*
     * @Transactional garante que:
     * OU tudo funciona -> saldo é atualizado + operação é registrada.
     * OU nada é salvo (tudo é revertido) mantendo dados consistentes.
     */
    @Transactional
    public TransactionDetailsDTO performDeposit(DepositRequestDTO requestDTO) {
        var destinationAccount = accountRepository.findByAccountNumber(requestDTO.destinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found."));

        if (!destinationAccount.isActive()) {
            throw new BusinessRuleException("Cannot deposit into an inactive account.");
        }

        destinationAccount.setBalance(destinationAccount.getBalance().add(requestDTO.amount()));
        // Usei .add() para somar, porque BigDecimal é imutável

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.DEPOSIT,
                LocalDateTime.now(),
                null,  // Conta de origem é nula para depósitos
                destinationAccount,
                "Deposit operation"
        );

        transactionRepository.save(transaction);

        return new TransactionDetailsDTO(transaction);
    }

    @Transactional
    public TransactionDetailsDTO performWithdrawal(WithdrawalRequestDTO requestDTO) {
        var sourceAccount = accountRepository.findByAccountNumber(requestDTO.sourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found."));

        if (!sourceAccount.isActive()) {
            throw new BusinessRuleException("Cannot withdraw from an inactive account.");
        }

        if (sourceAccount.getBalance().compareTo(requestDTO.amount()) < 0) {  // Há saldo?
            throw new BusinessRuleException("Insufficient funds.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(requestDTO.amount()));
        // Subtrair valor do saldo da conta

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.WITHDRAWAL,
                LocalDateTime.now(),
                sourceAccount,
                null,  // Conta de destino é nula para saques
                "Withdrawal operation"
        );

        transactionRepository.save(transaction);

        return new TransactionDetailsDTO(transaction);
    }

    @Transactional
    public TransactionDetailsDTO performTransfer(TransferRequestDTO requestDTO) {
        if (requestDTO.sourceAccountNumber().equals(requestDTO.destinationAccountNumber())) {
            throw new BusinessRuleException("Source and destination accounts cannot be the same.");
        }

        var sourceAccount = accountRepository.findByAccountNumber(requestDTO.sourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found."));

        var destinationAccount = accountRepository.findByAccountNumber(requestDTO.destinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found."));

        if (!sourceAccount.isActive() || !destinationAccount.isActive()) {
            throw new BusinessRuleException("Both source and destination accounts must be active.");
        }

        if (sourceAccount.getBalance().compareTo(requestDTO.amount()) < 0) {
            throw new BusinessRuleException("insufficient funds.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(requestDTO.amount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(requestDTO.amount()));

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.TRANSFER,
                LocalDateTime.now(),
                sourceAccount,
                destinationAccount,
                "Transfer operation"
        );

        transactionRepository.save(transaction);

        return new TransactionDetailsDTO(transaction);
    }
}
