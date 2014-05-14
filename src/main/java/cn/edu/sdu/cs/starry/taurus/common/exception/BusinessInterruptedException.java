package cn.edu.sdu.cs.starry.taurus.common.exception;

/**
 * 
 * @author SDU.xccui
 * 
 */
public class BusinessInterruptedException extends BusinessException {

	private static final long serialVersionUID = 3475663623504238680L;

	public BusinessInterruptedException() {
		super();
	}

	public BusinessInterruptedException(Throwable cause) {
		super(cause);
	}

	public BusinessInterruptedException(String message) {
		super(message);
	}

	public BusinessInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}
}
