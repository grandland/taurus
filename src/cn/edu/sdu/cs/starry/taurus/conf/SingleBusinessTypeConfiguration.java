package cn.edu.sdu.cs.starry.taurus.conf;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessConfigurationException;
import cn.edu.sdu.cs.starry.taurus.server.ConfigurationUtil;

public class SingleBusinessTypeConfiguration {
    private static final int DEFAULT_FACTORY_RESOURCE = 100;
    private static final long DEFAULT_SLEEP_TIME = 5000;

    private static final Logger LOG = LoggerFactory
            .getLogger(SingleBusinessTypeConfiguration.class);
    private boolean enable;
    private String name;
    private Integer factoryResource;
    private Long sleepTime;
    private SimpleClassConf requestProvider;
    private SimpleClassConf reporter;
    private SimpleClassConf responseHandler;
    private SimpleClassBaseConf requests;
    private SimpleClassBaseConf processors;
    private SimpleClassBaseConf responses;
    private Map<String, SingleBusinessConf> businesses;

    public SingleBusinessTypeConfiguration() {
        factoryResource = null;
        sleepTime = null;
        businesses = new HashMap<String, SingleBusinessConf>();
        requestProvider = null;
        reporter = null;
        responseHandler = null;
        requests = null;
        processors = null;
        responses = null;
    }

    public boolean getEnable() {
        return enable;
    }

    public String getName() {
        return name;
    }

