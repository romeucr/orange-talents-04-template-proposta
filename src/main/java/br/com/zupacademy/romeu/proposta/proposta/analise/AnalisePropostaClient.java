package br.com.zupacademy.romeu.proposta.proposta.analise;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${feign.client.analisaProposta.url}", name = "analisaProposta")
public interface AnalisePropostaClient {

  @PostMapping("/api/solicitacao")
  AnalisePropostaResponse analisaProposta(@RequestBody AnalisePropostaRequest analisePropostaRequest);

}
