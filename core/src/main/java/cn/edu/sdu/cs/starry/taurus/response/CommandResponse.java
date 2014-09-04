package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;

public abstract class CommandResponse extends BaseBusinessResponse {

    @Override
    public final CommandResponse fromBytes(byte[] bytes) throws SerializeException {
        return (CommandResponse) super.fromBytes(bytes);
    }
}
