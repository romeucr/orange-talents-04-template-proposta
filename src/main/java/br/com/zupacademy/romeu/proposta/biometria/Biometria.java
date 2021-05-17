package br.com.zupacademy.romeu.proposta.biometria;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Biometria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String fingerprint;

  @CreationTimestamp
  private LocalDateTime creadoEm;

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
}
