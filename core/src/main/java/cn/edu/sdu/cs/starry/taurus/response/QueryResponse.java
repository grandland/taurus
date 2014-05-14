package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

/**
 * @author SDU.xccui
 */
public abstract class QueryResponse extends BaseBusinessResponse {
    @Override
    public QueryResponse fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (QueryResponse) super.fromBytes(bytes);
    }
}
