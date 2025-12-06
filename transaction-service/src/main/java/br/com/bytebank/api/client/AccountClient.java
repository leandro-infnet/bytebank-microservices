package br.com.bytebank.api.client;

import br.com.bytebank.api.dto.AccountDetailsDTO;
import br.com.bytebank.api.dto.SaldoUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bytebank-monolith", path = "/accounts")
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDetailsDTO buscarConta(@PathVariable("id") Long id);

    @GetMapping("/search") // Endpoint: /accounts/search?number=1234
    AccountDetailsDTO buscarPorNumero(@RequestParam("number") String accountNumber);

    @PostMapping("/{id}/balance/update")
    void atualizarSaldo(@PathVariable("id") Long id, @RequestBody SaldoUpdateDTO dto);
}
