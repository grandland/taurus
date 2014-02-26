package cn.edu.sdu.cs.starry.taurus.server;

import cn.edu.sdu.cs.starry.taurus.processor.BaseProcessor;

/**
 * An interface for report process's progress.
 * 
 * @author SDU.xccui
 * 
 */
public interface BusinessReporter {
	public void initialize();

	/**
	 * Do as you wish to report the processor's progress. Note that this method
	 * <b>should never</b> be blocked!
	 * 
	 * @param processor
	 * @param requestAndIdentification
	 */
	public void report(BaseProcessor processor,
			RequestAndIdentification requestAndIdentification);

	public void close();
}
