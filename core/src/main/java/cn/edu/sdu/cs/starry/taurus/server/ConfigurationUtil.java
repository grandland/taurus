package cn.edu.sdu.cs.starry.taurus.server;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessConfigurationException;

/**
 * Utility for loading XML configuration file.
 *
 * @author SDU.xccui
 */
public class ConfigurationUtil {
    public static void checkDuplicate(Object o, String tagName,
                                      String parentTagName) throws BusinessConfigurationException {
        if (o != null) {
            throw new BusinessConfigurationException(
                    "Bad conf file: duplicate element <" + tagName + "> in <"
                            + parentTagName + ">");
        }
    }

    public static Integer genIntValue(Element element)
            throws BusinessConfigurationException {
        if (null == element || !element.hasChildNodes()) {
            return null;
        }
        String s = ((Text) (element.getFirstChild())).getData().trim();
        try {
            int i = Integer.valueOf(s);
            return i;
        } catch (NumberFormatException ex) {
            throw new BusinessConfigurationException("Bad conf file: " + s
                    + " is not a valid int value for <" + element.getTagName()
                    + ">");
        }
    }

    public static Long genLongValue(Element element)
            throws BusinessConfigurationException {
        if (null == element || !element.hasChildNodes()) {
            return null;
        }
        String s = ((Text) (element.getFirstChild())).getData().trim();
        try {
            long l = Long.valueOf(s);
            return l;
        } catch (NumberFormatException ex) {
            throw new BusinessConfigurationException("Bad conf file: " + s
                    + " is not a valid long value for <" + element.getTagName()
                    + ">");
        }
    }

    public static String genStringValue(Element element) {
        if (null == element || !element.hasChildNodes()) {
            return null;
        }
        return ((Text) (element.getFirstChild())).getData().trim();
    }
}
