package ru.itis.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itis.util.Formatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class ValueValidator implements ConstraintValidator<ValidValue, String> {
  @Autowired Formatter formatter;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {

      formatter.validationTransform(value);

      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
