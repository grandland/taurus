package cn.edu.sdu.cs.starry.taurus.example.timer;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.request.TimerRequest;

public class TestTimerRequest extends TimerRequest {

	private String name;
	
	public TestTimerRequest() {
		super();
	}

	public TestTimerRequest(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected int calRequestLoad() {
		return 1;
	}

	@Override
	protected String genUniqueRequestKey() {
		return name;
	}

	@Override
	protected void doSelfAttributeCheck()
			throws BusinessRequestAttributeException {

	}

}
