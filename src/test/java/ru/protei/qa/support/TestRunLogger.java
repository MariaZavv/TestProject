package ru.protei.qa.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public final class TestRunLogger implements ITestListener, ISuiteListener {
    private static final Logger LOG = LoggerFactory.getLogger("qa.run");

    @Override
    public void onStart(ISuite suite) {
        LOG.info("SUITE START: {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        LOG.info("SUITE FINISH: {}", suite.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("TEST START: {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("TEST PASS: {} ({} ms)", testName(result), durationMs(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("TEST FAIL: {} ({} ms)", testName(result), durationMs(result), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("TEST SKIP: {} ({} ms)", testName(result), durationMs(result));
    }

    private static String testName(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = testDescription(result);

        if (description.isBlank()) {
            return methodName;
        }

        return methodName + " - " + description;
    }

    private static String testDescription(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        Test test = method.getAnnotation(Test.class);
        return test == null ? "" : test.description();
    }

    private static long durationMs(ITestResult result) {
        return Math.max(0, result.getEndMillis() - result.getStartMillis());
    }
}
