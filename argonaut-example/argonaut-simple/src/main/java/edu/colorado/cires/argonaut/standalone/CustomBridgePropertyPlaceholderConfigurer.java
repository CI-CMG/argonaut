package edu.colorado.cires.argonaut.standalone;

import org.apache.camel.component.properties.PropertiesLookup;
import org.apache.camel.component.properties.PropertiesParser;
import org.apache.camel.spi.LoadablePropertiesSource;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;
import java.util.function.Predicate;

//import static org.apache.camel.component.properties.PropertiesComponent.SYSTEM_PROPERTIES_MODE_FALLBACK;

public class CustomBridgePropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer
        implements PropertiesParser, LoadablePropertiesSource, PropertyPlaceholderHelper.PlaceholderResolver {

    private PropertiesParser camelParser;
    private PropertyPlaceholderHelper helper;
//    private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;
    private ConfigurablePropertyResolver propertyResolver;

    public void setParser(PropertiesParser parser) {
        this.camelParser = parser;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
        super.processProperties(beanFactoryToProcess, propertyResolver);
        this.propertyResolver = propertyResolver;
        helper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator, DEFAULT_ESCAPE_CHARACTER, ignoreUnresolvablePlaceholders);
    }

    @Override
    public @Nullable String resolvePlaceholder(String placeholderName) {
        return propertyResolver.resolvePlaceholders("${" + placeholderName + "}");
    }

    protected String springResolvePlaceholders(String text, PropertiesLookup properties) {
        return helper.replacePlaceholders(text, this);
    }

    @Override
    public String parseUri(String text, PropertiesLookup properties, boolean fallback, boolean keepUnresolvedOptional, boolean nestedPlaceholder) throws IllegalArgumentException {
        String answer = camelParser.parseUri(text, properties, fallback, keepUnresolvedOptional, nestedPlaceholder);
        if (answer != null) {
            answer = springResolvePlaceholders(answer, properties);
        } else {
            answer = springResolvePlaceholders(text, properties);
        }
        return answer;
    }

    @Override
    public String parseProperty(String key, String value, PropertiesLookup properties) {
        String answer = camelParser.parseProperty(key, value, properties);
        if (answer != null) {
            answer = springResolvePlaceholders(answer, properties);
        } else {
            answer = springResolvePlaceholders(value, properties);
        }
        return answer;
    }

    @Override
    public Properties loadProperties() {
        return null;
    }

    @Override
    public Properties loadProperties(Predicate<String> filter) {
        return null;
    }

    @Override
    public void reloadProperties(String location) {

    }

    @Override
    public String getName() {
        return "CustomBridgePropertyPlaceholderConfigurer";
    }

    @Override
    public String getProperty(String name) {
        return propertyResolver.getProperty(name);
    }
}
