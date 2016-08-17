package cn.edu.sdu.cs.starry.taurus.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
/**
 * Manager the monitor map while processing
 * @author ytchen*/
public class BusinessMonitorManager {

	private static final Logger LOG = 
			LoggerFactory.getLogger(BusinessMonitorManager.class);
	
	private static BusinessMonitorManager businessMonitorManager;
	/**
	 * Store <BusinessType,Map> pair to manager different type of businessType monitor map*/
	private static Map<BusinessType,Map<UUID,BusinessMonitor>> monitorMaps 
						= new HashMap<BusinessType,Map<UUID,BusinessMonitor>>();
	
	private BusinessMonitorManager(){};
	
	public static BusinessMonitorManager getBusinessMonitorManager(){
		if(null == businessMonitorManager){
			businessMonitorManager = new BusinessMonitorManager();
			LOG.info("Create a new BusinessMonitorManager");
		}
		return businessMonitorManager;
	}
	
	/**
	 * Create a new monitor map for BusinessType
	 * @param businessType  businessType that should be initialized*/
	private Map<UUID,BusinessMonitor> createMonitorMap(BusinessType businessType){
		//initialize
		Map<UUID,BusinessMonitor> monitorMap = new ConcurrentHashMap<UUID, BusinessMonitor>();
		monitorMaps.put(businessType, monitorMap);
		LOG.info("Create a new monitorMap for BusinessType:"+businessType);
		return monitorMap;
	}
	
	/**
	 * Get the monitor map of specific BusinessType.<br>
	 * Create a new one if not exists.
	 * */
	public Map<UUID,BusinessMonitor> getMonitorMap(BusinessType businessType){
		Map<UUID,BusinessMonitor> monitorMap = monitorMaps.get(businessType);
		LOG.debug("MonitorMap :[{}] for BusinessType [{}]", monitorMap, businessType);
		if(null == monitorMap){
			LOG.debug("MonitorMap [{}] for BusinessType [{}] is null. Will create one", monitorMap, businessType);
			monitorMap = createMonitorMap(businessType);
		}
		return monitorMap;
	}
	
}
