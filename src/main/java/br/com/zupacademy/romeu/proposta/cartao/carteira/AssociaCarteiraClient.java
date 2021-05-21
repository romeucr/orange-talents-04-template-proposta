package br.com.zupacademy.romeu.proposta.cartao.carteira;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${feign.client.gerenciaCartao.url}", name = "associaCarteira")
public interface AssociaCarteiraClient {

  @PostMapping("api/cartoes/{id}/carteiras")
  NovaCarteiraResponse associaCartaoCarteira(@RequestParam("id") String numeroCartao,
                                             @RequestBody NovaCarteiraRequest novaCarteiraRequest);
}
