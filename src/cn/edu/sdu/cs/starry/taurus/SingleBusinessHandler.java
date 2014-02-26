package cn.edu.sdu.cs.starry.taurus;

import java.util.Map;
import java.util.UUID;

import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import cn.edu.sdu.cs.starry.taurus.server.ResponseAndIdentification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessResponseHandlerException;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;

/**
 * Can be used to handle a single business request.
 *
 * @author SDU.xccui
 */
public class SingleBusinessHandler implements Runnable {
    private static final Logger LOG = LoggerFactory
            .getLogger(SingleBusinessHandler.class);
    private BusinessCentralProcessor businessProcessor;
    private BusinessResponseHandler responseHandler;
    private RequestAndIdentification requestAndIndentification;
    private UUID uniqueId;
    private Map<UUID, BusinessMonitor> monitorMap;
    private BusinessMonitor monitor;
    private BusinessType businessType;
    private String taurusServerName;

    public SingleBusinessHandler(BusinessType businessType,
                                 BusinessCentralProcessor businessProcessor,
                                 RequestAndIdentification requesetAndIndentification,
                                 BusinessResponseHandler responseHandler, UUID uniqueId,
                                 Map<UUID, BusinessMonitor> monitorMap, String taurusServerName) {
        this.businessType = businessType;
        this.businessProcessor = businessProcessor;
        this.requestAndIndentification = requesetAndIndentification;
        this.responseHandler = responseHandler;
        this.uniqueId = uniqueId;
        this.monitorMap = monitorMap;
        this.taurusServerName = taurusServerName;
    }

    @Override
    public void run() {
        BusinessType requestType = BusinessTypeManager
                .decideBusinessType(requestAndIndentification.getBusinessKey());
        if (null == requestType) {
            LOG.error("Received an unknown request" + requestAndIndentification
                    + " Will ignore it");
            responseHandler.handleException(requestAndIndentification,
                    new BusinessCorrespondingException(
                            "Received an unknown request"
                                    + requestAndIndentification
                                    + " Will ignore it"));
        }
        if (null == requestAndIndentification.getIdentification()) {
            LOG.error("Received a request with no identification. Will ignore it");
            responseHandler
                    .handleException(
                            requestAndIndentification,
                            new BusinessRequestProviderException(
                                    "Received a request with no identification. Will ignore it"));
        }
        requestAndIndentification.getIdentification().setTaurusServerName(
                taurusServerName);
        if (!requestType.equals(businessType)) {
            LOG.error("Received a "
                    + BusinessTypeManager
                    .decideBusinessType(requestAndIndentification
                            .getBusinessKey()) + " type reuqest "
                    + requestAndIndentification + " from " + businessType
                    + " privider! Will ignore it.");
            responseHandler
                    .handleException(
                            requestAndIndentification,
                            new BusinessCorrespondingException(
                                    "Received a "
                                            + BusinessTypeManager
                                            .decideBusinessType(requestAndIndentification
                                                    .getBusinessKey())
                                            + " type reuqest "
                                            + requestAndIndentification
                                            + " from " + businessType
                                            + " privider! Will ignore it."));
        } else {
            BaseBusinessResponse response = null;
            monitor = requestAndIndentification.getMonitor();
            if (null != monitor) {
                monitor.setUUID(uniqueId);
            }
            monitorMap.put(uniqueId, monitor);
            try {
                if (null != requestAndIndentification.getRequest()) {
                    response = businessProcessor.process(
                            requestAndIndentification.getBusinessKey(),
                            requestAndIndentification.getRequest(), monitor);
                } else if (null != requestAndIndentification
                        .getRequestBytes()) {
                    response = businessProcessor.process(
                            requestAndIndentification.getBusinessKey(),
                            requestAndIndentification.getRequestBytes(),
                            monitor);
                } else {
                    throw new BusinessResponseHandlerException(
                            "request identification="
                                    + requestAndIndentification
                                    .getIdentification() + " is a null");
                }
                ResponseAndIdentification responseAndIndentification = new ResponseAndIdentification(
                        response,
                        requestAndIndentification.getIdentification(),
                        requestAndIndentification.getBusinessKey());
                responseHandler.handleResponse(responseAndIndentification);
            } catch (BusinessException ex) {
                //ex.printStackTrace();
                responseHandler.handleException(requestAndIndentification, ex);
            } finally {
                monitorMap.remove(uniqueId);
            }
        }
    }
}
