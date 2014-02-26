package cn.edu.sdu.cs.starry.taurus;

import cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessRequestProviderException;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.server.BusinessReporter;

/**
 * This is a base class for your business request provider class to extend from.
 * In order to control the progress, your provider should support four states.
 * They are {@link #PREPARED_STATE}, {@link #STARTED_STATE},
 * {@link #PAUSED_STATE} and {@link #STOPPED_STATE}.
 * <ul>
 * <li>When at {@link #PREPARED_STATE}, the provider should be prepared to
 * provide new request.</li>
 * <li>When at {@link #STARTED_STATE}, your provider should start providing new
 * request.</li>
 * <li>When at {@link #PAUSED_STATE}, your provider should pause to providing
 * new request.</li>
 * <li>When at {@link #STOPPED_STATE}, your provider should stop and, may be
 * deal with some buffered request.</li>
 * 
 * @author SDU.xccui
 * 
 */
public abstract class BusinessRequestProvider {
	private static final Logger LOG = LoggerFactory
			.getLogger(BusinessRequestProvider.class);
	public static final byte PREPARED_STATE = 0;
	public static final byte STOPPED_STATE = -1;
	public static final byte STARTED_STATE = 1;
	public static final byte PAUSED_STATE = 2;

	protected BusinessReporter reporter;

	private int state;
	private BusinessType businessType;

	public BusinessRequestProvider(BusinessType businessType) {
		state = STOPPED_STATE;
		this.businessType = businessType;
	}

	public void setReporter(BusinessReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * 
	 * @throws BusinessRequestProviderException
	 */
	public void prepare(SingleBusinessTypeConfiguration singleTypeConfig)
			throws BusinessRequestProviderException {
		LOG.info("'" + businessType + "' request provider prepare");
		switch (state) {
		case PREPARED_STATE:
			break;
		case STARTED_STATE:
		case PAUSED_STATE:
			throw new BusinessRequestProviderException(
					"State should be STOPPED_STATE or PREPARED_STATE");
		case STOPPED_STATE:
			prepareProvider(singleTypeConfig);
			state = PREPARED_STATE;
			break;
		}
	}

	public void start() throws BusinessRequestProviderException {
		LOG.info("'" + businessType + "' request provider start");
		switch (state) {
		case PREPARED_STATE:
			startProvider();
			state = STARTED_STATE;
			break;
		case STARTED_STATE:
			break;
		case PAUSED_STATE:
		case STOPPED_STATE:
			throw new BusinessRequestProviderException(
					"State should be PREPARED_STATE or STARTED_STATE");
		}
	}

	/**
	 * Pause the provider and set state to {@link #PAUSED_STATE}.
	 * 
	 * @throws BusinessRequestProviderException
	 *             when the provider is not started yet or encountered some
	 *             exceptions during pausing.
	 */
	public void pause() throws BusinessRequestProviderException {
		LOG.info("'" + businessType + "' request provider pause");
		switch (state) {
		case STARTED_STATE:
			pauseProvider();
			state = PAUSED_STATE;
			break;
		case PAUSED_STATE:
			break;
		case STOPPED_STATE:
		case PREPARED_STATE:
			throw new BusinessRequestProviderException(
					"State should be PAUSED_STATE or STARTED_STATE");
		}
	}

	/**
	 * Resume the paused provider and set state to {@link #STARTED_STATE}.
	 * 
	 * @throws BusinessRequestProviderException
	 *             when the provider is not paused or encountered some
	 *             exceptions during resuming.
	 */
	public void resume() throws BusinessRequestProviderException {
		LOG.info("'" + businessType + " request provider resume");
		switch (state) {
		case STARTED_STATE:
			break;
		case PAUSED_STATE:
			resumeProvider();
			state = STARTED_STATE;
			break;
		case STOPPED_STATE:
		case PREPARED_STATE:
			throw new BusinessRequestProviderException(
					"State should be PAUSED_STATE");
		}
	}

	/**
	 * Stop the provider.
	 * 
	 * @throws BusinessRequestProviderException
	 *             when encountered some exceptions during stopping.
	 */
	public void stop() throws BusinessRequestProviderException {
		LOG.info("'" + businessType + " request provider stop");
		switch (state) {
		case PREPARED_STATE:
		case STARTED_STATE:
		case PAUSED_STATE:
			stopProvider();
			state = STOPPED_STATE;
		case STOPPED_STATE:
			break;
		}
	}

	/**
	 * Each time invoking this method will return a new
	 * {@link cn.edu.sdu.cs.starry.taurus.server.RequestAndIdentification} object.
	 * 
	 * @return
	 * @throws BusinessRequestProviderException
	 *             when the provider is not started or encountered some
	 *             exceptions during fetching next request.
	 */
	public RequestAndIdentification next()
			throws BusinessRequestProviderException {
		if (state != STARTED_STATE) {
			throw new BusinessRequestProviderException(
					"State should be START_STATE");
		}
		return provideNext();
	}

	/**
	 * Return the current state. There are {@link #PREPARED_STATE},
	 * {@link #STARTED_STATE}, {@link #PAUSED_STATE} and {@link #STOPPED_STATE}.
	 * 
	 * @return
	 */
	public int getState() {
		return state;
	}

	/**
	 * Prepare the provider for providing requests.
	 * 
	 * @throws BusinessRequestProviderException
	 */
	protected abstract void prepareProvider(SingleBusinessTypeConfiguration singleTypeConfig)
			throws BusinessRequestProviderException;

	/**
	 * Start the provider for providing requests.
	 * 
	 * @throws BusinessRequestProviderException
	 */
	protected abstract void startProvider()
			throws BusinessRequestProviderException;

	/**
	 * Pause the provider.
	 * 
	 * @throws BusinessRequestProviderException
	 */
	protected abstract void pauseProvider()
			throws BusinessRequestProviderException;

	/**
	 * Resume a paused provider.
	 * 
	 * @throws BusinessRequestProviderException
	 */
	protected abstract void resumeProvider()
			throws BusinessRequestProviderException;

	/**
	 * Stop the provider.
	 * 
	 * @throws BusinessRequestProviderException
	 */
	protected abstract void stopProvider()
			throws BusinessRequestProviderException;

	/**
	 * Should provide next RequestAndIdentification for process.
	 * 
	 * @return
	 */
	public abstract RequestAndIdentification provideNext()
			throws BusinessRequestProviderException;

	/**
	 * Return the {@link BusinessType} for this provider.
	 * 
	 * @return
	 */
	public BusinessType getBusinessProviderType() {
		return businessType;
	}
}
