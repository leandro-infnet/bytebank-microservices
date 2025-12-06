package br.com.bytebank.api.dto;

import java.math.BigDecimal;

/**
 * DTO que mapeia a resposta da API do Monólito.
 * Contém apenas os dados necessários para validar uma transação.
 */
public record AccountDetailsDTO(
        Long id,
        String accountNumber,
        BigDecimal balance,
        Boolean isActive // Usamos Wrapper Boolean para evitar NullPointerException se o JSON vier nulo
) {
    // Opcional: Se o JSON do monólito retornar "active" em vez de "isActive",
    // o Jackson (biblioteca do Spring) pode precisar de ajuda ou de anotação
    // @JsonProperty("is_active")
    // Mas se o padrão for Java (camelCase), este record funciona nativamente.
}
