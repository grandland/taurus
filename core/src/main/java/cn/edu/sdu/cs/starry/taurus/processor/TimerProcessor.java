package cn.edu.sdu.cs.starry.taurus.processor;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.response.TimerResponse;

public abstract class TimerProcessor extends BaseProcessor {

    @Override
    public int getMinSystemResource() {
        return 1;
    }

    @Override
    public abstract TimerResponse process(BaseBusinessRequest request)
            throws BusinessException;

}
