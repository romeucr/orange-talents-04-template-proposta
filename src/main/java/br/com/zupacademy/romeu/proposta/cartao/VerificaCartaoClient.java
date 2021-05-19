package br.com.zupacademy.romeu.proposta.cartao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${feign.client.gerenciaCartao.url}", name = "verificaCartao")
public interface VerificaCartaoClient {

  @GetMapping("/api/cartoes")
  CartaoResponse verificaCartao(@RequestParam("idProposta") Long idProposta);
}