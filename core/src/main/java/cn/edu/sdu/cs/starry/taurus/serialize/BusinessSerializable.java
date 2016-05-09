package cn.edu.sdu.cs.starry.taurus.serialize;

public abstract class BusinessSerializable {

	private BusinessSerializer serializer = new KryoBusinessSerializer();
	
	public BusinessSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(BusinessSerializer serializer) {
		this.serializer = serializer;
	}

	public byte[] toBytes() throws SerializeException{
		return serializer.toBytes(this);
	}
	
	public BusinessSerializable fromBytes(byte[] bytes, Class<? extends BusinessSerializable> clazz) throws SerializeException{
		return serializer.fromBytes(bytes, clazz);
	}
}
