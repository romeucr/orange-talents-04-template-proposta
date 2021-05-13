package br.com.zupacademy.romeu.proposta.compartilhado.jobs;

import br.com.zupacademy.romeu.proposta.proposta.Proposta;
import br.com.zupacademy.romeu.proposta.proposta.PropostaRepository;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaClient;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaRequest;
import br.com.zupacademy.romeu.proposta.proposta.analise.AnalisePropostaResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* No caso de o sistema de análise financeira estar indisponível a Proposta é guardada
 * no banco de dados sem a informação do status (ELEGIVEL ou NAO_ELEGIVEL). Guarda status como null
 * Este job verifica as propostas com status null e tenta fazer a análise.
 **/
@Component
public class RealizarAnaliseProposta {

  @Autowired
  PropostaRepository propostaRepository;

  @Autowired
  private AnalisePropostaClient analisePropostaClient;

  private final Logger logger = LoggerFactory.getLogger(RealizarAnaliseProposta.class);

  @Transactional
  @Async
  @Scheduled(fixedDelayString = "${periodicidade.realizarAnaliseProposta}")
  public void verificaPropostasSemStatusERealizaAnalise() throws JsonProcessingException {
    logger.info("=== INICIO === JOB: RealizarAnaliseProposta ===");

    // recuperando as propostas sem status
    List<Optional<Proposta>> listaOptPropostas
            = propostaRepository.findByStatusAndCartaoId(null, null);

    // limpando a lista, transformando em List<Proposta> e removendo os null
    List<Proposta> listaPropostas = new ArrayList<>();
    for (Optional<Proposta> p : listaOptPropostas) {
      p.ifPresent(listaPropostas::add);
    }

    // criando uma lista de analises pendentes
    List<AnalisePropostaRequest> analisesPendentes = new ArrayList<>();
    listaPropostas.forEach(proposta ->
            analisesPendentes.add(new AnalisePropostaRequest(proposta.getDocumento(),
                    proposta.getNome(), proposta.getId().toString())));

    logger.info(analisesPendentes.size() + " propostas com análise pendente.");

    /* Para executar somente se houver propostas com cartão pendente */
    if (analisesPendentes.size() > 0) {
      List<AnalisePropostaResponse> respostasAnalises = new ArrayList<>();

      try {
        /* criando uma lista de respostas das analises pendentes, fazendo a consulta ao sistema externo
         * e adicionando na lista de respostas */

        analisesPendentes.forEach(analise ->
                respostasAnalises.add(analisePropostaClient.analisaProposta(analise)));
        logger.info(analisesPendentes.size() + " propostas enviadas para análise com sucesso!");

        // atualizando as propostas com as análises recebidas
        listaPropostas.forEach(proposta -> {
          respostasAnalises.forEach(proposta::verificaAnaliseEAtualizaStatus);
          logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());
        });

      } catch (FeignException.UnprocessableEntity ex) {
        System.out.println(ex.contentUTF8());

        // recuperando a resposta de dentro da exceção e transformando em um objeto AnalisePropostaResponse
        String analiseResponseString = ex.contentUTF8();
        AnalisePropostaResponse analiseResponse
                = new ObjectMapper().readValue(analiseResponseString, AnalisePropostaResponse.class);

        // Pega a análise não elegível e atualiza status
        listaPropostas.forEach(proposta -> {
          proposta.verificaAnaliseEAtualizaStatus(analiseResponse);
          logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());
        });

        // atualizando demais propostas
        listaPropostas.forEach(proposta -> {
          // para atualizar as propostas que não sejam a que deu erro
          if (!proposta.getId().equals(analiseResponse.getIdProposta()))
            respostasAnalises.forEach(proposta::verificaAnaliseEAtualizaStatus);
          logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso! Status: " + proposta.getStatus());
        });

      } catch (FeignException ex) {
        logger.warn("Sistema de análise indisponível. Nenhuma proposta foi analisada.");
      }
    }
    logger.info("=== FIM === JOB: RealizarAnaliseProposta ===");
  }
}
