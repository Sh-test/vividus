/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.ui.web.playwright;

import java.util.Optional;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import org.vividus.testcontext.TestContext;
import org.vividus.ui.web.playwright.locator.PlaywrightLocator;

public class UiContext
{
    private final TestContext testContext;

    public UiContext(TestContext testContext)
    {
        this.testContext = testContext;
    }

    public void setCurrentPage(Page page)
    {
        getPlaywrightContext().page = page;
    }

    public Page getCurrentPage()
    {
        return getPlaywrightContext().page;
    }

    public void setContext(Locator context)
    {
        getPlaywrightContext().context = context;
    }

    public void reset()
    {
        getPlaywrightContext().context = null;
    }

    public Locator locateElement(PlaywrightLocator playwrightLocator)
    {
        PlaywrightContext playwrightContext = getPlaywrightContext();
        String locator = playwrightLocator.getLocator();
        return Optional.ofNullable(playwrightContext.context)
                .map(context -> context.locator(locator))
                .orElseGet(() -> playwrightContext.page.locator(locator));
    }

    private PlaywrightContext getPlaywrightContext()
    {
        return testContext.get(PlaywrightContext.class, PlaywrightContext::new);
    }

    private static final class PlaywrightContext
    {
        private Page page;
        private Locator context;
    }
}
