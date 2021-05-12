package br.com.zupacademy.romeu.proposta.proposta.analise;

import br.com.zupacademy.romeu.proposta.proposta.enums.StatusProposta;

public enum StatusAnalise {

  COM_RESTRICAO {
    @Override
    public StatusProposta defineStatusProposta() {
      return StatusProposta.NAO_ELEGIVEL;
    }
  },

  SEM_RESTRICAO {
    @Override
    public StatusProposta defineStatusProposta() {
      return StatusProposta.ELEGIVEL;
    }
  };

  public abstract StatusProposta defineStatusProposta();
}
