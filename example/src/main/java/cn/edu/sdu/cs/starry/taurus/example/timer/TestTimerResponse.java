package cn.edu.sdu.cs.starry.taurus.example.timer;

import cn.edu.sdu.cs.starry.taurus.response.TimerResponse;

public class TestTimerResponse extends TimerResponse {

	private String sayHello;

	public TestTimerResponse() {
		super();
	}

	public TestTimerResponse(String sayHello) {
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
