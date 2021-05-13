package br.com.zupacademy.romeu.proposta.proposta;

import br.com.zupacademy.romeu.proposta.cartao.Cartao;
import br.com.zupacademy.romeu.proposta.proposta.enums.PropostaStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta, Long> {
  Optional<Proposta> findByDocumento(String email);

  List<Optional<Proposta>> findByStatusAndCartaoId(PropostaStatus propostaStatus, Cartao cartao);
}
