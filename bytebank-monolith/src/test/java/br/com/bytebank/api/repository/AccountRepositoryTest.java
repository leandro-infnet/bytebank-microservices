package br.com.bytebank.api.repository;

import br.com.bytebank.api.domain.account.Account;
import br.com.bytebank.api.domain.account.AccountType;
import br.com.bytebank.api.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should find an account successfully by its number")
    void findByAccountNumber_SuccessScenario() {
        var user = createUser(
                "Doug Heffernan", "doug@bytebank.com", "123456", "111222333");
        var accountNumber = "123456";
        var account = createAccount(user, accountNumber, "0001", AccountType.CHECKING);

        var foundAccountOptional = accountRepository.findByAccountNumber(accountNumber);

        assertThat(foundAccountOptional).isPresent();
        assertThat(foundAccountOptional.get().getAccountNumber()).isEqualTo(accountNumber);
        assertThat(foundAccountOptional.get().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should not find an account when its number does not exist")
    void findByAccountNumber_FailureScenario() {
        var accountNumber = "999999";

        var foundAccountOptional = accountRepository.findByAccountNumber(accountNumber);

        assertThat(foundAccountOptional).isNotPresent();
    }

    @Test
    @DisplayName("Should find a list of accounts by user ID")
    void findByUserId_SuccessScenario() {
        var carrie = createUser(
                "Carrie Heffernan", "carrie@bytebank.com", "123456", "444555666");
        createAccount(carrie, "111111", "0001", AccountType.CHECKING);
        createAccount(carrie, "222222", "0001", AccountType.SAVINGS);

        var arthur = createUser(
                "Arthur Spooner", "arthur@bytebank.com", "123456", "777888999");
        createAccount(arthur, "333333", "0001", AccountType.CHECKING);

        List<Account> carrieAccounts = accountRepository.findByUserId(carrie.getId());

        assertThat(carrieAccounts).hasSize(2);
    }

    @Test
    @DisplayName("Should return an empty list for a user with no accounts")
    void findByUserId_EmptyScenario() {
        var deacon = createUser(
                "Deacon Palmer", "deacon@bytebank.com", "123456", "123123123");

        List<Account> deaconAccounts = accountRepository.findByUserId(deacon.getId());

        assertThat(deaconAccounts).isNotNull();
        assertThat(deaconAccounts).isEmpty();
    }

    private User createUser(String name, String email, String password, String document) {
        var user = new User(null, name, email, password, document);
        entityManager.persist(user);
        return user;
    }

    private Account createAccount(User user, String accountNumber, String agency, AccountType type) {
        var account = new Account(null, user, agency, accountNumber, BigDecimal.ZERO, type, true);
        entityManager.persist(account);
        return account;
    }
}
