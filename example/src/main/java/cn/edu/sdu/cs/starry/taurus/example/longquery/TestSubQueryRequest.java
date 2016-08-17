package cn.edu.sdu.cs.starry.taurus.example.longquery;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;

public class TestSubQueryRequest extends SubQueryRequest {
	
	private int requestNum;

	@Override
	protected int calRequestLoad() {
		return 1;
	}

	@Override
	protected void doSelfAttributeCheck()
			throws BusinessRequestAttributeException {

	}

	public int getRequestNum() {
		return requestNum;
	}

	public void setRequestNum(int requestNum) {
		this.requestNum = requestNum;
	}

	
}
