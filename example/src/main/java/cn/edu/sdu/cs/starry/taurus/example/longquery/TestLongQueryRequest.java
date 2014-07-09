package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.LinkedList;
import java.util.List;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;

public class TestLongQueryRequest extends LongQueryRequest {

	private String name;
	
	public TestLongQueryRequest() {
		super();
	}

	public TestLongQueryRequest(String name) {
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

	@Override
	public List<SubQueryRequest> splitQuery() {
		List<SubQueryRequest> requests = new LinkedList<>();
		for(int i = 0 ; i < 4; i ++){
			SubQueryRequest r = new SubQueryRequest() {
				@Override
				protected void doSelfAttributeCheck()
						throws BusinessRequestAttributeException {
					
				}
				
				@Override
				protected int calRequestLoad() {
					return 1;
				}
			};
			requests.add(r);
		}
		return requests;
	}

}
