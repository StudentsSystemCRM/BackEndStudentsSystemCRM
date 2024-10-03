package edutrack.exception.handler;

import java.util.List;
import java.util.UUID;

import edutrack.exception.*;
import edutrack.exception.ResourceNotFoundException;
import edutrack.exception.response.GeneralErrorResponse;
import edutrack.exception.response.GeneralErrorResponseValidationDto;
import edutrack.student.exception.EmailAlreadyInUseException;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.InvalidDateFormatException;
import edutrack.user.exception.ResourceExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		return new GeneralErrorResponseValidationDto(UUID.randomUUID().toString(), messages);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleConstraintViolationException(HandlerMethodValidationException ex) {
		List<String> messages = ex.getAllErrors()
				.stream()
				.map(MessageSourceResolvable::getDefaultMessage)
				.toList();
		return new GeneralErrorResponseValidationDto(UUID.randomUUID().toString(), messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {
		logger.error("Invalid Date Format Exception occurred.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleResourceExistsException(ResourceExistsException ex) {
		logger.error("Resource already exists.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(AccessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleAccessException(AccessException ex) {
		logger.error("Access exception.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(StudentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleStudentNotFoundException(StudentNotFoundException ex) {
		logger.error("Student not found.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public GeneralErrorResponse handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
		logger.error("Email already in use.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleException(Exception ex) {
		logger.error("An unexpected error occurred.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), "An unexpected error: " + ex.getMessage());
	}

	@ExceptionHandler(JwtTokenMalformedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleJwtTokenMalformedException(JwtTokenMalformedException ex) {
		logger.error("JWT token is malformed.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(JwtTokenExpiredException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public GeneralErrorResponse handleJwtTokenExpiredException(JwtTokenExpiredException ex) {
		logger.error("JWT token has expired.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(JwtTokenMissingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleJwtTokenMissingException(JwtTokenMissingException ex) {
		logger.error("JWT token is missing.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}
	@ExceptionHandler(ResourceAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public GeneralErrorResponse handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
		logger.error("Resource already exists.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}


}
