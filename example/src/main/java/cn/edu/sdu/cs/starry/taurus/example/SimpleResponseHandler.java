package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.BusinessResponseHandler;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessResponseHandlerException;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import cn.edu.sdu.cs.starry.taurus.server.ResponseAndIdentification;

public class SimpleResponseHandler extends BusinessResponseHandler {
	private static final String TEST_BUSINESS_KEY = "TaurusTest";

	public SimpleResponseHandler(BusinessType businessType) {
		super(businessType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleResponse(
			ResponseAndIdentification responseAndIndentification)
			throws BusinessResponseHandlerException {
		TaurusTestResult result = (TaurusTestResult) responseAndIndentification
				.getResponse();
		System.out.println("Handle '" + TEST_BUSINESS_KEY + "' response: "
				+ responseAndIndentification.getIdentification()
				+ " over. Printed " + result.times + " times.");

	}

	@Override
	public void handleException(
			RequestAndIdentification requestAndIndentification,
			BusinessException exception) {
		//exception.printStackTrace();
	}

	@Override
	protected void startResponseHandler() throws BusinessResponseHandlerException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopResponseHandler()
			throws BusinessResponseHandlerException {
		// TODO Auto-generated method stub
		
	}

}
