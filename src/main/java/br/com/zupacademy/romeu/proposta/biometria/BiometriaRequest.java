package br.com.zupacademy.romeu.proposta.biometria;

import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.ValidFingerprint;
import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.ValorUnico;

import javax.validation.constraints.NotBlank;

public class BiometriaRequest {

  @ValidFingerprint
  @ValorUnico(campo = "fingerprint", tabela = Biometria.class)
  private String fingerprint;

  public BiometriaRequest() {}

  public BiometriaRequest(String fingerprint) {
    this.fingerprint = fingerprint;
  }

  public Biometria toModel(){
    return new Biometria(this.fingerprint);
  }

  public String getFingerprint() {
    return fingerprint;
  }

}
