package pods.project.accountservice;

public class AccountNotExistsException extends RuntimeException {

    public AccountNotExistsException(String message) {
        super(message);
    }

    public AccountNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
