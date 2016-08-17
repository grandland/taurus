package cn.edu.sdu.cs.starry.taurus.example.command;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.request.CommandRequest;

public class TestCommandRequest extends CommandRequest{

	private String name;
	
	public TestCommandRequest() {
		super();
	}

	public TestCommandRequest(String name) {
		super();
		this.name = name;
	}

	@Override
	protected int calRequestLoad() {
		return 0;
	}

	@Override
	protected String genUniqueRequestKey() {
		return name;
	}

	@Override
	protected void doSelfAttributeCheck()
			throws BusinessRequestAttributeException {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
