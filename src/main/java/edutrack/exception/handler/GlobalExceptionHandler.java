package edutrack.exception.handler;

import java.util.List;
import java.util.UUID;

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

import edutrack.dto.response.GeneralErrorResponse;
import edutrack.dto.response.GeneralErrorResponseValidation;
import edutrack.exception.AccessException;
import edutrack.exception.InvalidDateFormatException;
import edutrack.exception.ResourceExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponseValidation handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getAllErrors()
									.stream()
									.map(DefaultMessageSourceResolvable::getDefaultMessage)
									.toList();
		return new GeneralErrorResponseValidation(UUID.randomUUID().toString(), messages);
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidation handleConstraintViolationException(HandlerMethodValidationException ex) {
	    List<String> messages = ex.getAllErrors()
	                              .stream()
	                              .map(MessageSourceResolvable::getDefaultMessage)
	                              .toList();

	    return new GeneralErrorResponseValidation(UUID.randomUUID().toString(), messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.error("Invalid Date Format Exception occurred.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handlerMethodResourceExistsException(ResourceExistsException ex) {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.error("Resource already exists.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(AccessException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handlerMethodAccessException(AccessException ex) {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.error("Access exception.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handleException(Exception ex) {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.error("An unexpected error occurred.", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(),
				"An unexpected error: " + ex.getMessage());
	}
}
