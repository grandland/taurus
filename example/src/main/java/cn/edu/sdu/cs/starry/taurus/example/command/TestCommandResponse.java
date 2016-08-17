package cn.edu.sdu.cs.starry.taurus.example.command;

import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;

public class TestCommandResponse extends CommandResponse {

	private String sayHello;

	public TestCommandResponse() {
		super();
	}

	public TestCommandResponse(String sayHello) {
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
