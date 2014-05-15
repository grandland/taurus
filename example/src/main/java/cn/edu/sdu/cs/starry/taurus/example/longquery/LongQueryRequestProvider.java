package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessRequestProvider;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.example.SimpleRequestIdentification;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;

public class LongQueryRequestProvider extends BusinessRequestProvider {
	private static Logger LOG = LoggerFactory
			.getLogger(LongQueryRequestProvider.class);
	private boolean enable = false;
	private Map<String, SingleBusinessConf> businesses ;
	
	private List<TestLongQueryRequest> requests;
	
	public LongQueryRequestProvider(BusinessType type) {
		super(type);
	}

	@Override
	protected void prepareProvider(
			SingleBusinessTypeConfiguration singleTypeConfig)
			throws BusinessRequestProviderException {
		businesses = singleTypeConfig.getBusinesses();
		requests = new ArrayList<>();
		for(int i = 0 ; i < 1;i++){
			requests.add(new TestLongQueryRequest("LongQuery request No."+i));
		}
	}

	@Override
	protected void startProvider() throws BusinessRequestProviderException {
		LOG.info("Start query provider");
		enable = true;
	}

	@Override
	protected void pauseProvider() throws BusinessRequestProviderException {
		LOG.info("Pause query provider");
		enable = false;
	}

	@Override
	protected void resumeProvider() throws BusinessRequestProviderException {
		LOG.info("Stop query provider");
		enable = true;
	}

	@Override
	protected void stopProvider() throws BusinessRequestProviderException {
		LOG.info("Stop query provider");
		enable = false;
	}

	@Override
	public RequestAndIdentification provideNext()
			throws BusinessRequestProviderException {
		try {
			int random = (int)(Math.random()*5000);
			Thread.sleep(random);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int index = (int)(Math.random() * businesses.size());
		String key  = (String) businesses.keySet().toArray()[index];
		RequestAndIdentification requestAndIndentification = new RequestAndIdentification(requests.get(0), 
				new SimpleRequestIdentification(key, new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date(
				System.currentTimeMillis()))), key);
//		RequestAndIdentification requestAndIndentification = new RequestAndIdentification(
//				new TestLongQueryRequest("ytchen NO."+(int)(Math.random()*10)), new SimpleRequestIdentification(
//						key, new SimpleDateFormat(
//								"yyyy-MM-dd HH:mm:ss").format(new Date(
//								System.currentTimeMillis()))),key);
		requestAndIndentification.createInnerMonitor(2000, -1, reporter);
		return requestAndIndentification;
	}


	public static void main(String[] args) {
		LongQueryRequestProvider provider = new LongQueryRequestProvider(BusinessType.LONGQUERY);
		SingleBusinessTypeConfiguration singleTypeConfig = new SingleBusinessTypeConfiguration();
		singleTypeConfig.getBusinesses().put("Test",
				new SingleBusinessConf("Test", 10000));
		try {
			provider.prepare(singleTypeConfig);
			provider.start();
		} catch (BusinessRequestProviderException e) {
			e.printStackTrace();
		}
	}
}
