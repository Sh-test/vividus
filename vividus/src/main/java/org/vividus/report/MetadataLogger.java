/*
 * Copyright 2019-2021 the original author or authors.
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

package org.vividus.report;

import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vividus.bdd.StatisticsStoryReporter;
import org.vividus.bdd.model.NodeType;
import org.vividus.bdd.model.Statistic;
import org.vividus.reporter.environment.EnvironmentConfigurer;
import org.vividus.util.ResourceUtils;

public final class MetadataLogger
{
    private static final String HYPHEN = "-";
    private static final int HEADER_SIZE = 40;
    private static final String CATEGORY_FORMAT = "%s%n %s:%n";
    private static final String NEW_LINE = "%n";
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataLogger.class);
    private static final Pattern SECURE_KEY_PATTERN = Pattern
            .compile(".*(password|access-?key|api-?key|secret|token).*", Pattern.CASE_INSENSITIVE);
    private static final int HORIZONTAL_RULE_LENGTH = 60;

    private MetadataLogger()
    {
    }

    public static void drawBanner()
    {
        LOGGER.atInfo().addArgument(() -> ResourceUtils.loadResource("banner.vividus")).log("\n{}");
    }

    public static void logEnvironmentMetadata()
    {
        if (LOGGER.isInfoEnabled())
        {
            String horizontalRule = HYPHEN.repeat(HORIZONTAL_RULE_LENGTH);
            int maxKeyLength = EnvironmentConfigurer.ENVIRONMENT_CONFIGURATION
                    .values()
                    .stream()
                    .map(Map::keySet)
                    .flatMap(Set::stream)
                    .mapToInt(String::length)
                    .max().orElse(0);
            String propertyFormat = "   %-" + maxKeyLength + "s %s%n";
            try (Formatter message = new Formatter())
            {
                message.format(NEW_LINE);
                EnvironmentConfigurer.ENVIRONMENT_CONFIGURATION.forEach((category, properties) -> {
                    message.format(CATEGORY_FORMAT, horizontalRule, category.getCategoryName());
                    properties.forEach((name, value) -> message.format(propertyFormat, name, value));
                });
                logExecutionStatistics(message);
                LOGGER.atInfo().log(message::toString);
            }
        }
    }

    private static void logExecutionStatistics(Formatter message)
    {
        Map<NodeType, Statistic> statistics = StatisticsStoryReporter.getStatistics();
        Statistic story = statistics.get(NodeType.STORY);
        Statistic scenario = statistics.get(NodeType.SCENARIO);
        Statistic step = statistics.get(NodeType.STEP);
        String row = "%n   %-12s %6s %10s %8s";
        message.format("%n Execution statistics:");
        String rowsSeparator = "%n   " + HYPHEN.repeat(HEADER_SIZE);
        message.format(rowsSeparator);
        message.format(row, "", "Story", "Scenario", "Step");
        message.format(rowsSeparator);
        message.format(row, "Passed", story.getPassed(), scenario.getPassed(), step.getPassed());
        message.format(row, "Failed", story.getFailed(), scenario.getFailed(), step.getFailed());
        message.format(row, "Broken", story.getBroken(), scenario.getBroken(), step.getBroken());
        message.format(row, "Known Issue", story.getKnownIssue(), scenario.getKnownIssue(), step.getKnownIssue());
        message.format(row, "Pending", story.getPending(), scenario.getPending(), step.getPending());
        message.format(row, "Skipped", story.getSkipped(), scenario.getSkipped(), step.getSkipped());
        message.format(rowsSeparator);
        message.format(row, "TOTAL", story.getTotal(), scenario.getTotal(), step.getTotal());
    }

    public static void logPropertiesSecurely(Properties properties)
    {
        Maps.fromProperties(properties).entrySet().stream()
            .sorted(Entry.comparingByKey())
            .forEach(MetadataLogger::logPropertySecurely);
    }

    private static void logPropertySecurely(Entry<String, String> entry)
    {
        String key = entry.getKey();
        String value = SECURE_KEY_PATTERN.matcher(key).matches() ? "****" : entry.getValue();
        LOGGER.info("{}={}", key, value);
    }
}
