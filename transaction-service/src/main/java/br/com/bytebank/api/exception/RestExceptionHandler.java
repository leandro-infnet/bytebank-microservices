package br.com.bytebank.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice  // torna esta classe a "supervisora" global de exceções
public class RestExceptionHandler {

    public record ErrorResponseDTO(String message, HttpStatus status, LocalDateTime timestamp) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        var errorResponse = new ErrorResponseDTO(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus. NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResource(DuplicateResourceException ex) {
        var errorResponse = new ErrorResponseDTO(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRuleException(BusinessRuleException ex) {
        var errorResponse = new ErrorResponseDTO(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /*
     * @ExceptionHandler(MethodArgumentNotValidException.class)
     * Define que este método é o especialista para tratar exceções do tipo
     * MethodArgumentNotValidException. Ele é chamado quando o @Valid falha.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(  //Coleta todos os itens processados em um Map (objeto chave-valor)
                        error -> ((FieldError) error).getField(),  // Chave (nome do campo que deu erro)
                        error -> error.getDefaultMessage()  // Valor (captura mensagem de erro que defini no DTO)
                ));

        return ResponseEntity.badRequest().body(errors);  // Retorno código 400 com o Map de erros no corpo da resposta
        /*
         * Resumo do Fluxo
         *
         * @Valid falha
         * -> Spring lança MethodArgumentNotValidException
         * -> Nosso @ExceptionHandler captura
         * -> Ele extrai cada erro de campo e mensagem
         * -> Monta um Map simples com {"campo": "mensagem"}
         * -> Devolve o mapa como um JSON com status 400.
         */
    }
}
