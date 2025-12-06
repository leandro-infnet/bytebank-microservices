package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.user.User;
import br.com.bytebank.api.domain.user.UserCreationDTO;
import br.com.bytebank.api.exception.BusinessRuleException;
import br.com.bytebank.api.exception.DuplicateResourceException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should create a user successfully")
    void createUser_SuccessScenario() {
        var creationDTO = new UserCreationDTO(
                "Doug Heffernan", "doug@bytebank.com", "123456", "111222333");
        var user = new User(
                1L,
                creationDTO.name(),
                creationDTO.email(),
                "wW*8uuuu",
                creationDTO.documentNumber()
        );

        when(userRepository.findByEmail(creationDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findByDocumentNumber(creationDTO.documentNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(creationDTO.password())).thenReturn("wW*8uuuu");
        when(userRepository.save(any(User.class))).thenReturn(user);

        var userDetailsDTO = userService.createUser(creationDTO);

        assertNotNull(userDetailsDTO);
        assertEquals(creationDTO.name(), userDetailsDTO.name());
        assertEquals(creationDTO.email(), userDetailsDTO.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createUser_DuplicateEmailScenario() {
        var creationDTO = new UserCreationDTO(
                "Doug Heffernan", "doug@bytebank.com", "123456", "111222333");

        when(userRepository.findByEmail(creationDTO.email())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(creationDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_SuccessScenario() {
        var userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when trying to delete a user with accounts")
    void deleteUser_UserWithAccountsScenario() {
        var userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.findByUserId(userId)).thenReturn(Collections.singletonList(mock(
                br.com.bytebank.api.domain.account.Account.class)));

        assertThrows(BusinessRuleException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }
}
