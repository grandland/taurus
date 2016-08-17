package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * @author SDU.xccui
 */
public abstract class QueryResponse extends BaseBusinessResponse {
    @Override
    public QueryResponse fromBytes(byte[] bytes) throws SerializeException {
        return (QueryResponse) super.fromBytes(bytes);
    }
}
