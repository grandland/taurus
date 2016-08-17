package cn.edu.sdu.cs.starry.taurus.example;

import static cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessResponseHandler;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessResponseHandlerException;
import cn.edu.sdu.cs.starry.taurus.example.command.TestCommandResponse;
import cn.edu.sdu.cs.starry.taurus.example.longquery.TestLongQueryResponse;
import cn.edu.sdu.cs.starry.taurus.example.query.CopyOfTestQueryResponse;
import cn.edu.sdu.cs.starry.taurus.example.query.TestQueryResponse;
import cn.edu.sdu.cs.starry.taurus.example.timer.TestTimerResponse;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import cn.edu.sdu.cs.starry.taurus.server.ResponseAndIdentification;

public class TestAllResponseHandler extends BusinessResponseHandler {

	private static Logger LOG = LoggerFactory.getLogger(TestAllResponseHandler.class);
	
	private BusinessType businessType;
	public TestAllResponseHandler(BusinessType businessType) {
		super(businessType);
		this.businessType = businessType; 
	}

	@Override
	public void handleResponse(
			ResponseAndIdentification responseAndIndentification)
			throws BusinessResponseHandlerException {
		switch(businessType){
		case COMMAND:
			LOG.info(
					"handle command response of business: "+
					responseAndIndentification.getBusinessKey()+
					",response: "+
					((TestCommandResponse) responseAndIndentification.getResponse()).getSayHello()
					);
			break;
		case QUERY:
			if(responseAndIndentification.getResponse() instanceof TestQueryResponse)
				LOG.info(
						"handle query response of business: "+
						responseAndIndentification.getBusinessKey()+
						",response: "+
						((TestQueryResponse) responseAndIndentification.getResponse()).getSayHello()
						);
			else if(responseAndIndentification.getResponse() instanceof TestLongQueryResponse){
				LOG.info(
						"handle query response of business: "+
						responseAndIndentification.getBusinessKey()+
						",response: "+
						((TestLongQueryResponse) responseAndIndentification.getResponse()).getSayHello()
						);
			}
			else
				LOG.info(
						"handle query response of business: "+
						responseAndIndentification.getBusinessKey()+
						",response: "+
						((CopyOfTestQueryResponse) responseAndIndentification.getResponse()).getSayHello()
						);
			break;
		case TIMER:
			LOG.info(
					"handle timer response of business: "+
					responseAndIndentification.getBusinessKey()+
					",response: "+
					((TestTimerResponse) responseAndIndentification.getResponse()).getSayHello()
					);
			break;
		}
		LOG.info("Processed taurus name:" +responseAndIndentification.getIdentification().getTaurusServerName());
	}

	@Override
	public void handleException(
			RequestAndIdentification requestAndIndentification,
			BusinessException exception) {
		exception.printStackTrace();
	}

	@Override
	protected void startResponseHandler()
			throws BusinessResponseHandlerException {
		LOG.info("start response handler");
	}

	@Override
	protected void stopResponseHandler()
			throws BusinessResponseHandlerException {
		LOG.info("stop response handler");
	}

}
