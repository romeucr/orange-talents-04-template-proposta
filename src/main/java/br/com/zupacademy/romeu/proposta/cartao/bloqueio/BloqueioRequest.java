package br.com.zupacademy.romeu.proposta.cartao.bloqueio;

public class BloqueioRequest {

  private String sistemaResponsavel;

  public BloqueioRequest() {}

  public BloqueioRequest(String sistemaResponsavel) {
    this.sistemaResponsavel = sistemaResponsavel;
  }

  public String getSistemaResponsavel() {
    return sistemaResponsavel;
  }
}
