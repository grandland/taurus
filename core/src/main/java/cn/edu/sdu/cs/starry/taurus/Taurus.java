package cn.edu.sdu.cs.starry.taurus;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import net.rubyeye.xmemcached.utils.AddrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.sdu.cs.starry.taurus.common.BusinessEnums.BusinessType;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessConfigurationException;
import cn.edu.sdu.cs.starry.taurus.common.exception.BusinessException;
import cn.edu.sdu.cs.starry.taurus.conf.BusinessConfiguration;
import cn.edu.sdu.cs.starry.taurus.conf.SingleBusinessTypeConfiguration;
import cn.edu.sdu.cs.starry.taurus.factory.CommandProcessorFactory;
import cn.edu.sdu.cs.starry.taurus.factory.QueryWorkerFactory;
import cn.edu.sdu.cs.starry.taurus.factory.TimerProcessorFactory;
import cn.edu.sdu.cs.starry.taurus.server.BusinessReporter;
import cn.edu.sdu.cs.starry.taurus.server.CacheTool;
import cn.edu.sdu.cs.starry.taurus.server.MemcachedTool;
import cn.edu.sdu.cs.starry.taurus.server.TaurusListener;
import cn.edu.sdu.cs.starry.taurus.state.BusinessFactoryInfo;
import cn.edu.sdu.cs.starry.taurus.state.ServerStateInfo;

public class Taurus {
    private static final Logger LOG = LoggerFactory
            .getLogger(Taurus.class);

    /**
     * TaurusListeners which listen Taurus startup and shutdown
     */
    private List<TaurusListener> taurusListeners = new LinkedList<TaurusListener>();
    private BusinessConfiguration configuration = null;
    private BusinessHandlerContainer handlerContainer;
    private CacheTool cacheTool = null;
    private List<BusinessReporter> reporterList = new LinkedList<BusinessReporter>();
    private BusinessCentralProcessor centralProcessor;
    private static Taurus starryTaurus;

    private Taurus() {}

    protected static Taurus getTaurus() {
        if (null == starryTaurus) {
            LOG.info("Creating a new taurus instance.");
            starryTaurus = new Taurus();
        }
        return starryTaurus;
    }

