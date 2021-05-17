package br.com.zupacademy.romeu.proposta.compartilhado.validacoes;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidFingerprintValidator.class )
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidFingerprint {
  String message() default "A biometria informada não é válida";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
