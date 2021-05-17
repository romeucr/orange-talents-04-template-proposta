package br.com.zupacademy.romeu.proposta.compartilhado;

public class Ofuscadores {

  public static String ofuscaIdDoCartao(String idCartao) {
    char[] idCharArray = idCartao.toCharArray();

    // 2307-5184-7709-6094 formato recebido
    for (int i = 0; i <= idCartao.length(); i++) {
      if (i >= 0 && i <= 3 || i >= 5 && i <= 8 || i >= 10 && i <= 13)
        idCharArray[i] = '*';
    }

    return String.valueOf(idCharArray);
  }
}
