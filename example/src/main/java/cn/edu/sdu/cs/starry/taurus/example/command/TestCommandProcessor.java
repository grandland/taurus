package cn.edu.sdu.cs.starry.taurus.example.command;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.processor.CommandProcessor;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;

public class TestCommandProcessor extends CommandProcessor{

	@Override
	public CommandResponse process(BaseBusinessRequest request)
			throws BusinessException {
		TestCommandResponse response = new TestCommandResponse();
		response.setSayHello("Hello "+ ((TestCommandRequest) request).getName()+" !");
		return response;
	}

	@Override
	protected void prepareProcessor() {
		
	}

	@Override
	protected void cleanProcessor() {
		
	}

	@Override
	public String getAuthor() {
		return "ytchen";
	}

}
