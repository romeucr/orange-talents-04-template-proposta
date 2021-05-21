package br.com.zupacademy.romeu.proposta.cartao.carteira;

import br.com.zupacademy.romeu.proposta.cartao.carteira.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
}
