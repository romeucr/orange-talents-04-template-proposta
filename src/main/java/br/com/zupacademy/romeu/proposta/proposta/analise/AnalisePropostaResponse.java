package br.com.zupacademy.romeu.proposta.proposta.analise;

public class AnalisePropostaResponse {
  private String documento;
  private String nome;
  private Long idProposta;
  private AnaliseStatus resultadoSolicitacao;

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
