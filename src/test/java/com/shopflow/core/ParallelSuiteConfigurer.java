package com.shopflow.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

public final class ParallelSuiteConfigurer implements IAlterSuiteListener {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelSuiteConfigurer.class);

    @Override
    public void alter(List<XmlSuite> suites) {
        int configuredThreads = ConfigReader.parallelThreads();
        for (XmlSuite suite : suites) {
            suite.setDataProviderThreadCount(configuredThreads);
            if (suite.getParallel() == XmlSuite.ParallelMode.NONE) {
                LOG.info("event=data_provider_pool_configured suite={} threads={}",
                        suite.getName(), configuredThreads);
                continue;
            }
            suite.setThreadCount(configuredThreads);
            LOG.info("event=parallel_suite_configured suite={} mode={} threads={}",
                    suite.getName(), suite.getParallel(), configuredThreads);
        }
    }
}
