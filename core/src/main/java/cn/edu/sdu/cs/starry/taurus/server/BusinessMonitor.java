package cn.edu.sdu.cs.starry.taurus.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessInterruptedException;
import cn.edu.sdu.cs.starry.taurus.processor.BaseProcessor;

/**
 * This monitor is used to keep a watch on a dedicated business processor's
 * progress. You can keep this monitor and use it to stop the monitored
 * processor.(need supported by processor's implementation)
 *
 * @author SDU.xccui
 */
public class BusinessMonitor extends Thread {

    private static final Logger LOG = LoggerFactory
            .getLogger(BusinessMonitor.class);
    private BaseProcessor processor;
    private BusinessReporter reporter;
    private long reportInterval;
    private long longestProcessTime;
    private boolean shouldStop;
    private UUID uniqueId;
    private RequestAndIdentification requestAndIdentification;

    /**
     * Fully constructor for this monitor
     *
     * @param reportInterval
     * @param longestProcessTime       a millisecond time for the longest process time. If the
     *                                 processor's escaped time exceed this time, a
     *                                 {@link BusinessInterruptedException} will be thrown. <br/>
     *                                 A nonpositive time means no time limit.
     * @param requestAndIdentification
     * @param reporter
     */
    public BusinessMonitor(long reportInterval, long longestProcessTime,
                           RequestAndIdentification requestAndIdentification,
                           BusinessReporter reporter) {
        this.processor = null;
        this.uniqueId = null;
        this.reportInterval = reportInterval;
        if (longestProcessTime > 0) {
            this.longestProcessTime = longestProcessTime;
        } else {
            this.longestProcessTime = Long.MAX_VALUE;
        }
        this.reporter = reporter;
        this.requestAndIdentification = requestAndIdentification;
    }

    /**
     * Set the processor to monitor.
     *
     * @param processor
     */
    public void setProcessor(BaseProcessor processor) {
        if (null == this.processor) {
            this.processor = processor;
            LOG.debug("Set monitor '" + uniqueId + "' for processor '"
                    + processor.getClass().getName() + "'.");
        }
    }

    /**
     * Set an unique ID for this monitor.
     *
     * @param uniqueId
     */
    public void setUUID(UUID uniqueId) {
        if (null == this.uniqueId) {
            this.uniqueId = uniqueId;
        }
    }

    /**
     * Get the unique ID for this monitor.
     *
     * @return
     */
    public UUID getUUID() {
        return uniqueId;
    }

    /**
     * Stop the monitored worker if possible and also invoke
     * {@link #closeMonitor()} to close this monitor.
     */
    public void stopWork() {
        processor.stop(this);
        closeMonitor();
    }

    /**
     * Stop monitoring the given business processor.
     */
    public void closeMonitor() {
        shouldStop = true;
    }

    @Override
    public void run() {
        while (!shouldStop && processor.getProcessRate() != 1) {

            if (null != reporter) {
                reporter.report(processor, requestAndIdentification);
            }
            if (processor.getEscapedTime() > longestProcessTime) {
                stopWork();
            }
            try {
                Thread.sleep(reportInterval);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
}
