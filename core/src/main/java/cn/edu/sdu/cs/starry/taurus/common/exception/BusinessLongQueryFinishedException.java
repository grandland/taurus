package cn.edu.sdu.cs.starry.taurus.common.exception;

/**
 * When encountered an uncorrectable attribute in query.
 * 
 * @author SDU.xccui
 * 
 */
public class BusinessLongQueryFinishedException extends BusinessException {

	private static final long serialVersionUID = 8700810947891418559L;

	public BusinessLongQueryFinishedException() {
		super();
	}

	public BusinessLongQueryFinishedException(Throwable cause) {
		super(cause);
	}

	public BusinessLongQueryFinishedException(String message) {
		super(message);
	}

	public BusinessLongQueryFinishedException(String message, Throwable cause) {
		super(message, cause);
	}
}
