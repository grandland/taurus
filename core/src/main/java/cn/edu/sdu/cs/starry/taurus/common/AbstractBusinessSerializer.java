package cn.edu.sdu.cs.starry.taurus.common;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessCorrespondingException;

/**
 * Abstract business serializer.
 *
 * @author SDU.xccui
 */
public abstract class AbstractBusinessSerializer {

	
    /**
     * Implementation for {@link #fromBytes(byte[])} method.
     *
     * @return
     * @throws BusinessCorrespondingException
     */
    protected abstract AbstractBusinessSerializer fromBytesImpl(String json);

    /**
     * Return a {@link AbstractBusinessSerializer} object from serialized bytes.
     *
     * @param bytes
     * @return
     * @throws BusinessCorrespondingException
     */
    public AbstractBusinessSerializer fromBytes(byte[] bytes)
            throws BusinessCorrespondingException {
        try {
            return fromBytesImpl(SerializeUtil.toStringFromBytes(bytes));
        } catch (RuntimeException ex) {
            throw new BusinessCorrespondingException(ex);
        }
    }

    protected abstract String toBytesImpl();

    /**
     * Return a byte array for serial transmission.
     *
     * @return
     */
    public final byte[] toBytes() {
        return SerializeUtil.toBytesFromString(toBytesImpl());
    }
}
