package cn.edu.sdu.cs.starry.taurus.common;

import java.io.UnsupportedEncodingException;

/**
 * 
 * @author SDU.xccui
 * 
 */
public class SerializeUtil {

	public static final String toStringFromBytes(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static final byte[] toBytesFromString(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
