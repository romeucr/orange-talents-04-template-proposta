package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;

import java.math.BigDecimal;

public class ConsultaPropostaNaoElegivelResponse {

  private Long id;
  private String documento;
  private String email;
  private String nome;
  private String endereco;
  private BigDecimal salario;
  private PropostaStatus status;

  public ConsultaPropostaNaoElegivelResponse() {
  }

  public ConsultaPropostaNaoElegivelResponse(Proposta proposta) {
    this.id = proposta.getId();
    this.documento = proposta.getDocumento();
    this.email = proposta.getEmail();
    this.nome = proposta.getNome();
    this.endereco = proposta.getEndereco();
    this.salario = proposta.getSalario();
    this.status = proposta.getStatus();
  }

  public Long getId() {
    return id;
  }

  public String getDocumento() {
    return documento;
  }

  public String getEmail() {
    return email;
  }

  public String getNome() {
    return nome;
  }

  public String getEndereco() {
    return endereco;
  }

  public BigDecimal getSalario() {
    return salario;
  }

  public PropostaStatus getStatus() {
    return status;
  }

}
