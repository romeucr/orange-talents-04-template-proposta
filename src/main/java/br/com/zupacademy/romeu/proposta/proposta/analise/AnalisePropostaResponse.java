package br.com.zupacademy.romeu.proposta.proposta.analise;

import br.com.zupacademy.romeu.proposta.proposta.analise.enums.AnaliseStatus;

public class AnalisePropostaResponse {
  private String documento;
  private String nome;
  private Long idProposta;
  private AnaliseStatus resultadoSolicitacao;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public AnalisePropostaResponse(){}

  public AnalisePropostaResponse(String documento, String nome, Long idProposta, AnaliseStatus resultadoSolicitacao) {
    this.documento = documento;
    this.nome = nome;
    this.idProposta = idProposta;
    this.resultadoSolicitacao = resultadoSolicitacao;
  }

  public String getDocumento() {
    return documento;
  }

  public String getNome() {
    return nome;
  }

  public Long getIdProposta() {
    return idProposta;
  }

  public AnaliseStatus getResultadoSolicitacao() {
    return resultadoSolicitacao;
  }
}
