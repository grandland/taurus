package cn.edu.sdu.cs.starry.taurus.request;

import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * This class is for query request with a begin number and a number for paging.
 *
 * @author SDU.xccui
 */
public abstract class QueryRequest extends BaseBusinessRequest {

    private int beginNumber;
    private int number;
    
    private boolean autoCache = true;

    public QueryRequest() {
        super();
    }

    public QueryRequest(int beginNumber, int number, RequestInfo info) {
        super(info.getSessionId(), info.getUserName(), info.getUserIp());
        this.beginNumber = beginNumber;
        this.number = number;
        sessionId = info.getSessionId();
    }

    public int getBeginNumber() {
        return beginNumber;
    }

    public int getNumber() {
        return number;
    }
    
    public boolean isAutoCache() {
		return autoCache;
	}

	public void setAutoCache(boolean autoCache) {
		this.autoCache = autoCache;
	}

	/**
     * Return an unique key for cache use. This key will <b>not</b> be refreshed
     * when attributes changed.
     *
     * @param withSessionId put sessionId in this key or not
     * @param withPage      put page info in this key or not
     * @return{@code null} if cache is disabled
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
    public QueryRequest fromBytes(byte[] bytes) throws SerializeException{
        return (QueryRequest) super.fromBytes(bytes);
    }
}
