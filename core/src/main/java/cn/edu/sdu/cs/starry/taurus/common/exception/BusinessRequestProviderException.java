package cn.edu.sdu.cs.starry.taurus.common.exception;

/**
 * 
 * @author SDU.xccui
 * 
 */
public class BusinessRequestProviderException extends BusinessException {

	private static final long serialVersionUID = 8481796273332762505L;

	public BusinessRequestProviderException() {
		super();
	}

	public BusinessRequestProviderException(Throwable cause) {
		super(cause);
	}

	public BusinessRequestProviderException(String message) {
		super(message);
	}

	public BusinessRequestProviderException(String message, Throwable cause) {
		super(message, cause);
	}
}
