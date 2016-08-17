package cn.edu.sdu.cs.starry.taurus.example.query;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;

public class TestQueryRequest extends QueryRequest {

	private String name;
	
	public TestQueryRequest() {
		super();
	}

	public TestQueryRequest(String name) {
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
