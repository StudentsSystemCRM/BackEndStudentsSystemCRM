package edutrack.emailService.exception;

public class MailgunBadRequestException extends RuntimeException {
    public MailgunBadRequestException(String message) {
        super(message);
    }
}
