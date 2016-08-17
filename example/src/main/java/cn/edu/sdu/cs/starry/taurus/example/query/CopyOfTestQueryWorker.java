package cn.edu.sdu.cs.starry.taurus.example.query;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class CopyOfTestQueryWorker extends QueryWorker {

	@Override
	protected void prepareWorker() {

	}

	@Override
	protected QueryResponse doWork(CacheTool cacheTool, QueryRequest query)
			throws BusinessException {
		CopyOfTestQueryResponse response = new CopyOfTestQueryResponse();
		for(int i = 0 ; i < 4 ;i ++){
			processRate = Math.min(0.3f * i,1.0f);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		response.setSayHello("this is a copied query ,saying hello "+ ((TestQueryRequest)query).getName());
		return response;
	}

	@Override
	protected void cleanProcessor() {

	}

	@Override
	public String getAuthor() {
		return "ytChen";
	}

}
