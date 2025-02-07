package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.cartao.Cartao;
import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.CPFOuCNPJ;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Proposta {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotBlank
  @CPFOuCNPJ
  @ColumnTransformer(read = "AES_DECRYPT(UNHEX(documento), 'encryption.password')",
          write = "HEX(AES_ENCRYPT(?, 'encryption.password'))")
  private String documento;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String nome;

  @NotBlank
  private String endereco;

  @NotNull
  @Positive
  private BigDecimal salario;

  @Enumerated(EnumType.STRING)
  private PropostaStatus status;

  @OneToOne
  private Cartao cartao;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public Proposta() {
  }

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

  public PropostaStatus getStatus() {
    return status;
  }

  public Cartao getCartao() {
    return cartao;
  }

  public void setCartao(Cartao cartao) {
    this.cartao = cartao;
  }

  public void verificaAnaliseEAtualizaStatus(AnalisePropostaResponse analiseResponse) {
    if (this.id.equals(analiseResponse.getIdProposta()))
      this.status = analiseResponse.getResultadoSolicitacao().defineStatusProposta();
  }

}
