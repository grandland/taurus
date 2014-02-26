package cn.edu.sdu.cs.starry.taurus.common.exception;
/**
 * 
 * @author SDU.xccui
 *
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = -251952801716839444L;

	public BusinessException() {
		super();
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
