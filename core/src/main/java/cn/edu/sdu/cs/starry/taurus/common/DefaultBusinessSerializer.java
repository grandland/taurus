package cn.edu.sdu.cs.starry.taurus.common;

import com.google.gson.Gson;
/**
 * Default business serializer using Gson implemented.
 * @author ytchen*/
public class DefaultBusinessSerializer extends AbstractBusinessSerializer{

	private static final Gson gson = new Gson();

	@Override
	protected DefaultBusinessSerializer fromBytesImpl(String json) {
		return gson.fromJson(json, this.getClass());
	}

	@Override
	protected String toBytesImpl() {
		return gson.toJson(this);
	}
}
