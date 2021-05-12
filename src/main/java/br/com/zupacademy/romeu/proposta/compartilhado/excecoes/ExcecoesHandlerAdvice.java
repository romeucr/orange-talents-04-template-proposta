package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExcecoesHandlerAdvice {

  @Autowired
  MessageSource messageSource;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handle(MethodArgumentNotValidException exception) {
    /* não é para retornar nenhum erro, apenas o código 400 BAD REQUEST */
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(EntidadeDuplicadaException.class)
  public ResponseEntity<?> handle(EntidadeDuplicadaException exception) {
    /* não é para retornar nenhum erro, apenas o código 422 UNPROCESSABLE ENTITY */
    return ResponseEntity.unprocessableEntity().build();
  }

}