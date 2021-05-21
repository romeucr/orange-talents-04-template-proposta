package br.com.zupacademy.romeu.proposta.cartao;

import java.time.LocalDateTime;

public class CartaoConsultaPropostaResponse {

  private String id;
  private LocalDateTime emitidoEm;
  private Integer limite;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public CartaoConsultaPropostaResponse(){}

  public CartaoConsultaPropostaResponse(Cartao cartao) {
    this.id = cartao.getNumero();
    this.emitidoEm = cartao.getEmitidoEm();
    this.limite = cartao.getLimite();
  }

  public String getId() {
    return id;
  }

  public LocalDateTime getEmitidoEm() {
    return emitidoEm;
  }

  public Integer getLimite() {
    return limite;
  }

}
