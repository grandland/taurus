package cn.edu.sdu.cs.starry.taurus.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessInterruptedException;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;

/**
 * @author SDU.xccui
 */
public abstract class BaseProcessor {
    private static final Logger LOG = LoggerFactory
            .getLogger(BaseProcessor.class);
    private static final long MONITOR_WAITING_TIME = 10000;
    protected long startTime;
    // for process control
    private float processRate;
    private boolean shouldStop;

    /**
     * Prepare this processor for working.
     */
    public void prepare() {
        LOG.debug("processor prepare");
        startTime = System.currentTimeMillis();
        shouldStop = false;
        processRate = 0;
    }

    /**
     * Check whether the current processor received a stop command.
     *
     * @return
     */
    protected boolean shouldStop() {
        return shouldStop;
    }

    /**
     * Return millisecond time escaped from {@link #prepare()} to now.
     *
     * @return
     */
    public long getEscapedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Send a stop command to current processor.
     *
     * @param monitor monitor sent the stop command
     */
    public void stop(BusinessMonitor monitor) {
        LOG.info("processor received a stop command by monitor "
                + monitor.getUUID());
        shouldStop = true;
    }

    /**
     * Return the process rate if supported by process implementation.
     *
     * @return
     */
    public float getProcessRate() {
        return processRate;
    }

    protected void setProcessRate(float processRate)
            throws BusinessInterruptedException {
        if (processRate >= 0 && processRate <= 1) {
            this.processRate = processRate;
        }
        if (shouldStop) {
            LOG.warn("processor is interrupted by a stop command");
            throw new BusinessInterruptedException();
        }

    }

    /**
     * Just clean the current processor.
     */
    public void clean() {
        processRate = 1;
        cleanProcessor();
    }

    /**
     * Clean the current processor and wait the monitor thread to die.
     *
     * @param monitor the monitor thread for waiting
     */
    public void cleanWithMonitor(Thread monitor)
            throws BusinessInterruptedException {
        clean();
        try {
            monitor.interrupt();
            monitor.join(MONITOR_WAITING_TIME);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new BusinessInterruptedException(ex);
        }
    }

    public abstract BaseBusinessResponse process(BaseBusinessRequest request)
            throws BusinessException;

    /**
     * Clean the processor. Make it forget what it's done this time.
     */
    protected abstract void cleanProcessor();

    public abstract String getAuthor();

    /**
     * Return an integer for calculating size of the processor pool.
     *
     * @return
     */
    public abstract int getMinSystemResource();
}
