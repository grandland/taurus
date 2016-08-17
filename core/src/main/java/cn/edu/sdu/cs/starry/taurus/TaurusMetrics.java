package cn.edu.sdu.cs.starry.taurus;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Measure taurus metrics.
 */
public class TaurusMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(TaurusMetrics.class);

    private static final String PACKAGE = "cn.edu.sdu.cs.starry.taurus";

    private static Map<String, MetricRegistry> registryMap = new HashMap<>();

    private static Meter queryRate = new Meter();
    private static Meter commandRate = new Meter();
    private static Meter timerRate = new Meter();


    static {
        init();
    }

    public static void init() {
        // init all the metrics.
        register(PACKAGE, "QueryRate", queryRate);
        register(PACKAGE, "CommandRate", commandRate);
        register(PACKAGE, "TimerRate", timerRate);
    }

    public static void incQueryMeter(){
        queryRate.mark();
    }

    public static void incCommandMeter(){
        commandRate.mark();
    }

    public static void incTimerMeter(){
        timerRate.mark();
    }

    /**
     * Register JMX
     */
    public static void register(String packageName, String name, Metric metric) {
        LOG.debug("received a register : [{}] : [{}] : [{}]", packageName, name, metric);
        MetricRegistry registry = registryMap.get(packageName);
        if (registry == null) {
            registry = new MetricRegistry();
            registryMap.put(packageName, registry);
            // start new reporter.
            JmxReporter reporter = newReporter(registry, packageName);
            reporter.start();
            LOG.debug("start a new JMX reporter ,package : [{}]", packageName);
        }
        registry.register(name, metric);
        LOG.debug("registered a new metric : {}", metric);
    }


    /**
     * create a new reporter
     *
     * @param registry
     * @param packageName
     * @return
     */
    protected static JmxReporter newReporter(MetricRegistry registry,String packageName) {
        JmxReporter reporter = JmxReporter.forRegistry(registry)
                .inDomain(packageName).build();
        return reporter;
    }
}
