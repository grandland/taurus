package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessTypeManager;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessLongQueryFinishedException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.processor.LongQueryWorker;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.request.LongQueryRequest;
import cn.edu.sdu.cs.starry.taurus.request.SubQueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class LongQueryWorkerFactory extends BaseBusinessFactory{
	
	private static final Logger LOG = LoggerFactory.getLogger(LongQueryWorkerFactory.class);
	
	private static LongQueryWorkerFactory factory;
	
	private Map<String, LongQueryRequest> requestMap;
	private Map<String, WorkerDepartment<LongQueryWorker>> departmentMap;
	private Map<String, QueryResponse> resultMap;
	
	@SuppressWarnings("unchecked")
	private LongQueryWorkerFactory(
			SingleBusinessTypeConfiguration singleTypeConfiguration,
			CacheTool cacheTool) throws BusinessException{
		super(singleTypeConfiguration.getFactoryResource(), cacheTool);
		requestMap = new HashMap<String, LongQueryRequest>();
		departmentMap = new HashMap<String, WorkerDepartment<LongQueryWorker>>();
		resultMap = new HashMap<String, QueryResponse>();
		try {
			String requestClass;
			String processorClass;
			String responseClass;
			for (SingleBusinessConf singleBusinessConf : singleTypeConfiguration
					.getBusinesses().values()) {
				requestClass = singleBusinessConf.getRequestClass();
				processorClass = singleBusinessConf.getProcessorClass();
				responseClass = singleBusinessConf.getResponseClass();
				requestMap.put(
						singleBusinessConf.getName(),
						(LongQueryRequest) genConfObject("request",
								singleBusinessConf.getName(), requestClass,
								singleTypeConfiguration.getRequests()));
				resultMap.put(
						singleBusinessConf.getName(),
						(QueryResponse) genConfObject("response",
								singleBusinessConf.getName(), responseClass,
								singleTypeConfiguration.getResponses()));
				departmentMap
						.put(singleBusinessConf.getName(), WorkerDepartment
								.buildNewDepartment(
										(LongQueryWorker) genConfObject("processor",
												singleBusinessConf.getName(),
												processorClass,
												singleTypeConfiguration
														.getProcessors()),
										singleTypeConfiguration
												.getFactoryResource(),
										cacheTool));
				BusinessTypeManager.businessTypeMap.put(
						singleBusinessConf.getName(), BusinessType.LONGQUERY);
			}
		} catch (Exception ex) {
			throw new BusinessCorrespondingException(ex);
		}
	}

	@Override
	public BaseBusinessResponse process(String businessKey,
			BaseBusinessRequest request, BusinessMonitor monitor)
			throws BusinessException {
		if( !(request instanceof LongQueryRequest)){
			throw new BusinessCorrespondingException();
		}
		LongQueryRequest query = (LongQueryRequest) request;
		LOG.info("Received a long query request :["+query.getUserName() + "@" 
				+ query.getUserIP() + " >> " + query.getClass().getName()
				+ " >> " +query.getRequestKey(true, true)+" >> " + query.getRequestId()+"]");
		query.doAttributeCheck();
		byte[] resultBytes;
		String queryKeyWithSession = null;
		if(null == cacheTool){
			LOG.warn("Cache tool disabled. LongQuery won't work normally!");
		}
    	String requestId = query.getRequestId();
    	int position = 0;
    	List<SubQueryRequest> requests = query.getSplitQuery();
		if(null != cacheTool){
			byte[] positionBytes =  cacheTool.get(requestId);
			String positionS = null;
			if(null != positionBytes){
		    	 positionS= new String(positionBytes);	
			}

	    	if(positionS != null){
	    		position = Integer.parseInt(positionS);
	    	}
	    	if(position  > requests.size() -1){
	    		LOG.warn("PANIC! position [{}] is greater than requests size [{}]! Will ignore.",position,requests.size());
	    		throw new BusinessLongQueryFinishedException();
	    	}
	    	
	    	//get sub query cache key.
			queryKeyWithSession = requests.get(position).getRequestKey(true, true);
			//try fetch cache
			if(queryKeyWithSession != null){
				resultBytes = cacheTool.get(queryKeyWithSession);
				if(resultBytes != null){
					LOG.info("read sub query response from cache with key" + queryKeyWithSession);
					QueryResponse response = resultMap.get(businessKey).fromBytes(resultBytes);
					//inc index.
					position ++;
					//write to cache.
					cacheTool.set(requestId,(position +"").getBytes());
					return response;
				}
			}
		}
		
		//no cache hit, let's work
		final int requestLoad = query.getRequestLoad() > 0 ? query.getRequestLoad() : 0;
		minResource(requestLoad);
		
		WorkerDepartment<LongQueryWorker> workerDepartment = departmentMap.get(businessKey);
		
		LongQueryWorker worker = workerDepartment.hireAWorker();
		LOG.info("After hire a worker, factory resource left = " + resource);
		worker.prepare();
		if(null != monitor){
			monitor.setProcessor(worker);
			monitor.start();
		}
		QueryResponse result = null;
		try{
			result = worker.process(requests.get(position));
		} catch (Exception ex){
			LOG.error(ex.getMessage());
			throw new BusinessException(ex);
		} finally {
			if (null != monitor){
				worker.cleanWithMonitor(monitor);
			} else {
				worker.clean();
			}
			workerDepartment.fireAWorker(worker);
			addResource(requestLoad);
			LOG.info("After fire a worker, resource left = " + resource);
		}
		if(null != cacheTool){
			//store position 
			//inc index.
			position ++;
			//write to cache.
			cacheTool.set(requestId,(position +"").getBytes());
			LOG.info("set key[{}]=[{}]", requestId, position);
			if(queryKeyWithSession != null){
				//save to cache.
				cacheTool.set(queryKeyWithSession, result.toBytes());
			}
		}
		
		return result;
	}

	@Override
	public BaseBusinessResponse process(String rpcRequestKey,
			byte[] rpcRequestBytes, BusinessMonitor monitor)
			throws BusinessException {
		LongQueryRequest query = requestMap.get(rpcRequestKey).fromBytes(rpcRequestBytes);
		return process(rpcRequestKey, query, monitor);
	}

	@Override
	public void destroy() {
		LOG.info("LongQuery worker factory will be destroyed...");
		requestMap.clear();
		resultMap.clear();
	}
	
	public static LongQueryWorkerFactory newLongQueryWorkerFactory(
			SingleBusinessTypeConfiguration singleTypeConfiguration,
			CacheTool cacheTool) throws BusinessException {
		if(null == factory){
			factory = new LongQueryWorkerFactory(singleTypeConfiguration, cacheTool);
		}
		return factory;
	}
	
	/**
	 * DON'T USE IT unless you want to create an new factory instance next time you call {@link #newCommandProcessorFactory(SingleBusinessTypeConfiguration, CacheTool)}.
	 * And you are sure that you DO NOT need the factory instance existing now.
	 * This will reset factory which will make factory=null
	 * It should be called ONLY before reboot.*/
	public static void reset(){
		LOG.info("Reset factory : "+LongQueryWorkerFactory.class.getSimpleName());
		factory = null;
	}
}
