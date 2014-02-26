package cn.edu.sdu.cs.starry.taurus;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitor;
import cn.edu.sdu.cs.starry.taurus.server.BusinessMonitorManager;
import cn.edu.sdu.cs.starry.taurus.state.ServerStateInfo;
/**
 * For manage Taurus .
 * @author ytchen*/
public class TaurusManager {

	private static final Logger LOG = 
			LoggerFactory.getLogger(TaurusManager.class);
	
	private static Taurus taurus;
	private static BusinessMonitorManager businessMonitorManager;
	private static TaurusManager taurusManager;
	private TaurusManager(){}
	
	/**
	 * Get Taurus manager,which should be only one instance while Taurus running.
	 * */
	public static TaurusManager getTaurusManager(){
		if(null == taurusManager){
			taurusManager = new TaurusManager();
		}
		if(null == taurus){
			taurus = Taurus.getTaurus();
		}
		if(null == businessMonitorManager){
			BusinessMonitorManager.getBusinessMonitorManager();			
		}
		return taurusManager;
	}
	/**
	 * Get Taurus server running state information.
	 * @return {@link cn.edu.sdu.cs.starry.taurus.state.ServerStateInfo}
	*/
	public ServerStateInfo getServerStateInfo(){
		return taurus.getServerStateInfo();
	}
	
	/**
	 * Start the Taurus using default configuration file path.<br>
	 * @throws BusinessException if starting with error*/
	public void startTaurus() throws BusinessException{
		LOG.info("Command from Taurus Manager : " +
				"start taurus using default configuration file path");
		taurus.start();
	}
	/**
	 * Start the Taurus using given path as configuration file path.<br>
	 * @param path configuration file path
	 * @throws BusinessException if starting with error*/
	public void startTaurus(String path) throws BusinessException{
		LOG.info("Command from Taurus Manager : " +
				"start taurus using configuration file path:"+path);
		taurus.start(path);
	}
	
	/**
	 * Restart the Taurus using default configuration file path.<br>
	 * @throws BusinessException if starting with error*/
	public void restartTaurus() throws BusinessException{
		LOG.info("Command from Taurus Manager : " +
				"restart taurus using default configuration file path");
		taurus.restart();
	}
	
	/**
	 * Restart the Taurus using given path as configuration file path.<br>
	 * @param path configuration file path
	 * @throws BusinessException if starting with error*/
	public void restartTaurus(String path) throws BusinessException{
		LOG.info("Command from Taurus Manager : " +
				"restart taurus using configuration file path:"+path);
		taurus.restart(path);
	}
	
	/**
	 * Stop single processor of specific businessType using given uuid.
	 * @param businessType the BusinessType of processor which need stop
	 * @param uuidStr the processor's UUID
	 * @throws BusinessException if specific uuid is invalid
	 * */
	public void stopProcessor(BusinessType businessType,String uuidStr) 
			throws BusinessException{
		//TODO test this
		LOG.info("Command from Taurus Manager : " +
				"stop processor of BusinessType:" + businessType+
				",uuid is:" + uuidStr);
		UUID commandUUID = UUID.fromString(uuidStr);
		BusinessMonitor monitor = businessMonitorManager
				.getMonitorMap(businessType).get(commandUUID);
		if(null == monitor){
			LOG.warn("Trying to stop processor of BusinessType : " + businessType +
					" ,but the uuid :" + uuidStr + "is invalid");
			throw new BusinessException("Invalid UUID");
		}
		monitor.stopWork();
	}
}
