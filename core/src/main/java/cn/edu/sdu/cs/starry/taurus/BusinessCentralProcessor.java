package cn.edu.sdu.cs.starry.taurus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.factory.CommandProcessorFactory;
import cn.edu.sdu.cs.starry.taurus.factory.QueryWorkerFactory;
import cn.edu.sdu.cs.starry.taurus.factory.TimerProcessorFactory;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * Business central processor.
 *
 * @author SDU.xccui
 */
public class BusinessCentralProcessor {

    private static final Logger LOG = LoggerFactory
            .getLogger(BusinessCentralProcessor.class);
    private QueryWorkerFactory syncWorkerFactory;
    private CommandProcessorFactory asyncProcessorFactory;
    private TimerProcessorFactory timerProcessorFactory;
    private Map<BusinessType, Boolean> businessEnabledMap;

    public BusinessCentralProcessor() {
        businessEnabledMap = Collections
                .synchronizedMap(new HashMap<BusinessType, Boolean>());
        // set type enable
        for (BusinessType businessType : BusinessType.values()) {
            businessEnabledMap.put(businessType, true);
        }
    }

    /**
     * Initialize a business type in the central processor.
     *
     * @throws BusinessException
     */
    public void initialize(
            SingleBusinessTypeConfiguration singleTypeConfiguration,
            CacheTool cacheTool) throws BusinessException {
        switch (BusinessType.valueOf(singleTypeConfiguration.getName())) {
            case QUERY:
                LOG.info("Initialize central processor for business type: 'SYNC'");
                syncWorkerFactory = QueryWorkerFactory.newQueryWorkerFactory(
                        singleTypeConfiguration, cacheTool);
                break;
            case COMMAND:
                LOG.info("Initialize central processor for business type: 'ASYNC'");
                asyncProcessorFactory = CommandProcessorFactory
                        .newCommandProcessorFactory(singleTypeConfiguration,
                                cacheTool);
                break;
            case TIMER:
                LOG.info("Initialize central processor for business type: 'TIMER'");
                timerProcessorFactory = TimerProcessorFactory
                        .newTimerProcessorFactory(singleTypeConfiguration,
                                cacheTool);
                break;
        }
    }

    /**
     * Process the given {@code byte[]} of request with a businessKey and a
     * optional businessMonitor.
     *
     * @param businessKey
     * @param requestBytes
     * @param businessMonitor
     * @return a generated response
     * @throws BusinessException when encountered some exceptions during process
     * @see #process(String, BaseBusinessRequest, BusinessMonitor)
     */
    public BaseBusinessResponse process(String businessKey,
                                        byte[] requestBytes, BusinessMonitor businessMonitor)
            throws BusinessException {
        BaseBusinessResponse response = null;
        BusinessType businessType = BusinessTypeManager.businessTypeMap
                .get(businessKey);
        switch (businessType) {
            case QUERY:
                response = syncWorkerFactory.process(businessKey, requestBytes,
                        businessMonitor);
                break;
            case COMMAND:
                response = asyncProcessorFactory.process(businessKey, requestBytes,
                        businessMonitor);
                break;
            case TIMER:
                response = timerProcessorFactory.process(businessKey, requestBytes,
                        businessMonitor);
                break;
            default:
                throw new BusinessCorrespondingException("Unknown business key: '"
                        + businessKey + "'");
        }
        return response;
    }

    /**
     * Process the given request with a businessKey and a optional
     * businessMonitor.
     *
     * @param businessKey
     * @param request
     * @param businessMonitor
     * @return a generated response
     * @throws BusinessException when encountered some exceptions during process
     * @see #process(String, byte[], BusinessMonitor)
     */
    public BaseBusinessResponse process(String businessKey,
                                        BaseBusinessRequest request, BusinessMonitor businessMonitor)
            throws BusinessException {
        BaseBusinessResponse response = null;
        BusinessType businessType = BusinessTypeManager.businessTypeMap
                .get(businessKey);
        switch (businessType) {
            case QUERY:
                response = syncWorkerFactory.process(businessKey, request,
                        businessMonitor);
                break;
            case COMMAND:
                response = asyncProcessorFactory.process(businessKey, request,
                        businessMonitor);
                break;
            case TIMER:
                response = timerProcessorFactory.process(businessKey, request,
                        businessMonitor);
                break;
            default:
                throw new BusinessCorrespondingException("Unknown business key: '"
                        + businessKey + "'");
        }
        return response;
    }

    /**
     * Check whether the dedicated businessType's factory is overloading. Note
     * that if the given businessType's processing is disabled, this method will
     * return {@code false}.
     *
     * @param businessType
     * @return {@code true} if the given businessType's processing is enabled
     * and the factory is not overloading.<br/>
     * {@code false} if the given businessType's processing is disabled
     * or the factory is overloading .
     */
    public boolean isOverLoading(BusinessType businessType) {
        boolean typeEnable = businessEnabledMap.get(businessType);
        switch (businessType) {
            case QUERY:
                return !typeEnable || syncWorkerFactory.isOverloading();
            case COMMAND:
                return !typeEnable || asyncProcessorFactory.isOverloading();
            case TIMER:
                return !typeEnable || timerProcessorFactory.isOverloading();
            default:
                return false;
        }
    }

    /**
     * Get an integer value representing the given businessType's resource.
     *
     * @param businessType
     * @return an integer representing the resource
     */
    public int getCurrentFactoryResource(BusinessType businessType) {
        switch (businessType) {
            case QUERY:
                return syncWorkerFactory.getResource();
            case COMMAND:
                return asyncProcessorFactory.getResource();
            case TIMER:
                return timerProcessorFactory.getResource();
            default:
                return 0;
        }
    }

    /**
     * Enable or disable processing the given businessType's requests.
     *
     * @param businessType
     * @param whetherEnable
     */
    public void setBusinessEnable(BusinessType businessType,
                                  boolean whetherEnable) {
        businessEnabledMap.put(businessType, whetherEnable);
    }

    /**
     * Return whether this given businessType's processing is enabled.
     *
     * @param businessType
     * @return {@code true} if it's enabled.<br/>
     * {@code false} if it's disabled.
     */
    public boolean isBusinessEnabled(BusinessType businessType) {
        return businessEnabledMap.get(businessType);
    }

    /**
     * Destroy the central processor.
     */
    public void destroy() {
        LOG.info("Business central processor will be destroyed");
        syncWorkerFactory.destroy();
        asyncProcessorFactory.destroy();
        timerProcessorFactory.destroy();
    }
}
