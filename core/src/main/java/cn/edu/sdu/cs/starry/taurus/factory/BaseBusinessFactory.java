package cn.edu.sdu.cs.starry.taurus.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessConfigurationException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SimpleClassBaseConf;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * @author SDU.xccui
 */
public abstract class BaseBusinessFactory {
    private static final Logger LOG = LoggerFactory
            .getLogger(BaseBusinessFactory.class);
    protected volatile int resource = 100;// 100 for default
    protected CacheTool cacheTool;

    protected BaseBusinessFactory(int resource, CacheTool cacheTool) {
        this.resource = resource;
        this.cacheTool = cacheTool;
    }

    /**
     * Process business request.
     *
     * @param businessKey
     * @param request
     * @param monitor
     * @return
     * @throws BusinessException if encountered problem during process
     * @see #process(String, byte[], BusinessMonitor)
     */
    public abstract BaseBusinessResponse process(String businessKey,
                                                 BaseBusinessRequest request, BusinessMonitor monitor)
            throws BusinessException;

    /**
     * Process {@code byte[]} format business request.
     *
     * @param businessKey
     * @param requestBytes
     * @param monitor
     * @return
     * @throws BusinessException if encountered problem during process
     * @see #process(String, BaseBusinessRequest, BusinessMonitor)
     */
    public abstract BaseBusinessResponse process(String businessKey,
                                                 byte[] requestBytes, BusinessMonitor monitor)
            throws BusinessException;

    public int getResource() {
        return resource;
    }

    /**
     * Check whether this factory is overloading.
     *
     * @return {@code true} if resource remains is nonpositive<br/>
     * {@code false} if resource remains is positive
     */
    public boolean isOverloading() {
        return resource < 0;
    }

    /**
     * Destroy the factory.
     */
    public abstract void destroy();

    protected Object genConfObject(String desName, String businessName,
                                   String className, SimpleClassBaseConf baseConf)
            throws BusinessConfigurationException {
        try {
            if (null != className) {
                if ("default".equals(className)) {
                    if (null == baseConf || null == baseConf.getDefaultClass()) {
                        throw new BusinessConfigurationException(
                                "Can't find default class for " + desName);
                    } else {
                        LOG.info("For business '" + businessName
                                + "', using class '"
                                + baseConf.getDefaultClass() + "' for "
                                + desName);
                        return Class.forName(baseConf.getDefaultClass())
                                .newInstance();
                    }
                } else {
                    LOG.info("For business '" + businessName
                            + "', using class '" + className + "' for "
                            + desName);
                    return Class.forName(className).newInstance();

                }
            } else {
                if (null == baseConf || null == baseConf.getBasePath()) {
                    throw new BusinessConfigurationException(
                            "Can't find base path for " + desName);
                } else {
                    String prefix = (null == baseConf.getPrefix()) ? ""
                            : baseConf.getPrefix();
                    String suffix = (null == baseConf.getSuffix()) ? ""
                            : baseConf.getSuffix();
                    LOG.info("For business '" + businessName
                            + "', using class '" + baseConf.getBasePath() + "."
                            + prefix + businessName + suffix + "' for "
                            + desName);
                    return Class.forName(
                            baseConf.getBasePath() + "." + prefix
                                    + businessName + suffix).newInstance();

                }
            }
        } catch (InstantiationException e) {
            throw new BusinessConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessConfigurationException(e);
        } catch (ClassNotFoundException e) {
            throw new BusinessConfigurationException(e);
        }
    }

    protected synchronized void addResource(int r) {
        resource += r;
    }

    protected synchronized void minResource(int r) {
        resource -= r;
    }
}
