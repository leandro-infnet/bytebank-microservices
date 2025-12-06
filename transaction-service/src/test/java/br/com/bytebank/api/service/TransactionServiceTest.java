package br.com.bytebank.api.service;

import br.com.bytebank.api.client.AccountClient;
import br.com.bytebank.api.domain.transaction.Transaction;
import br.com.bytebank.api.domain.transaction.DepositRequestDTO;
import br.com.bytebank.api.dto.AccountDetailsDTO;
import br.com.bytebank.api.dto.SaldoUpdateDTO;
import br.com.bytebank.api.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private TransactionService service;

    @Test
    @DisplayName("Deve realizar depósito com sucesso quando a conta existe e está ativa")
    void performDepositSuccess() {
        String numeroConta = "1234-5";
        BigDecimal valorDeposito = new BigDecimal("100.00");
        BigDecimal saldoAtual = new BigDecimal("50.00");

        DepositRequestDTO request = new DepositRequestDTO(numeroConta, valorDeposito);

        AccountDetailsDTO contaSimulada = new AccountDetailsDTO(
                1L,
                numeroConta,
                saldoAtual,
                true // Ativa
        );

        when(accountClient.buscarPorNumero(numeroConta)).thenReturn(contaSimulada);

        when(repository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        var resultado = service.performDeposit(request);

        assertEquals(new BigDecimal("150.00"), resultado.finalBalance());

        verify(accountClient, times(1)).atualizarSaldo(eq(1L), any(SaldoUpdateDTO.class));
    }
}
