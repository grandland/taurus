package cn.edu.sdu.cs.starry.taurus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessResponseHandlerException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitorManager;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;

/**
 * Handle a dedicated type of business.
 *
 * @author SDU.xccui
 */
public class SingleBusinessTypeHandler implements Runnable {
    private static final Logger LOG = LoggerFactory
            .getLogger(SingleBusinessTypeHandler.class);

    private static Map<BusinessType, SingleBusinessTypeHandler> businessTypeHandlerMap = new HashMap<BusinessType, SingleBusinessTypeHandler>();
    private BusinessType businessType;
    private SingleBusinessTypeConfiguration singleTypeConfig;
    private long exceptionSleepTime;
    private BusinessRequestProvider requestProvider;
    private BusinessCentralProcessor centralProcessor;
    private BusinessResponseHandler responseHandler;
    private boolean isOverloading;
    private boolean shouldStop;
    //private ConcurrentHashMap<UUID, BusinessMonitor> monitorMap;
    private Map<UUID, BusinessMonitor> monitorMap;
    private String taurusServerName;
    private int shutdownWait;

    private SingleBusinessTypeHandler(BusinessType businessType,
                                      BusinessRequestProvider requestProvider,
                                      BusinessCentralProcessor businessProcessor,
                                      BusinessResponseHandler responseHandler,
                                      SingleBusinessTypeConfiguration singleTypeConfig,
                                      String taurusServerName, int shutdownWait) {
        shouldStop = false;
        isOverloading = false;
//        monitorMap = new ConcurrentHashMap<UUID, BusinessMonitor>();
        monitorMap = BusinessMonitorManager.getBusinessMonitorManager().getMonitorMap(businessType);
        this.businessType = businessType;
        this.requestProvider = requestProvider;
        this.centralProcessor = businessProcessor;
        this.responseHandler = responseHandler;
        this.exceptionSleepTime = singleTypeConfig.getSleepTime();
        this.singleTypeConfig = singleTypeConfig;
        this.taurusServerName = taurusServerName;
        this.shutdownWait = shutdownWait;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setTaurusServerName(String taurusServerName) {
        this.taurusServerName = taurusServerName;
    }

    @Override
    public void run() {
        LOG.info("For business type: '" + businessType
                + "' handler started running");
        if (!centralProcessor.isBusinessEnabled(businessType)) {
            LOG.info("For business type: '" + businessType
                    + "' is not enable. Will stop.");
            return;
        }
        RequestAndIdentification requestAndIndentification;
        UUID uniqueId;
        try {
            requestProvider.prepare(singleTypeConfig);
            requestProvider.start();
        } catch (BusinessRequestProviderException ex) {
            ex.printStackTrace();
            LOG.error("For business type: '" + businessType
                    + "', provider start failed. Will stop.");
            stop();
        }
        isOverloading = centralProcessor.isOverLoading(businessType);
        while (!shouldStop) {
            while (!isOverloading && !shouldStop) {
                // start a new thread to handle a request
                try {
                    requestAndIndentification = requestProvider.next();
                    uniqueId = UUID.randomUUID();
                    Thread handleThread = new Thread(new SingleBusinessHandler(
                            businessType, centralProcessor,
                            requestAndIndentification, responseHandler,
                            uniqueId, monitorMap, taurusServerName));
                    handleThread.setName("Thread-business handler " + uniqueId);
                    handleThread.start();
                } catch (BusinessRequestProviderException ex) {
                    ex.printStackTrace();
                    LOG.error("Provider error! " + ex.getMessage()
                            + ". Will sleep 10s and retry.");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isOverloading = centralProcessor.isOverLoading(businessType);
            }
            if (shouldStop)
                break;
            LOG.warn("For business type '" + businessType
                    + "', factory is overloading. Try to pause provider");
            // overloading, pause provider
            while (requestProvider.getState() != BusinessRequestProvider.PAUSED_STATE
                    && !shouldStop) {
                try {
                    requestProvider.pause();
                    LOG.info("For business type '" + businessType
                            + "', provider paused.");
                } catch (BusinessRequestProviderException e) {
                    e.printStackTrace();
                    if (shouldStop)
                        return;
                    try {
                        Thread.sleep(exceptionSleepTime);
                    } catch (InterruptedException ex) {
                        LOG.error(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            while (isOverloading && !shouldStop) {
                try {
                    LOG.warn("For business type '"
                            + businessType
                            + "', factory is still overloading. Will re-check in "
                            + exceptionSleepTime + "ms");
                    Thread.sleep(exceptionSleepTime);
                    isOverloading = centralProcessor
                            .isOverLoading(businessType);
                } catch (InterruptedException ex) {
                    LOG.error(ex.getMessage());
                    ex.printStackTrace();
                }
            }
            LOG.info("For business type '"
                    + businessType
                    + "', factory is recovered from overloading. Current factory resource is "
                    + centralProcessor.getCurrentFactoryResource(businessType)
                    + ". Try to resume provider.");
            while (requestProvider.getState() != BusinessRequestProvider.STARTED_STATE
                    && !shouldStop) {
                try {
                    requestProvider.resume();
                    LOG.info("For business type '" + businessType
                            + "', provider resumed.");
                } catch (BusinessRequestProviderException e) {
                    LOG.warn("For business type: " + businessType
                            + ", provider resume failed. Will retry in "
                            + exceptionSleepTime + "ms.");
                    e.printStackTrace();
                    try {
                        Thread.sleep(exceptionSleepTime);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        LOG.info("For business type: '"
                + businessType
                + "', handler stopped. Will stop request provider and response handler.");
        // received a stop command
        try {
            requestProvider.stop();
            LOG.info("For business type: " + businessType + " provider stopped");
        } catch (BusinessRequestProviderException e) {
            LOG.warn("For business type: '" + businessType
                    + "' request provider stop failed!");
            e.printStackTrace();
        }
        try {
            int waitTime = shutdownWait;
            while (monitorMap.size() > 0 && waitTime > 0) {
                LOG.warn("Can't stop response handler for business type: '"
                        + businessType + "'. Waiting " + monitorMap.size()
                        + " processes to stop");
                try {
                    Thread.sleep(5000);
                    waitTime -= 5;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (monitorMap.size() > 0) {
                LOG.warn("There are "
                        + monitorMap.size()
                        + " processors working. But has exceeded shutdown waiting time ("
                        + shutdownWait + "s), will force stop responseHandler.");
            }
            responseHandler.stop();
            LOG.info("For business type: " + businessType
                    + " response handler stopped");
        } catch (BusinessResponseHandlerException e) {
            LOG.error("For business type: '" + businessType
                    + "' response handler stop failed!");
            e.printStackTrace();
        }
    }

    /**
     * Stop this business type handler.
     */
    public void stop() {
        shouldStop = true;
        LOG.info("For business type: '" + businessType
                + "', received stop command");
    }

    /**
     * Reset the shouldStop flag and isOverloading flag
     */
    public static void reset() {
    	LOG.info("Clear businessTypeHandlerMap");
        businessTypeHandlerMap.clear();
    }


    /**
     * Create a new handler for the dedicated business type.
     *
     * @param businessType
     * @param requestProvider
     * @param centralProcessor
     * @param responseHandler
     * @param singleTypeConfig
     * @param taurusServerName
     * @param shutdownWait
     * @return
     */

    public static SingleBusinessTypeHandler newSingleTypeHandler(
            BusinessType businessType, BusinessRequestProvider requestProvider,
            BusinessCentralProcessor centralProcessor,
            BusinessResponseHandler responseHandler,
            SingleBusinessTypeConfiguration singleTypeConfig,
            String taurusServerName, int shutdownWait) {
        SingleBusinessTypeHandler handler = businessTypeHandlerMap
                .get(businessType);
        if (null == handler) {
            synchronized (SingleBusinessTypeHandler.class) {
                if (null == (handler = businessTypeHandlerMap.get(businessType))) {
                    handler = new SingleBusinessTypeHandler(businessType,
                            requestProvider, centralProcessor, responseHandler,
                            singleTypeConfig, taurusServerName, shutdownWait);
                    businessTypeHandlerMap.put(businessType, handler);
                }
            }

        }
        return handler;
    }
}
