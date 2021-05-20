package br.com.zupacademy.romeu.proposta.cartao.viagem;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ViagemRequest {

  private String numeroDoCartao;

  @NotBlank
  private String destino;

  @NotNull @Future @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
  private LocalDate dataFimViagem;

  public ViagemRequest() {}

  public ViagemRequest(@NotBlank String destino, @NotNull @Future LocalDate dataFimViagem) {
    this.destino = destino;
    this.dataFimViagem = dataFimViagem;
  }

  public String getNumeroDoCartao() {
    return numeroDoCartao;
  }

  public String getDestino() {
    return destino;
  }

  public LocalDate getDataFimViagem() {
    return dataFimViagem;
  }

  public void setNumeroDoCartao(String numeroDoCartao) {
    this.numeroDoCartao = numeroDoCartao;
  }

  public Viagem toModel(String enderecoIp, String userAgent) {
    return new Viagem(numeroDoCartao, destino, dataFimViagem.atStartOfDay(), enderecoIp, userAgent);
  }
}
