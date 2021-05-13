package br.com.zupacademy.romeu.proposta.compartilhado.jobs;

import br.com.zupacademy.romeu.proposta.cartao.Cartao;
import br.com.zupacademy.romeu.proposta.cartao.CartaoRepository;
import br.com.zupacademy.romeu.proposta.cartao.CartaoResponse;
import br.com.zupacademy.romeu.proposta.cartao.VerificaCartaoClient;
import br.com.zupacademy.romeu.proposta.proposta.Proposta;
import br.com.zupacademy.romeu.proposta.proposta.PropostaRepository;
import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;
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

@Component
public class AssignarCartaoProposta {

  @Autowired
  PropostaRepository propostaRepository;

  @Autowired
  CartaoRepository cartaoRepository;

  @Autowired
  VerificaCartaoClient verificaCartaoClient;

  private final Logger logger = LoggerFactory.getLogger(AssignarCartaoProposta.class);

  @Transactional
  @Async
  @Scheduled(fixedDelayString = "${periodicidade.assignarCartaoProposta}")
  public void verificaSeHaCartaoEAssignaProposta() {
    logger.info("=== INICIO JOB: AssignarCartaoProposta ===");
    List<Optional<Proposta>> listaOptPropostas
            = propostaRepository.findByStatusAndCartaoId(PropostaStatus.ELEGIVEL, null);

    // limpando a lista, transformando em List<Proposta> e removendo os null
    List<Proposta> listaPropostas = new ArrayList<>();
    for (Optional<Proposta> p : listaOptPropostas) {
      p.ifPresent(listaPropostas::add);
    }
    logger.info(listaPropostas.size() + " propostas com status ELEGIVEL e com cartões pendentes.");

    /* Para executar somente se houver propostas com cartão pendente */
    if (listaPropostas.size() > 0) {
      // fazendo consulta na api de cartoes e salvando na lista
      List<CartaoResponse> cartoesResponse = new ArrayList<>();
      listaPropostas.stream().parallel()
              .forEach(p -> cartoesResponse.add(verificaCartaoClient.verificaCartao(p.getId())));

      // para cada cartaoResponse, criando um cartao, salvando no banco e atualizando a proposta relativa
      cartoesResponse.forEach(cartaoResp -> {
        Cartao cartao = cartaoResp.toModel(propostaRepository);
        cartaoRepository.save(cartao);
        logger.info("Cartão ID " + cartaoResp.getId() + " gravado com sucesso!");

        Optional<Proposta> optProposta = propostaRepository.findById(Long.parseLong(cartaoResp.getIdProposta()));
        Proposta proposta = optProposta.get();
        proposta.setCartao(cartao);
        propostaRepository.save(proposta);
        logger.info("Proposta ID " + proposta.getId() + " atualizada com sucesso!");
      });
    }
    logger.info("=== FIM JOB: AssignarCartaoProposta ===");
  }
}
