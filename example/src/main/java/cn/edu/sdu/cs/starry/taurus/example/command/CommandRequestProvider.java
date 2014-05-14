package cn.edu.sdu.cs.starry.taurus.example.command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessRequestProvider;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.example.SimpleRequestIdentification;
import cn.edu.sdu.cs.starry.taurus.request.CommandRequest;
import cn.edu.sdu.cs.starry.taurus.request.RequestInfo;
import cn.edu.sdu.cs.starry.taurus.request.TimerRequest;
import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;

public class CommandRequestProvider extends BusinessRequestProvider {
	private static Logger LOG = LoggerFactory
			.getLogger(CommandRequestProvider.class);
	private boolean enable = false;
	private Map<String, SingleBusinessConf> businesses ;

	public CommandRequestProvider(BusinessType businessType) {
		super(businessType);
	}

	@Override
	protected void prepareProvider(
			SingleBusinessTypeConfiguration singleTypeConfig)
			throws BusinessRequestProviderException {
		businesses = singleTypeConfig.getBusinesses();
	}

	@Override
	protected void startProvider() throws BusinessRequestProviderException {
		LOG.info("Start command provider");
		enable = true;
	}

	@Override
	protected void pauseProvider() throws BusinessRequestProviderException {
		LOG.info("Pause command provider");
		enable = false;
	}

	@Override
	protected void resumeProvider() throws BusinessRequestProviderException {
		LOG.info("Stop command provider");
		enable = true;
	}

	@Override
	protected void stopProvider() throws BusinessRequestProviderException {
		LOG.info("Stop command provider");
		enable = false;
	}

	@Override
	public RequestAndIdentification provideNext()
			throws BusinessRequestProviderException {
		try {
			int random = (int)(Math.random()*5) * 1000;
			LOG.info("sleep random :"+random);
			Thread.sleep(random);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int index = (int)(Math.random() * businesses.size());
		String key  = (String) businesses.keySet().toArray()[index];
		RequestAndIdentification requestAndIndentification = new RequestAndIdentification(
				new TestCommandRequest("ytchen NO."+(int)(Math.random()*10)), new SimpleRequestIdentification(
						key, new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date(
								System.currentTimeMillis()))),key);
		requestAndIndentification.createInnerMonitor(2000, -1, reporter);
		return requestAndIndentification;
	}


	public static void main(String[] args) {
		CommandRequestProvider provider = new CommandRequestProvider(BusinessType.COMMAND);
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
