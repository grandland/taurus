package cn.edu.sdu.cs.starry.taurus.request;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;
import cn.edu.sdu.cs.starry.taurus.serialize.BusinessSerializable;
import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * This is a base class for all business requests.
 *
 * @author SDU.xccui
 */
public abstract class BaseBusinessRequest extends BusinessSerializable {
    protected String requestKey;
    protected String sessionId;
    protected String userName;
    protected String userIP;
    protected long version = System.currentTimeMillis();
    private Integer requestLoad;

    public BaseBusinessRequest(String sessionId, String userName, String userIP) {
        this();
        this.sessionId = sessionId;
        this.userName = userName;
        this.userIP = userIP;
    }

    public BaseBusinessRequest() {
        super();
        requestLoad = null;
        requestKey = null;
    }
    
    @Override
	public BaseBusinessRequest fromBytes(byte[] bytes) throws SerializeException{
		return (BaseBusinessRequest) super.fromBytes(bytes);
	}

    /**
     * Return session id for the request
     *
     * @return <code>null</code> if sessionId is not set yet
     */
    public final String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserIP() {
        return userIP;
    }

    public final long getVersion() {
        return version;
    }
    
    @Override
	public String toString() {
		return "BaseBusinessRequest [requestKey=" + requestKey + ", sessionId="
				+ sessionId + ", userName=" + userName + ", userIP=" + userIP
				+ ", version=" + version + ", requestLoad=" + requestLoad + "]";
	}

	/**
     * Do base self content check and correct attribute if possible.<br/>
     * This method can be used in front.
     *
     * @throws BusinessRequestAttributeException
     */
    public void doBaseAttributeCheck() throws BusinessRequestAttributeException {
        // TODO do base check
    }

    /**
     * Do self attribute check and correct attribute if possible.<br/>
     * Just invoke {@link #doBaseAttributeCheck()} and
     * {@link #doSelfAttributeCheck()}.
     *
     * @throws BusinessRequestAttributeException if encountered an uncorrectable attribute
     */
    public final void doAttributeCheck()
            throws BusinessRequestAttributeException {
        doBaseAttributeCheck();
        doSelfAttributeCheck();
    }

    /**
     * Calculate a load metric for this request. Note that this load metric will
     * <b>not</b> be changed once it's been calculated.
     *
     * @return <ul>
     * <li>a positive integer if the load can be calculated</li>
     * <li>a nonpositive integer if the load can be ignored</li>
     * <ul>
     */
    public final int getRequestLoad() {
        if (requestLoad == null) {
            requestLoad = calRequestLoad();
        }
        return requestLoad < 0 ? 0 : requestLoad;
    }

    /**
     * Calculate a load metric for this request.
     *
     * @return <ul>
     * <li>a positive integer if the load can be calculated</li>
     * <li>a nonpositive integer if the load can be ignored</li>
     * <ul>
     */
    protected abstract int calRequestLoad();

    /**
     * To return an unique key based on parameters contained.<br/>
     * Note that this method may sort some parameter arrays.
     *
     * @return
     */
    protected abstract String genUniqueRequestKey();

    /**
     * Do self attribute check and correct attribute if possible.
     *
     * @throws BusinessRequestAttributeException if encountered an uncorrectable attribute
     */
    protected abstract void doSelfAttributeCheck()
            throws BusinessRequestAttributeException;

}