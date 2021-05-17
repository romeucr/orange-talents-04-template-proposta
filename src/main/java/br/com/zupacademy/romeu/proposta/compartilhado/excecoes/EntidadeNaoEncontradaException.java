package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

public class EntidadeNaoEncontradaException extends RuntimeException {

  private String campo;

  public EntidadeNaoEncontradaException(String campo, String msg) {
    super(msg);
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }
}
