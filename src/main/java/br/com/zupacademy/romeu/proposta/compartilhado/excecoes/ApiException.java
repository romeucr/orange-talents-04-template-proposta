package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
  private String campo;
  private HttpStatus httpStatus;

  public ApiException(String campo, String msg, HttpStatus httpStatus) {
    super(msg);
    this.campo = campo;
    this.httpStatus = httpStatus;
  }

  public String getCampo() {
    return campo;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
