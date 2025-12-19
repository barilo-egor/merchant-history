package tgb.cryptoexchange.merchanthistory.exception;

/**
 * Пробрасывается в случае ошибки при преобразовании в json объект.
 */
public class SerializeEventException extends RuntimeException {

    public SerializeEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
