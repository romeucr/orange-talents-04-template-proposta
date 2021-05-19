package br.com.zupacademy.romeu.proposta.cartao.biometria;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Biometria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Lob
  private String fingerprint;

  @CreationTimestamp
  private LocalDateTime criadoEm;

  /* @deprecated
   * para uso exclusivo do hibernate
   * */
  public Biometria() {}

  public Biometria(@NotNull String fingerprint) {
    this.fingerprint = fingerprint;
  }

  public Long getId() {
    return id;
  }

  public String getFingerprint() {
    return fingerprint;
  }
}
