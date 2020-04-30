/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.applicationelb;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import static com.appdynamics.extensions.aws.applicationelb.Constants.AMAZON_SERVICE;
import static com.appdynamics.extensions.aws.applicationelb.Constants.CUSTOM_METRICS;
import static com.appdynamics.extensions.aws.applicationelb.Constants.MONITOR_NAME;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.google.common.collect.Lists;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bhuvnesh Kumar
 */
public class ELBMonitor extends SingleNamespaceCloudwatchMonitor<Configuration> {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(ELBMonitor.class);
    private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s", CUSTOM_METRICS, "|", AMAZON_SERVICE, "|");

    public ELBMonitor() {
        super(Configuration.class);
        LOGGER.info(String.format("Using AWS ELB Monitor Version [%s]",
                this.getClass().getPackage().getImplementationTitle()));
    }

    @Override
    public String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        return Lists.newArrayList();
    }

    @Override
    protected void initialize(Configuration config) {
        super.initialize(config);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
            Configuration config) {
        MetricsProcessor metricsProcessor = createMetricsProcessor(config);
        return new NamespaceMetricStatisticsCollector
                .Builder(config.getAccounts(),
                config.getConcurrencyConfig(),
                config.getMetricsConfig(),
                metricsProcessor,
                config.getMetricPrefix())
                .withCredentialsDecryptionConfig(config.getCredentialsDecryptionConfig())
                .withProxyConfig(config.getProxyConfig())
                .build();
    }

    private MetricsProcessor createMetricsProcessor(Configuration config) {
        return new ELBMetricsProcessor(
                config.getMetricsConfig().getIncludeMetrics(),
                config.getDimensions());
    }

    public static void main(String[] args) throws TaskExecutionException {

        ELBMonitor monitor = new ELBMonitor();

        Map<String, String> taskArgs = new HashMap<String, String>();

        taskArgs.put("config-file", "/Applications/appdynamics/ma4518/monitors/AWSApplicationELBMonitor/config.yml");

        monitor.execute(taskArgs, null);

    }
}
