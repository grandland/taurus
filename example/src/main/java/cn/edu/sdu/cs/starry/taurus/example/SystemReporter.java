package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.processor.BaseProcessor;
import cn.edu.sdu.cs.starry.taurus.server.BusinessReporter;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;

public class SystemReporter implements BusinessReporter {
	@Override
	public void report(BaseProcessor processor,
			RequestAndIdentification requestAndIdentification) {
		StringBuilder rate = new StringBuilder();
		for (float i = 0; i < processor.getProcessRate(); i += 0.1) {
			rate.append("〓");
		}
//		System.out.println("Reporter:"
//				+ requestAndIndentification.getIdentification()
//				+ "\tprocessed: "
//				+ Math.round(processor.getProcessRate() * 100) + "%" + "\t"
//				+ rate.toString());
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
