package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.proposta.Proposta;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Cartao {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String numero;

  @NotNull
  private LocalDateTime emitidoEm;

  @NotNull
  private Integer limite;

  @NotNull
  @OneToOne(mappedBy = "cartao")
  private Proposta proposta;

  /* @deprecated para uso do hibernate
   * */
  public Cartao(){}

  public Cartao(String numero, LocalDateTime emitidoEm, Integer limite, Proposta proposta) {
    this.numero = numero;
    this.emitidoEm = emitidoEm;
    this.limite = limite;
    this.proposta = proposta;
  }

  public Long getId() {
    return id;
  }

  public String getNumero() {
    return numero;
  }

  public LocalDateTime getEmitidoEm() {
    return emitidoEm;
  }

  public Integer getLimite() {
    return limite;
  }

  public Proposta getProposta() {
    return proposta;
  }
}
