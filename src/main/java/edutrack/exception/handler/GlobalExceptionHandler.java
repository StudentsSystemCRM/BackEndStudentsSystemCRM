package edutrack.exception.handler;

import java.util.List;

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

import edutrack.elasticsearch.service.ElasticsearchLogging;
import edutrack.exception.StudentNotFoundException;
import edutrack.exception.response.GeneralErrorResponse;
import edutrack.exception.response.GeneralErrorResponseValidationDto;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.student.exception.EmailAlreadyInUseException;
import edutrack.user.exception.AccessException;
import edutrack.user.exception.AccessRoleException;
import edutrack.user.exception.InvalidDateFormatException;
import edutrack.user.exception.ResourceExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ElasticsearchLogging elasticsearchLogging;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponseValidationDto(errorId, messages);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleConstraintViolationException(HandlerMethodValidationException ex) {
		List<String> messages = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponseValidationDto(errorId, messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {		
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleResourceExistsException(ResourceExistsException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(AccessRoleException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public GeneralErrorResponse handleAccessException(AccessRoleException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(AccessException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public GeneralErrorResponse handleAccessException(AccessException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(StudentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleStudentNotFoundException(StudentNotFoundException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public GeneralErrorResponse handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(GroupNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(GroupNotFoundException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(NoResourceFoundException ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleException(Exception ex) {
		String errorId = elasticsearchLogging.saveLogExeption(ex);
		return new GeneralErrorResponse(errorId, "An unexpected error: " + ex.getMessage());
	}

}
