package cn.edu.sdu.cs.starry.taurus.serialize;

public abstract class BusinessSerializable {

	/**
	 * Starts with '_' in order to keep this field away from gson serializer.
	 */
	private BusinessSerializer _serializer = new KryoBusinessSerializer();
	
	public BusinessSerializer getSerializer() {
		return _serializer;
	}

	public void setSerializer(BusinessSerializer serializer) {
		this._serializer = serializer;
	}

	public byte[] toBytes() throws SerializeException{
		return _serializer.toBytes(this);
	}
	
	public BusinessSerializable fromBytes(byte[] bytes, Class<? extends BusinessSerializable> clazz) throws SerializeException{
		return _serializer.fromBytes(bytes, clazz);
	}
}
