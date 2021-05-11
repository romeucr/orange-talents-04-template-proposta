package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaClient;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaRequest;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import br.com.zupacademy.romeu.proposta.proposta.enums.StatusProposta;
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
                                        UriComponentsBuilder uriBuilder) {

    Proposta proposta = novaPropostaRequest.toModel(propostaRepository);
    propostaRepository.save(proposta);
    logger.info("Proposta ID " + proposta.getId() + " criada com sucesso!");

    /* A API de análise devolve código 422 UNPROCESSABLE ENTITY quando há restrição na proposta.
     * O Feign lança uma exceção quando recebe um código diferente de 2XX.
     * Capturamos a exceção e lançamos novamente o código.
     * Se lançar a exceção sabemos que o status da proposta é NAO_ELEGIVEL.
     * SE NÃO lançar a exceção o status é ELEGIVEL.
     * Está atualizando o status por setStatus porque não consegui recuperar o objeto AnalisePropostaResponse
     * quando o Feign lança a exceção. Uma opção seria usar a biblioteca GSON ou Jackson do Google para converter o
     * a String JSON em um objeto da classe AnalisePropostaResponse */

    try {
      // faz a consulta de análise
      AnalisePropostaRequest analiseRequest = new AnalisePropostaRequest(
              proposta.getDocumento(), proposta.getNome(), proposta.getId().toString());

      // resposta da análise
      AnalisePropostaResponse analiseResponse = analisePropostaClient.analisaProposta(analiseRequest);
      logger.info("Proposta ID " + proposta.getId() + " enviada para análise com sucesso!");

      // atualiza status
      proposta.setStatus(StatusProposta.ELEGIVEL);
      logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());

      URI uri = uriBuilder.path("/propostas/{id}")
              .buildAndExpand(proposta.getId())
              .toUri();

      return ResponseEntity.created(uri).build();

    } catch (FeignException.UnprocessableEntity ex) {
      logger.info("Proposta ID " + proposta.getId() + " enviada para análise com sucesso!");
      proposta.setStatus(StatusProposta.NAO_ELEGIVEL);
      logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());

      return ResponseEntity.unprocessableEntity().build();
    }
  }
}
