package edutrack.validation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BirthdateValidator implements ConstraintValidator<ValidRangeDate, LocalDate> {
    private int min;
    private int max;

    @Override
    public void initialize(ValidRangeDate constraintAnnotation) {
        // ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.yearsFromTodayToFuture();
        max = constraintAnnotation.yearsFromTodayToPast();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        LocalDate today = LocalDate.now();
        int year = (int) ChronoUnit.YEARS.between(value, today);
        return year >= min && year <= max;
    }
}
