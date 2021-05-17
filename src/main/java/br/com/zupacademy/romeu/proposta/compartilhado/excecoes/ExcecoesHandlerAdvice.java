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

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExcecoesHandlerAdvice {

  @Autowired
  MessageSource messageSource;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handle(MethodArgumentNotValidException exception) {
    List<ErroPadrao> listaDeErros = new ArrayList<>();
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    fieldErrors.forEach(e -> {
      String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
      ErroPadrao erro = new ErroPadrao(e.getField(), mensagem);
      listaDeErros.add(erro);
    });

    return ResponseEntity.badRequest().body(listaDeErros);
  }

  @ExceptionHandler(EntidadeDuplicadaException.class)
  public ResponseEntity<?> handle(EntidadeDuplicadaException exception) {
    ErroPadrao erro = new ErroPadrao(exception.getCampo(), exception.getMessage());
    return ResponseEntity.unprocessableEntity().body(erro);
  }

  @ExceptionHandler(AnalisePropostaException.class)
  public ResponseEntity<?> handle(AnalisePropostaException exception) {
    /* não é para retornar nenhum erro, apenas o código 422 UNPROCESSABLE ENTITY */
    return ResponseEntity.unprocessableEntity().build();
  }

  @ExceptionHandler(EntidadeNaoEncontradaException.class)
  public ResponseEntity<?> handle(EntidadeNaoEncontradaException exception) {
    ErroPadrao erro = new ErroPadrao(exception.getCampo(), exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
  }

}