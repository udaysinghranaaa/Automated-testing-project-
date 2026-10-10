package com.shopflow.tests.advanced;

import com.shopflow.tests.BaseTest;
import com.shopflow.utils.FixtureResources;

public abstract class AdvancedFixtureTest extends BaseTest {
    @Override
    protected String initialUrl() {
        return "about:blank";
    }

    protected void openFixture(String fileName) {
        getDriver().get(FixtureResources.uri(fileName));
    }
}
