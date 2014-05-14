package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.server.TaurusListener;

public class TestTaurusListener implements TaurusListener {

	@Override
	public void onStartup() {
		System.out.println("=========================================");
		System.out.println("===============listener start up=========");
		System.out.println("=========================================");
	}

	@Override
	public void onShutdown() {
		System.out.println("=========================================");
		System.out.println("===============listener shutdown=========");
		System.out.println("=========================================");
	}
	
	@Override
	public void onRestart(){
		System.out.println("=========================================");
		System.out.println("===============listener restart==========");
		System.out.println("=========================================");
	}

}
