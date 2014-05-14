package cn.edu.sdu.cs.starry.taurus;

import java.util.HashMap;
import java.util.Map;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;

/**
 * Contains mappings between businessKeys and businessTypes.
 *
 * @author SDU.xccui
 */
public class BusinessTypeManager {
    public static final Map<String, BusinessType> businessTypeMap = new HashMap<String, BusinessType>();

    public static BusinessType decideBusinessType(String businessKey) {
        return businessTypeMap.get(businessKey);
    }
}
