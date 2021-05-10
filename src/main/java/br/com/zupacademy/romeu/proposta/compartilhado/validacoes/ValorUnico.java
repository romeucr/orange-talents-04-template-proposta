package br.com.zupacademy.romeu.proposta.compartilhado.validacoes;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValorUnicoValidator.class )
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValorUnico {
  String message() default "O valor informado já existe no banco de dados";

  String campo();
  Class<?> tabela();

  /**
   * Em alguns casos é desejável gravar na base de dados somente os números.
   * Ao remover os números e salvar na base de dados, também né necessário removê-los
   * quando for fazer a validação.
   */
  boolean removeStrings() default false;

  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
