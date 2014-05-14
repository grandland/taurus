package cn.edu.sdu.cs.starry.taurus.conf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessConfigurationException;
import cn.edu.sdu.cs.starry.taurus.server.ConfigurationUtil;

public class BusinessConfiguration {
    private static final Logger LOG = LoggerFactory
            .getLogger(BusinessConfiguration.class);
    public static final String TAURUS_CONF_FILE = "conf/taurus/taurus-conf.xml";
    private File confFile;

    private String cacheHosts;
    private String serverName;
    private int shutdownWait;
    private List<String> listenerList;
    private Map<String, BusinessTypeConf> businessTypeConfMap;
    private Map<String, SingleBusinessTypeConfiguration> businessConfMap;

    /**
     * new a BusinessConfiguration from default file path ,which is conf/taurus/taurus-conf.xml
     */
    public BusinessConfiguration() throws BusinessConfigurationException {
        this(TAURUS_CONF_FILE);
    }

    public BusinessConfiguration(String path) throws BusinessConfigurationException {
        businessTypeConfMap = new HashMap<String, BusinessConfiguration.BusinessTypeConf>();
        businessConfMap = new HashMap<String, SingleBusinessTypeConfiguration>();
        listenerList = new LinkedList<String>();
        if (null == confFile) {
            LOG.info("Using conf file: " + path);
            /*
			 * classLoader = Thread.class.getClassLoader(); if (null ==
			 * classLoader) { classLoader =
			 * BusinessConfiguration.class.getClassLoader(); } confURL =
			 * classLoader.getResource(TAURUS_CONF_FILE); if (null == confURL) {
			 * throw new BusinessConfigurationException("Can't find " +
			 * TAURUS_CONF_FILE); } confFile = new File(confURL.getFile());
			 */
            confFile = new File(path);
        }
    }

    public List<String> getListenerList() {
        return listenerList;
    }

    public String getCacheHosts() {
        return cacheHosts;
    }

    public String getServerName() {
        return serverName;
    }

    public int getShutdownWait() {
        return shutdownWait;
    }

    public void loadConfig(BusinessType[] systemTypes)
            throws BusinessConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                .newInstance();
        builderFactory.setIgnoringComments(true);
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(confFile);
            Element root = doc.getDocumentElement();
            if (!"taurus".equals(root.getTagName())) {
                throw new BusinessConfigurationException(
                        "Bad conf file: element<business-type> must contains a valid 'name' attribute");
            }
            NodeList nodes = root.getChildNodes();
            Node node;
            Element element;
            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                element = (Element) node;
                if ("business-type".equals(element.getTagName())) {
                    String businessTypeName = element.getAttribute("name");
                    if (null == businessTypeName
                            || businessTypeName.trim().length() == 0) {
                        throw new BusinessConfigurationException(
                                "Bad conf file: element<business-type> must contains a valid 'name' attribute");
                    }
                    NodeList businessTypeNodeList = element.getChildNodes();
                    for (int j = 0; j < businessTypeNodeList.getLength(); j++) {
                        node = businessTypeNodeList.item(j);
                        if (node.getNodeType() != Node.ELEMENT_NODE)
                            continue;
                        Element subElement = (Element) node;
                        if ("conf-file".equals(subElement.getTagName())) {
                            String subConfigFile = ConfigurationUtil
                                    .genStringValue(subElement);
                            if (subConfigFile.length() == 0) {
                                throw new BusinessConfigurationException(
                                        "Bad conf file: <conf-file> must contains a valid data");
                            }
                            businessTypeConfMap.put(businessTypeName,
                                    new BusinessTypeConf(businessTypeName,
                                            subConfigFile));

                        } else {
                            throw new BusinessConfigurationException(
                                    "Bad conf file: unknown element <"
                                            + subElement.getTagName()
                                            + "> in <business-type>");
                        }

                    }
                } else if ("cache-hosts".equals(element.getTagName())) {
                    cacheHosts = ConfigurationUtil.genStringValue(element);
                } else if ("server-name".equals(element.getTagName())) {
                    serverName = ConfigurationUtil.genStringValue(element);
                } else if ("shutdown-wait".equals(element.getTagName())) {
                    shutdownWait = ConfigurationUtil.genIntValue(element);
                } else if ("listeners".equals(element.getTagName())) {
                    NodeList listenerNodeList = element.getChildNodes();
                    for (int j = 0; j < listenerNodeList.getLength(); j++) {
                        node = listenerNodeList.item(j);
                        if (node.getNodeType() != Node.ELEMENT_NODE)
                            continue;
                        Element subElement = (Element) node;
                        if ("listener-class".equals(subElement.getTagName())) {
                            listenerList.add(ConfigurationUtil
                                    .genStringValue(subElement));
                        }
                    }
                }
            }
            Set<String> typeNameSet = new HashSet<String>();
            for (BusinessType businessType : systemTypes) {
                if (!businessTypeConfMap.containsKey(businessType.name())) {
                    throw new BusinessConfigurationException(
                            "Can't find config file for type '"
                                    + businessType.name() + "'");
                }
                typeNameSet.add(businessType.name());
            }
            for (BusinessTypeConf typeConf : businessTypeConfMap.values()) {
                File singleConfFile;
                if (typeNameSet.contains(typeConf.businessTypeName)) {
                    singleConfFile = new File(confFile.getParentFile() + "/"
                            + typeConf.businessTypeConfFile);
                    if (null == singleConfFile || !singleConfFile.exists()) {
                        throw new BusinessConfigurationException(
                                "Can't find config file '" + singleConfFile
                                        + "' for business type '"
                                        + typeConf.businessTypeName + "'");
                    }
                    doc = builder.parse(singleConfFile);
                    SingleBusinessTypeConfiguration singleTypeConfiguration = new SingleBusinessTypeConfiguration();
                    singleTypeConfiguration.loadConfig(doc,
                            typeConf.businessTypeName);
                    businessConfMap.put(typeConf.businessTypeName,
                            singleTypeConfiguration);
                } else {
                    LOG.warn("Config warnning! Unsupported business type '"
                            + typeConf.businessTypeName
                            + "'. Will ignore config file '"
                            + typeConf.businessTypeConfFile + "'");
                }
            }
        } catch (ParserConfigurationException e) {
            LOG.error("Error parsing conf file: " + confFile);
            throw new BusinessConfigurationException(e);
        } catch (SAXException e) {
            LOG.error("Error parsing conf file: " + confFile);
            throw new BusinessConfigurationException(e);
        } catch (IOException e) {
            LOG.error("Error parsing conf file: " + confFile);
            throw new BusinessConfigurationException(e);
        }

    }

    public SingleBusinessTypeConfiguration getSingleBusinessTypeConf(
            String businessType) {
        return businessConfMap.get(businessType);
    }

    public void printServerInfo() {
        System.out
                .println("====================== Starry-Taurus Server ====================== ");
        System.out.println("name: " + serverName);
        System.out.println("business-type: " + businessTypeConfMap.keySet());
        System.out.println();
    }

    private static class BusinessTypeConf {
        private String businessTypeName;
        private String businessTypeConfFile;

        public BusinessTypeConf(String businessTypeName,
                                String businessTypeConfFile) {
            this.businessTypeName = businessTypeName;
            this.businessTypeConfFile = businessTypeConfFile;
        }
    }
}
