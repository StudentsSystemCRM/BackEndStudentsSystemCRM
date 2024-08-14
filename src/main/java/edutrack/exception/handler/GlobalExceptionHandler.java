package edutrack.exception.handler;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edutrack.dto.response.GeneralErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handlerMethodResourceExistsException(MethodArgumentNotValidException ex){
		return  new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	GeneralErrorResponse handleException(Exception ex){
		
		return  new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

}

