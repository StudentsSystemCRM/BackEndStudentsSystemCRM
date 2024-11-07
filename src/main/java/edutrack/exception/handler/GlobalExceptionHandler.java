package edutrack.exception.handler;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import edutrack.elasticsearch.service.ElasticsearchDeleteDataScheduler;
import edutrack.exception.StudentNotFoundException;
import edutrack.exception.response.GeneralErrorResponse;
import edutrack.exception.response.GeneralErrorResponseValidationDto;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.student.exception.EmailAlreadyInUseException;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.AccessRoleException;
import edutrack.user.exception.InvalidDateFormatException;
import edutrack.user.exception.ResourceExistsException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponseValidationDto(errorId, messages);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleConstraintViolationException(HandlerMethodValidationException ex) {
		List<String> messages = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponseValidationDto(errorId, messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {		
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleResourceExistsException(ResourceExistsException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(AccessRoleException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public GeneralErrorResponse handleAccessException(AccessRoleException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(AccessException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public GeneralErrorResponse handleAccessException(AccessException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(StudentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleStudentNotFoundException(StudentNotFoundException ex) {
		String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		log.error(ex.getMessage(), ex);
		return new GeneralErrorResponse("1", ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public GeneralErrorResponse handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(GroupNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(GroupNotFoundException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(NoResourceFoundException ex) {
				String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleException(Exception ex) {
		String errorId = getUUID();
		log.error(ex.getMessage() + ", uuid: " + errorId, ex); 
		return new GeneralErrorResponse(errorId, "An unexpected error: " + ex.getMessage());
	}

	private String getUUID() {
		return UUID.randomUUID().toString();
	}

}
