package br.com.zupacademy.romeu.proposta.cartao;

import br.com.zupacademy.romeu.proposta.biometria.Biometria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

  Optional<Cartao> findByIdAndBiometria(Long cartaoId, Biometria biometria);
}
