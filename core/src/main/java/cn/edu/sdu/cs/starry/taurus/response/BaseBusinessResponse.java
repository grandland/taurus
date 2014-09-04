package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.serialize.BusinessSerializable;
import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

/**
 * @author SDU.xccui
 */
public abstract class BaseBusinessResponse extends BusinessSerializable{

	@Override
	public BaseBusinessResponse fromBytes(byte[] bytes) throws SerializeException{
		return (BaseBusinessResponse) super.fromBytes(bytes);
	}
	
}
