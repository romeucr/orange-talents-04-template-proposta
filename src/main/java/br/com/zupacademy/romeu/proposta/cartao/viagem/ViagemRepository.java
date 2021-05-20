package br.com.zupacademy.romeu.proposta.cartao.viagem;

import br.com.zupacademy.romeu.proposta.cartao.viagem.Viagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {
}
