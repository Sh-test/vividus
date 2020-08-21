/*
 * Copyright 2019-2020 the original author or authors.
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

package org.vividus.bdd.variable.ui.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.vividus.ui.web.context.WebUiContext;

@ExtendWith(MockitoExtension.class)
class SearchContextXCoordinateDynamicVariableTests
{
    @Mock
    private WebUiContext webUiContext;

    @InjectMocks
    private SearchContextXCoordinateDynamicVariable xDynamicVariable;

    @Test
    void shouldReturnElementHeight()
    {
        WebElement webElement = mock(WebElement.class);
        when(webUiContext.getSearchContext(WebElement.class)).thenReturn(webElement);
        Rectangle rectangle = mock(Rectangle.class);
        when(webElement.getRect()).thenReturn(rectangle);
        when(rectangle.getX()).thenReturn(1924);
        assertEquals("1924", xDynamicVariable.getValue());
    }
}
