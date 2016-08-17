package cn.edu.sdu.cs.starry.taurus.example.exception;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;

public class BusinessLongQueryJumpException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5701870788566710378L;

	public BusinessLongQueryJumpException() {
		super();
	}

	public BusinessLongQueryJumpException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessLongQueryJumpException(String message) {
		super(message);
	}

	public BusinessLongQueryJumpException(Throwable cause) {
		super(cause);
	}
	
	

}
