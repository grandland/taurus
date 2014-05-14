package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;

public class TaurusTestResult extends CommandResponse {
	public int times;

	public TaurusTestResult() {

	}

	public TaurusTestResult(int times) {
		this.times = times;
	}

}
