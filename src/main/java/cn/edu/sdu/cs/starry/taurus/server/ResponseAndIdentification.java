package cn.edu.sdu.cs.starry.taurus.server;

import cn.edu.sdu.cs.starry.taurus.common.RequestIdentification;
import cn.edu.sdu.cs.starry.taurus.response.BaseBusinessResponse;

/**
 * Encapsulate a businessKey,a response and it's identification.
 *
 * @author SDU.xccui
 */
public class ResponseAndIdentification {

    private BaseBusinessResponse response;
    private RequestIdentification identification;
    private String businessKey;

    public ResponseAndIdentification(BaseBusinessResponse response,
                                     RequestIdentification identification, String businessKey) {
        this.response = response;
        this.identification = identification;
        this.businessKey = businessKey;
    }

    public byte[] getResponseBytes() {
        return response.toBytes();
    }

    public BaseBusinessResponse getResponse() {
        return response;
    }

    public RequestIdentification getIdentification() {
        return identification;
    }

    public String getBusinessKey() {
        return businessKey;
    }

}
