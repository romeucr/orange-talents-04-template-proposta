package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.CPFOrCNPJ;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Proposta {
  
  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotBlank
  @CPFOrCNPJ
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
  public Proposta(){}

  public Proposta(@NotBlank String documento, @NotBlank @Email String email,
                  @NotBlank String nome, @NotBlank String endereco,
                  @NotBlank BigDecimal salario) {
    this.documento = documento;
    this.email = email;
    this.nome = nome;
    this.endereco = endereco;
    this.salario = salario;
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
}
