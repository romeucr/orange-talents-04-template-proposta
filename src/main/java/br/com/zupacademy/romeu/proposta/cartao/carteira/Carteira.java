package br.com.zupacademy.romeu.proposta.cartao.carteira;

import br.com.zupacademy.romeu.proposta.cartao.carteira.enums.EmissorCarteira;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Carteira {

  @Id @NotBlank
  private String id;

  @Email @NotBlank
  private String email;

  @CreationTimestamp
  private LocalDateTime associadaEm;

  @NotNull
  @Enumerated(EnumType.STRING)
  private EmissorCarteira emissor;

  /* @deprecated - para uso exclusivo do hibernate
   * */
  @Deprecated
  public Carteira() {}

  public Carteira(@NotBlank String id, @Email @NotBlank String email, @NotNull EmissorCarteira emissor) {
    this.id = id;
    this.email = email;
    this.emissor = emissor;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public EmissorCarteira getEmissor() {
    return emissor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Carteira carteira = (Carteira) o;
    return emissor == carteira.emissor;
  }

  @Override
  public int hashCode() {
    return Objects.hash(emissor);
  }
}
