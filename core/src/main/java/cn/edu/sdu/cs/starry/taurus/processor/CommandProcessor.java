package cn.edu.sdu.cs.starry.taurus.processor;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.CommandResponse;

/**
 * @author SDU.xccui
 */
public abstract class CommandProcessor extends BaseProcessor {
    @Override
    public void prepare() {
        super.prepare();
        prepareProcessor();
    }

    @Override
    public int getMinSystemResource() {
        return 0;
    }

    @Override
    public abstract CommandResponse process(BaseBusinessRequest request)
            throws BusinessException;

    protected abstract void prepareProcessor();
}