    private void startTaurus(BusinessConfiguration configuration) throws BusinessException {
        this.configuration = configuration;
        handlerContainer = new BusinessHandlerContainer();
        centralProcessor = new BusinessCentralProcessor();
        LOG.info("Starting taurus...");
        try {
            //get listeners instances defined in configuration
            for (String listenerName : configuration.getListenerList()) {
                Class<?> listenerClass = Class.forName(listenerName);
                TaurusListener listener = (TaurusListener) listenerClass.newInstance();
                listener.onStartup();
                taurusListeners.add(listener);
            }
            List<InetSocketAddress> cacheHostList = AddrUtil
                    .getAddresses(configuration.getCacheHosts());
            if (cacheHostList.size() == 0) {
                throw new RuntimeException();
            }
            cacheTool = new MemcachedTool(cacheHostList);
            LOG.info("Using cache hosts: " + cacheHostList);
        } catch (IOException e) {
            LOG.warn("XMemcache tool initialise failed. "
                    + e.toString());
        } catch (RuntimeException re) {
            LOG.warn("Invalid cache hosts:'"
                    + configuration.getCacheHosts() + "'");
        } catch (ClassNotFoundException e) {
            throw new BusinessConfigurationException(e);
        } catch (InstantiationException e) {
            throw new BusinessConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessConfigurationException(e);
        }


        SingleBusinessTypeConfiguration singleTypeConfiguration;
        for (BusinessType businessType : BusinessType.values()) {
            LOG.info("Starting Starry-Taurus for business type: '"
                    + businessType + "'");
            singleTypeConfiguration = configuration
                    .getSingleBusinessTypeConf(businessType.name());
            if (null == singleTypeConfiguration) {
                throw new BusinessConfigurationException(
                        "Can't find configuration for type '"
                                + businessType.name() + "'");
            }
            try {
                LOG.info("Using request-provider class: '"
                        + singleTypeConfiguration.getReqeustProvider()
                        .getClassName() + "'");
                Class<?> requestProviderClass = Class
                        .forName(singleTypeConfiguration
                                .getReqeustProvider().getClassName());
                Constructor<?> providerConstructor = requestProviderClass
                        .getConstructor(BusinessType.class);
                BusinessRequestProvider requestProvider = (BusinessRequestProvider) providerConstructor
                        .newInstance(businessType);
                LOG.info("Using reporter class: '"
                        + singleTypeConfiguration.getReporter()
                        .getClassName() + "'");
                BusinessReporter reporter = (BusinessReporter) Class
                        .forName(
                                singleTypeConfiguration.getReporter()
                                        .getClassName()).newInstance();
                reporter.initialize();
                reporterList.add(reporter);
                requestProvider.setReporter(reporter);
                LOG.info("Using response-handler class: '"
                        + singleTypeConfiguration.getResponseHandler()
                        .getClassName() + "'");
                Class<?> responseHandlerClass = Class
                        .forName(singleTypeConfiguration
                                .getResponseHandler().getClassName());
                Constructor<?> responseHandlerConstructor = responseHandlerClass
                        .getConstructor(BusinessType.class);
                BusinessResponseHandler responseHandler = (BusinessResponseHandler) responseHandlerConstructor
                        .newInstance(businessType);
                centralProcessor.initialize(singleTypeConfiguration,
                        cacheTool);
                centralProcessor.setBusinessEnable(businessType,
                        singleTypeConfiguration.getEnable());
                handlerContainer
                        .registerTypeHandler(SingleBusinessTypeHandler
                                .newSingleTypeHandler(businessType,
                                        requestProvider,
                                        centralProcessor,
                                        responseHandler,
                                        singleTypeConfiguration,
                                        configuration.getServerName(),
                                        configuration.getShutdownWait()));

            } catch (ClassNotFoundException e) {
                throw new BusinessConfigurationException(e);
            } catch (NoSuchMethodException e) {
                throw new BusinessConfigurationException(e);
            } catch (SecurityException e) {
                throw new BusinessConfigurationException(e);
            } catch (InstantiationException e) {
                throw new BusinessConfigurationException(e);
            } catch (IllegalAccessException e) {
                throw new BusinessConfigurationException(e);
            } catch (IllegalArgumentException e) {
                throw new BusinessConfigurationException(e);
            } catch (InvocationTargetException e) {
                throw new BusinessConfigurationException(e);
            }
        }
        handlerContainer.startAll();
        Runtime.getRuntime().addShutdownHook(
                new ShutdownThread(handlerContainer, reporterList,
                        centralProcessor, cacheTool, taurusListeners));
        LOG.info("Taurus starting finished.");
    }

    /**
     * Start Taurus server.
     *
     * @throws BusinessException
     */
    protected void start() throws BusinessException {
        startTaurus(loadConfiguration());
    }

    protected void start(String path) throws BusinessException {
        startTaurus(loadConfiguration(path));
    }

    /**
     * Load configuration
     *
     * @throws BusinessConfigurationException
     */
    private BusinessConfiguration loadConfiguration() throws BusinessConfigurationException {
        BusinessConfiguration configuration = new BusinessConfiguration();
        configuration.loadConfig(BusinessType.values());
        return configuration;
    }

    protected BusinessConfiguration loadConfiguration(String path) throws BusinessConfigurationException {
        BusinessConfiguration configuration = new BusinessConfiguration(path);
        configuration.loadConfig(BusinessType.values());
        return configuration;
    }

    protected ServerStateInfo getServerStateInfo() {
        ServerStateInfo infos = new ServerStateInfo();
        for (BusinessType businessType : BusinessType.values()) {
            int currentResources =
                    centralProcessor.getCurrentFactoryResource(businessType);
            int totalResources =
                    configuration.getSingleBusinessTypeConf(businessType.toString()).getFactoryResource();
            BusinessFactoryInfo factoryInfo = new BusinessFactoryInfo();
            factoryInfo.setCurrentResources(currentResources);
            factoryInfo.setTotalResources(totalResources);
            infos.putBusinessFactoryInfo(businessType, factoryInfo);
            LOG.info("Resources for " + businessType +
                    ":Config: max resources:" + totalResources +
                    ",now left resources:" + currentResources);
        }
        return infos;
    }

    protected void restart() throws BusinessException {
        restart(null);
    }

