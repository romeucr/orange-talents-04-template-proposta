package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaClient;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaRequest;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import br.com.zupacademy.romeu.proposta.proposta.enums.StatusProposta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  @Autowired
  private AnalisePropostaClient analisePropostaClient;

  private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

  @PostMapping
  @Transactional
  public ResponseEntity<?> novaProposta(@RequestBody @Valid NovaPropostaRequest novaPropostaRequest,
                                        UriComponentsBuilder uriBuilder) throws JsonProcessingException {

    Proposta proposta = novaPropostaRequest.toModel(propostaRepository);
    propostaRepository.save(proposta);
    logger.info("Proposta ID " + proposta.getId() + " criada com sucesso!");

    /* A API de análise devolve código 422 UNPROCESSABLE ENTITY quando há restrição na proposta.
     * O Feign lança uma exceção quando recebe um código diferente de 2XX.
     * Capturamos a exceção (para não quebrar o sistema) e lançamos novamente o código.
     * */

    try {
      // faz a consulta de análise
      AnalisePropostaRequest analiseRequest = new AnalisePropostaRequest(
              proposta.getDocumento(), proposta.getNome(), proposta.getId().toString());

      // resposta da análise
      AnalisePropostaResponse analiseResponse = analisePropostaClient.analisaProposta(analiseRequest);
      logger.info("Proposta ID " + proposta.getId() + " enviada para análise com sucesso!");

      // Verifica se a análise recebida corresponde à proposta e atualiza o status caso positivo
      proposta.verificaAnaliseEAtualizaStatus(analiseResponse);
      logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());

      URI uri = uriBuilder.path("/propostas/{id}")
              .buildAndExpand(proposta.getId())
              .toUri();

      return ResponseEntity.created(uri).build();

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
    }
  }
}
