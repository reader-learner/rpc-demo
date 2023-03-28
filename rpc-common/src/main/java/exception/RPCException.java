package exception;

import enums.ErrorEnum;

public class RPCException extends RuntimeException {
    public RPCException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public RPCException(ErrorEnum errorEnum, String message) {
        super(errorEnum.getMessage() + ":" + message);
    }

    public RPCException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMessage(), cause);
    }
}
