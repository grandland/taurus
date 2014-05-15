package cn.edu.sdu.cs.starry.taurus.example.longquery;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.processor.LongQueryWorker;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;
import cn.edu.sdu.cs.starry.taurus.request.SubQueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class TestLongQueryWorker extends LongQueryWorker {

	@Override
	protected void prepareWorker() {

	}

	@Override
	protected void cleanProcessor() {

	}

	@Override
	public String getAuthor() {
		return "ytChen";
	}

	@Override
	protected QueryResponse doWork(CacheTool cacheTool, SubQueryRequest query)
			throws BusinessException {
		TestLongQueryResponse response = new TestLongQueryResponse();
//		for(int i = 0 ; i < 10 ;i ++){
//			processRate = 0.1f * i;
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		response.setSayHello("This is part of long query result, long query id = "+query.getRequestId() +", index = "+query.getIndex());
		
		return response;
	}

}
