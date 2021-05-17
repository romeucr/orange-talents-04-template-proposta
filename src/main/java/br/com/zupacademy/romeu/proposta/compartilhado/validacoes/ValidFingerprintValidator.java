package br.com.zupacademy.romeu.proposta.compartilhado.validacoes;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidFingerprintValidator implements ConstraintValidator<ValidFingerprint, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return false;

    return Base64.isBase64(value);
  }
}
