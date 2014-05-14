package cn.edu.sdu.cs.starry.taurus.example.query;

import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;

public class CopyOfTestQueryResponse extends QueryResponse {

	private String sayHello;

	public CopyOfTestQueryResponse() {
		super();
	}

	public CopyOfTestQueryResponse(String sayHello) {
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
