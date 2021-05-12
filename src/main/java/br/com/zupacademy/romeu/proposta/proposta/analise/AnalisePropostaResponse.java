package br.com.zupacademy.romeu.proposta.proposta.analise;

public class AnalisePropostaResponse {
  private String documento;
  private String nome;
  private Long idProposta;
  private StatusAnalise resultadoSolicitacao;

  public AnalisePropostaResponse(){}

  public AnalisePropostaResponse(String documento, String nome, Long idProposta, StatusAnalise resultadoSolicitacao) {
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

  public StatusAnalise getResultadoSolicitacao() {
    return resultadoSolicitacao;
  }
}
