package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.proposta.Proposta;
import br.com.zupacademy.romeu.proposta.proposta.PropostaRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

public class CartaoResponse {

  @NotNull
  private String id;

  @NotNull
  private LocalDateTime emitidoEm;

  @NotNull
  private Integer limite;

  @NotNull
  private String idProposta;

  public String getId() {
    return id;
  }

  public LocalDateTime getEmitidoEm() {
    return emitidoEm;
  }

  public Integer getLimite() {
    return limite;
  }

  public String getIdProposta() {
    return idProposta;
  }

  public Cartao toModel(PropostaRepository propostaRepository) {
    Optional<Proposta> optProposta = propostaRepository.findById(Long.parseLong(this.idProposta));
    Proposta proposta = optProposta.get();
    return new Cartao(this.id, this.emitidoEm, this.limite, proposta);
  }

}
