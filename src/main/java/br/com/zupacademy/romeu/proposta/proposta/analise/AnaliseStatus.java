package br.com.zupacademy.romeu.proposta.proposta.analise;

import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;

public enum AnaliseStatus {

  COM_RESTRICAO {
    @Override
    public PropostaStatus defineStatusProposta() {
      return PropostaStatus.NAO_ELEGIVEL;
    }
  },

  SEM_RESTRICAO {
    @Override
    public PropostaStatus defineStatusProposta() {
      return PropostaStatus.ELEGIVEL;
    }
  };

  public abstract PropostaStatus defineStatusProposta();
}
