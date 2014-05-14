package cn.edu.sdu.cs.starry.taurus.response;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

public abstract class CommandResponse extends BaseBusinessResponse {

    @Override
    public final CommandResponse fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        return (CommandResponse) super.fromBytes(bytes);
    }
}
