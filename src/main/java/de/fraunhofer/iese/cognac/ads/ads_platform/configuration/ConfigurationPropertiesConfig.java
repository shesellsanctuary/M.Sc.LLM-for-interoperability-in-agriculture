package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {AdsPlatformConfigurationProperties.class})
public class ConfigurationPropertiesConfig {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesConfig.class);

  @Autowired
  public ConfigurationPropertiesConfig(AdsPlatformConfigurationProperties adsPlatformConfigurationProperties) {
    logger.debug("Config: {}", adsPlatformConfigurationProperties);
  }

}
