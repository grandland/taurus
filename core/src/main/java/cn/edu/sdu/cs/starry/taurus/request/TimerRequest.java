package cn.edu.sdu.cs.starry.taurus.request;


import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestAttributeException;

public class TimerRequest extends BaseBusinessRequest {
    private long lastRequestTime;
    private long interval;
    private long currentTime;

    public TimerRequest() {
        super();
    }

    public TimerRequest(long interval, RequestInfo queryInfo) {
        super(queryInfo.getSessionId(), queryInfo.getUserName(), queryInfo
                .getUserIp());
        this.interval = interval;
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    protected int calRequestLoad() {
        return 0;
    }

    @Override
    protected String genUniqueRequestKey() {
        return null;
    }

    @Override
    protected void doSelfAttributeCheck()
            throws BusinessRequestAttributeException {
    }

    public String getRequestKey() {
        return genUniqueRequestKey();
    }

}