    public int getFactoryResource() {
        return factoryResource;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public SimpleClassConf getReqeustProvider() {
        return requestProvider;
    }

    public SimpleClassConf getReporter() {
        return reporter;
    }

    public SimpleClassConf getResponseHandler() {
        return responseHandler;
    }

    public SimpleClassBaseConf getRequests() {
        return requests;
    }

    public SimpleClassBaseConf getProcessors() {
        return processors;
    }

    public SimpleClassBaseConf getResponses() {
        return responses;
    }

    public Map<String, SingleBusinessConf> getBusinesses() {
        return businesses;
    }

    public void loadConfig(Document document, String typeName)
            throws BusinessConfigurationException {
        if (null == document) {
            throw new BusinessConfigurationException(
                    "Can't parse config file for document is null");
        }
        Element root = document.getDocumentElement();
        if (!"business-type".equals(root.getTagName())) {
            throw new BusinessConfigurationException(
                    "Bad conf file: top-level element not <business-type>");
        }
        name = root.getAttribute("name");
        if (null == name || !name.equals(typeName)) {
            throw new BusinessConfigurationException(
                    "Bad conf file: element <business-type> must contain a 'name' attribute matches '"
                            + typeName + "'");
        }
        enable = Boolean.valueOf(root.getAttribute("enable"));
        NodeList nodeList = root.getChildNodes();
        Node node;
        Element element;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) node;
            if ("factory-resource".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(factoryResource,
                        "factory-resource", "business-type");
                factoryResource = ConfigurationUtil.genIntValue(element);
            } else if ("sleep-time".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(sleepTime, "sleep-time",
                        "business-type");
                sleepTime = ConfigurationUtil.genLongValue(element);
            } else if ("request-provider".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(requestProvider,
                        "request-provider", "business-type");
                requestProvider = genSimpleClassConf(element,
                        "request-provider");
            } else if ("reporter".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(reporter, "reporter",
                        "business-type");
                reporter = genSimpleClassConf(element, "reporter");
            } else if ("response-handler".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(responseHandler,
                        "response-handler", "business-type");
                responseHandler = genSimpleClassConf(element,
                        "response-handler");
            } else if ("requests".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(requests, "requests",
                        "business-type");
                requests = genSimpleClassBaseConf(element, "requests");
            } else if ("processors".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(processors, "processors",
                        "business-type");
                processors = genSimpleClassBaseConf(element, "processors");
            } else if ("responses".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(responses, "responses",
                        "business-type");
                responses = genSimpleClassBaseConf(element, "responses");
            } else if ("businesses".equals(element.getTagName())) {
                genAndPutBusinesses(element);
            } else {
                throw new BusinessConfigurationException(
                        "Bad conf file: unknown element <"
                                + element.getTagName()
                                + "> for parent <business-type>");
            }

        }
        if (null == factoryResource) {
            LOG.warn("<factory-resource> was not set. Using "
                    + DEFAULT_FACTORY_RESOURCE + " as default");
            factoryResource = DEFAULT_FACTORY_RESOURCE;
        }
        if (null == sleepTime) {
            LOG.warn("<sleep-time> was not set. Using " + DEFAULT_SLEEP_TIME
                    + " as default");
            sleepTime = DEFAULT_SLEEP_TIME;
        }
        if (null == requestProvider) {
            throw new BusinessConfigurationException(
                    "Bad conf file: <request-provider> was not set");
        }
        if (null == reporter) {
            throw new BusinessConfigurationException(
                    "Bad conf file: <reporter> was not set");
        }
        if (null == responseHandler) {
            throw new BusinessConfigurationException(
                    "Bad conf file: <response-handler> was not set");
        }
        if (null == requests) {
            LOG.warn("<requests> was not set");
        }
        if (null == processors) {
            LOG.warn("<processors> was not set");
        }
        if (null == responses) {
            LOG.warn("<responses> was not set");
        }
    }

    public void printBusinessTypeInfo() {
        System.out.println("================ '" + name
                + "' business-keys ================");
        for (String businessKey : businesses.keySet()) {
            System.out.println(businessKey);
        }
        System.out.print("=================");
        for (int i = 0; i < name.length(); i++) {
            System.out.print("=");
        }
        System.out.print("=================================\n");
    }

    private SimpleClassConf genSimpleClassConf(Element root,
                                               String parentTagName) throws BusinessConfigurationException {
        SimpleClassConf simpleClassConf = new SimpleClassConf();
        boolean hasClassName = false;
        NodeList nodeList = root.getChildNodes();
        Node node;
        Element element;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) node;
            if ("class".equals(element.getTagName())) {
                String className = ConfigurationUtil.genStringValue(element);
                if (null == className || className.trim().length() == 0) {
                    throw new BusinessConfigurationException(
                            "Bad conf file: <"
                                    + parentTagName
                                    + "> must contain a <class> element with class name for it's value");
                }
                ConfigurationUtil.checkDuplicate(simpleClassConf.className,
                        "class", parentTagName);
                simpleClassConf.className = className;
                hasClassName = true;
            } else if ("description".equals(element.getTagName())) {
                String description = ConfigurationUtil.genStringValue(element);
                ConfigurationUtil.checkDuplicate(simpleClassConf.description,
                        "description", parentTagName);
                simpleClassConf.description = description;
            } else {
                throw new BusinessConfigurationException(
                        "Bad conf file: unknown element <"
                                + element.getTagName() + "> for parent <"
                                + parentTagName + ">");
            }
        }
        if (!hasClassName) {
            throw new BusinessConfigurationException(
                    "Bad conf file: <"
                            + parentTagName
                            + "> must contain a <class> element with class name for it's value ");
        }
        return simpleClassConf;
    }

    private SimpleClassBaseConf genSimpleClassBaseConf(Element root,
                                                       String parentTagName) throws BusinessConfigurationException {
        SimpleClassBaseConf simpleClassBaseConf = new SimpleClassBaseConf();
        NodeList nodeList = root.getChildNodes();
        Node node;
        Element element;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) node;
            if ("default-class".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(
                        simpleClassBaseConf.defaultClass, "default-class",
                        parentTagName);
                simpleClassBaseConf.defaultClass = ConfigurationUtil
                        .genStringValue(element);
            } else if ("base-path".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(simpleClassBaseConf.basePath,
                        "base-path", parentTagName);
                simpleClassBaseConf.basePath = ConfigurationUtil
                        .genStringValue(element);
            } else if ("prefix".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(simpleClassBaseConf.prefix,
                        "prefix", parentTagName);
                simpleClassBaseConf.prefix = ConfigurationUtil
                        .genStringValue(element);
            } else if ("suffix".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(simpleClassBaseConf.suffix,
                        "suffix", parentTagName);
                simpleClassBaseConf.suffix = ConfigurationUtil
                        .genStringValue(element);
            } else {
                throw new BusinessConfigurationException(
                        "Bad conf file: unknown element <"
                                + element.getTagName() + "> for parent <"
                                + parentTagName + ">");
            }
        }
        return simpleClassBaseConf;
    }

    private void genAndPutBusinesses(Element root)
            throws BusinessConfigurationException {
        NodeList nodeList = root.getChildNodes();
        Node node;
        Element element;
        SingleBusinessConf singleBusinessConf;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) node;
            if ("business".equals(element.getTagName())) {
                singleBusinessConf = genSingleBusinessConf(element);
                businesses.put(singleBusinessConf.name, singleBusinessConf);
            } else {
                throw new BusinessConfigurationException(
                        "Bad conf file: unknown element <"
                                + element.getTagName()
                                + "> for parent <businesses>");
            }
        }
    }

    private SingleBusinessConf genSingleBusinessConf(Element root)
            throws BusinessConfigurationException {
        if (!root.hasAttribute("name")) {
            throw new BusinessConfigurationException(
                    "Bad conf file: <business> must contain a 'name' attribute");
        }
        String name = root.getAttribute("name");
        name = name.trim();
        if (businesses.containsKey(name)) {
            throw new BusinessConfigurationException(
                    "Bad conf file: duplicate config for business '" + name
                            + "'");
        }
        if (!(name.length() > 0)) {
            throw new BusinessConfigurationException(
                    "Bad conf file: "
                            + name
                            + " is not a valid value for 'name' attribute in <business>");
        }
        SingleBusinessConf singleBusinessConf = new SingleBusinessConf();
        singleBusinessConf.name = name;
        String intervalStr = root.getAttribute("interval");
        if (null != intervalStr && intervalStr.length() > 0) {
            try {
                singleBusinessConf.interval = Long.valueOf(intervalStr);
            } catch (NumberFormatException ex) {
                throw new BusinessConfigurationException(
                        "Bad conf file: "
                                + intervalStr
                                + " is not a valid long value for 'interval' attribute in <business>");
            }
        }
        String delayStr = root.getAttribute("delay");
        if (null != delayStr && delayStr.length() > 0) {
            try {
                singleBusinessConf.delay = Long.valueOf(delayStr);
            } catch (NumberFormatException ex) {
                throw new BusinessConfigurationException(
                        "Bad conf file: "
                                + delayStr
                                + " is not a valid long value for 'delay' attribute in <business>");
            }
        }
        NodeList nodeList = root.getChildNodes();
        Node node;
        Element element;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            element = (Element) node;
            if ("request-class".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(
                        singleBusinessConf.requestClass, "request-class",
                        "business");
                singleBusinessConf.requestClass = ConfigurationUtil
                        .genStringValue(element);
            } else if ("processor-class".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(
                        singleBusinessConf.processorClass, "request-class",
                        "business");
                singleBusinessConf.processorClass = ConfigurationUtil
                        .genStringValue(element);
            } else if ("response-class".equals(element.getTagName())) {
                ConfigurationUtil.checkDuplicate(
                        singleBusinessConf.responseClass, "response-class",
                        "business");
                singleBusinessConf.responseClass = ConfigurationUtil
                        .genStringValue(element);
            }
        }
        return singleBusinessConf;
    }

    public static class SimpleClassConf {
        String className;
        String description;

        public SimpleClassConf() {
            className = null;
            description = null;
        }

        public String getClassName() {
            return className;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class SimpleClassBaseConf {
        String defaultClass;
        String basePath;
        String prefix;
        String suffix;

        public SimpleClassBaseConf() {
            defaultClass = null;
            basePath = null;
            prefix = null;
            suffix = null;
        }

        public String getDefaultClass() {
            return defaultClass;
        }

        public String getBasePath() {
            return basePath;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

    }

    public static class SingleBusinessConf {
        String name;
        Long delay;
        Long interval;
        String requestClass;
        String processorClass;
        String responseClass;

        public SingleBusinessConf() {
            name = null;
            delay = null;
            interval = null;
            requestClass = null;
            processorClass = null;
            responseClass = null;
        }

        public SingleBusinessConf(String name, long interval) {
            this.name = name;
            this.interval = interval;
        }

        public String getName() {
            return name;
        }

        public String getRequestClass() {
            return requestClass;
        }

        public String getProcessorClass() {
            return processorClass;
        }

        public String getResponseClass() {
            return responseClass;
        }

        public Long getInterval() {
            return interval;
        }

        public Long getDelay() {
            return delay;
        }
    }
}
