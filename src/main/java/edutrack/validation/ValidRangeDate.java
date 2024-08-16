package edutrack.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = BirthdateValidator.class)
public @interface ValidRangeDate {

	String message() default "birthdate format fullyear-month-day, or day/month/fullyear, or month-day-fullyear or day.month.fullyear";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	int yearsfromTodaytoFuture() default 120;
	int yearsfromTodaytoPast() default 120;
}
