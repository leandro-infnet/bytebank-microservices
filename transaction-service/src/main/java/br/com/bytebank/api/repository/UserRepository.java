package br.com.bytebank.api.repository;

import br.com.bytebank.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA entende "findByEmail" e cria a query: "SELECT u FROM User u WHERE u.email = ?"
    Optional<User> findByEmail(String email);

    Optional<User> findByDocumentNumber(String documentNumber);
}
