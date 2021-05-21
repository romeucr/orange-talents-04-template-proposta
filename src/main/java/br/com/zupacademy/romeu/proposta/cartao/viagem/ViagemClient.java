package br.com.zupacademy.romeu.proposta.cartao.viagem;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${feign.client.gerenciaCartao.url}", name = "novoAvisoViagem")
public interface ViagemClient {

  @PostMapping("/api/cartoes/{id}/avisos")
  NovaViagemResponse novoAvisoViagem(@PathVariable("id") String numeroCartao,
                                     @RequestBody NovaViagemRequest novaViagemRequest);

}