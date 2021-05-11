package br.com.zupacademy.romeu.proposta.proposta.analise;

public class AnalisePropostaRequest {

  private String documento;
  private String nome;
  private String idProposta;

  public AnalisePropostaRequest(){}

  public AnalisePropostaRequest(String documento, String nome, String idProposta) {
    this.documento = documento;
    this.nome = nome;
    this.idProposta = idProposta;
  }

  public String getDocumento() {
    return documento;
  }

  public String getNome() {
    return nome;
  }

  public String getIdProposta() {
    return idProposta;
  }
}
