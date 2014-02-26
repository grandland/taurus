package cn.edu.sdu.cs.starry.taurus;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessResponseHandlerException;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import cn.edu.sdu.cs.starry.taurus.server.ResponseAndIdentification;

/**
 * A business response hander should handle normal response and also should deal
 * with exception thrown during processing.
 * 
 * @author SDU.xccui
 * 
 */
public abstract class BusinessResponseHandler {
	private BusinessType businessType;

	public BusinessResponseHandler(BusinessType businessType) {
		this.businessType = businessType;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public abstract void handleResponse(
			ResponseAndIdentification responseAndIndentification)
			throws BusinessResponseHandlerException;

	public abstract void handleException(
			RequestAndIdentification requestAndIndentification,
			BusinessException exception);

	public void start() throws BusinessResponseHandlerException {
		startResponseHandler();
	}

	public void stop() throws BusinessResponseHandlerException {
		stopResponseHandler();
	}

	protected abstract void startResponseHandler()
			throws BusinessResponseHandlerException;

	protected abstract void stopResponseHandler()
			throws BusinessResponseHandlerException;
}
