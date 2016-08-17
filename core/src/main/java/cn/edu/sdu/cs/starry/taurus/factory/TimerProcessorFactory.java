package cn.edu.sdu.cs.starry.taurus.factory;

import java.util.HashMap;
import java.util.Map;

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
import cn.edu.sdu.cs.starry.taurus.processor.TimerProcessor;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.request.TimerRequest;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

public class TimerProcessorFactory extends BaseBusinessFactory {
    private static TimerProcessorFactory timerProcessorFactory;
    private static final Logger LOG = LoggerFactory
            .getLogger(TimerProcessorFactory.class);

    private Map<String, Class<? extends TimerProcessor>> timerProcessorClassMap;

    @SuppressWarnings("unchecked")
    private TimerProcessorFactory(
            SingleBusinessTypeConfiguration singleTypeConfiguration,
            CacheTool cacheTool) throws BusinessException {
        super(singleTypeConfiguration.getFactoryResource(), cacheTool);
        timerProcessorClassMap = new HashMap<String, Class<? extends TimerProcessor>>();
        String processorClass;
        for (SingleBusinessConf singleBusinessConf : singleTypeConfiguration
                .getBusinesses().values()) {
            processorClass = singleBusinessConf.getProcessorClass();
            timerProcessorClassMap
                    .put(singleBusinessConf.getName(),
                            (Class<? extends TimerProcessor>) genConfObject(
                                    "processor", singleBusinessConf.getName(),
                                    processorClass,
                                    singleTypeConfiguration.getProcessors())
                                    .getClass());
            BusinessTypeManager.businessTypeMap.put(
                    singleBusinessConf.getKey(), BusinessType.TIMER);
        }
    }

    @Override
    public BaseBusinessResponse process(String businessKey,
                                        BaseBusinessRequest request, BusinessMonitor monitor)
            throws BusinessException {
        TaurusMetrics.incTimerMeter();
        if (!(request instanceof TimerRequest)) {
            throw new BusinessCorrespondingException();
        }
        TimerRequest timerRequest = (TimerRequest) request;
        TimerProcessor processor = null;
        LOG.info("Received a TIMER request: [" + request.getUserName() + "@"
                + request.getUserIP() + ">>'" + request.getClass().getName()
                + "'>>" + timerRequest.getRequestKey() + "]");
        try {
            processor = timerProcessorClassMap.get(businessKey).newInstance();
            processor.prepare();
            BaseBusinessResponse response = processor.process(request);
            return response;
        } catch (InstantiationException e) {
            throw new BusinessProcessException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessProcessException(e);
        } finally {
            if (null != processor) {
                processor.cleanWithMonitor(monitor);
                processor = null;
            }
        }
    }

    @Override
    public BaseBusinessResponse process(String businessKey,
                                        byte[] requestBytes, BusinessMonitor monitor)
            throws BusinessException {
        throw new BusinessException("Received a byte array for TIMER business!");
    }

    @Override
    public void destroy() {
        timerProcessorClassMap.clear();
    }

    public static TimerProcessorFactory newTimerProcessorFactory(
            SingleBusinessTypeConfiguration singleTypeConfiguration,
            CacheTool cacheTool) throws BusinessException {
        if (null == timerProcessorFactory) {
            timerProcessorFactory = new TimerProcessorFactory(
                    singleTypeConfiguration, cacheTool);
        }
        return timerProcessorFactory;
    }

    /**
     * DON'T USE IT unless you want to create an new factory instance next time you call {@link #newCommandProcessorFactory(SingleBusinessTypeConfiguration, CacheTool)}.
     * And you are sure that you DO NOT need the factory instance existing now.
     * This will reset factory which will make factory=null
     * It should be called ONLY before reboot.
     */
    public static void reset() {
    	LOG.info("Reset factory :" + TimerProcessorFactory.class.getSimpleName());
        timerProcessorFactory = null;
    }
}
