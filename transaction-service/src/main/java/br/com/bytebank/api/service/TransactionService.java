package br.com.bytebank.api.service;

import br.com.bytebank.api.client.AccountClient;
import br.com.bytebank.api.domain.transaction.*;
import br.com.bytebank.api.dto.SaldoUpdateDTO;
import br.com.bytebank.api.exception.BusinessRuleException;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    @Transactional
    public TransactionDetailsDTO performDeposit(DepositRequestDTO requestDTO) {
        var destinationAccountDto = accountClient.buscarPorNumero(requestDTO.destinationAccountNumber());

        if (destinationAccountDto == null) {
            throw new ResourceNotFoundException("Destination account not found.");
        }

        if (!destinationAccountDto.isActive()) {
            throw new BusinessRuleException("Cannot deposit into an inactive account.");
        }

        accountClient.atualizarSaldo(destinationAccountDto.id(), new SaldoUpdateDTO(requestDTO.amount()));

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.DEPOSIT,
                LocalDateTime.now(),
                null, // Sem conta origem
                destinationAccountDto.id(),
                "Deposit operation");

        transactionRepository.save(transaction);

        var finalBalance = destinationAccountDto.balance().add(requestDTO.amount());

        return new TransactionDetailsDTO(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                null,
                destinationAccountDto.accountNumber(),
                finalBalance);
    }

    @Transactional
    public TransactionDetailsDTO performWithdrawal(WithdrawalRequestDTO requestDTO) {
        var sourceAccountDto = accountClient.buscarPorNumero(requestDTO.sourceAccountNumber());

        if (sourceAccountDto == null)
            throw new ResourceNotFoundException("Source account not found.");

        if (!sourceAccountDto.isActive()) {
            throw new BusinessRuleException("Cannot withdraw from an inactive account.");
        }

        if (sourceAccountDto.balance().compareTo(requestDTO.amount()) < 0) {
            throw new BusinessRuleException("Insufficient funds.");
        }

        accountClient.atualizarSaldo(sourceAccountDto.id(), new SaldoUpdateDTO(requestDTO.amount().negate()));

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.WITHDRAWAL,
                LocalDateTime.now(),
                sourceAccountDto.id(),
                null,
                "Withdrawal operation");

        transactionRepository.save(transaction);

        var finalBalance = sourceAccountDto.balance().subtract(requestDTO.amount());

        return new TransactionDetailsDTO(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                sourceAccountDto.accountNumber(),
                null,
                finalBalance);
    }

    @Transactional
    public TransactionDetailsDTO performTransfer(TransferRequestDTO requestDTO) {
        if (requestDTO.sourceAccountNumber().equals(requestDTO.destinationAccountNumber())) {
            throw new BusinessRuleException("Source and destination accounts cannot be the same.");
        }

        var sourceDto = accountClient.buscarPorNumero(requestDTO.sourceAccountNumber());
        var destDto = accountClient.buscarPorNumero(requestDTO.destinationAccountNumber());

        if (sourceDto == null)
            throw new ResourceNotFoundException("Source account not found.");
        if (destDto == null)
            throw new ResourceNotFoundException("Destination account not found.");

        if (!sourceDto.isActive() || !destDto.isActive()) {
            throw new BusinessRuleException("Both accounts must be active.");
        }

        if (sourceDto.balance().compareTo(requestDTO.amount()) < 0) {
            throw new BusinessRuleException("Insufficient funds.");
        }

        accountClient.atualizarSaldo(sourceDto.id(), new SaldoUpdateDTO(requestDTO.amount().negate()));
        accountClient.atualizarSaldo(destDto.id(), new SaldoUpdateDTO(requestDTO.amount()));

        var transaction = new Transaction(
                null,
                requestDTO.amount(),
                TransactionType.TRANSFER,
                LocalDateTime.now(),
                sourceDto.id(),
                destDto.id(),
                "Transfer operation");

        transactionRepository.save(transaction);

        var sourceFinalBalance = sourceDto.balance().subtract(requestDTO.amount());

        return new TransactionDetailsDTO(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                sourceDto.accountNumber(),
                destDto.accountNumber(),
                sourceFinalBalance);
    }
}
