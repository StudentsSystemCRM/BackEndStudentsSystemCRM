package edutrack.exception.handler;

import java.util.List;
import java.util.UUID;

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
									.map(e -> e.getDefaultMessage())
									.toList();
		return new GeneralErrorResponseValidation(UUID.randomUUID().toString(), messages);
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidation handleConstraintViolationException(HandlerMethodValidationException ex) {
	    List<String> messages = ex.getAllErrors()
	                              .stream()
	                              .map(e -> e.getDefaultMessage())
	                              .toList();

	    return new GeneralErrorResponseValidation(UUID.randomUUID().toString(), messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {
		ex.printStackTrace();
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handlerMethodResourceExistsException(ResourceExistsException ex) {
		ex.printStackTrace();
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(AccessException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handlerMethodAccessException(AccessException ex) {
		ex.printStackTrace();
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handleException(Exception ex) {
		ex.printStackTrace();
		return new GeneralErrorResponse(UUID.randomUUID().toString(),
				"An unexpected error: " + ex.getMessage());
	}

}
