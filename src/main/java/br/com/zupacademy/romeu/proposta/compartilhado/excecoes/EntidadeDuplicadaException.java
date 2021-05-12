package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

public class EntidadeDuplicadaException extends RuntimeException {
  public EntidadeDuplicadaException(String msg) {
    super(msg);
  }
}
