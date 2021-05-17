package br.com.zupacademy.romeu.proposta.compartilhado.excecoes;

public class ErroPadrao {
  private String campo;
  private String erro;

  public ErroPadrao(String campo, String erro) {
    this.campo = campo;
    this.erro = erro;
  }

  public String getCampo() {
    return campo;
  }

  public String getErro() {
    return erro;
  }
}
