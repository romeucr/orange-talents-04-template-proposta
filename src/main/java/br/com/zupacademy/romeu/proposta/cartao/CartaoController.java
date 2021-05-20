package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.cartao.biometria.Biometria;
import br.com.zupacademy.romeu.proposta.cartao.biometria.BiometriaRepository;
import br.com.zupacademy.romeu.proposta.cartao.biometria.BiometriaRequest;
import br.com.zupacademy.romeu.proposta.cartao.bloqueio.*;
import br.com.zupacademy.romeu.proposta.cartao.viagem.*;
import br.com.zupacademy.romeu.proposta.compartilhado.Ofuscadores;
import br.com.zupacademy.romeu.proposta.compartilhado.excecoes.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

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

  @Autowired
  private ViagemRepository viagemRepository;

  @Autowired
  private ViagemClient viagemClient;

  private final Logger logger = LoggerFactory.getLogger(CartaoController.class);

  /* ==============================
   * INSERIR BIOMETRIA NO CARTÃO
   * ==============================
   * */
  @Transactional
  @PostMapping("/cartoes/{cartaoId}/biometrias")
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
      throw new ApiException("cartaoId", "O cartão informado não foi encontrado na base de dados", NOT_FOUND);
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
   * */
  @Transactional
  @PostMapping("/cartoes/{id}/bloqueios")
  public ResponseEntity<?> bloqueiaCartao(@PathVariable("id") String numeroDoCartao,
                                          @RequestHeader(value = "User-Agent") String userAgent,
                                          UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    if (numeroDoCartao.isBlank())
      throw new ApiException("id", "O id do cartão deve ser informado", BAD_REQUEST);

    Optional<Cartao> optCartao = cartaoRepository.findByNumero(numeroDoCartao);
    Cartao cartao = optCartao.orElseThrow(() ->
            new ApiException("cartaoId", "O cartão informado não foi encontrado na base de dados", NOT_FOUND));

    if (cartao.possuiBloqueioAtivo())
      throw new ApiException("cartaoId", "O cartão informado já se encontra bloqueado", UNPROCESSABLE_ENTITY);

    // Verificando se o dono do cartão é o usuário logado.
    // Comparando email que está no token com o email que está na proposta do cartão
    Jwt tokenUsuarioAutenticado = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    String emailUsuarioAutenticado = (String) tokenUsuarioAutenticado.getClaims().get("email");

    if (!cartao.solicitanteEDonoDoCartao(emailUsuarioAutenticado)) {
      logger.warn("Tentativa de bloqueio inválida. " +
              "Cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero()) +
              ". Dono: " + cartao.getProposta().getEmail() +
              ". Solicitante: " + emailUsuarioAutenticado);
      throw new ApiException("cartaoId", "O cartão informado não pertence ao usuário logado", FORBIDDEN);
    }

    // enviar a solicitação de bloqueio
    // user-agent é recuperado através do @RequestHeader nos atributos do método
    BloqueioResponse bloqueioResponse = null;
    try {
      bloqueioResponse = bloqueiaCartaoClient
              .bloqueiaCartao(cartao.getNumero(), new BloqueioRequest(userAgent));

      // recuperando o IP do solicitante
      WebAuthenticationDetails webDetails =
              (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
      String enderecoIp = webDetails.getRemoteAddress();

      Bloqueio bloqueio = new Bloqueio(userAgent, enderecoIp, bloqueioResponse.getResultado().getStatus());
      bloqueioRepository.save(bloqueio);

      cartao.adicionaBloqueio(bloqueio);
      cartaoRepository.save(cartao);

      logger.info("Cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero()) + " bloqueado com sucesso!");
      logger.info("Dados do bloqueio: " + bloqueio.toString());

      return ResponseEntity.ok().body(bloqueioResponse);

    } catch (FeignException.UnprocessableEntity ex) {
      String bloqueioResponseString = ex.contentUTF8();
      bloqueioResponse = new ObjectMapper().readValue(bloqueioResponseString, BloqueioResponse.class);

      return ResponseEntity.unprocessableEntity().body(bloqueioResponse);
    } catch (FeignException ex) {
      throw new ApiException("", "Sistema de bloqueio temporariamente indisponível.", SERVICE_UNAVAILABLE);
    }
  }

  /* ==============================
   * CRIAR AVISO DE VIAGEM
   * ==============================
   * */
  @Transactional
  @PostMapping("/cartoes/{id}/viagens")
  public ResponseEntity<?> novoAvisoViagem(@PathVariable("id") String numeroDoCartao,
                                          @Valid @RequestBody NovaViagemRequest novaViagemRequest,
                                          HttpServletRequest servletRequest, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    if (numeroDoCartao.isBlank())
      throw new ApiException("id", "O id do cartão deve ser informado", BAD_REQUEST);

    Optional<Cartao> optCartao = cartaoRepository.findByNumero(numeroDoCartao);
    Cartao cartao = optCartao.orElseThrow(() ->
            new ApiException("cartaoId", "O cartão informado não foi encontrado na base de dados", NOT_FOUND));

    if (cartao.possuiBloqueioAtivo()) {
      logger.info("Falha ao criar aviso de viagem para o cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero() +
              ". Destino: " + novaViagemRequest.getDestino() + ". Válido até: " + novaViagemRequest.getValidoAte()) +
              ". Cartão Bloqueado.");
      throw new ApiException("", "Cartão Bloqueado. Não é possível incluir aviso de mensagem", BAD_REQUEST);
    }

    ViagemResponse viagemResponse = null;
    try {
      viagemResponse = viagemClient.novoAvisoViagem(cartao.getNumero(), novaViagemRequest);

      novaViagemRequest.setNumeroDoCartao(cartao.getNumero());

      String enderecoIp = servletRequest.getRemoteAddr();
      String userAgent = servletRequest.getHeader("User-Agent");

      Viagem viagem = novaViagemRequest.toModel(enderecoIp, userAgent);
      viagemRepository.save(viagem);

      cartao.adicionaViagem(viagem);
      logger.info("Aviso de viagem criado com sucesso. Cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero()) +
              ". Destino: " + novaViagemRequest.getDestino() + ". Válido até: " + novaViagemRequest.getValidoAte());

      Map<String, String> mapIds = new HashMap<>();
      mapIds.put("cartaoId", cartao.getNumero());
      mapIds.put("viagemId", viagem.getId().toString());

      URI uri = uriBuilder.path("/cartoes/{cartaoId}/viagens/{viagemId}")
              .buildAndExpand(mapIds)
              .toUri();

      return ResponseEntity.created(uri).body(viagemResponse);

    } catch (FeignException.UnprocessableEntity ex) {
      String viagemResponseString = ex.contentUTF8();
      viagemResponse = new ObjectMapper().readValue(viagemResponseString, ViagemResponse.class);
      logger.info("Falha ao criar aviso de viagem para o cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero() +
              ". Destino: " + novaViagemRequest.getDestino() + ". Válido até: " + novaViagemRequest.getValidoAte()));
      return ResponseEntity.unprocessableEntity().body(viagemResponse);
    } catch (FeignException ex) {
      logger.info("Falha ao criar aviso de viagem para o cartão " + Ofuscadores.ofuscaIdDoCartao(cartao.getNumero() +
              ". Destino: " + novaViagemRequest.getDestino() + ". Válido até: " + novaViagemRequest.getValidoAte()));
      throw new ApiException("", "Sistema de aviso de viagem temporariamente indisponível.", SERVICE_UNAVAILABLE);
    }

  }
}
