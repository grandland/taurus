package cn.edu.sdu.cs.starry.taurus.common.exception;

/**
 * 
 * @author SDU.xccui
 * 
 */
public class BusinessProcessException extends BusinessException {
	private static final long serialVersionUID = -7294626359382349404L;

	public BusinessProcessException() {
		super();
	}

	public BusinessProcessException(Throwable cause) {
		super(cause);
	}

	public BusinessProcessException(String message) {
		super(message);
	}

	public BusinessProcessException(String message, Throwable cause) {
		super(message, cause);
	}
}
