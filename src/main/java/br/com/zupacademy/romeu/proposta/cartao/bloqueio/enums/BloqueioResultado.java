package br.com.zupacademy.romeu.proposta.cartao.bloqueio.enums;

public enum BloqueioResultado {
  BLOQUEADO(true),
  FALHA(false);

  private final Boolean status;

  BloqueioResultado(Boolean ativo) {
    this.status = ativo;
  }

  public Boolean getStatus() {
    return status;
  }
}
