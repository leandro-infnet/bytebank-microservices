package br.com.bytebank.api.service;

import br.com.bytebank.api.domain.user.User;
import br.com.bytebank.api.domain.user.UserCreationDTO;
import br.com.bytebank.api.domain.user.UserDetailsDTO;
import br.com.bytebank.api.domain.user.UserUpdateDTO;
import br.com.bytebank.api.exception.BusinessRuleException;
import br.com.bytebank.api.exception.DuplicateResourceException;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.repository.AccountRepository;
import br.com.bytebank.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDetailsDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserDetailsDTO::new)
                .toList();
    }

    @Transactional
    public UserDetailsDTO createUser(UserCreationDTO creationDTO) {

        String normalizedEmail = creationDTO.email().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new DuplicateResourceException("Email already in use: " + normalizedEmail);
        }

        if (userRepository.findByDocumentNumber(creationDTO.documentNumber()).isPresent()) {
            throw new DuplicateResourceException(
                    "Document number already in use: " + creationDTO.documentNumber());
        }

        var encryptedPassword = passwordEncoder.encode(creationDTO.password());

        var user = new User(
                null,
                creationDTO.name(),
                normalizedEmail,
                encryptedPassword,
                creationDTO.documentNumber()
        );

        var savedUser = userRepository.save(user);

        return new UserDetailsDTO(savedUser);
    }

    public UserDetailsDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return new UserDetailsDTO(user);
    }

    public UserDetailsDTO getUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));

        return new UserDetailsDTO(user);
    }

    @Transactional
    public UserDetailsDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Valida e atualiza e-mail, se foi fornecido
        if (updateDTO.email() != null) {
            String normalizedEmail = updateDTO.email().toLowerCase();

            // Busca se já há algum usuário com novo e-mail
            Optional<User> userWithSameEmail = userRepository.findByEmail(normalizedEmail);

            // Valida se e-mail já existe E pertence a usuário diferente
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new DuplicateResourceException("Email already in use: " + normalizedEmail);
            }
            userToUpdate.setEmail(normalizedEmail);
        }

        if (updateDTO.name() != null) {
            userToUpdate.setName(updateDTO.name());
        }

        /*
         * @Transactional já garante que a alteração será salva ao final do método.
         * JPA detecta a alteração no objeto 'user' e persiste no banco.
         * Daí não haver necessidade de chamar userRepository.save(user) aqui.
         */

        return new UserDetailsDTO(userToUpdate);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }

        if (!accountRepository.findByUserId(id).isEmpty()) {
            throw new BusinessRuleException("User cannot be deleted because they have associated accounts.");
        }

        /*
         * @Transactional garante que a exclusão será gerenciada como transação segura no db.
         */

        userRepository.deleteById(id);
    }
}
