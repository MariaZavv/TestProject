package ru.protei.qa.support;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class TestStep {
    private static final Logger LOG = LoggerFactory.getLogger("qa.steps");

    private TestStep() {}

    public static void run(String name, Runnable action) {
        LOG.info("STEP: {}", name);
        Allure.step(name, () -> {
            try {
                action.run();
            } finally {
                TestConfig.pauseForUiDemo();
            }
        });
    }

    public static <T> T call(String name, Supplier<T> action) {
        LOG.info("STEP: {}", name);
        return Allure.step(name, () -> {
            try {
                return action.get();
            } finally {
                TestConfig.pauseForUiDemo();
            }
        });
    }
}
