package br.com.bytebank.api.dto;

import java.math.BigDecimal;

/**
 * DTO responsável por transportar apenas o valor da alteração do saldo.
 * Se o valor for positivo, será um crédito.
 * Se for negativo, será um débito (o Service manipula o sinal).
 */
public record SaldoUpdateDTO(
        BigDecimal amount) {
}
