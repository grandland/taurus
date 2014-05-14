package cn.edu.sdu.cs.starry.taurus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;

/**
 * This is a container for {@link SingleBusinessTypeHandler}.
 *
 * @author SDU.xccui
 */
public class BusinessHandlerContainer {
    private static final Logger LOG = LoggerFactory
            .getLogger(BusinessHandlerContainer.class);
    private Map<BusinessType, SingleBusinessTypeHandler> typeHandlerMap;
    /**
     * Used for keep started typeHandler threads reference,
     * in order to get their running state when trying stop them*/
    private Map<BusinessType, Thread> typeHandlerThreadMap;

    public BusinessHandlerContainer() {
        typeHandlerMap = Collections
                .synchronizedMap(new HashMap<BusinessType, SingleBusinessTypeHandler>());
        typeHandlerThreadMap = Collections.
                synchronizedMap(new HashMap<BusinessType, Thread>());
    }

    /**
     * Initialize the container.
     */
    public void initialize() {
        LOG.info("Initialize");
    }

    /**
     * Register a new type of handler.
     *
     * @param singleTypeHandler
     */
    public void registerTypeHandler(SingleBusinessTypeHandler singleTypeHandler) {
        if (null != singleTypeHandler) {
            LOG.info("For business type: '"
                    + singleTypeHandler.getBusinessType()
                    + "', set type handler");
            typeHandlerMap.put(singleTypeHandler.getBusinessType(),
                    singleTypeHandler);
        } else {
            LOG.warn("Set type handler failed! For given typeHandler is null!");
        }
    }

    /**
     * Remove and return the handler for the given business type. If it's
     * registered, this method will first invoke {@link #stop(BusinessType)} to
     * stop handling.
     *
     * @param businessType
     * @return {@code null} if the given business type's handler is not
     * registered. <br/>
     * The removed {@link SingleBusinessTypeHandler} if it's registered.
     */
    public SingleBusinessTypeHandler removeTypeHandler(BusinessType businessType) {
        LOG.info("For business type: '" + businessType
                + "', remove type handler.");
        stop(businessType);
        return typeHandlerMap.remove(businessType);
    }

    /**
     * Make all registered businessTypeHandlers start running.
     */
    public void startAll() {
        LOG.info("Start all business handlers");
        Iterator<BusinessType> iterator = typeHandlerMap.keySet().iterator();
        synchronized (typeHandlerMap) {
            while (iterator.hasNext()) {
                start(iterator.next());
            }
        }
    }

    /**
     * Start the given business type's handler.
     *
     * @param businessType
     */
    public void start(BusinessType businessType) {
        SingleBusinessTypeHandler handler = typeHandlerMap.get(businessType);
        if (null != handler) {
            synchronized (handler) {
                Thread thread = new Thread(handler);
                typeHandlerThreadMap.put(businessType, thread);
                thread.setName("Thread-'" + businessType + "' type handler");
                thread.start();
            }
        } else {
            LOG.warn("For business type: '"
                    + businessType
                    + "', starting failed. Because business handler is not set yet.");
        }

    }

    /**
     * Stop the given business type's handler.
     *
     * @param businessType
     */
    public void stop(final BusinessType businessType) {
        LOG.info("For business type: '" + businessType
                + "', stop business handler");
        Thread thread = new Thread() {
            public void run() {
                SingleBusinessTypeHandler typeHandler = typeHandlerMap
                        .get(businessType);
                if (null != typeHandler) {
                    synchronized (typeHandler) {
                        typeHandler.stop();
                    }
                } else {
                    LOG.warn("For business type: '"
                            + businessType
                            + "', stopping failed. Because business handler is not set yet.");
                }
            }
        };
        thread.setName("Thread-'" + businessType + "' type handler stop");
        thread.start();
    }

    /**
     * Stop all the registered singleBusinessTypehandlers.
     */
    public void stopAll() {
        LOG.info("Stop all business handlers.");
        Iterator<BusinessType> iterator = typeHandlerMap.keySet().iterator();
        synchronized (typeHandlerMap) {
            while (iterator.hasNext()) {
                stop(iterator.next());
            }
        }
    }

    public boolean isStopped(BusinessType businessType) {
        return !typeHandlerThreadMap.get(businessType).isAlive();
    }

    /**
     * Reset thread map.
     */
    public void reset() {
    	LOG.info("Clear typeHandlerThreadMap");
        typeHandlerThreadMap.clear();
    }
}
