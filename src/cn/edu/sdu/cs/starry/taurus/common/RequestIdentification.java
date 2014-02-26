package cn.edu.sdu.cs.starry.taurus.common;

/**
 * @author SDU.xccui
 */
public abstract class RequestIdentification {
    private String taurusServerName;

    public String getTaurusServerName() {
        return taurusServerName;
    }

    public void setTaurusServerName(String taurusServerName) {
        this.taurusServerName = taurusServerName;
    }

}
