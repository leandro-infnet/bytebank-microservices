package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.account.Account;
import br.com.bytebank.api.domain.account.AccountCreationDTO;
import br.com.bytebank.api.domain.account.AccountType;
import br.com.bytebank.api.domain.user.User;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should create an account successfully for an existing user")
    void createAccount_SuccessScenario() {
        var userId = 1L;
        var creationDTO = new AccountCreationDTO(userId, AccountType.CHECKING);
        var user = new User(
                userId, "Doug Heffernan", "doug@bytebank.com", "wW*8uuuu", "123");
        var savedAccount = new Account(
                10L,
                user,
                "0001",
                "123456",
                BigDecimal.ZERO,
                creationDTO.accountType(),
                true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        var accountDetailsDTO = accountService.createAccount(creationDTO);

        assertNotNull(accountDetailsDTO);
        assertEquals(userId, accountDetailsDTO.userId());
        assertEquals(savedAccount.getId(), accountDetailsDTO.id());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        var capturedAccount = accountCaptor.getValue();

        assertEquals(BigDecimal.ZERO, capturedAccount.getBalance());
        assertTrue(capturedAccount.isActive());
        assertEquals(user, capturedAccount.getUser());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating an account for a non-existing user")
    void createAccount_UserNotFoundScenario() {
        var userId = 99L;
        var creationDTO = new AccountCreationDTO(userId, AccountType.CHECKING);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.createAccount(creationDTO));

        verify(accountRepository, never()).save(any(Account.class));
    }
}
