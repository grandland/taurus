package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessTypeManager;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessProcessException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.processor.QueryWorker;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * A singleton factory for dealing with sync requests.
 *
 * @author SDU.xccui
 *
 */
public class QueryWorkerFactory extends BaseBusinessFactory {
	private static final Logger LOG = LoggerFactory
			.getLogger(QueryWorkerFactory.class);
	private static QueryWorkerFactory factory;

	private Map<String, QueryRequest> requestMap;
	private Map<String, QueryWorkerDepartment> departmentMap;
	private Map<String, QueryResponse> resultMap;

	private QueryWorkerFactory(
			SingleBusinessTypeConfiguration singleTypeConfiguration,
			CacheTool cacheTool) throws BusinessException {
		super(singleTypeConfiguration.getFactoryResource(), cacheTool);
		requestMap = new HashMap<String, QueryRequest>();
		departmentMap = new HashMap<String, QueryWorkerDepartment>();
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
						(QueryRequest) genConfObject("request",
								singleBusinessConf.getName(), requestClass,
								singleTypeConfiguration.getRequests()));
				resultMap.put(
						singleBusinessConf.getName(),
						(QueryResponse) genConfObject("response",
								singleBusinessConf.getName(), responseClass,
								singleTypeConfiguration.getResponses()));
				departmentMap
						.put(singleBusinessConf.getName(), QueryWorkerDepartment
								.buildNewDepartment(
										(QueryWorker) genConfObject("processor",
												singleBusinessConf.getName(),
												processorClass,
												singleTypeConfiguration
														.getProcessors()),
										singleTypeConfiguration
												.getFactoryResource(),
										cacheTool));
				BusinessTypeManager.businessTypeMap.put(
						singleBusinessConf.getName(), BusinessType.QUERY);
			}
		} catch (Exception ex) {
			throw new BusinessCorrespondingException(ex);
		}
	}

	@Override
	public QueryResponse process(String businessKey,
			BaseBusinessRequest request, BusinessMonitor monitor)
			throws BusinessException {
		if (!(request instanceof QueryRequest)) {
			throw new BusinessCorrespondingException();
		}
		QueryRequest query = (QueryRequest) request;
		LOG.info("Received a query request: [" + query.getUserName() + "@"
				+ query.getUserIP() + " >> " + query.getClass().getName()
				+ " >> " + query.getRequestKey(true, true) + "]");
		query.doAttributeCheck();
		byte[] resultBytes;
		String queryKeyWithSession = null;
		if (null != cacheTool) {// cache enable
			queryKeyWithSession = query.getRequestKey(true, true);
			if (queryKeyWithSession != null) {
				// out cache enabled, check cache first
				resultBytes = cacheTool.get(queryKeyWithSession);
				if (resultBytes != null) {
					// Oh yeah, hit!
					return resultMap.get(businessKey).fromBytes(resultBytes);
				}
			}
		}
		final int requestLoad = query.getRequestLoad() > 0 ? query
				.getRequestLoad() : 0;
		minResource(requestLoad);
		QueryWorkerDepartment workerDepartment = departmentMap.get(businessKey);
		QueryWorker worker = workerDepartment.hireAWorker();
		LOG.info("After hire a worker, factory resource left = " + resource);
		worker.prepare();
		if (null != monitor) {
			monitor.setProcessor(worker);
			monitor.start();
		}
		QueryResponse result = null;
		try {
			result = worker.process(query);
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			throw new BusinessProcessException(ex);
		} finally {
			if (null != monitor) {
				worker.cleanWithMonitor(monitor);
			} else {
				worker.clean();
			}
			workerDepartment.fireAWorker(worker);
			addResource(requestLoad);
			LOG.info("After fire a worker, resource left = " + resource);
		}
		if (null != cacheTool && queryKeyWithSession != null) {// cache
																// enable
			// save to cache
			cacheTool.set(queryKeyWithSession, result.toBytes());
		}
		return result;
	}

	@Override
	public QueryResponse process(String rpcRequestKey, byte[] rpcRequestBytes,
			BusinessMonitor monitor) throws BusinessException {
		QueryRequest query = requestMap.get(rpcRequestKey).fromBytes(rpcRequestBytes);
		return process(rpcRequestKey, query, monitor);
	}

	@Override
	public void destroy() {
		LOG.info("Query worker factory will be destroyed...");
		requestMap.clear();
		resultMap.clear();
		QueryWorkerDepartment.destroy();
	}

	public static QueryWorkerFactory newQueryWorkerFactory(
			SingleBusinessTypeConfiguration singleTypeConfiguration,
			CacheTool cacheTool) throws BusinessException {
		if (null == factory) {
			factory = new QueryWorkerFactory(singleTypeConfiguration, cacheTool);
		}
		return factory;
	}
	/**
	 * DON'T USE IT unless you want to create an new factory instance next time you call {@link #newCommandProcessorFactory(SingleBusinessTypeConfiguration, CacheTool)}.
	 * And you are sure that you DO NOT need the factory instance existing now.
	 * This will reset factory which will make factory=null
	 * It should be called ONLY before reboot.*/
	public static void reset(){
    	LOG.info("Reset factory :" + QueryWorkerFactory.class.getSimpleName());
		factory = null;
	}
}
