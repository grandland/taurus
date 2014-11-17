package cn.edu.sdu.cs.starry.taurus.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Serializer using kryo*/
public class KryoBusinessSerializer implements BusinessSerializer{

	private static final int bufferSize = 1024 * 1024 * 512;

	private KryoThreadLocal kryoLocal = new KryoThreadLocal();
	
	@Override
	public byte[] toBytes(Object o) throws SerializeException {
		Output output = new Output(bufferSize);
		kryoLocal.get().writeObject(output, o);
		output.close();
		return output.toBytes();
	}

	@Override
	public <T> T fromBytes(byte[] bytes, Class<T> clazz)
			throws SerializeException {
		Input input = new Input(bytes);
		T object  =  kryoLocal.get().readObject(input, clazz);
		input.close();
		return object;
	}
	
	/**
	 * Kryo is not thread-safe, so we use ThreadLocal.*/
	private static class KryoThreadLocal extends ThreadLocal<Kryo>{
		@Override
		protected Kryo initialValue() {
			return new Kryo();
		}
	}
}
