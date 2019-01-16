/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.applicationelb;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import org.apache.log4j.Logger;

import static com.appdynamics.extensions.aws.applicationelb.Constants.*;

/**
 * @author Bhuvnesh Kumar
 */
public class ELBMonitor extends SingleNamespaceCloudwatchMonitor<Configuration> {

    private static final Logger LOGGER = Logger.getLogger(ELBMonitor.class);
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
    protected int getTaskCount() {
        return 3;
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
}
