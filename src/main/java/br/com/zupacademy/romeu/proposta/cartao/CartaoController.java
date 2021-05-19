package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.cartao.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.cartao.biometria.BiometriaRepository;
import br.com.zupacademy.romeu.proposta.cartao.biometria.BiometriaRequest;
import br.com.zupacademy.romeu.proposta.cartao.bloqueio.*;
import br.com.zupacademy.romeu.proposta.compartilhado.Ofuscadores;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.EntidadeNaoEncontradaException;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.ApiException;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.ErroPadrao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

@RestController
public class CartaoController {

  @Autowired
  private BiometriaRepository biometriaRepository;

  @Autowired
  private CartaoRepository cartaoRepository;

  @Autowired
  private BloqueioRepository bloqueioRepository;

  @Autowired
  private BloqueiaCartaoClient bloqueiaCartaoClient;

  private final Logger logger = LoggerFactory.getLogger(CartaoController.class);


  /* ==============================
   * INSERIR BIOMETRIA NO CARTÃO
   * ==============================
   **/
  @PostMapping("/cartoes/{cartaoId}/biometria")
  public ResponseEntity<?> adicionaBiometria(@NotNull @PathVariable("cartaoId") Long cartaoId,
                                             @Valid @RequestBody BiometriaRequest biometriaRequest,
                                             UriComponentsBuilder uriBuilder) {

    Biometria biometria = biometriaRequest.toModel();
    biometriaRepository.save(biometria);
    logger.info("Biometria ID " + biometria.getId() + " gravada com sucesso no banco de dados!");

    // ROLLBACK da biometria caso o cartão não seja encontrado no banco
    Optional<Cartao> optCartao = cartaoRepository.findById(cartaoId);
    if (optCartao.isEmpty()) {
      biometriaRepository.delete(biometria);
      logger.info("Biometria ID " + biometria.getId() + " removida com sucesso no banco de dados! ROLLBACK");
      throw new EntidadeNaoEncontradaException("cartaoId", "O cartão informado não foi encontrado na base de dados");
    }

    Cartao cartao = optCartao.get();
    cartao.adicionaBiometria(biometria);
    cartaoRepository.save(cartao);
    logger.info("Biometria ID " + biometria.getId() + "inserida com sucesso no Cartão ID " + cartao.getId());

    URI uri = uriBuilder.path("/biometrias/{id}")
            .buildAndExpand(biometria.getId())
            .toUri();

    return ResponseEntity.created(uri).build();
  }

  /* ==============================
   * BLOQUEAR CARTÃO
   * ==============================
   **/
  @Transactional
  @PostMapping("/cartoes/{id}/bloqueio")
  public ResponseEntity<?> bloqueiaCartao(@NotBlank @PathVariable("id") String numeroDoCartao,
                                          @RequestHeader(value = "User-Agent") String userAgent) throws JsonProcessingException {
    if (numeroDoCartao.isBlank())
      throw new ApiException("id", "O id do cartão deve ser informado", HttpStatus.BAD_REQUEST);

    Optional<Cartao> optCartao = cartaoRepository.findByNumero(numeroDoCartao);

    // se cartão não encontrado, lança exceção que retorna 404
    if (optCartao.isEmpty())
      throw new EntidadeNaoEncontradaException("cartaoId", "O cartão informado não foi encontrado na base de dados");

    //verificar se o cartão não está bloqueado
    Cartao cartao = optCartao.get();
    if (cartao.possuiBloqueioAtivo())
      throw new ApiException("cartaoId", "O cartão informado já se encontra bloqueado", HttpStatus.UNPROCESSABLE_ENTITY);

    // Verificando se o dono do cartão é o usuário logado.
    // Comparando email que está no token com o email que está na proposta do cartão
    Jwt tokenUsuarioAutenticado = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    String emailUsuarioAutenticado = (String) tokenUsuarioAutenticado.getClaims().get("email");

    if (!cartao.solicitanteEDonoDoCartao(emailUsuarioAutenticado)) {
      logger.warn("Tentativa de bloqueio inválida. " +
              "Cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero()) +
              ". Dono: " + cartao.getProposta().getEmail() +
              ". Solicitante: " + emailUsuarioAutenticado);
      throw new ApiException("cartaoId", "O cartão informado não pertence ao usuário logado", HttpStatus.FORBIDDEN);
    }

    // enviar a solicitação de bloqueio
    // user-agent é recuperado através do @RequestHeader nos atributos do método
    BloqueioResponse bloqueioResponse = null;
    try {
      bloqueioResponse = bloqueiaCartaoClient
              .bloqueiaCartao(cartao.getNumero(), new BloqueioRequest(userAgent));

      // recuperando o IP do solicitanter
      WebAuthenticationDetails webDetails =
              (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
      String enderecoIp = webDetails.getRemoteAddress();

      Bloqueio bloqueio = new Bloqueio(userAgent, enderecoIp, bloqueioResponse.getResultado().getStatus());
      bloqueioRepository.save(bloqueio);

      cartao.adicionaBloqueio(bloqueio);
      cartaoRepository.save(cartao);

      logger.info("Cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero()) + " bloqueado com sucesso!");
      logger.info("Dados do bloqueio: " + bloqueio.toString());

      return ResponseEntity.ok().build();

    } catch (FeignException.UnprocessableEntity ex) {
      String bloqueioResponseString = ex.contentUTF8();
      bloqueioResponse = new ObjectMapper().readValue(bloqueioResponseString, BloqueioResponse.class);

      return ResponseEntity.unprocessableEntity().body(bloqueioResponse);
    } catch (FeignException ex) {
      throw new ApiException("", "Sistema de bloqueio temporariamente indisponível.", HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}
