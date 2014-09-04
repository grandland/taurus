package cn.edu.sdu.cs.starry.taurus.serialize;

public interface BusinessSerializer {
	
	public byte[] toBytes(Object o) throws SerializeException;

	public <T> T fromBytes(byte[] bytes, Class<T> clazz) throws SerializeException;
}
