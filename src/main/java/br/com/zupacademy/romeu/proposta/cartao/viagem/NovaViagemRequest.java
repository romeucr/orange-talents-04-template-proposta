package br.com.zupacademy.romeu.proposta.cartao.viagem;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class NovaViagemRequest {

  private String numeroDoCartao;

  @NotBlank
  private String destino;

  @NotNull @Future @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
  private LocalDate validoAte;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public NovaViagemRequest() {}

  public NovaViagemRequest(@NotBlank String destino, @NotNull @Future LocalDate validoAte) {
    this.destino = destino;
    this.validoAte = validoAte;
  }

  public String getNumeroDoCartao() {
    return numeroDoCartao;
  }

  public String getDestino() {
    return destino;
  }

  public LocalDate getValidoAte() {
    return validoAte;
  }

  public void setNumeroDoCartao(String numeroDoCartao) {
    this.numeroDoCartao = numeroDoCartao;
  }

  public Viagem toModel(String enderecoIp, String userAgent) {
    // .atStartOfDay() para converter em LocalDateTime
    return new Viagem(numeroDoCartao, destino, validoAte.atStartOfDay(), enderecoIp, userAgent);
  }
}
