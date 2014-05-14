package cn.edu.sdu.cs.starry.taurus.common.exception;

/**
 * When encountered an uncorrectable attribute in query.
 * 
 * @author SDU.xccui
 * 
 */
public class BusinessRequestAttributeException extends BusinessException {

	private static final long serialVersionUID = 8700810947891418559L;

	public BusinessRequestAttributeException() {
		super();
	}

	public BusinessRequestAttributeException(Throwable cause) {
		super(cause);
	}

	public BusinessRequestAttributeException(String message) {
		super(message);
	}

	public BusinessRequestAttributeException(String message, Throwable cause) {
		super(message, cause);
	}
}
