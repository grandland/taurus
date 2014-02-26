package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.request.CommandRequest;

public class TaurusTestQuery extends CommandRequest {
	private String name;

	public TaurusTestQuery() {

	}

	public TaurusTestQuery(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected String genUniqueRequestKey() {
		return name;
	}

	@Override
	protected void doSelfAttributeCheck()
			throws BusinessRequestAttributeException {
		if (name.startsWith("zhdong")) {
			throw new BusinessRequestAttributeException("zhdong is a bad boy");
		}

	}

	@Override
	protected int calRequestLoad() {
		return 1;
	}

}
