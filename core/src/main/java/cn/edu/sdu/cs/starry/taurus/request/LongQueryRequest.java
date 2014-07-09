package cn.edu.sdu.cs.starry.taurus.request;

import java.util.List;
import java.util.UUID;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

/**
 * This class is for long query request with 
 * */
public abstract class LongQueryRequest extends BaseBusinessRequest{

    private int beginNumber;
    private int number;
    
    //When set by user. It means start page.
    private int page = 0;
    //page size per page.
    private int pageSize = 0;
     
    /**
     * Identify this request*/
    private String requestId;

    public LongQueryRequest() {
        super();
    }

    public LongQueryRequest(int beginNumber, int number,RequestInfo info) {
    	this(beginNumber,number,null,info);
    }
    
    public LongQueryRequest(int beginNumber, int number, String requestId, RequestInfo info) {
        super(info.getSessionId(), info.getUserName(), info.getUserIp());
        this.beginNumber = beginNumber;
        this.number = number;
        sessionId = info.getSessionId();
    	this.requestId = requestId;
    }

    public int getBeginNumber() {
        return beginNumber;
    }

    public int getNumber() {
        return number;
    }
    
    public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public final String getRequestId(){
    	if(requestId == null){
    		requestId = genRequestId();
    	}
    	return requestId;
    }
    
    public void setRequestId(String requestId){
    	this.requestId = requestId;
    }
    
    private String genRequestId(){
    	return UUID.randomUUID().toString();
    }

    /**
     * Return an unique key for cache use. This key will <b>not</b> be refreshed
     * when attributes changed.
     *
     * @param withSessionId put sessionId in this key or not
     * @param withPage      put page info in this key or not
     * @return null if cache is disabled
     */
    public String getRequestKey(boolean withSessionId, boolean withPage) {
    	
        if (null == requestKey) {
            requestKey = genUniqueRequestKey();
        }
        if (null == requestKey)
            return null;
        StringBuilder keyBuilder = new StringBuilder();
        if (withSessionId) {
            if (null == sessionId)
                return null;
            keyBuilder.append(sessionId + "&");
        }
        keyBuilder.append(this.getClass().getName() + "&" + requestKey);
        if (withPage) {
            keyBuilder.append("&" + beginNumber + "&" + number);
        }
        return keyBuilder.toString();
    }

    @Override
    public final LongQueryRequest fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (LongQueryRequest) super.fromBytes(bytes);
    }
    
    public final List<SubQueryRequest> getSplitQuery(){
    	List<SubQueryRequest> requests = splitQuery();
    	for(int i = 0 ; i < requests.size() ; i++){
    		SubQueryRequest request = requests.get(i);
    		request.setRequestId(requestId);
    		request.setIndex(i);
    	}
    	return requests;
    }
    
    public abstract List<SubQueryRequest> splitQuery();
}