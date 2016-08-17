package cn.edu.sdu.cs.starry.taurus.processor;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessInterruptedException;
import cn.edu.sdu.cs.starry.taurus.request.BaseBusinessRequest;
import cn.edu.sdu.cs.starry.taurus.request.QueryRequest;
import cn.edu.sdu.cs.starry.taurus.response.QueryResponse;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * @author SDU.xccui
 */
public abstract class QueryWorker extends Worker {


    @Override
    public void prepare() {
        super.prepare();
        prepareWorker();
    }

    /**
     * Prepare the worker. Maybe should initialize some instance variables.
     */
    protected abstract void prepareWorker();

    /**
     * Work! Work! Work! This method should invoke
     * {@link #setProcessRate(float)} method to achieve process control
     * mechanism and you should make sure that this method will <b>never</b>
     * block forever. <b>Do not</b> catch the
     * {@link BusinessInterruptedException} thrown by
     * {@link #setProcessRate(float)}. In this method, you can use the
     * {@link #cacheTool} to achieve your own cache mechanism. But be
     * careful for the {@link #cacheTool} may be {@code null}.
     *
     * @param query
     * @return
     * @throws BusinessException
     */
    protected abstract QueryResponse doWork(CacheTool cacheTool, QueryRequest query)
            throws BusinessException;

    @Override
    public QueryResponse process(BaseBusinessRequest request)
            throws BusinessException {
        if (request instanceof QueryRequest) {
            return doWork(cacheTool, (QueryRequest) request);
        } else {
            throw new BusinessCorrespondingException();
        }
    }

    @Override
    public int getMinSystemResource() {
        return 1;
    }
}
