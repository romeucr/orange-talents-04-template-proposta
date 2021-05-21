package br.com.zupacademy.romeu.proposta.cartao.carteira;

import br.com.zupacademy.romeu.proposta.cartao.carteira.enums.ResultadoCarteira;

public class NovaCarteiraResponse {
  private String id;
  private ResultadoCarteira resultado;

  public String getId() {
    return id;
  }

  public ResultadoCarteira getResultado() {
    return resultado;
  }
}
