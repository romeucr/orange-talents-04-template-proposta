package br.com.zupacademy.romeu.proposta.cartao.bloqueio;

import br.com.zupacademy.romeu.proposta.cartao.CartaoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(url = "${feign.client.gerenciaCartao.url}", name = "bloqueiaCartao")
public interface BloqueiaCartaoClient {

  @PostMapping("/api/cartoes/{id}/bloqueios")
  BloqueioResponse bloqueiaCartao(@PathVariable("id") String numeroCartao,
                                @RequestBody BloqueioRequest bloqueioRequest);
}