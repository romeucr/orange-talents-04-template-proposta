package br.com.zupacademy.romeu.proposta.cartao.carteira;

import br.com.zupacademy.romeu.proposta.cartao.carteira.enums.EmissorCarteira;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NovaCarteiraRequest {

  @Email
  @NotBlank
  private String email;

  @NotNull
  @Enumerated(EnumType.STRING)
  private EmissorCarteira carteira;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public NovaCarteiraRequest() {
  }

  public NovaCarteiraRequest(String email, EmissorCarteira carteira) {
    this.email = email;
    this.carteira = carteira;
  }

  public String getEmail() {
    return email;
  }

  public EmissorCarteira getCarteira() {
    return carteira;
  }

  public Carteira toModel(String carteiraId) {
    return new Carteira(carteiraId, this.email, this.carteira);
  }

}