    protected void restart(String path) throws BusinessException {
        //clear first.
    	LOG.info("Received restart Taurus command");
        clearTaurus();
        boolean isStopped = false;
        LOG.info("Waiting Taurus shutdown...");
        do {
            try {
                Thread.sleep(1000);
                //System.out.println("Waiting handler thread die......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isStopped = isStopped();
        } while (!isStopped);
        LOG.info("Taurus stopped.Preparing to start again...");
        prepareRestart();
        //start using new configuration file.Default if undefined.
        if (null == path) {
            start();
        } else {
            start(path);
        }
    }

    private boolean isStopped() {
        boolean result = true;
        for (BusinessType type : BusinessType.values()) {
//        	if( configuration.getSingleBusinessTypeConf(type.toString()).getEnable() ){
//        		BusinessFactoryInfo factoryInfo = serverStateInfo.getFactoryInfo(type);
//        		result = result && (factoryInfo.getCurrentResources() == factoryInfo.getTotalResources());
//        	}
            result = result && handlerContainer.isStopped(type);
        }
        //LOG.info("is stopped:" + result);
        return result;
    }

    private void prepareRestart() {
        //reset it.
        SingleBusinessTypeHandler.reset();
        handlerContainer.reset();
        CommandProcessorFactory.reset();
        QueryWorkerFactory.reset();
        TimerProcessorFactory.reset();
    }

    private void clearTaurus() {
        handlerContainer.stopAll();
        centralProcessor.destroy();
        for (BusinessReporter reporter : reporterList) {
            if (null != reporter) {
                reporter.close();
            }
        }
        for (TaurusListener listener : taurusListeners) {
        	listener.onRestart();
        }
        if (null != cacheTool) {
            cacheTool.shutdown();
        }
        reporterList.clear();
        taurusListeners.clear();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: StarryTaurus start | info");
            System.exit(1);
        }
        try {
            if (args[0].equals("start") || args[0].equals("info")) {
                Taurus starryTaurus = Taurus.getTaurus();
//				BusinessConfiguration configuration = new BusinessConfiguration();
//				configuration.loadConfig(BusinessType.values());
                if (args[0].equals("info")) {
                    BusinessConfiguration configuration = starryTaurus.loadConfiguration();
                    SingleBusinessTypeConfiguration singleTypeConfiguration;
                    configuration.printServerInfo();
                    for (BusinessType businessType : BusinessType.values()) {
                        singleTypeConfiguration = configuration
                                .getSingleBusinessTypeConf(businessType.name());
                        if (null == singleTypeConfiguration) {
                            throw new BusinessConfigurationException(
                                    "Can't find configuration for type '"
                                            + businessType.name() + "'");
                        }
                        singleTypeConfiguration.printBusinessTypeInfo();
                    }
                    System.exit(0);
                }
                if (args[0].equals("start")) {
                    starryTaurus.start();
                }
                //MainStatic_Server taurusListener = new MainStatic_Server();
                //taurusListener.onStartup();

                // try {
                // Thread.sleep(1000);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                // System.exit(0);
                // {
                // LOG.info("Starry-Taurus will stop...");
                // handlerContainer.stopAll();
                // centralProcessor.destroy();
                // for (IBusinessReporter reporter : reporterList) {
                // if (null != reporter) {
                // reporter.close();
                // }
                // }
                // if (null != cacheTool)
                // cacheTool.shutdown();
                // }
            }
            if (args[0].equals("restarttest")) {
                Taurus.getTaurus().start();
                try {
                    Thread.sleep(4000);
                    Taurus.getTaurus().restart("conf/taurus2/taurus-conf.xml");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (BusinessException e) {
            LOG.error(e.toString());
            e.printStackTrace();
        }

    }

    public static class ShutdownThread extends Thread {
        private BusinessHandlerContainer handlerContainer;
        private List<BusinessReporter> reporterList;
        private BusinessCentralProcessor centralProcessor;
        private CacheTool cacheTool;
        private List<TaurusListener> taurusListeners;

        public ShutdownThread(BusinessHandlerContainer handlerContainer,
                              List<BusinessReporter> reporterList,
                              BusinessCentralProcessor centralProcessor, CacheTool cacheTool, List<TaurusListener> taurusListeners) {
            this.handlerContainer = handlerContainer;
            this.reporterList = reporterList;
            this.centralProcessor = centralProcessor;
            this.cacheTool = cacheTool;
            this.taurusListeners = taurusListeners;
        }

        @Override
        public void run() {
            super.run();
            LOG.info("Starry-Taurus will stop...");
            for (TaurusListener listener : taurusListeners) {
                listener.onShutdown();
            }
            handlerContainer.stopAll();
            centralProcessor.destroy();
            for (BusinessReporter reporter : reporterList) {
                if (null != reporter) {
                    reporter.close();
                }
            }
            if (null != cacheTool)
                cacheTool.shutdown();
            // System.exit(0);
        }
    }
}