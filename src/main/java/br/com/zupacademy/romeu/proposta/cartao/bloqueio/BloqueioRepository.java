package br.com.zupacademy.romeu.proposta.cartao.bloqueio;

import br.com.zupacademy.romeu.proposta.cartao.bloqueio.Bloqueio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueioRepository extends JpaRepository<Bloqueio, Long> {
}
