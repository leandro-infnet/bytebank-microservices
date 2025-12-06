package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.account.Account;
import br.com.bytebank.api.domain.account.AccountType;
import br.com.bytebank.api.domain.transaction.Transaction;
import br.com.bytebank.api.domain.transaction.TransferRequestDTO;
import br.com.bytebank.api.exception.BusinessRuleException;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private Account dougAccount;
    private Account carrieAccount;

    @BeforeEach
    void setup() {
        dougAccount = new Account(
                1L,
                null,
                "0001",
                "111222",
                new BigDecimal("1000.00"), AccountType.CHECKING, true);
        carrieAccount = new Account(
                2L,
                null,
                "0001",
                "333444",
                new BigDecimal("500.00"), AccountType.CHECKING, true);
    }

    @Test
    @DisplayName("Should perform transfer successfully")
    void performTransfer_SuccessScenario() {
        var requestDTO = new TransferRequestDTO(
                "111222", "333444", new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("111222")).thenReturn(Optional.of(dougAccount));
        when(accountRepository.findByAccountNumber("333444")).thenReturn(Optional.of(carrieAccount));

        transactionService.performTransfer(requestDTO);

        assertEquals(new BigDecimal("900.00"), dougAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), carrieAccount.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException for insufficient funds")
    void performTransfer_InsufficientFundsScenario() {
        var requestDTO = new TransferRequestDTO(
                "111222", "333444", new BigDecimal("2000.00"));

        when(accountRepository.findByAccountNumber("111222")).thenReturn(Optional.of(dougAccount));
        when(accountRepository.findByAccountNumber("333444")).thenReturn(Optional.of(carrieAccount));

        assertThrows(BusinessRuleException.class, () -> transactionService.performTransfer(requestDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException for self-transfer")
    void performTransfer_SelfTransferScenario() {
        var requestDTO = new TransferRequestDTO(
                "111222", "111222", new BigDecimal("100.00"));

        assertThrows(BusinessRuleException.class, () -> transactionService.performTransfer(requestDTO));
        verify(accountRepository, never()).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when source account does not exist")
    void performTransfer_SourceAccountNotFoundScenario() {
        var requestDTO = new TransferRequestDTO(
                "999999", "333444", new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.performTransfer(requestDTO));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when an account is inactive")
    void performTransfer_InactiveAccountScenario() {
        carrieAccount.setActive(false);
        var requestDTO = new TransferRequestDTO(
                "111222", "333444", new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("111222")).thenReturn(Optional.of(dougAccount));
        when(accountRepository.findByAccountNumber("333444")).thenReturn(Optional.of(carrieAccount));

        assertThrows(BusinessRuleException.class, () -> transactionService.performTransfer(requestDTO));
    }
}
