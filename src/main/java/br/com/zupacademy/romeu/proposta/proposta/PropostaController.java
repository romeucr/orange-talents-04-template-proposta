package br.com.zupacademy.romeu.proposta.proposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

  @Autowired
  private PropostaRepository propostaRepository;

  @PostMapping
  @Transactional
  public ResponseEntity<?> novaProposta(@RequestBody @Valid NovaPropostaRequest novaPropostaRequest,
                                        UriComponentsBuilder uriBuilder) {

    Proposta proposta = novaPropostaRequest.toModel();
    propostaRepository.save(proposta);

    URI uri = uriBuilder.path("/propostas/{id}")
                        .buildAndExpand(proposta.getId())
                        .toUri();

    return ResponseEntity.created(uri).build();
  }
}
