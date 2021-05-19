package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.cartao.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.romeu.proposta.cartao.enums.EstadoCartao;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.EntidadeDuplicadaException;
import br.com.zupacademy.romeu.proposta.proposta.Proposta;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class Cartao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  @OneToMany
  private Set<Biometria> biometria;

  @OneToMany(cascade = CascadeType.ALL)
  private List<Bloqueio> bloqueios;

  @NotNull
  @Enumerated(EnumType.STRING)
  private EstadoCartao estado = EstadoCartao.ATIVADO;

  /* @deprecated para uso do hibernate
   * */
  public Cartao() {
  }

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

  public Set<Biometria> getBiometria() {
    return biometria;
  }

  public List<Bloqueio> getBloqueios() {
    return bloqueios;
  }

  public void adicionaBloqueio(Bloqueio bloqueio) {
    this.estado = EstadoCartao.BLOQUEADO;
    this.bloqueios.add(bloqueio);
  }

  public void adicionaBiometria(Biometria biometria) {
    if (this.biometria.contains(biometria)) {
      throw new EntidadeDuplicadaException("Biometria:", "A biometria informada já está cadastrada no cartão informado");
    }
    this.biometria.add(biometria);
  }

  public boolean possuiBloqueioAtivo() {
    if (bloqueios.isEmpty())
      return false;

    int totalBloqueios = bloqueios.size();
    Bloqueio ultimoBloqueio = bloqueios.get(totalBloqueios - 1);

    return ultimoBloqueio.isAtivo();
  }

  public boolean solicitanteEDonoDoCartao(String emailUsuarioAutenticado) {
    return this.proposta.getEmail().equals(emailUsuarioAutenticado);
  }
}
