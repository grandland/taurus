package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.processor.CommandProcessor;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;

public class TaurusTestWorker extends CommandProcessor {

	@Override
	public CommandResponse process(BaseBusinessRequest request)
			throws BusinessException {
		if (!(request instanceof TaurusTestQuery)) {
			throw new BusinessCorrespondingException("query is not a "
					+ TaurusTestQuery.class.getName());
		}
		TaurusTestQuery taurusTestQuery = (TaurusTestQuery) request;
		TaurusTestResult result = new TaurusTestResult();
		for (int i = 0; i < 10; i++) {
			try {
				taurusTestQuery.getName();
				// System.out.println("Hello, " + taurusTestQuery.getName()
				// + " for the " + i + "st time!");
				Thread.sleep(1000);
				setProcessRate(0.1f * i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		result.times = 100;
		return result;
	}

	@Override
	protected void cleanProcessor() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAuthor() {
		return "xccui";
	}

	@Override
	protected void prepareProcessor() {

	}

}
