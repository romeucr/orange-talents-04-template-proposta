package br.com.zupacademy.romeu.proposta.cartao.bloqueio;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Bloqueio {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  private LocalDateTime bloqueadoEm;

  @NotBlank
  private String sistemaResponsavel;

  @NotBlank
  private String enderecoIp;

  @NotNull
  private boolean ativo;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public Bloqueio() {}

  public Bloqueio(@NotBlank String sistemaResponsavel, @NotBlank String enderecoIp, @NotNull boolean ativo) {
    this.sistemaResponsavel = sistemaResponsavel;
    this.enderecoIp = enderecoIp;
    this.ativo = ativo;
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getBloqueadoEm() {
    return bloqueadoEm;
  }

  public String getSistemaResponsavel() {
    return sistemaResponsavel;
  }

  public String getEnderecoIp() {
    return enderecoIp;
  }

  public boolean isAtivo() {
    return ativo;
  }

  @Override
  public String toString() {
    return "Bloqueio{" +
            "id=" + id +
            ", bloqueadoEm=" + bloqueadoEm +
            ", sistemaResponsavel='" + sistemaResponsavel + '\'' +
            ", enderecoIp='" + enderecoIp + '\'' +
            ", ativo=" + ativo +
            '}';
  }
}
