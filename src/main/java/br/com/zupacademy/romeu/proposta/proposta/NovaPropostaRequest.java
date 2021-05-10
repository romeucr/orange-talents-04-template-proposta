package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.CPFOuCNPJ;
import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.EntidadeDuplicadaException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Optional;

public class NovaPropostaRequest {

  @NotBlank @CPFOuCNPJ
  private String documento;

  @NotBlank @Email
  private String email;

  @NotBlank
  private String nome;

  @NotBlank
  private String endereco;

  @NotNull @Positive
  private BigDecimal salario;

  /**
   * @deprecated pra uso do hibernate somente
   */
  public NovaPropostaRequest(){}

  public NovaPropostaRequest(@NotBlank String documento, @NotBlank @Email String email,
                             @NotBlank String nome, @NotBlank String endereco,
                             @NotBlank BigDecimal salario) {
    this.documento = documento;
    this.email = email;
    this.nome = nome;
    this.endereco = endereco;
    this.salario = salario;
  }

  public Proposta toModel(PropostaRepository propostaRepository) {
    Optional<Proposta> optProposta = propostaRepository.findByDocumento(this.documento);
    if (optProposta.isPresent())
      throw new EntidadeDuplicadaException("Já existe proposta para este documento cadastrada na base de dados");

    return new Proposta(documento, email, nome, endereco, salario);
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
}
