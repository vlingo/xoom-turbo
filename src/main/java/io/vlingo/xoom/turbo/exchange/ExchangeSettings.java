// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.exchange;

import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.turbo.ApplicationProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExchangeSettings {

    private static final String EXCHANGE_NAMES = "exchange.names";
    private static final List<ExchangeSettings> ALL_EXCHANGE_PARAMETERS = new ArrayList<>();
    private static final List<String> PROPERTIES_KEYS =
            Arrays.asList("exchange.%s.hostname", "exchange.%s.username",
                    "exchange.%s.password", "exchange.%s.port", "exchange.%s.virtual.host");

    private final String exchangeName;
    private final List<String> keys;
    private final List<ExchangeSettingsItem> parameters;

    public static List<ExchangeSettings> all() {
        return ALL_EXCHANGE_PARAMETERS;
    }

    public static ExchangeSettings of(final String exchangeName) {
        return ALL_EXCHANGE_PARAMETERS.stream().filter(params -> params.hasName(exchangeName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exchange with name " + exchangeName + " not found"));
    }

    public static List<ExchangeSettings> load(final Properties properties) {
        if(ALL_EXCHANGE_PARAMETERS.isEmpty()) {
            final Function<String, ExchangeSettings> mapper =
                    exchangeName -> new ExchangeSettings(exchangeName, properties);

            final List<ExchangeSettings> exchangeParameters =
                    ApplicationProperty.readMultipleValues(EXCHANGE_NAMES, ";", properties)
                            .stream().map(mapper).collect(Collectors.toList());

            ALL_EXCHANGE_PARAMETERS.addAll(exchangeParameters);
        }
        return ALL_EXCHANGE_PARAMETERS;
    }

    private ExchangeSettings(final String exchangeName, final Properties properties) {
        this.exchangeName = exchangeName;
        this.keys = prepareKeys(exchangeName);
        this.parameters = retrieveParameters(properties);
        this.validate();
    }

    private List<String> prepareKeys(final String exchangeName) {
        return PROPERTIES_KEYS.stream().map(key -> String.format(key, exchangeName))
                .collect(Collectors.toList());
    }

    private void validate() {
        final List<String> parametersNotFound =
                this.parameters.stream().filter(param -> param.value == null)
                        .map(param -> param.key).collect(Collectors.toList());

        if(!parametersNotFound.isEmpty()) {
            throw new ExchangeSettingsNotFoundException(parametersNotFound);
        }
    }

    private List<ExchangeSettingsItem> retrieveParameters(final Properties properties) {
        final Function<String, ExchangeSettingsItem> mapper =
                key -> new ExchangeSettingsItem(key, ApplicationProperty.readValue(key, properties));

        return this.keys.stream().map(mapper).collect(Collectors.toList());
    }

    private boolean hasName(final String exchangeName) {
        return this.exchangeName.equals(exchangeName);
    }

    public ConnectionSettings mapToConnection() {
        return new ConnectionSettings(retrieveParameterValue("hostname"),
                retrieveParameterValue("port", Integer::valueOf),
                retrieveParameterValue("virtual.host"),
                retrieveParameterValue("username"),
                retrieveParameterValue("password"));
    }

    private String retrieveParameterValue(final String key) {
        return retrieveParameterValue(key, value -> value);
    }

    private <T> T retrieveParameterValue(final String keySuffix, final Function<String, T> converter) {
        final String key = resolveKey(keySuffix);

        final String value =
                parameters.stream().filter(param -> param.hasKey(key))
                        .map(param -> param.value).findFirst().get();

        return converter.apply(value);
    }

    private String resolveKey(final String keySuffix)  {
        return String.format("exchange.%s.%s", this.exchangeName, keySuffix);
    }
}
