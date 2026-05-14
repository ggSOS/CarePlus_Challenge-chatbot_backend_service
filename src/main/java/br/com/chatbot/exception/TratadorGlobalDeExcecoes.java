package br.com.chatbot.exception;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.chatbot.exception.type.loginauth.TokenGenerationException;
import br.com.chatbot.exception.type.loginauth.TokenValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "br.com.chatbot.adapter.in.controller")@Order(Ordered.LOWEST_PRECEDENCE)
public class TratadorGlobalDeExcecoes {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarNotFound() {
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>> tratarBadRequest(
            HttpServletRequest request,
            MethodArgumentNotValidException ex) throws MethodArgumentNotValidException {

        String path = request.getRequestURI();

        if (path.contains("/v3/api-docs")) {
            throw ex; // deixa o Spring tratar
        }

        List<FieldError> erros = ex.getFieldErrors();
        return ResponseEntity
                .badRequest()
                .body(erros
                        .stream()
                        .map(DadosBadRequest::new)
                        .toList());
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<String> tratarErroToken(TokenGenerationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<String> tratarTokenInvalido(TokenValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    private record DadosBadRequest(
            String campo,
            String mensagem) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
