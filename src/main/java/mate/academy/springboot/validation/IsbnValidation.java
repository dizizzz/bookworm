package mate.academy.springboot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidation implements ConstraintValidator<Isbn, String> {
    private static final String PATTERN_OF_ISBN =
            "^(?=(?:\\\\D*\\\\d){10}(?:(?:\\\\D*\\\\d){3})?$)[\\\\d-]+$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return isbn != null && Pattern.compile(PATTERN_OF_ISBN).matcher(isbn).matches();
    }
}
