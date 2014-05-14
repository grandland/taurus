package cn.edu.sdu.cs.starry.taurus.common.exception;

public class BusinessConfigurationException extends BusinessException {

	private static final long serialVersionUID = 5996201406837228177L;

	public BusinessConfigurationException() {
		super();
	}

	public BusinessConfigurationException(Throwable cause) {
		super(cause);
	}

	public BusinessConfigurationException(String message) {
		super(message);
	}

	public BusinessConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
