package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.cartao.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.cartao.bloqueio.Bloqueio;
import br.com.zupacademy.romeu.proposta.cartao.carteira.Carteira;
import br.com.zupacademy.romeu.proposta.cartao.carteira.CarteiraRepository;
import br.com.zupacademy.romeu.proposta.cartao.enums.EstadoCartao;
import br.com.zupacademy.romeu.proposta.cartao.viagem.Viagem;
import br.com.zupacademy.romeu.proposta.compartilhado.ofuscadores.OfuscadorCartao;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.ApiException;
import br.com.zupacademy.romeu.proposta.proposta.Proposta;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

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

  @OneToMany(cascade = CascadeType.ALL)
  private Set<Carteira> carteiras;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
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

  public boolean possuiCarteira(Carteira novaCarteira) {
    for (Carteira cart : carteiras) {
      if (cart.equals(novaCarteira))
        return true;
    }
    return false;
  }

  @Transactional
  public URI validaEAdicionaCarteira(CartaoRepository cartaoRepository, CarteiraRepository carteiraRepository,
                                     Carteira novaCarteira, UriComponentsBuilder uriBuilder, Logger logger) {
    if (this.possuiBloqueioAtivo()) {
      logger.info("Falha ao associar carteira ao cartão " + OfuscadorCartao.ofuscaIdDoCartao(this.numero +
              ". Carteira: " + novaCarteira.getEmissor() + ". Email: " + novaCarteira.getEmail()));
      throw new ApiException(null, "Cartão Bloqueado. Não é possível associar carteira.", BAD_REQUEST);
    }

    if (this.possuiCarteira(novaCarteira)) {
      logger.info("Falha ao associar carteira ao cartão " + OfuscadorCartao.ofuscaIdDoCartao(this.numero +
              ". Carteira: " + novaCarteira.getEmissor() + ". Email: " + novaCarteira.getEmail()));
      throw new ApiException(null, "O cartão informado já possui uma carteira " +
              novaCarteira.getEmissor() + " associada.", UNPROCESSABLE_ENTITY);
    }

    carteiraRepository.save(novaCarteira);
    this.carteiras.add(novaCarteira);
    cartaoRepository.save(this);
    logger.info("Carteira associada com sucesso! Cartão: " + OfuscadorCartao.ofuscaIdDoCartao(this.numero +
            ". Carteira: " + novaCarteira.getEmissor() + ". Email: " + novaCarteira.getEmail()));

    Map<String, String> cartaoCarteiraIds = new HashMap<>();
    cartaoCarteiraIds.put("cartaoId", this.numero);
    cartaoCarteiraIds.put("carteiraId", novaCarteira.getId().toString());

    return uriBuilder.path("/cartoes/{cartaoId}/carteiras/{carteiraId}")
                     .buildAndExpand(cartaoCarteiraIds)
                     .toUri();
  }
}
