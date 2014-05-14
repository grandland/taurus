package cn.edu.sdu.cs.starry.taurus.server;

import cn.edu.sdu.cs.starry.taurus.common.RequestIdentification;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;

/**
 * Encapsulate a businessKey, a {@link BaseBusinessRequest} (or a {@code byte[]}
 * ), it's identification and a optional {@link BusinessMonitor}.
 * 
 * @author SDU.xccui
 * 
 */
public class RequestAndIdentification {

	private byte[] requestBytes;
	private BaseBusinessRequest request;
	private RequestIdentification identification;
	private String businessKey;
	private BusinessMonitor monitor;

	public RequestAndIdentification(byte[] requestBytes,
                                    RequestIdentification identification, String businessKey) {
		this.requestBytes = requestBytes;
		this.identification = identification;
		this.businessKey = businessKey;
	}

	public RequestAndIdentification(BaseBusinessRequest request,
                                    RequestIdentification identification, String businessKey) {
		this.request = request;
		this.identification = identification;
		this.businessKey = businessKey;
	}

	public byte[] getRequestBytes() {
		if (null != requestBytes) {
			return requestBytes;
		} else if (null != request) {
			return request.toBytes();
		}
		return null;
	}

	public void createInnerMonitor(long reportInterval,
			long longestprocessTime, BusinessReporter reporter) {
		monitor = new BusinessMonitor(reportInterval, longestprocessTime, this,
				reporter);
	}

	public BaseBusinessRequest getRequest() {
		return request;
	}

	public RequestIdentification getIdentification() {
		return identification;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public BusinessMonitor getMonitor() {
		return monitor;
	}

	public String toString() {
		return "[business key: '" + businessKey + "'; identification: '"
				+ identification + "']";
	}
}
