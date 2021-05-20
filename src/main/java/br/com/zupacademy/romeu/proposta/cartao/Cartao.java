package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.cartao.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.romeu.proposta.cartao.enums.EstadoCartao;
import br.com.zupacademy.romeu.proposta.cartao.viagem.Viagem;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.ApiException;
import br.com.zupacademy.romeu.proposta.proposta.Proposta;
import org.springframework.http.HttpStatus;

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

  @OneToMany(cascade = CascadeType.ALL)
  private List<Viagem> viagens;

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

  public List<Viagem> getViagens() {
    return viagens;
  }

  public void adicionaBloqueio(Bloqueio bloqueio) {
    this.estado = EstadoCartao.BLOQUEADO;
    this.bloqueios.add(bloqueio);
  }

  public void adicionaBiometria(Biometria biometria) {
    if (this.biometria.contains(biometria)) {
      throw new ApiException("Biometria:",
                             "A biometria informada já está cadastrada no cartão informado",
                              HttpStatus.UNPROCESSABLE_ENTITY);
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

  public void adicionaViagem(Viagem viagem) {
    this.viagens.add(viagem);
  }

}
