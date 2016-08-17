package cn.edu.sdu.cs.starry.taurus.request;

/**
 * This class is for basic request information.
 */
public class RequestInfo {
    private String sessionId;
    private String userName;
    private String userIp;

    public RequestInfo() {
        super();
    }

    public RequestInfo(String sessionId, String userName, String userIp) {
        super();
        this.sessionId = sessionId;
        this.userName = userName;
        this.userIp = userIp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

	@Override
	public String toString() {
		return "RequestInfo [sessionId=" + sessionId + ", userName=" + userName
				+ ", userIp=" + userIp + "]";
	}

}
