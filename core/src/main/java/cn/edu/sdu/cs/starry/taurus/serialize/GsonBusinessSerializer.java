package cn.edu.sdu.cs.starry.taurus.serialize;

import cn.edu.sdu.cs.starry.taurus.common.SerializeUtil;

import com.google.gson.Gson;

/**
 * Default business serializer using Gson implemented.
 * @author ytchen*/
public class GsonBusinessSerializer implements BusinessSerializer{

	/**
	 * Gson should be <a href='https://groups.google.com/forum/#!topic/google-gson/Vju1HuJJUIE'>thread-safe</a>
	 * , so we do not need t protect this instance.<br/> */
	private static final Gson gson = new Gson();
	
	@Override
	public byte[] toBytes(Object o) throws SerializeException{
		byte[] result ;
		try{
			result = SerializeUtil.toBytesFromString(gson.toJson(o));
		} catch (Exception e){
			throw new SerializeException(e);
		}
		return result;
	}

	@Override
	public <T> T fromBytes(byte[] bytes, Class<T> clazz) throws SerializeException {
		String content;
		try{
			content = SerializeUtil.toStringFromBytes(bytes);
		} catch (Exception e){
			throw new SerializeException(e);
		}
		return gson.fromJson(content, clazz);
	}
	
}