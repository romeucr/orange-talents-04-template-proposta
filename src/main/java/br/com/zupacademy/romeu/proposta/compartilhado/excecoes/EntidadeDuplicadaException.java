package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

public class EntidadeDuplicadaException extends RuntimeException {

  private String campo;

  public EntidadeDuplicadaException(String campo, String msg) {
    super(msg);
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }
}
