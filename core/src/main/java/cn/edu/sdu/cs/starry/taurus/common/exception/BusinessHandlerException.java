package cn.edu.sdu.cs.starry.taurus.common.exception;
/**
 * 
 * @author SDU.xccui
 *
 */
public class BusinessHandlerException extends BusinessException {

	private static final long serialVersionUID = -5046651437322666610L;

	public BusinessHandlerException() {
		super();
	}

	public BusinessHandlerException(Throwable cause) {
		super(cause);
	}

	public BusinessHandlerException(String message) {
		super(message);
	}

	public BusinessHandlerException(String message, Throwable cause) {
		super(message, cause);
	}
}
