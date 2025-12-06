package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.account.Account;
import br.com.bytebank.api.domain.account.AccountCreationDTO;
import br.com.bytebank.api.domain.account.AccountDetailsDTO;
import br.com.bytebank.api.domain.account.AccountUpdateDTO;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private static final String DEFAULT_AGENCY = "0001";
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(
            AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AccountDetailsDTO createAccount(AccountCreationDTO creationDTO) {
        // Usuário dono da conta existe?
        var user = userRepository.findById(creationDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + creationDTO.userId()));

        var accountNumber = generateUniqueAccountNumber();
        var initialBalance = BigDecimal.ZERO;
        var isActive = true;

        var newAccount = new Account(
                null,
                user,
                DEFAULT_AGENCY,
                accountNumber,
                initialBalance,
                creationDTO.accountType(),
                isActive);

        var savedAccount = accountRepository.save(newAccount);

        return new AccountDetailsDTO(savedAccount);
    }

    public AccountDetailsDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        return new AccountDetailsDTO(account);
    }

    public List<AccountDetailsDTO> getAccountsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountDetailsDTO::new) // Method Reference substitui lambda
                // .map(account -> new AccountDetailsDTO(account)) // Converter cada account
                // para AccountDetailsDTO
                .toList();
    }

    @Transactional
    public AccountDetailsDTO updateAccountStatus(Long id, AccountUpdateDTO updateDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setActive(updateDTO.isActive());

        return new AccountDetailsDTO(account); // Método com @Transactional salva automaticamente alteração
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        accountRepository.deleteById(id);
    }

    public AccountDetailsDTO getAccountByNumber(String number) {
        var account = accountRepository.findByAccountNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + number));
        return new AccountDetailsDTO(account);
    }

    @Transactional
    public void updateBalance(Long id, BigDecimal amount) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        // @Transactional faz save automático, mas posso forçar se quiser
        accountRepository.save(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do { // Loop para garantir que o número gerado seja único
            int number = 100000 + new Random().nextInt(900000);
            accountNumber = String.valueOf(number);
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }
}
