package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

/**
 * @author SDU.xccui
 */
public abstract class LongQueryResponse extends BaseBusinessResponse {
	
    @Override
    public LongQueryResponse fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (LongQueryResponse) super.fromBytes(bytes);
    }
    
}
