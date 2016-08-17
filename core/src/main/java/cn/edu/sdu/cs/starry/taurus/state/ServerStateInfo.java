package cn.edu.sdu.cs.starry.taurus.state;

import java.util.HashMap;
import java.util.Map;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;

/**
 * Contains server state information
 * @author ytchen*/
public class ServerStateInfo {

    private Map<BusinessType, BusinessFactoryInfo> businessFactoryInfoMap;

    public ServerStateInfo() {
        businessFactoryInfoMap = new HashMap<BusinessType, BusinessFactoryInfo>();
    }

    /**
     * Get business factory information*/
    public BusinessFactoryInfo getFactoryInfo(BusinessType businessType) {
        return businessFactoryInfoMap.get(businessType);
    }
    /**
     * Put business factory information
     * */
    public void putBusinessFactoryInfo(BusinessType businessKey, BusinessFactoryInfo info) {
        businessFactoryInfoMap.put(businessKey, info);
    }
}
