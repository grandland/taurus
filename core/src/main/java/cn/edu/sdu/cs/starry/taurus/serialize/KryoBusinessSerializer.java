package cn.edu.sdu.cs.starry.taurus.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Serializer using kryo*/
public class KryoBusinessSerializer implements BusinessSerializer{

	private static final int bufferSize = 1024 * 1024 * 512;

	@Override
	public synchronized byte[] toBytes(Object o) throws SerializeException {
		Output output = new Output(bufferSize);
		new Kryo().writeObject(output, o);
		output.close();
		return output.toBytes();
	}

	@Override
	public synchronized <T> T fromBytes(byte[] bytes, Class<T> clazz)
			throws SerializeException {
		Input input = new Input(bytes);
		T object = new Kryo().readObject(input, clazz);
		input.close();
		return object;
	}

}
