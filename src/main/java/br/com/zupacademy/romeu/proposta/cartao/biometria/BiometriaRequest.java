package br.com.zupacademy.romeu.proposta.cartao.biometria;

import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.ValidFingerprint;
import br.com.zupacademy.romeu.proposta.compartilhado.validacoes.ValorUnico;

public class BiometriaRequest {

  @ValidFingerprint
  @ValorUnico(campo = "fingerprint", tabela = Biometria.class)
  private String fingerprint;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
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
