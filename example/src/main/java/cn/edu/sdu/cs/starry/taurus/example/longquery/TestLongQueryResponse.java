package cn.edu.sdu.cs.starry.taurus.example.longquery;

import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;

public class TestLongQueryResponse extends QueryResponse {

	private String sayHello;

	public TestLongQueryResponse() {
		super();
	}

	public TestLongQueryResponse(String sayHello) {
		super();
		this.sayHello = sayHello;
	}

	public String getSayHello() {
		return sayHello;
	}

	public void setSayHello(String sayHello) {
		this.sayHello = sayHello;
	}
	
	
	
}
