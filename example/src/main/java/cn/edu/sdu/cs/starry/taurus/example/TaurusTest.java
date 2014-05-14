package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.TaurusManager;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;

public class TaurusTest{

	/**
	 * @param args
	 * @throws BusinessException 
	 */
	public static void main(String[] args) throws BusinessException {

		TaurusManager tm = TaurusManager.getTaurusManager();
		tm.startTaurus("conf/taurus/taurus-conf.xml");
		
	}

}
