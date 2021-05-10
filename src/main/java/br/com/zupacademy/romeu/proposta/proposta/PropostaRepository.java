package br.com.zupacademy.romeu.proposta.proposta;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta, Long> {
  public Optional<Proposta> findByDocumento(String email);
}
