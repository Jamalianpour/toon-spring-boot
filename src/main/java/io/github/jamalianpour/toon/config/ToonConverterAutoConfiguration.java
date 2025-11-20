package io.github.jamalianpour.toon.config;

import io.github.jamalianpour.toon.core.ToonConverter;
import io.github.jamalianpour.toon.model.ToonConfiguration;
import io.github.jamalianpour.toon.service.ToonConverterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for TOON converter.
 *
 * @author Mohammad Jamalianpour
 */
@Configuration
@EnableConfigurationProperties(ToonConfiguration.class)
public class ToonConverterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ToonConverter toonConverter(ToonConfiguration configuration) {
        return new ToonConverter(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public ToonConverterService toonConverterService(ToonConverter converter) {
        return new ToonConverterService(converter);
    }
}