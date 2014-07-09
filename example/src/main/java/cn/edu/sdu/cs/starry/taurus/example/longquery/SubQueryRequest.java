package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.List;
import java.util.UUID;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.request.RequestInfo;

/**
 * This class is for long query request with 
 * */
public abstract class SubQueryRequest extends BaseBusinessRequest{

    private int beginNumber;
    private int number;
    
    /**
     * Identify this request*/
    private String requestId;
    /**
     * The order in LongQuery*/
    private int index;

    public SubQueryRequest() {
        super();
    }

    public SubQueryRequest(int beginNumber, int number, RequestInfo info) {
    	this(beginNumber,number,null,info);
    }
    
    public SubQueryRequest(int beginNumber, int number, String requestId, RequestInfo info) {
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
    
    public void setIndex(int index){
    	this.index = index;
    }
    
    public int getIndex(){
    	return index;
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
    public final SubQueryRequest fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (SubQueryRequest) super.fromBytes(bytes);
    }
    
    @Override
	protected String genUniqueRequestKey() {
		return requestId + index;
	}

}