package cn.edu.sdu.cs.starry.taurus.example.longquery;

import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * @author SDU.xccui
 */
public abstract class LongQueryResponse extends QueryResponse {
	
	private boolean finished;
	
	public boolean isFinished(){
		return finished;
	}
	
	public void setFinished(boolean f){
		finished = f;
	}
	
    @Override
    public LongQueryResponse fromBytes(byte[] bytes) throws SerializeException {
        return (LongQueryResponse) super.fromBytes(bytes);
    }
    
}
