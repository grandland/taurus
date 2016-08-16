package cn.edu.sdu.cs.starry.taurus.serialize;

public interface BusinessSerializer {
	
	byte[] toBytes(Object o) throws SerializeException;

	<T> T fromBytes(byte[] bytes, Class<T> clazz) throws SerializeException;
}
