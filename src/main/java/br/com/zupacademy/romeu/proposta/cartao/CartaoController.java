package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.biometria.BiometriaRepository;
import br.com.zupacademy.romeu.proposta.biometria.BiometriaRequest;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.EntidadeNaoEncontradaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

@RestController
public class CartaoController {

  @Autowired
  BiometriaRepository biometriaRepository;

  @Autowired
  CartaoRepository cartaoRepository;

  @PostMapping("/cartoes/{cartaoId}/biometria")
  public ResponseEntity<?> adicionaBiometria(@NotNull @PathVariable("cartaoId") Long cartaoId,
                                             @Valid @RequestBody BiometriaRequest biometriaRequest,
                                             UriComponentsBuilder uriBuilder) {

    Biometria biometria = biometriaRequest.toModel();
    biometriaRepository.save(biometria);

    Optional<Cartao> optCartao = cartaoRepository.findById(cartaoId);
    if (optCartao.isEmpty()) {
      biometriaRepository.delete(biometria);
        throw new EntidadeNaoEncontradaException("cartaoId", "O cartão informado não foi encontrado na base de dados");
    }

    Cartao cartao = optCartao.get();
    cartao.adicionaBiometria(biometria);
    cartaoRepository.save(cartao);

    URI uri = uriBuilder.path("/biometrias/{id}")
                        .buildAndExpand(biometria.getId())
                        .toUri();

    return ResponseEntity.created(uri).build();

  }
}
