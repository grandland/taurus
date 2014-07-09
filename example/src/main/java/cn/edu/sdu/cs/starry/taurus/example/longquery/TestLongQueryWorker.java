package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessProcessException;
import cn.edu.sdu.cs.starry.taurus.example.exception.BusinessLongQueryFinishedException;
import cn.edu.sdu.cs.starry.taurus.example.exception.BusinessLongQueryJumpException;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class TestLongQueryWorker extends QueryWorker {

	private Logger LOG = LoggerFactory.getLogger(TestLongQueryWorker.class);
	
	//seconds.
	private static int RESULT_KEEP_TIME = 30 * 60;
	
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
	protected QueryResponse doWork(CacheTool cacheTool, QueryRequest queryRequest)
			throws BusinessException {
		LongQueryRequest query;
		if(queryRequest instanceof LongQueryRequest){
			query = (LongQueryRequest) queryRequest;
		}else{
			throw new BusinessProcessException("Wrong request type!");
		}
		
		Gson gson = new Gson();
		TestLongQueryResponse response = new TestLongQueryResponse();
		String requestId = query.getRequestId();
		String pageResultKey = requestId + "&result";
		
		byte[] cacheInfoBytes = cacheTool.get(requestId);
		
		TestCacheInfo cacheInfo = (cacheInfoBytes == null) ? 
									new TestCacheInfo() : 
									TestCacheInfo.fromBytes(cacheInfoBytes);

		pageResultKey += query.getPage();
		
		//set the response page.
		response.setPage(query.getPage());
		
		LOG.info("currnet page : "+ cacheInfo.getCurrentPage());
		LOG.info("Query page : "+ query.getPage());
		
		if(query.getPage() < cacheInfo.getCurrentPage()){
			// get cache.
			byte[] cachedBytes =  cacheTool.get(pageResultKey) ;
			if(cachedBytes == null){
				response.setRecords(null);
				response.setError("无结果缓存，请重新查询");
				//MARK set query page to 0?
//				query.setPage(0);
//				cacheTool.set(requestId, new TestCacheInfo().toBytes());
			}else{
				List<Record> cachedRecords = gson.fromJson( new String( cachedBytes) , 
						new TypeToken<List<Record>>() {
							private static final long serialVersionUID = 2360078732644247397L;}.getType()
						);
				response.setRecords(cachedRecords);
			}
		}else if(query.getPage() == cacheInfo.getCurrentPage()){
			
			if(cacheInfo.getCurrentLeftRecords().size() < query.getPageSize()){
				//need more.
				doQuery(cacheTool, cacheInfo, query, response);
				
			}else{
				//return cache is enough.
				List<Record> leftRecords = cacheInfo.getCurrentLeftRecords();
				List<Record> resultRecords = leftRecords.subList(0, query.getPageSize()); 
				response.setRecords(resultRecords);
				
				//store this page cache.
				cacheTool.set(pageResultKey , gson.toJson(resultRecords).getBytes(), RESULT_KEEP_TIME);
				
				//store left records to cache.
				cacheInfo.setCurrentLeftRecords(leftRecords.subList(query.getPageSize(), leftRecords.size()));
			}
			
			//add pageNum;
			cacheInfo.setCurrentPage(cacheInfo.getCurrentPage() +1);
			cacheTool.set(requestId, cacheInfo.toBytes());

			//set query request to next page.
			query.setPage(cacheInfo.getCurrentPage());
		}else {
			//if is finished , throws finished exception
			if(cacheInfo.isFinished()){
				throw new BusinessLongQueryFinishedException();
			}else{
				//throws cannot query ahead exception.
				throw new BusinessLongQueryJumpException();
			}
		}
		
		processRate = 1f;
		
		return response;
	}
	
	private void doQuery(CacheTool cacheTool, TestCacheInfo cacheInfo, LongQueryRequest query, TestLongQueryResponse response) throws BusinessLongQueryFinishedException{
		Gson gson = new Gson();
		
		String requestId = query.getRequestId();
		String pageResultKey = requestId + "&result";
		pageResultKey += query.getPage();
		
		int position = cacheInfo.getPosition();
		List<Record> leftRecords = cacheInfo.getCurrentLeftRecords();
		
		if(position < query.getSplitQuery().size()){
			SubQueryRequest subRequest = query.getSplitQuery().get(position);
			//TODO do subRequestQuery.
			List<Record> queryRecords = doSubQuery(subRequest);
			int needRecordNum = query.getPageSize() - leftRecords.size();
			if(needRecordNum > queryRecords.size()){
				position ++;
				//set to cache.
				cacheInfo.setPosition(position);
				leftRecords.addAll(queryRecords);
				
				//query again.
				doQuery(cacheTool,cacheInfo,query,response);
				return ;
			} else{
				leftRecords.addAll( queryRecords.subList(0, needRecordNum) );
				response.setRecords(leftRecords);
				
				//store position. Here, position means next query position.
				position ++ ;
				cacheInfo.setPosition(position);
				
				//store this page cache.
				cacheTool.set(pageResultKey , gson.toJson(leftRecords).getBytes(), RESULT_KEEP_TIME);
				
				//store left records to cache.
				cacheInfo.setCurrentLeftRecords(queryRecords.subList(needRecordNum, queryRecords.size()));
			}
		}else{
			//query finished.
			
			if(leftRecords == null || leftRecords.size() == 0){
				response.setFinished(true);
				throw new BusinessLongQueryFinishedException();
			}
			
			response.setFinished(true);
			response.setRecords(leftRecords);
			
			cacheInfo.setFinished(true);
			
			//store this page cache.
			cacheTool.set(pageResultKey , gson.toJson(leftRecords).getBytes(), RESULT_KEEP_TIME);
			
			//store left records to cache.
			cacheInfo.setCurrentLeftRecords(new ArrayList<Record>());
		}
	}
	
	private List<Record> doSubQuery(SubQueryRequest request){
		LOG.info("query index: "+ request.getIndex());
		Random random = new Random();
		//random
		int[] recordNum = {10,1,2,30};
		
		List<Record> records= new ArrayList<Record>();
		
		for(int i = 0 ; i < recordNum[request.getIndex()] ; i++){
			Record record = new Record(new Date(), request.getIndex()+"-"+i);
			records.add(record);
		}
		LOG.info("queryCount:"+recordNum[request.getIndex()]+" , query result:" + new Gson().toJson(records));
		
		return records;
	}

}
