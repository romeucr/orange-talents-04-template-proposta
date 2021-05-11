package br.com.zupacademy.romeu.proposta.proposta.analise;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "${feign.client.analisaProposta.url}", name = "analisaProposta")
public interface AnalisePropostaClient {

  @GetMapping("/api/solicitacao")
  AnalisePropostaResponse analisaProposta(AnalisePropostaRequest analisePropostaRequest);
}
