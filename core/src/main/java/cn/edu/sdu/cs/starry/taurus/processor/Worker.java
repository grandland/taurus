package cn.edu.sdu.cs.starry.taurus.processor;

import cn.edu.sdu.cs.starry.taurus.server.CacheTool;

/**
 * @author SDU.xccui
 */
public abstract class Worker extends BaseProcessor {

    protected CacheTool cacheTool;
    

    public void setCacheUtility(CacheTool cacheUtility) {
        this.cacheTool = cacheUtility;
    }

}
