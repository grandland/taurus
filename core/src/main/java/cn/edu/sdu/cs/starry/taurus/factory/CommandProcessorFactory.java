package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.edu.sdu.cs.starry.taurus.TaurusMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessTypeManager;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessProcessException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.processor.CommandProcessor;
import cn.edu.sdu.cs.starry.taurus.request.CommandRequest;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class CommandProcessorFactory extends BaseBusinessFactory {
    private static final Logger LOG = LoggerFactory
            .getLogger(CommandProcessorFactory.class);
    private static CommandProcessorFactory factory;
    private Map<String, CommandRequest> requestMap;
    private Map<String, CommandProcessor> processorMap;
    private AsyncProcessorPool processorPool;

    public CommandProcessorFactory(
            SingleBusinessTypeConfiguration singleTypeConfiguration,
            CacheTool cacheTool) throws BusinessException {
        super(singleTypeConfiguration.getFactoryResource(), cacheTool);
        requestMap = new HashMap<String, CommandRequest>();
        processorMap = new HashMap<String, CommandProcessor>();
        try {
            String requestClass;
            String processorClass;
            for (SingleBusinessConf singleBusinessConf : singleTypeConfiguration
                    .getBusinesses().values()) {
                requestClass = singleBusinessConf.getRequestClass();
                processorClass = singleBusinessConf.getProcessorClass();
                requestMap.put(
                        singleBusinessConf.getName(),
                        (CommandRequest) genConfObject("request",
                                singleBusinessConf.getName(), requestClass,
                                singleTypeConfiguration.getRequests()));
                processorMap.put(
                        singleBusinessConf.getName(),
                        (CommandProcessor) genConfObject("processor",
                                singleBusinessConf.getName(), processorClass,
                                singleTypeConfiguration.getProcessors()));
                BusinessTypeManager.businessTypeMap.put(
                        singleBusinessConf.getKey(), BusinessType.COMMAND);

            }
            processorPool = new AsyncProcessorPool(resource);
            processorPool.initPool(processorMap);
        } catch (Exception ex) {
            throw new BusinessCorrespondingException(ex);
        }
    }

    @Override
    public BaseBusinessResponse process(String businessKey,
                                        BaseBusinessRequest request, BusinessMonitor monitor)
            throws BusinessException {
        if (!(request instanceof CommandRequest)) {
            throw new BusinessCorrespondingException();
        }
        TaurusMetrics.incCommandMeter();
        CommandRequest asyncRequest = (CommandRequest) request;
        LOG.info("Received a COMMAND request: [" + request.getUserName() + "@"
                + request.getUserIP() + ">>'" + request.getClass().getName()
                + "'>>" + asyncRequest.getRequestKey(true) + "]");
        CommandProcessor processor;
        CommandResponse response = null;
        processor = processorPool.getProcessor(businessKey);
        processor.prepare();
        try {
            response = processor.process(asyncRequest);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BusinessProcessException(ex);
        } finally {
            processor.clean();
            processorPool.putProcessor(businessKey, processor);
        }

        return response;
    }

    @Override
    public BaseBusinessResponse process(String businessKey,
                                        byte[] rpcRequestBytes, BusinessMonitor monitor)
            throws BusinessException {
        CommandRequest request = requestMap.get(businessKey).fromBytes(rpcRequestBytes);
        return process(businessKey, request, monitor);
    }

    @Override
    public void destroy() {
        requestMap.clear();
        processorMap.clear();
        processorPool.destroy();
    }

    private static class AsyncProcessorPool {
        private int maxCount;
        private Map<String, List<CommandProcessor>> processorPool = new HashMap<String, List<CommandProcessor>>();
        private Map<String, Integer> processorCountMap = Collections
                .synchronizedMap(new HashMap<String, Integer>());

        public AsyncProcessorPool(int maxCount) {
            this.maxCount = maxCount;
        }

        public void initPool(Map<String, CommandProcessor> processorMap) {
            List<CommandProcessor> processList;
            for (Entry<String, CommandProcessor> entry : processorMap.entrySet()) {
                processList = Collections
                        .synchronizedList(new LinkedList<CommandProcessor>());
                processList.add(entry.getValue());
                processorCountMap.put(entry.getKey(), 0);
                processorPool.put(entry.getKey(), processList);
            }
        }

        public CommandProcessor getProcessor(String businessKey)
                throws BusinessCorrespondingException {
            CommandProcessor processor;
            try {
                processor = processorPool.get(businessKey).remove(1);
                return processor;
            } catch (RuntimeException ex) {
                LOG.info("Create a new async processor for '" + businessKey
                        + "'. Current size is: "
                        + processorCountMap.get(businessKey));
                try {
                    processorCountMap.put(businessKey,
                            processorCountMap.get(businessKey) + 1);
                    return processorPool.get(businessKey).get(0).getClass()
                            .newInstance();
                } catch (InstantiationException e) {
                    throw new BusinessCorrespondingException(e);
                } catch (IllegalAccessException e) {
                    throw new BusinessCorrespondingException(e);
                }
            }
        }

        public void putProcessor(String businessKey, CommandProcessor processor) {
            int count = processorPool.get(businessKey).size();
            if (count < maxCount) {
                processorPool.get(businessKey).add(processor);
            }
        }

        public synchronized void destroy() {
            // TODO destroy pool
        }
    }

    public static CommandProcessorFactory newCommandProcessorFactory(
            SingleBusinessTypeConfiguration singleTypeConfiguration,
            CacheTool cacheTool) throws BusinessException {
        if (null == factory) {
            factory = new CommandProcessorFactory(singleTypeConfiguration,
                    cacheTool);
        }
        return factory;
    }

    /**
     * DON'T USE IT unless you want to create an new factory instance next time you call {@link #newCommandProcessorFactory(SingleBusinessTypeConfiguration, CacheTool)}.
     * And you are sure that you DO NOT need the factory instance existing now.
     * This will reset factory which will make factory=null
     * It should be called ONLY before reboot.
     */
    public static void reset() {
    	LOG.info("Reset factory :" + CommandProcessorFactory.class.getSimpleName());
        factory = null;
    }
}
