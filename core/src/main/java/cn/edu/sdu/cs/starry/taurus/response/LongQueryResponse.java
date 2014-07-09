package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

/**
 * @author SDU.xccui
 */
public abstract class LongQueryResponse extends BaseBusinessResponse {
	
	private boolean finished;
	
	public boolean isFinished(){
		return finished;
	}
	
	public void setFinished(boolean f){
		finished = f;
	}
	
    @Override
    public LongQueryResponse fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (LongQueryResponse) super.fromBytes(bytes);
    }
    
}
