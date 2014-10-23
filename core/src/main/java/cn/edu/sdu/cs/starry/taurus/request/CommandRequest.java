package cn.edu.sdu.cs.starry.taurus.request;

import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * This class is for async request.
 *
 * @author SDU.xccui
 */
public abstract class CommandRequest extends BaseBusinessRequest {

    /**
     * Return an unique key for cache use. This key will <b>not</b> be refreshed
     * when attributes changed.
     *
     * @param withSessionId put sessionId in this key or not
     * @return {@code null} if cache is disabled
     */
    public String getRequestKey(boolean withSessionId) {
        if (null == requestKey)
            requestKey = genUniqueRequestKey();
        if (null == requestKey)
            return null;
        if (withSessionId) {
            if (null == sessionId)
                return null;
            return sessionId + "&" + this.getClass().getName() + "&"
                    + requestKey;
        } else {
            return this.getClass().getName() + "&" + requestKey;
        }
    }

    @Override
    public CommandRequest fromBytes(byte[] bytes) throws SerializeException {
        return (CommandRequest) super.fromBytes(bytes);
    }
}
