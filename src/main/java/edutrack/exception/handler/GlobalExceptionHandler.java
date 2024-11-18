package edutrack.exception.handler;

import java.util.List;
import java.util.UUID;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import edutrack.emailService.exception.EmailServiceException;
import edutrack.emailService.exception.MailgunBadRequestException;
import edutrack.emailService.exception.TriggerNotFoundException;
import edutrack.exception.StudentNotFoundException;
import edutrack.exception.ResourceNotFoundException;
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
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		List<String> messages = ex.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		return new GeneralErrorResponseValidationDto(idResponseError, messages);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponseValidationDto handleConstraintViolationException(HandlerMethodValidationException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		List<String> messages = ex.getAllErrors()
				.stream()
				.map(MessageSourceResolvable::getDefaultMessage)
				.toList();
		return new GeneralErrorResponseValidationDto(idResponseError, messages);
	}

	@ExceptionHandler(InvalidDateFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleInvalidDateFormatException(InvalidDateFormatException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error("Invalid Date Format Exception occurred."  + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(ResourceExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleResourceExistsException(ResourceExistsException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(AccessRoleException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public GeneralErrorResponse handleAccessException(AccessRoleException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}
	
	@ExceptionHandler(AccessException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public GeneralErrorResponse handleAccessException(AccessException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(StudentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleStudentNotFoundException(StudentNotFoundException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public GeneralErrorResponse handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(TriggerNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleTriggerNotFoundException(TriggerNotFoundException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(MailgunBadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleMailgunBadRequestException(MailgunBadRequestException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
	}

	@ExceptionHandler(EmailServiceException.class)
	public GeneralErrorResponse handleEmailServiceException(EmailServiceException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(GroupNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(GroupNotFoundException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
		log.error("ResourceNotFoundException", ex);
		return new GeneralErrorResponse(UUID.randomUUID().toString(), ex.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GeneralErrorResponse handleJwtTokenExpiredException(NoResourceFoundException ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GeneralErrorResponse handleException(Exception ex) {
		String idResponseError = UUID.randomUUID().toString();
		log.error(ex.getMessage() + " , id response error: " + idResponseError, ex);
		return new GeneralErrorResponse(idResponseError, "An unexpected error: " + ex.getMessage());
	}

}

