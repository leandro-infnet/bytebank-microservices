package br.com.bytebank.api.controller;

import br.com.bytebank.api.domain.transaction.DepositRequestDTO;
import br.com.bytebank.api.domain.transaction.TransactionDetailsDTO;
import br.com.bytebank.api.domain.transaction.TransferRequestDTO;
import br.com.bytebank.api.domain.transaction.WithdrawalRequestDTO;
import br.com.bytebank.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDetailsDTO> deposit(
            @RequestBody @Valid DepositRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        var newTransactionDetails = transactionService.performDeposit(requestDTO);
        var uri = uriBuilder
                .path("/transactions/{id}")
                .buildAndExpand(newTransactionDetails.transactionId()).toUri();
        /*
         * Boa prática REST diz que o header Location deve apontar
         * para endereço do recurso criado ("/transactions/{id}"),
         * não para o endereço da ação que o criou ("/transactions/deposit/{id}").
         * Todas as transações (deposit, withdrawal, transfer)
         * pertencem à mesma coleção de recursos: "/transactions".
         */

        return ResponseEntity.created(uri).body(newTransactionDetails);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionDetailsDTO> withdrawal(
            @RequestBody @Valid WithdrawalRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        var newTransactionDetails = transactionService.performWithdrawal(requestDTO);
        var uri = uriBuilder
                .path("/transactions/{id}")
                .buildAndExpand(newTransactionDetails.transactionId()).toUri();

        return ResponseEntity.created(uri).body(newTransactionDetails);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDetailsDTO> transfer(
            @RequestBody @Valid TransferRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        var newTransactionDetails = transactionService.performTransfer(requestDTO);
        var uri = uriBuilder
                .path("/transactions/{id}")
                .buildAndExpand(newTransactionDetails.transactionId()).toUri();

        return ResponseEntity.created(uri).body(newTransactionDetails);
    }
}
