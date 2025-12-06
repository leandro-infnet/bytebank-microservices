package br.com.bytebank.api.controller;

import br.com.bytebank.api.domain.account.AccountCreationDTO;
import br.com.bytebank.api.domain.account.AccountDetailsDTO;
import br.com.bytebank.api.domain.account.AccountUpdateDTO;
import br.com.bytebank.api.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDetailsDTO> createAccount(
            @RequestBody @Valid AccountCreationDTO creationDTO,
            UriComponentsBuilder uriBuilder
            ) {
        var newAccountDetails = accountService.createAccount(creationDTO);
        var uri = uriBuilder
                .path("/accounts/{id}")
                .buildAndExpand(newAccountDetails.id()).toUri();

        return ResponseEntity.created(uri).body(newAccountDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsDTO> getAccountDetails(@PathVariable Long id) {
        var accountDetails = accountService.getAccountById(id);

        return ResponseEntity.ok(accountDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDetailsDTO> updateAccount(
            @PathVariable Long id, @RequestBody @Valid AccountUpdateDTO updateDTO) {
        var updateAccount = accountService.updateAccountStatus(id, updateDTO);

        return ResponseEntity.ok(updateAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }
}
