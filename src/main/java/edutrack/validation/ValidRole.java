package edutrack.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = RoleValidator.class)
public @interface ValidRole {
	String message() default "Valid role is CEO or ADMIN or USER";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
