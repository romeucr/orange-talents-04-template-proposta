package br.com.zupacademy.romeu.proposta.cartao.bloqueio;

public class BloqueioRequest {

  private String sistemaResponsavel;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public BloqueioRequest() {}

  public BloqueioRequest(String sistemaResponsavel) {
    this.sistemaResponsavel = sistemaResponsavel;
  }

  public String getSistemaResponsavel() {
    return sistemaResponsavel;
  }
}
