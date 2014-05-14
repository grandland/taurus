package cn.edu.sdu.cs.starry.taurus.common.exception;
/**
 * 
 * @author SDU.xccui
 *
 */
public class BusinessResponseHandlerException extends BusinessException {

	private static final long serialVersionUID = -4286925825642673802L;

	public BusinessResponseHandlerException() {
		super();
	}

	public BusinessResponseHandlerException(Throwable cause) {
		super(cause);
	}

	public BusinessResponseHandlerException(String message) {
		super(message);
	}

	public BusinessResponseHandlerException(String message, Throwable cause) {
		super(message, cause);
	}
}
