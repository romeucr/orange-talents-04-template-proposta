package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaClient;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaRequest;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
public class PropostaController {

  @Autowired
  private PropostaRepository propostaRepository;

  @Autowired
  private AnalisePropostaClient analisePropostaClient;

  private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

  @PostMapping("/propostas")
  @Transactional
  public ResponseEntity<?> novaProposta(@RequestBody @Valid NovaPropostaRequest novaPropostaRequest,
                                        UriComponentsBuilder uriBuilder) throws JsonProcessingException {

    /* A API de análise devolve código 422 UNPROCESSABLE ENTITY quando há restrição na proposta.
     * O Feign lança uma exceção quando recebe um código diferente de 2XX.
     * Capturamos a exceção (para não quebrar o sistema) e lançamos novamente o código.
     * */
    Proposta proposta = novaPropostaRequest.toModel(propostaRepository);

    propostaRepository.save(proposta);
    logger.info("Proposta ID " + proposta.getId() + " criada com sucesso!");

    // faz a consulta de análise
    AnalisePropostaRequest analiseRequest = new AnalisePropostaRequest(
            proposta.getDocumento(), proposta.getNome(), proposta.getId().toString());

    try {
      // resposta da análise
      AnalisePropostaResponse analiseResponse = analisePropostaClient.analisaProposta(analiseRequest);
      logger.info("Proposta ID " + proposta.getId() + " enviada para análise com sucesso!");

      // Verifica se a análise recebida corresponde à proposta e atualiza o status caso positivo
      proposta.verificaAnaliseEAtualizaStatus(analiseResponse);
      logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());

    } catch (FeignException.UnprocessableEntity ex) {
      logger.info("Proposta ID " + proposta.getId() + " enviada para análise com sucesso!");

      // recuperando a resposta de dentro da exceção e transformando em um objeto AnalisePropostaResponse
      String analiseResponseString = ex.contentUTF8();
      AnalisePropostaResponse analiseResponse
              = new ObjectMapper().readValue(analiseResponseString, AnalisePropostaResponse.class);

      // Verifica se a análise recebida corresponde à proposta e atualiza o status caso positivo
      proposta.verificaAnaliseEAtualizaStatus(analiseResponse);
      logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());

      return ResponseEntity.unprocessableEntity().build();
    } catch (FeignException ex) {
      // se o sistema de análise estiver offline, não faz nada.
    }

    URI uri = uriBuilder.path("/propostas/{id}")
            .buildAndExpand(proposta.getId())
            .toUri();

    return ResponseEntity.created(uri).build();
  }

  @GetMapping("/propostas/{id}")
  public ResponseEntity<?> consultaProposta(@PathVariable(name = "id") Long id) {
    Optional<Proposta> optProposta = propostaRepository.findById(id);

    if (optProposta.isPresent()) {
      Proposta proposta = optProposta.get();

      /* Se a proposta é não elegível, não tem cartão */
      if (proposta.getStatus().equals(PropostaStatus.NAO_ELEGIVEL))
        return ResponseEntity.ok().body(new ConsultaPropostaNaoElegivelResponse(proposta));

        return ResponseEntity.ok().body(new ConsultaPropostaResponse(proposta));
    }

    return ResponseEntity.notFound().build();
  }
}
