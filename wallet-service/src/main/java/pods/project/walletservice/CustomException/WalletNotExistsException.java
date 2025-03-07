package pods.project.walletservice;

public class WalletNotExistsException extends RuntimeException {

    public WalletNotExistsException(String message) {
        super(message);
    }

    public WalletNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
