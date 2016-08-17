package cn.edu.sdu.cs.starry.taurus.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.BusinessRequestProvider;
import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration.SingleBusinessConf;
import cn.edu.sdu.cs.starry.taurus.request.RequestInfo;
import cn.edu.sdu.cs.starry.taurus.request.TimerRequest;

public class TimerRequestProvider extends BusinessRequestProvider {
	private static Logger LOG = LoggerFactory
			.getLogger(TimerRequestProvider.class);
	private Timer timer;
	private BlockingQueue<TimerRequestAndKey> requestQueue;
	private Map<String, TimerRequestProviderTimeTask> timerTaskMap;

	public TimerRequestProvider(BusinessType businessType) {
		super(businessType);
		timer = new Timer();
		requestQueue = new LinkedBlockingQueue<TimerRequestAndKey>();
		timerTaskMap = new HashMap<String, TimerRequestProvider.TimerRequestProviderTimeTask>();
	}

	@Override
	protected void prepareProvider(
			SingleBusinessTypeConfiguration singleTypeConfig)
			throws BusinessRequestProviderException {
		TimerRequestProviderTimeTask timerTask;
		for (SingleBusinessConf singleConf : singleTypeConfig.getBusinesses()
				.values()) {
			if (null == singleConf.getInterval()) {
				throw new BusinessRequestProviderException(
						"For TIMER business " + singleConf.getName()
								+ ", missing interval!");
			}
			long delay = null == singleConf.getDelay() ? 0 : singleConf
					.getDelay();
			LOG.info("Set TIMER business: '" + singleConf.getName()
					+ "' with interval " + singleConf.getInterval()
					+ "ms and delay " + delay + " ms");
			try {
				timerTask = new TimerRequestProviderTimeTask(
						singleConf.getName(), singleConf.getInterval(),
						requestQueue);
				timerTaskMap.put(singleConf.getName(), timerTask);
				timer.scheduleAtFixedRate(timerTask, delay,
						singleConf.getInterval());
			} catch (RuntimeException ex) {
				throw new BusinessRequestProviderException(ex);
			}

		}
	}

	@Override
	protected void startProvider() throws BusinessRequestProviderException {
		LOG.info("Start timer provider");
		for (TimerRequestProviderTimeTask task : timerTaskMap.values()) {
			task.enable = true;
		}
	}

	@Override
	protected void pauseProvider() throws BusinessRequestProviderException {
		for (TimerRequestProviderTimeTask task : timerTaskMap.values()) {
			task.enable = false;
		}

	}

	@Override
	protected void resumeProvider() throws BusinessRequestProviderException {
		for (TimerRequestProviderTimeTask task : timerTaskMap.values()) {
			task.enable = true;
		}
	}

	@Override
	protected void stopProvider() throws BusinessRequestProviderException {
		LOG.info("Stop timer provider");
		timer.cancel();
	}

	@Override
	public RequestAndIdentification provideNext()
			throws BusinessRequestProviderException {
		TimerRequestAndKey requestAndKey = null;
		while (null == requestAndKey) {
			try {
				requestAndKey = requestQueue.poll(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				throw new BusinessRequestProviderException(e);
			}
		}
		RequestAndIdentification requestAndIndentification = new RequestAndIdentification(
				requestAndKey.request, new SimpleRequestIdentification(
						requestAndKey.key, new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date(
								requestAndKey.request.getCurrentTime()))),
				requestAndKey.key);
		requestAndIndentification.createInnerMonitor(2000, -1, reporter);
		return requestAndIndentification;
	}

	private static class TimerRequestProviderTimeTask extends TimerTask {
		private String businessKey;
		private TimerRequest request;
		private BlockingQueue<TimerRequestAndKey> requestQueue;
		private long lastRequestTime;
		private boolean enable;

		public TimerRequestProviderTimeTask(String businessKey, long interval,
				BlockingQueue<TimerRequestAndKey> requestQueue) {
			this.businessKey = businessKey;
			this.requestQueue = requestQueue;
			lastRequestTime = System.currentTimeMillis();
			RequestInfo queryInfo = new RequestInfo();
			try {
				queryInfo.setUserName("Taurus");
				queryInfo.setUserIp(InetAddress.getLocalHost().toString());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			request = new TimerRequest(interval, queryInfo);
			enable = false;
		}

		@Override
		public void run() {
			if (enable) {
				request.setLastRequestTime(lastRequestTime);
				request.setCurrentTime(System.currentTimeMillis());
				try {
					requestQueue.put(new TimerRequestAndKey(request,
							businessKey));
					LOG.info("Provide a '"
							+ businessKey
							+ "' timer request:"
							+ request.getRequestKey()
							+ " with interval of "
							+ request.getInterval()
							+ "ms. Last privide time is "
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date(lastRequestTime)));
					lastRequestTime = request.getCurrentTime();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class TimerRequestAndKey {
		private TimerRequest request;
		private String key;

		private TimerRequestAndKey(TimerRequest request, String key) {
			this.request = request;
			this.key = key;
		}
	}

	public static void main(String[] args) {
		TimerRequestProvider provider = new TimerRequestProvider(
				BusinessType.TIMER);
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
