package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.cartao.CartaoConsultaPropostaResponse;
import br.com.zupacademy.romeu.proposta.cartao.CartaoResponse;
import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;

import java.math.BigDecimal;

public class ConsultaPropostaResponse {

  private Long id;
  private String documento;
  private String email;
  private String nome;
  private String endereco;
  private BigDecimal salario;
  private PropostaStatus status;
  private CartaoConsultaPropostaResponse cartao;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public ConsultaPropostaResponse() {
  }

  public ConsultaPropostaResponse(Proposta proposta) {
    this.id = proposta.getId();
    this.documento = proposta.getDocumento();
    this.email = proposta.getEmail();
    this.nome = proposta.getNome();
    this.endereco = proposta.getEndereco();
    this.salario = proposta.getSalario();
    this.status = proposta.getStatus();

    /* A proposta pode estar ELEGIVEL porém o cartão ainda não foi associado.
     * Nesse caso, mostrar cartão como nulo */
    if(proposta.getCartao() == null) {
      this.cartao = null;
    } else {
      this.cartao = new CartaoConsultaPropostaResponse(proposta.getCartao());
    }

  }

  public Long getId() {
    return id;
  }

  public String getDocumento() {
    return documento;
  }

  public String getEmail() {
    return email;
  }

  public String getNome() {
    return nome;
  }

  public String getEndereco() {
    return endereco;
  }

  public BigDecimal getSalario() {
    return salario;
  }

  public PropostaStatus getStatus() {
    return status;
  }

  public CartaoConsultaPropostaResponse getCartao() {
    return cartao;
  }
}
