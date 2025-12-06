package br.com.bytebank.api.repository;

import br.com.bytebank.api.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should find a user successfully by email from the database")
    void findByEmail_SuccessScenario() {
        // Arrange: Preparar o cenário
        var email = "carrie@bytebank.com";
        var user = new User(null, "Carrie", email, "wW*8uuuu", "111.111.111-11");
        entityManager.persistAndFlush(user);

        // Act: Executar a ação que queremos testar
        var foundUserOptional = userRepository.findByEmail(email);

        // Assert: Verificar se o resultado é o esperado
        assertThat(foundUserOptional).isPresent();
        assertThat(foundUserOptional.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should not find a user when email does not exist")
    void findByEmail_FailureScenario() {
        var email = "doug@bytebank.com";

        var foundUserOptional = userRepository.findByEmail(email);

        assertThat(foundUserOptional).isNotPresent();
    }
}
