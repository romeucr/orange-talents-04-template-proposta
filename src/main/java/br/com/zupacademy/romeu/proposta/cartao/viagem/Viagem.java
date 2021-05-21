package br.com.zupacademy.romeu.proposta.cartao.viagem;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Viagem {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String numeroDoCartao;

  @NotBlank
  private String destino;

  @Future
  private LocalDateTime dataFimViagem;

  @CreationTimestamp
  private LocalDateTime criadoEm;

  @NotBlank
  private String enderecoIp;

  @NotBlank
  private String userAgentRequisicao;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public Viagem() {}

  public Viagem(@NotBlank String numeroDoCartao, @NotBlank String destino,
                LocalDateTime dataFimViagem, @NotBlank String enderecoIp,
                @NotBlank String userAgentRequisicao) {
    this.numeroDoCartao = numeroDoCartao;
    this.destino = destino;
    this.dataFimViagem = dataFimViagem.plusSeconds(59).plusMinutes(59).plusHours(23);
    this.enderecoIp = enderecoIp;
    this.userAgentRequisicao = userAgentRequisicao;
  }

  public Long getId() {
    return id;
  }
}
