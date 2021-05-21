package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @Autowired
  MessageSource messageSource;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<?> handle(MethodArgumentNotValidException exception) {
    List<ErroPadrao> listaDeErros = new ArrayList<>();
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    fieldErrors.forEach(e -> {
      String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
      ErroPadrao erro = new ErroPadrao(e.getField(), mensagem);
      listaDeErros.add(erro);
    });

    return ResponseEntity.badRequest().body(listaDeErros);
  }

  @ExceptionHandler(ApiException.class)
  protected ResponseEntity<?> handle(ApiException exception) {
    ErroPadrao erro = new ErroPadrao(exception.getCampo(), exception.getMessage());
    HttpStatus httpStatus = exception.getHttpStatus();
    return ResponseEntity.status(httpStatus).body(erro);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<?> handle(ConstraintViolationException exception) {
    List<ErroPadrao> erros = new ArrayList<>();
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();

    constraintViolations.forEach(erro -> {
      String campo = erro.getPropertyPath().toString();
      for (int i = 0; i < campo.length(); i++) {
        if (campo.charAt(i) == '.') {
          campo = campo.substring(i + 1);
          break;
        }
      }
      erros.add(new ErroPadrao(campo, erro.getMessage()));
    });

    return ResponseEntity.badRequest().body(erros);
  }

}