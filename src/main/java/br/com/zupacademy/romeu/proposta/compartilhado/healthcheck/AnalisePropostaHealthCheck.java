package br.com.zupacademy.romeu.proposta.compartilhado.healthcheck;

import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaClient;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaRequest;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AnalisePropostaHealthCheck implements HealthIndicator {

  @Autowired
  private AnalisePropostaClient analisePropostaClient;

  @Override
  public Health health() {

    /* Cria uma requisição de análise null. Ao enviar para análise deve receber um erro 400 BAD REQUEST
     * Se recebe exceção e 400 BAD REQUEST é porque está respondendo, serviço UP
     * Se recebe outra exceção do FEIGN é porque não está respondendo, serviço DOWN
     * */
    try {
      AnalisePropostaRequest analiseRequest = new AnalisePropostaRequest();
      AnalisePropostaResponse analiseResponse = analisePropostaClient.analisaProposta(analiseRequest);
      return null; // nunca vai cair aqui porque lançará uma das exceções

    } catch (FeignException.BadRequest ex) {
      return Health.up().withDetail("Análise de propostas", "UP").build();

    } catch (FeignException ex) {
      return Health.down().withDetail("Análise de propostas", "DOWN").build();
    }

  }
}
