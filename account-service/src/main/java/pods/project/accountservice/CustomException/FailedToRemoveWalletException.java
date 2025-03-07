package pods.project.accountservice;

public class FailedToRemoveWalletException extends RuntimeException {

    public FailedToRemoveWalletException(String message) {
        super(message);
    }

    public FailedToRemoveWalletException(String message, Throwable cause) {
        super(message, cause);
    }
}
